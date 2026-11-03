package com.tantacat.silverlighting.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

/**
 * 耀魂袋物品的能力提供者：把 {@link ProudSoulStorage} 暴露成 Forge 标准能力
 * {@link CapabilityItemHandler#ITEM_HANDLER_CAPABILITY}，供其它 mod 使用。
 *
 * <p><b>为什么这里刻意不实现 {@code ICapabilitySerializable}</b>：
 * {@code CapabilityDispatcher} 只把实现了 {@code INBTSerializable} 的提供者当作“可写节点”，
 * 只要有一个可写节点，{@code ItemStack.writeToNBT} 就会把
 * {@code ForgeCaps:{Parent:{...}}} 写进物品 NBT（{@code ItemStack.java:260-263}）。
 * 而袋子内容已经写在物品自己的标签里了，能力这边没有任何东西需要序列化——
 * 不实现它，{@code serializeNBT()} 就是空的，物品上永远不会出现 {@code ForgeCaps}，
 * 空袋子也就真的没有任何 NBT。
 *
 * <p>内容随物品标签在两端流动（服务端 -> 客户端有 {@code getNBTShareTag}，
 * 客户端 -> 服务端原版也只发标签），所以这里不需要任何"记住旧标签"的同步逻辑。
 */
public class ProudSoulStorageProvider implements ICapabilityProvider {

	private final ItemStack stack;
	private final ProudSoulStorage storage;

	/**
	 * @param stack     数据要挂在哪个物品堆上
	 * @param oldCapNbt {@code Item#initCapabilities} 传进来的旧能力数据（老存档在
	 *                  {@code ForgeCaps.Parent} 里），用于把旧数据迁移进物品标签
	 */
	public ProudSoulStorageProvider(ItemStack stack, @Nullable NBTTagCompound oldCapNbt) {
		this.stack = stack;
		// 老存档迁移：旧信封键 / 旧能力数据 -> 物品自带标签里的新键
		ProudSoulBagData.migrate(stack, oldCapNbt);
		this.storage = new ProudSoulStorage(stack);
	}

	public ItemStack getStack() {
		return this.stack;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return isItemHandler(capability);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (isItemHandler(capability)) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.storage);
		}
		return null;
	}

	/**
	 * 能力字段在 Forge 自身的 preInit 中才被注入，早期构造物品堆时可能为 null，故一并判断。
	 */
	private static boolean isItemHandler(@Nullable Capability<?> capability) {
		return capability != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	/**
	 * 通过标准物品处理器能力取出耀魂袋的存储（双端可用）。
	 *
	 * @return 该物品堆的耀魂存储；物品堆为空或不是耀魂袋时返回 null
	 */
	@Nullable
	public static IProudSoulStorage getStorage(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty()) return null;

		IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		return handler instanceof IProudSoulStorage ? (IProudSoulStorage) handler : null;
	}

	/**
	 * 服务端：把袋子的最新内容推给客户端。
	 *
	 * <p>袋子本身就在玩家背包里，所以正常情况下原版容器同步会把它带走；这里额外显式做两件事：
	 * <ol>
	 * <li>{@code openContainer.detectAndSendChanges()}：立刻刷新我们打开的容器
	 *     （玩家背包槽位，含拿在主手的袋子），不用等下一 tick；</li>
	 * <li>{@code SPacketSetSlot(-2, idx, bag)}：<b>直接</b>把玩家背包的某一格写到客户端。
	 *     原版自己就这么用（{@code InventoryPlayer:537}、{@code NetHandlerPlayServer:1846}），
	 *     客户端 {@code handleSetSlot} 对 windowId == -2 直接
	 *     {@code inventory.setInventorySlotContents(i, stack)}，<b>不要求打开的是哪个容器</b>。
	 *     这一条专门解决"袋子拿在副手时界面内容不刷新"——副手槽位不在我们的容器里，
	 *     {@code EntityPlayerMP.onUpdate} 也只同步 openContainer。</li>
	 * </ol>
	 */
	public static void syncBagToClient(@Nullable EntityPlayer player, @Nullable ItemStack bag) {
		if (player == null || bag == null || bag.isEmpty()) return;
		if (player.world == null || player.world.isRemote) return;
		if (!(player instanceof EntityPlayerMP)) return;

		EntityPlayerMP mp = (EntityPlayerMP) player;
		if (mp.openContainer != null) mp.openContainer.detectAndSendChanges();

		int index = inventoryIndexOf(mp, bag);
		if (index >= 0) mp.connection.sendPacket(new SPacketSetSlot(-2, index, bag));
	}

	/**
	 * 找出这个物品堆在玩家背包里的下标（主手 = 当前快捷栏位，副手 = 40，其余按身份查找）。
	 *
	 * @return 下标，找不到返回 -1
	 */
	public static int inventoryIndexOf(EntityPlayer player, ItemStack bag) {
		if (player == null || bag == null || bag.isEmpty()) return -1;
		if (player.getHeldItemMainhand() == bag) return player.inventory.currentItem;
		if (player.getHeldItemOffhand() == bag) return 40;

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			if (player.inventory.getStackInSlot(i) == bag) return i;
		}
		return -1;
	}
}
