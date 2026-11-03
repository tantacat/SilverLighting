package com.tantacat.silverlighting.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 耀魂袋内容在<b>物品自身 NBT</b> 里的读写与旧格式迁移。
 *
 * <p>为什么内容要写在物品自己的标签里（而不是能力 / ForgeCaps 里）：
 * <ul>
 * <li>原版从服务端发往客户端的物品（{@code PacketBuffer.writeItemStack}）只带
 *     {@code Item#getNBTShareTag}，也就是物品标签；</li>
 * <li>客户端发往服务端的物品（创造模式整理背包、其它模组同步物品）走
 *     {@code PacketUtil.writeItemStackFromClientToServer}，<b>只写物品标签</b>，
 *     完全不带 ForgeCaps。</li>
 * </ul>
 * 所以只有把内容放在物品标签里，这两条通道才都是完整的：客户端能画界面，
 * 数据往返也不会丢。这也正是原版潜影盒（{@code BlockEntityTag}）的做法。
 *
 * <p>数据布局（挂在物品自带标签下的一个键）：
 * <pre>
 * ProudSoulBag: { Size: 3, Items: [ {Slot:0, id:"...", Count:128, Damage:0, tag:{...}}, ... ] }
 * </pre>
 * 数量用 int（{@value #COUNT_KEY}）而不是 {@code ItemStack} 自带的 byte，
 * 因为一叠耀魂轻易超过 127（原版 {@code ItemStack.writeToNBT} 把 Count 写成 byte）。
 *
 * <p><b>空袋子没有任何标签</b>：内容为空时这个键会被摘掉，标签空了整个标签也会被清掉，
 * 于是空袋子和"刚做出来的袋子"完全一样（也就不会出现 F3+H 的 NBT 提示行）。
 *
 * <p>本类是无状态的：每次都直接读物品标签，不做缓存，因此不存在"缓存的能力数据
 * 和物品标签谁更新"的问题。
 */
public final class ProudSoulBagData {

	/** 袋子内容在物品自带标签下的键名 */
	public static final String KEY = "ProudSoulBag";
	/** 旧格式（上一版为兼容创造模式写的“信封”），迁移时升级为 {@link #KEY} */
	public static final String LEGACY_ENVELOPE_KEY = "SLBagContents";
	/** 旧格式：ForgeCaps 里能力数据所在的父节点名 */
	public static final String LEGACY_CAPS_PARENT = "Parent";
	/** 内容列表、大小、数量的键名（与 {@link AddableItemStackHandler} 的格式一致） */
	public static final String LIST_KEY = "Items";
	public static final String SIZE_KEY = "Size";
	public static final String COUNT_KEY = "Count";

	private ProudSoulBagData() {
	}

	/**
	 * 只读地取袋子内容，<b>不会修改物品标签</b>。
	 * <p>
	 * 依次找：新键 -> 旧信封键。客户端可能拿到还没被服务端迁移过的旧存档物品，
	 * 因此这里也认旧键（但绝不改写它，客户端不该改物品数据）。
	 * <p>
	 * <b>返回的是一份副本</b>：1.12.2 的 {@code NBTTagCompound.getCompoundTag} 返回的是
	 * 标签里的活对象，如果直接把活对象交给工作副本，读取路径里的一次规范化（清理空的
	 * SB.SEffect）就会原地改到袋子自己的 NBT，或者让外部 mod 拿到的物品堆和袋子共享同一个
	 * 标签对象——两种都会绕过"读改写 + 同步"的正规路径。
	 */
	@Nullable
	public static NBTTagCompound read(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty()) return null;
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) return null;
		if (tag.hasKey(KEY, Constants.NBT.TAG_COMPOUND)) return tag.getCompoundTag(KEY).copy();
		if (tag.hasKey(LEGACY_ENVELOPE_KEY, Constants.NBT.TAG_COMPOUND))
			return tag.getCompoundTag(LEGACY_ENVELOPE_KEY).copy();
		return null;
	}

	/**
	 * 把袋子内容写进物品标签。
	 * <p>
	 * 内容为空时摘掉键（标签空了就把整个标签清空），于是<b>空袋子没有任何 NBT</b>。
	 */
	public static void set(@Nullable ItemStack stack, @Nullable NBTTagCompound data) {
		if (stack == null || stack.isEmpty()) return;

		NBTTagCompound tag = stack.getTagCompound();
		if (data == null || !hasEntries(data)) {
			if (tag == null) return;
			NBTTagCompound out = tag.copy();
			out.removeTag(KEY);
			stack.setTagCompound(out.hasNoTags() ? null : out);
			return;
		}

		NBTTagCompound out = tag == null ? new NBTTagCompound() : tag.copy();
		out.setTag(KEY, data);
		stack.setTagCompound(out);
	}

	/**
	 * 旧存档迁移（原地修改物品标签，幂等）。
	 * <p>
	 * 优先级：新键 &gt; 旧信封（{@link #LEGACY_ENVELOPE_KEY}）&gt; 旧能力数据
	 * （{@code Item#initCapabilities} 传进来的 {@code ForgeCaps} 里的 {@code Parent}）。
	 * 迁移后旧信封键会被摘掉；旧能力数据本来就在物品标签之外，Forge 在能力不再可序列化后
	 * 也不会再写它，所以下次存档就自然消失了。
	 *
	 * @param oldCapNbt {@code Item#initCapabilities(stack, nbt)} 收到的旧能力数据，可为 null
	 */
	public static void migrate(@Nullable ItemStack stack, @Nullable NBTTagCompound oldCapNbt) {
		if (stack == null || stack.isEmpty()) return;

		NBTTagCompound tag = stack.getTagCompound();
		boolean hasNew = tag != null && tag.hasKey(KEY, Constants.NBT.TAG_COMPOUND);

		NBTTagCompound legacy = null;
		if (tag != null && tag.hasKey(LEGACY_ENVELOPE_KEY, Constants.NBT.TAG_COMPOUND)) {
			legacy = tag.getCompoundTag(LEGACY_ENVELOPE_KEY).copy();
			NBTTagCompound out = tag.copy();
			out.removeTag(LEGACY_ENVELOPE_KEY);
			stack.setTagCompound(out.hasNoTags() ? null : out);
		}

		if (hasNew) return;

		if (legacy == null && oldCapNbt != null && oldCapNbt.hasKey(LEGACY_CAPS_PARENT, Constants.NBT.TAG_COMPOUND)) {
			legacy = oldCapNbt.getCompoundTag(LEGACY_CAPS_PARENT).copy();
		}
		if (legacy != null) set(stack, legacy);
	}

	/** 当前内容快照：每种耀魂一条（数量为真实数量），顺序就是存储槽顺序 */
	public static List<ItemStack> readEntries(@Nullable ItemStack stack) {
		NBTTagCompound data = read(stack);
		if (data == null) return Collections.emptyList();

		NBTTagList list = data.getTagList(LIST_KEY, Constants.NBT.TAG_COMPOUND);
		List<ItemStack> out = new ArrayList<ItemStack>(list.tagCount());
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound itemTag = list.getCompoundTagAt(i);
			// 注意顺序：数量是 int 存的，而 ItemStack 读 NBT 时把 Count 当 byte 读
			// （读不到就是 0，物品会先被当成空），所以必须先把数量设回去，再判断空不空。
			ItemStack item = new ItemStack(itemTag);
			item.setCount(itemTag.getInteger(COUNT_KEY));
			if (item.isEmpty() || item.getCount() <= 0) continue;
			out.add(item);
		}
		return out;
	}

	/** 内容里是否有真实的物品（没有就等同于空袋子） */
	public static boolean hasEntries(@Nullable NBTTagCompound data) {
		if (data == null) return false;
		NBTTagList list = data.getTagList(LIST_KEY, Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			if (list.getCompoundTagAt(i).getInteger(COUNT_KEY) > 0) return true;
		}
		return false;
	}
}
