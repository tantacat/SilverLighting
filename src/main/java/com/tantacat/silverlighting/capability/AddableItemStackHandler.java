package com.tantacat.silverlighting.capability;

import mods.flammpfeil.slashblade.item.ItemProudSoul;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * 耀魂袋内容的编解码与内存工作副本。
 *
 * <p>这个类本身<b>不持有袋子数据</b>：它只被 {@link ProudSoulStorage} 当成一次操作的
 * 临时工作副本（载入 -> 改 -> 写回物品标签），所以没有缓存过期问题。
 *
 * <p>相对原版 {@link ItemStackHandler} 的几点改动：
 * <ul>
 * <li>单条数量上限是 {@link Integer#MAX_VALUE}（一叠耀魂可以非常多）；</li>
 * <li>记录条数按需自动增长（每种物品一条，标识相同会被 {@link ItemStackHandler} 合并）；</li>
 * <li><b>袋子的存储现在挂在标准 {@code ITEM_HANDLER_CAPABILITY} 上</b>，而原版
 *     {@code ItemStackHandler.insertItem} 根本不会调用 {@code isItemValid}，
 *     所以这里自己挡一道：不是耀魂的一律拒绝，免得别的 mod 往袋子里塞任意物品
 *     （那些东西在界面上永远取不出来，还会写出嵌套 NBT）；</li>
 * <li>槽位下标有上限，物品 NBT 里的 {@code Size} 也有上限：避免坏数据 / 恶意数据让服务端疯狂扩容。</li>
 * </ul>
 *
 * <p>序列化格式与原实现完全一致（{@code Size} + {@code Items}，数量用 int），
 * 所以旧存档里的袋子内容可以直接读出来。
 */
public class AddableItemStackHandler extends ItemStackHandler {

	/** 槽位数量上限（防止坏 NBT / 恶意数据导致的内存爆炸） */
	public static final int MAX_SLOTS = 4096;

	/**
	 * 载入时<b>认不出来</b>的条目（例如提供该物品的模组被临时移除，物品 id 注册不上）。
	 * 这些条目原样保留、保存时再写回去，绝不静默删除玩家的东西。
	 */
	private final List<NBTTagCompound> unloaded = new ArrayList<NBTTagCompound>();

	public AddableItemStackHandler() {
		this(1);
	}

	public AddableItemStackHandler(int size) {
		stacks = NonNullList.create();
		for (int i = 0; i < size; i++) {
			stacks.add(ItemStack.EMPTY);
		}
	}

	/**
	 * make NonNullList can add
	 */
	@Override
	public void setSize(int size) {
		stacks = NonNullList.create();
		int n = Math.max(0, Math.min(size, MAX_SLOTS));
		for (int i = 0; i < n; i++) {
			stacks.add(ItemStack.EMPTY);
		}
	}

	/** 载入时是否有认不出来的条目（一般说明提供该物品的模组不在了） */
	public boolean hasUnloadedEntries() {
		return !this.unloaded.isEmpty();
	}

	/**
	 * 插入时按需扩容，并且自己挡掉不该进袋子的东西：
	 * <ul>
	 * <li>不是耀魂 -> 原样退回（原版 {@code ItemStackHandler} 不检查 {@code isItemValid}）；</li>
	 * <li>槽位数为 0（例如存档里的 Size 被写成 0）也要能安全插入；</li>
	 * <li>允许最多"末尾再加一格"，更大的下标直接拒绝（否则外部一个巨大的 slot 就能把内存吃光）；</li>
	 * <li>最后一条已被占用时补一条空位，这样「每种耀魂一条」总能找到位置。</li>
	 * </ul>
	 */
	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		if (!isItemValid(slot, stack)) return stack;
		if (slot < 0 || slot > stacks.size()) return stack;

		if (stacks.isEmpty()) {
			stacks.add(ItemStack.EMPTY);
		}
		while (stacks.size() <= slot && stacks.size() < MAX_SLOTS) {
			stacks.add(ItemStack.EMPTY);
		}
		if (slot >= stacks.size()) return stack;

		if (!stacks.get(stacks.size() - 1).isEmpty() && stacks.size() < MAX_SLOTS) {
			stacks.add(ItemStack.EMPTY);
		}

		return super.insertItem(slot, stack, simulate);
	}

	/**
	 * change MaxCount
	 */
	@Override
	protected int getStackLimit(int slot, ItemStack stack) {
		return Integer.MAX_VALUE;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagList nbtTagList = new NBTTagList();
		for (int i = 0; i < stacks.size(); i++) {
			if (!stacks.get(i).isEmpty()) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				ItemStack item = stacks.get(i);
				item.writeToNBT(itemTag);
				// ItemStack.writeToNBT 把 Count 写成 byte，超过 127 会溢出，这里用 int 覆盖
				itemTag.setInteger("Count", item.getCount());
				// writeToNBT 会把物品自己的标签对象直接挂上去（共享引用）。工作副本里可能
				// 正好是调用方传进来的那个物品堆，如果共享引用，之后别人改那个物品堆的 NBT
				// 就会悄悄改到袋子的存档数据，所以这里深拷贝一份。
				if (item.hasTagCompound() && itemTag.hasKey("tag", Constants.NBT.TAG_COMPOUND)) {
					itemTag.setTag("tag", item.getTagCompound().copy());
				}
				nbtTagList.appendTag(itemTag);
			}
		}

		// 认不出来的条目原样写回（排到末尾，避免和正常条目抢槽位），绝不静默删除
		int next = stacks.size();
		for (int i = 0; i < unloaded.size() && next < MAX_SLOTS; i++) {
			NBTTagCompound raw = unloaded.get(i).copy();
			raw.setInteger("Slot", next++);
			nbtTagList.appendTag(raw);
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Items", nbtTagList);
		nbt.setInteger("Size", Math.max(stacks.size(), next));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);

		// Size 可能缺失或与实际槽位不符：以"实际用到的最大槽位"为准，绝不因为一个坏掉的
		// Size 就把 Slot>=1 的内容全丢掉（然后在下一次保存时把它们永久删除）。
		int maxSlot = -1;
		for (int i = 0; i < tagList.tagCount(); i++) {
			maxSlot = Math.max(maxSlot, tagList.getCompoundTagAt(i).getInteger("Slot"));
		}
		int size = nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : 0;
		size = Math.max(size, maxSlot + 1);
		if (size <= 0) size = 1;
		setSize(Math.min(size, MAX_SLOTS));

		this.unloaded.clear();
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
			int slot = itemTags.getInteger("Slot");

			// 注意顺序：数量是 int 存的，而 ItemStack 读 NBT 时把 Count 当 byte 读，
			// 大数量会先被当成"空物品"，所以必须先把数量设回去再判断空不空。
			ItemStack item = new ItemStack(itemTags);
			item.setCount(itemTags.getInteger("Count"));

			if (item.isEmpty() || item.getCount() <= 0) {
				// 认不出来（例如物品 id 没注册）或数量为 0：前者原样留着，避免永久删除
				if (itemTags.hasKey("id", Constants.NBT.TAG_STRING)) this.unloaded.add(itemTags.copy());
				continue;
			}
			if (slot < 0 || slot >= stacks.size()) {
				// 槽位越界（坏数据/恶意数据）：同样原样留着，不要静默删掉
				this.unloaded.add(itemTags.copy());
				continue;
			}
			stacks.set(slot, item);
		}
		onLoad();
	}

	/**
	 * just save ProudSoul
	 */
	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemProudSoul;
	}
}
