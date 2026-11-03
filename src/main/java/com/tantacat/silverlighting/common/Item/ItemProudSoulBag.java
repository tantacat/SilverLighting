package com.tantacat.silverlighting.common.Item;

import com.google.common.collect.Lists;
import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.capability.IProudSoulStorage;
import com.tantacat.silverlighting.capability.ProudSoulStorage;
import com.tantacat.silverlighting.capability.ProudSoulStorageProvider;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 耀魂袋。
 *
 * <p>袋子的内容存在<b>物品自身的 NBT</b> 里（键见
 * {@link com.tantacat.silverlighting.capability.ProudSoulBagData}），
 * 通过标准能力 {@code ITEM_HANDLER_CAPABILITY} 暴露给其它 mod。
 *
 * <p>因此这里不需要任何"把能力数据塞进物品标签"的额外逻辑：
 * <ul>
 * <li>服务端 -> 客户端：原版 {@code PacketBuffer.writeItemStack} 发的就是
 *     {@code Item#getNBTShareTag}（默认就是物品标签），客户端自然拿得到内容；</li>
 * <li>客户端 -> 服务端：创造模式整理背包等原版路径发的也只有物品标签，
 *     内容跟着标签走，所以不会像以前那样把袋子清空。</li>
 * </ul>
 * 空袋子身上没有任何 NBT（能力提供者不参与序列化，不会留下 {@code ForgeCaps}）。
 */
public class ItemProudSoulBag extends Item {

	public ItemProudSoulBag() {
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.openGui(ModMain.instance, 0, worldIn, 0, handIn.ordinal(), 0);
		// 返回 SUCCESS：PASS 会让客户端继续处理另一只手，双手都拿袋子时会开两次界面
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (!(entity instanceof EntityBladeStand))
			return super.onLeftClickEntity(stack, player, entity);

		ItemStack offHand = player.getHeldItemOffhand();
		if (entity.isBurning() && offHand.getItem() instanceof ItemProudSoul) {
			offHand.grow(1);
			boolean result = offHand.getItem().onLeftClickEntity(offHand, player, entity);
			offHand.shrink(1);
			return result;
		}

		if (entity.isBurning()) {
			entity.extinguish();
		}
		else {
			entity.setFire(200);
		}

		return super.onLeftClickEntity(stack, player, entity);
	}

	@Nonnull
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound oldCapNbt) {
		// oldCapNbt 是老存档里 ForgeCaps 下的旧能力数据，用来把内容迁移进物品标签
		return new ProudSoulStorageProvider(stack, oldCapNbt);
	}

	/**
	 * 每次自动补齐最多补几个破碎耀魂。
	 * <p>
	 * 与改动前的行为一致（每 tick 补 1 个，所以贴着背包那一叠慢慢涨）；
	 * 想让它一次补齐就把这个数字调大（补满一叠的 tick 数 = 64 / 这个值）。
	 */
	private static final int REFILL_PER_TICK = 8;

	/** 用于比较"是不是破碎耀魂"的模板（懒加载缓存，避免每 tick 新建物品堆） */
	@Nullable
	private static ItemStack tinySoulTemplate;

	/**
	 * 服务端每 tick：
	 * <ol>
	 * <li><b>自动补齐</b>：袋子在<b>快捷栏</b>（0..8，含主手，不需要拿在手上）时，
	 *     把背包里"没满的破碎耀魂"从袋子里补到满；袋子在副手或背包 9~35 格时不补——
	 *     这是设计，不是省事；</li>
	 * <li><b>副手袋子补推</b>：内容一变就推给客户端（副手槽位不在袋子界面容器里，
	 *     容器的 {@code detectAndSendChanges()} 永远带不到它）。</li>
	 * </ol>
	 * <p>
	 * 只优化开销，不改上面的行为：
	 * <ul>
	 * <li>补齐先扫背包拿到"没满的那一叠"的活引用，扫不到就<b>完全不碰袋子</b>
	 *     （袋内没货时也从 4 次以上的整袋 NBT 解析降到 0 次）；</li>
	 * <li>扫到之后走 {@link IProudSoulStorage#getStoredCount} + {@link IProudSoulStorage#takeItem}，
	 *     不再每 tick 调 {@code getSlots()/getStackInSlot()/extractItem()} 各解析一遍整袋 NBT；</li>
	 * <li>模板物品堆只建一次；超出那一叠上限的部分不会被取出，物品不会多也不会少。</li>
	 * </ul>
	 */
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if (worldIn == null || worldIn.isRemote) return;                 // 只在服务端
		if (!(entityIn instanceof EntityPlayer)) return;

		EntityPlayer player = (EntityPlayer) entityIn;
		boolean inMainHand = player.getHeldItemMainhand() == stack;

		// 1) 袋子在快捷栏（0..8）时，背包里的破碎耀魂自动补齐；不需要拿在手上
		if (itemSlot >= 0 && itemSlot < 9 && !inMainHand) {
			refillTinyProudSoul(player, stack);
		}

		// 2) 主手：容器同步每 tick 会把内容带给客户端，不用管
		if (inMainHand) return;
		// 也不是副手：不用补推
		if (player.getHeldItemOffhand() != stack) return;

		IProudSoulStorage storage = ProudSoulStorageProvider.getStorage(stack);
		if (storage instanceof ProudSoulStorage && ((ProudSoulStorage) storage).pollTagChanged()) {
			ProudSoulStorageProvider.syncBagToClient(player, stack);
		}
	}

	/**
	 * 把背包里"没满的破碎耀魂"从袋子里补到满（每次最多 {@link #REFILL_PER_TICK} 个）。
	 * <p>
	 * 直接对玩家的活物品堆调用 {@code grow}：那一叠就在背包列表里，改完同一个 tick 的
	 * 容器同步就会把这一格发给客户端，不需要走一遍"插入背包"的流程。
	 * <p>
	 * 解析次数是固定的：<b>先扫背包</b>（本来就要做）确定确实有可补的那一叠，才去读袋子；
	 * 读袋子只用两次整袋 NBT 解析——一次 {@code getStoredCount} 确认袋内有货，
	 * 一次 {@code takeItem} 真正取出。袋内没有可补的耀魂时只花一次。
	 */
	private static void refillTinyProudSoul(EntityPlayer player, ItemStack bag) {
		// 1) 扫一遍背包，拿到"没满的那一叠"的活引用
		ItemStack held = ItemStack.EMPTY;
		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			ItemStack candidate = player.inventory.mainInventory.get(i);
			if (candidate.isEmpty()) continue;
			if (candidate.getCount() >= candidate.getMaxStackSize()) continue;
			if (!isTinyProudSoul(candidate)) continue;
			held = candidate;
			break;
		}
		if (held.isEmpty()) return;                                      // 没有可补的：不碰袋子

		// 2) 再读袋子
		IProudSoulStorage storage = ProudSoulStorageProvider.getStorage(bag);
		if (storage == null) return;
		if (storage.getStoredCount(held) <= 0) return;                   // 袋内没有：只花这一次

		int amount = Math.min(REFILL_PER_TICK, held.getMaxStackSize() - held.getCount());
		if (amount <= 0) return;

		// 超出上限的部分不会被取出（takeItem 只取真正的数量），物品不会凭空多也不会少
		int taken = storage.takeItem(held, amount);
		if (taken > 0) held.grow(taken);
	}

	/** 是不是"破碎的耀魂"（TinyBladeSoul）；模板只建一次 */
	private static boolean isTinyProudSoul(@Nonnull ItemStack stack) {
		ItemStack template = tinySoulTemplate;
		if (template == null || template.isEmpty()) {
			template = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1);
			if (template.isEmpty()) return false;
			tinySoulTemplate = template;
		}
		return ItemHandlerHelper.canItemStacksStack(stack, template);
	}

	@Override
	public void addInformation(ItemStack stack, World world, List par3List, ITooltipFlag inFlag) {
		super.addInformation(stack, world, par3List, inFlag);
		par3List.addAll(Lists.newArrayList(I18n.translateToLocal("silverlighting.proudsoulbag.tooltips").split(" ")));
	}
}
