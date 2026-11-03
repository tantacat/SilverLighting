package com.tantacat.silverlighting.capability;

import mods.flammpfeil.slashblade.item.ItemProudSoul;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 耀魂袋的存储：<b>无状态</b>地架在物品自带 NBT 之上。
 *
 * <p>每次调用都是「读物品标签 -> 改一份工作副本 -> 写回物品标签」，所以：
 * <ul>
 * <li>任何一方（本模组的界面、别的 mod 抓住的 handler、原版槽位同步、创造模式背包整理）
 *     拿到的永远是最新数据，不存在缓存过期；</li>
 * <li>不需要任何“内容变化回调 / 信封 / 同步标记”的额外机制；</li>
 * <li>物品标签变了（存档读取、网络包、{@code ItemStack.copy()}）这里自然就读到新的。</li>
 * </ul>
 *
 * <p>这份存储通过 Forge 标准能力
 * {@code CapabilityItemHandler.ITEM_HANDLER_CAPABILITY} 暴露给其它 mod；
 * 它继承 {@link IProudSoulStorage}，额外提供耀魂专用的存取方法。
 *
 * <p>要点：袋子对每种耀魂只保留一条记录（相同标识会被 {@link AddableItemStackHandler}
 * 合并），单条的数量上限是 {@link Integer#MAX_VALUE}，记录条数随种类自动增长，
 * 所以“物品身份”就是这份存储里无歧义的主键——界面的点击指令靠它来定位。
 */
public class ProudSoulStorage implements IProudSoulStorage {

	/** 内容挂在哪个物品上 */
	private final ItemStack bag;
	/** 上一次推给客户端的标签对象；只用于"副手袋子"的补推去重（不是数据，数据永远只从物品标签读） */
	private NBTTagCompound lastPushedTag;

	public ProudSoulStorage(ItemStack bag) {
		this.bag = bag;
	}

	/** 物品堆（可能为空物品堆，此时存储视为空） */
	public ItemStack getBagStack() {
		return this.bag;
	}

	// ------------------------------------------------------------------
	// 读写：无状态视图
	// ------------------------------------------------------------------

	/** 载入一份工作副本（顺带把历史遗留的空 SB.SEffect 规范化掉，保证两侧比较条件一致） */
	private AddableItemStackHandler load() {
		AddableItemStackHandler handler = new AddableItemStackHandler();
		NBTTagCompound data = ProudSoulBagData.read(this.bag);
		if (data != null) handler.deserializeNBT(data);
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack stored = handler.getStackInSlot(i);
			if (!stored.isEmpty()) fixProudSoul(stored);
		}
		return handler;
	}

	/** 写回物品标签（内容为空时会顺带把标签清干净） */
	private void save(AddableItemStackHandler handler) {
		ProudSoulBagData.set(this.bag, handler.serializeNBT());
	}

	/**
	 * 物品标签是否在上次询问之后变过（只用于"副手袋子"的按需补推）。
	 * <p>
	 * 每次写回都会换一个新的 compound 对象（{@link ProudSoulBagData#set} 用的是副本），
	 * 所以比较对象身份就够了，不需要每 tick 把整份内容推给客户端。
	 */
	public boolean pollTagChanged() {
		NBTTagCompound tag = this.bag == null || this.bag.isEmpty() ? null : this.bag.getTagCompound();
		if (tag == this.lastPushedTag) return false;
		this.lastPushedTag = tag;
		return true;
	}

	// ------------------------------------------------------------------
	// IItemHandler
	// ------------------------------------------------------------------

	@Override
	public int getSlots() {
		return load().getSlots();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		if (slot < 0) return ItemStack.EMPTY;
		AddableItemStackHandler handler = load();
		if (slot >= handler.getSlots()) return ItemStack.EMPTY;
		return handler.getStackInSlot(slot);
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		AddableItemStackHandler handler = load();
		ItemStack left = handler.insertItem(slot, stack, simulate);
		// 只有真的放进去了才写回（被拒绝时别白写一次标签、白发一次同步包）
		if (!simulate && left.getCount() != stack.getCount()) save(handler);
		return left;
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount <= 0) return ItemStack.EMPTY;
		AddableItemStackHandler handler = load();
		if (slot < 0 || slot >= handler.getSlots()) return ItemStack.EMPTY;
		ItemStack out = handler.extractItem(slot, amount, simulate);
		if (!simulate && !out.isEmpty()) save(handler);
		return out;
	}

	@Override
	public int getSlotLimit(int slot) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return stack.getItem() instanceof ItemProudSoul;
	}

	// ------------------------------------------------------------------
	// 耀魂专用操作
	// ------------------------------------------------------------------

	/** 内容快照：每种耀魂一条，数量为真实数量 */
	@Override
	public List<ItemStack> getEntries() {
		AddableItemStackHandler handler = load();
		List<ItemStack> out = new ArrayList<ItemStack>(handler.getSlots());
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack stored = handler.getStackInSlot(i);
			if (!stored.isEmpty()) out.add(stored);
		}
		return out;
	}

	@Override
	public boolean canStore(ItemStack stack) {
		return isProudSoul(stack);
	}

	@Override
	public int getStoredCount(ItemStack stack) {
		if (stack == null || stack.isEmpty()) return 0;
		ItemStack key = stack.copy();
		fixProudSoul(key);

		AddableItemStackHandler handler = load();
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack stored = handler.getStackInSlot(i);
			if (ItemHandlerHelper.canItemStacksStack(key, stored)) return stored.getCount();
		}
		return 0;
	}

	/**
	 * 存入耀魂。
	 * <p>
	 * <b>要么全装进去、要么一个都不装</b>：先拿一份工作副本模拟一遍，确认这一批能整个装下
	 * 才真正写入。这样 {@link IProudSoulStorage#storeItem} 的返回值就永远只有两个含义
	 * （0 = 全部装好，否则 = 完全没有装、返回剩余量），调用方不会因为"装了一半"而把
	 * 物品既留在掉落物里又留在袋子里（复制），或者直接丢掉。
	 *
	 * @return 剩余数量（0 表示全部装进去了）
	 */
	@Override
	public int storeItem(ItemStack stack, int amount) {
		if (stack == null || stack.isEmpty()) return 0;
		// 不能存的东西：返回它的全部数量，表示"一点都没装"，调用方不会误以为存好了
		if (!canStore(stack)) return stack.getCount();

		int num = Math.min(stack.getCount(), amount);
		if (num <= 0) return stack.getCount();

		ItemStack toStore = stack.copy();
		fixProudSoul(toStore);
		toStore.setCount(num);

		// 先模拟：确认能全部装下
		AddableItemStackHandler probe = load();
		ItemStack left = toStore;
		for (int i = 0; i < probe.getSlots() && !left.isEmpty(); i++) {
			left = probe.insertItem(i, left, true);
		}
		if (!left.isEmpty()) return num;    // 装不下：一个也不装

		// 真正写入
		AddableItemStackHandler handler = load();
		left = toStore;
		for (int i = 0; i < handler.getSlots() && !left.isEmpty(); i++) {
			left = handler.insertItem(i, left, false);
		}
		if (!left.isEmpty()) return num;    // 理论上到不了这里；保守处理：不写回
		save(handler);
		return 0;
	}

	/**
	 * 取出耀魂（取标识相同的那一条）。
	 *
	 * @return 真正取到的数量
	 */
	@Override
	public int takeItem(ItemStack stack, int amount) {
		if (stack == null || stack.isEmpty() || amount <= 0 || !canStore(stack)) return 0;

		ItemStack key = stack.copy();
		fixProudSoul(key);

		AddableItemStackHandler handler = load();
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack stored = handler.getStackInSlot(i);
			if (stored.isEmpty()) continue;
			if (!ItemHandlerHelper.canItemStacksStack(key, stored)) continue;

			int taken = handler.extractItem(i, amount, false).getCount();
			if (taken > 0) save(handler);
			return taken;
		}
		return 0;
	}

	// ------------------------------------------------------------------
	// 耀魂判定
	// ------------------------------------------------------------------

	/**
	 * 清掉历史遗留的空 {@code SB.SEffect}（与原实现一致）。
	 * <p>
	 * 额外把"整个空标签"也去掉：SlashBlade 的 {@code ItemSlashBlade.getItemTagCompound} 会
	 * 给没有标签的物品塞一个空标签（tooltip 路径就会触发），而
	 * {@code ItemHandlerHelper.canItemStacksStack} 要求两侧的"有没有标签"一致，
	 * 所以"空标签"和"没有标签"必须当成同一件事，否则悬停一下就会让袋子里的东西匹配不上。
	 */
	private static ItemStack fixProudSoul(ItemStack stack) {
		if (!stack.hasTagCompound()) return stack;

		NBTTagCompound tag = stack.getTagCompound();
		if (tag.hasKey("SB.SEffect") && tag.getCompoundTag("SB.SEffect").hasNoTags()) {
			tag.removeTag("SB.SEffect");
		}
		if (tag.hasNoTags()) stack.setTagCompound(null);
		return stack;
	}

	/**
	 * 判断是不是能装进袋子的耀魂。
	 * <p>
	 * 这是静态版本：不依赖任何一份存储，因此客户端也能用同样的条件判断，
	 * 从而让两端的点击结果保持一致。同样会顺手清掉空的 SB.SEffect
	 * （与原来 canStore 的行为一致，也让两端比较条件一致）。
	 */
	public static boolean isProudSoul(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty()) return false;
		fixProudSoul(stack);
		return stack.getItem() instanceof ItemProudSoul;
	}
}
