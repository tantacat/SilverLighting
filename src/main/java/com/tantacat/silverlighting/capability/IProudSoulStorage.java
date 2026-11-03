package com.tantacat.silverlighting.capability;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

/**
 * 耀魂袋的耀魂存储（对外接口）。
 *
 * <p>存储本身是 Forge 标准能力
 * {@link net.minecraftforge.items.CapabilityItemHandler#ITEM_HANDLER_CAPABILITY} 的实例，
 * 因此别的 mod 只要用标准能力就能读写它：
 *
 * <pre>
 * IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
 * </pre>
 *
 * 本接口只在 {@link IItemHandler} 之上补充耀魂专用的操作；它本身<b>不是能力</b>。
 *
 * <p>实现是无状态的（数据在物品自带 NBT 里），所以外部长期持有这个 handler 也不会读到旧数据。
 */
public interface IProudSoulStorage extends IItemHandler {

	/** 这个物品堆能不能装进袋子（只认耀魂） */
	boolean canStore(ItemStack stack);

	/**
	 * 存入耀魂。
	 *
	 * @return 剩余数量（0 表示全部装进去了）
	 */
	int storeItem(ItemStack stack, int amount);

	/**
	 * 取出耀魂（按物品标识匹配）。
	 *
	 * @return 真正取到的数量
	 */
	int takeItem(ItemStack stack, int amount);

	/** 袋子里某种耀魂的数量（没有则 0） */
	int getStoredCount(ItemStack stack);

	/** 当前内容快照：每种耀魂一条，数量为真实数量 */
	List<ItemStack> getEntries();
}
