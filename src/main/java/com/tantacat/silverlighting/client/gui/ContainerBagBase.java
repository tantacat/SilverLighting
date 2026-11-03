package com.tantacat.silverlighting.client.gui;

import com.tantacat.silverlighting.capability.IProudSoulStorage;
import com.tantacat.silverlighting.capability.ProudSoulStorage;
import com.tantacat.silverlighting.capability.ProudSoulStorageProvider;
import com.tantacat.silverlighting.common.Item.ItemProudSoulBag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

/**
 * 耀魂袋界面的容器基类。
 *
 * <p><b>这一层的设计要点：容器里只有玩家背包的 36 个槽位，袋子里的东西一个槽位都不占。</b>
 * 袋子的展示格由界面在客户端"画"出来（虚拟格子，类似 JEI 的物品列表），
 * 点击时发一条只有"哪一种耀魂 + 哪个按钮"的指令包
 * （{@link com.tantacat.silverlighting.network.PacketBagAction}）。
 *
 * <p>为什么不再用"镜像槽位"：
 * <ul>
 * <li>镜像槽位既要服务端每 tick 重建、又要靠原版槽位同步下发，数据一变就容易和真实存储错位；</li>
 * <li>袋子内容本身就写在物品标签里（见
 *     {@link com.tantacat.silverlighting.capability.ProudSoulBagData}），
 *     客户端<b>本来就有完整内容</b>：原版会把袋子这一格（连同标签）同步过来。</li>
 * </ul>
 * 于是：
 * <ul>
 * <li>展示、搜索、翻页全是客户端本地计算，实时且任意布局都不会和数据错位；</li>
 * <li>点击指令用"物品身份"定位（袋子里每种耀魂只有一条记录，所以身份就是无歧义主键），
 *     不依赖下标，翻页/排序/内容变化都不会取错东西；</li>
 * <li>真正的读写只在服务端发生，客户端只做预测，服务端的结果是唯一权威。</li>
 * </ul>
 */
public abstract class ContainerBagBase extends Container {

	/** 玩家背包（27 主背包 + 9 快捷栏）在所有袋类容器里都排在最后 */
	public static final int PLAYER_INVENTORY_SLOTS = 36;

	protected final EntityPlayer player;
	protected final BagPage page;

	public ContainerBagBase(EntityPlayer player, BagPage page) {
		this.player = player;
		this.page = page;
		initPlayerSlots(page.playerSlotX);
	}

	private void initPlayerSlots(int x0) {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, x0 + x * 18, 84 + y * 18));
			}
		}
		for (int x = 0; x < 9; x++) {
			this.addSlotToContainer(new Slot(player.inventory, x, x0 + x * 18, 142));
		}
	}

	public BagPage getPage() {
		return this.page;
	}

	/** 是否为逻辑服务端 */
	protected boolean isServer() {
		return this.player != null && this.player.world != null && !this.player.world.isRemote;
	}

	/**
	 * 袋子里的耀魂存储（无状态视图，数据在物品标签里）。
	 * <p>
	 * 只在服务端使用：客户端虽然也能读出同一份内容，但绝不允许客户端写袋子，
	 * 所以所有写操作都走这里并且只在服务端执行。
	 */
	@Nullable
	protected IProudSoulStorage getStorage() {
		if (!isServer()) return null;
		return ProudSoulStorageProvider.getStorage(getBag());
	}

	/** 玩家手上那个袋子（主手优先，与旧实现一致） */
	public ItemStack getBag() {
		if (this.player == null) return ItemStack.EMPTY;
		ItemStack main = this.player.getHeldItemMainhand();
		if (main.getItem() instanceof ItemProudSoulBag) return main;
		ItemStack off = this.player.getHeldItemOffhand();
		if (off.getItem() instanceof ItemProudSoulBag) return off;
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getHeldItemMainhand().getItem() instanceof ItemProudSoulBag
				|| playerIn.getHeldItemOffhand().getItem() instanceof ItemProudSoulBag;
	}

	// ------------------------------------------------------------------
	// 客户端点击指令（服务端处理）
	// ------------------------------------------------------------------

	/** 空手左键取 1 个 */
	public static final int BUTTON_LEFT = 0;
	/** 空手右键取 32 个 */
	public static final int BUTTON_RIGHT = 1;
	/** 空手中键取 64 个 */
	public static final int BUTTON_MIDDLE = 2;

	/** 按鼠标按键决定取多少个（数量由服务端自己算，不信客户端报的数） */
	public static int amountForButton(int button) {
		if (button == BUTTON_RIGHT) return 32;
		if (button == BUTTON_MIDDLE) return 64;
		return 1;
	}

	/**
	 * 服务端：处理一次袋子格子的点击。
	 *
	 * <p>操作逻辑与原实现一致：Shift 搬进背包、空手左键 1 个 / 右键 32 个 / 中键 64 个、
	 * 手上已有同种耀魂时左键再抓一个。区别只在于服务端是唯一权威，
	 * 客户端的预测会被这里的真实结果覆盖。
	 *
	 * <p><b>无论处理成功还是被拒绝</b>，最后都会把权威状态（鼠标上的物品 + 袋子本身）
	 * 回给客户端：客户端点击时已经按同样的规则预测了光标，如果服务端拒绝却什么都不回，
	 * 客户端就会停在一个并不存在的"幻影光标/幻影数量"上。
	 *
	 * @param key   点击的是哪一种耀魂（数量恒为 1，仅用于按物品身份匹配）
	 * @param button 0=左键 1=右键 2=中键
	 * @param shift  是否按住 Shift（尽量搬进背包）
	 */
	public void handleBagAction(ItemStack key, int button, boolean shift) {
		if (!isServer() || key == null || key.isEmpty()) return;

		try {
			// 陈旧窗口 / 袋子已经不在手上 / 点击的不是耀魂：一律什么都不做（但下面照样回包）
			if (this.player.openContainer == this && canInteractWith(this.player)
					&& ProudSoulStorage.isProudSoul(key)) {
				IProudSoulStorage storage = getStorage();
				if (storage != null) applyBagAction(storage, key, button, shift);
			}
		} finally {
			afterBagChanged();
			// 光标上的物品以服务端为准（原版现成机制：windowId -1 即鼠标上的那一叠）
			if (this.player instanceof EntityPlayerMP) {
				((EntityPlayerMP) this.player).updateHeldItem();
			}
		}
	}

	/** 真正执行一次点击（只在服务端、且前置条件都满足时调用） */
	private void applyBagAction(IProudSoulStorage storage, ItemStack key, int button, boolean shift) {
		if (shift) {
			moveEntryToInventory(storage, key);
			return;
		}

		ItemStack hand = this.player.inventory.getItemStack();
		if (!hand.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(hand, key)) return;   // 手上是别的东西：什么都不做
			if (hand.getCount() < hand.getMaxStackSize()) {
				// 只有真的从袋子里取到了才增加手上的数量，绝不凭空多出来
				hand.grow(storage.takeItem(key, 1));
			}
		} else {
			int got = storage.takeItem(key, amountForButton(button));
			if (got > 0) {
				ItemStack take = key.copy();
				take.setCount(got);
				this.player.inventory.setItemStack(take);
			}
		}
	}

	/**
	 * Shift 点击：尽量把这一种耀魂搬进玩家背包，直到背包塞不下或这一种被取空。
	 * <p>
	 * 顺序很重要：<b>先从袋子里取，再往背包里放，放不下的原样退回袋子</b>，
	 * 所以无论背包空间如何都不可能凭空多出物品。
	 * <p>
	 * 另外会先"探一下"背包还有没有位置：完全没有的话一个都不取，
	 * 免得白白重写一次袋子标签（那会让条目顺序/界面位置发生变化）。
	 */
	private void moveEntryToInventory(IProudSoulStorage storage, ItemStack key) {
		int perMove = Math.max(1, Math.min(key.getMaxStackSize(), 64));

		for (int guard = 0; guard < PLAYER_INVENTORY_SLOTS; guard++) {
			ItemStack probe = key.copy();
			probe.setCount(perMove);
			if (!canAcceptIntoPlayerInventory(probe)) return;   // 背包塞不下：一个都不取

			int taken = storage.takeItem(key, perMove);
			if (taken <= 0) return;

			ItemStack move = key.copy();
			move.setCount(taken);
			// 只往玩家背包槽位里放（0..35 正好是 27 主背包 + 9 快捷栏）
			this.mergeItemStack(move, 0, PLAYER_INVENTORY_SLOTS, true);

			if (!move.isEmpty()) {
				// 背包塞不下了：把没放进去的退回袋子
				storage.storeItem(move, move.getCount());
				return;
			}
			if (taken < perMove) return;    // 这一种已经取空
		}
	}

	/** 玩家背包里还有没有任何一格能接受这个物品堆（与 {@link #mergeItemStack} 的判定一致） */
	private boolean canAcceptIntoPlayerInventory(ItemStack stack) {
		for (int i = 0; i < PLAYER_INVENTORY_SLOTS; i++) {
			Slot slot = this.getSlot(i);
			if (slot == null || !slot.isItemValid(stack)) continue;

			ItemStack current = slot.getStack();
			if (current.isEmpty()) return true;
			if (ItemHandlerHelper.canItemStacksStack(current, stack)
					&& current.getCount() < Math.min(slot.getSlotStackLimit(), current.getMaxStackSize())) {
				return true;
			}
		}
		return false;
	}

	/** 袋子内容变了：立刻把袋子本身同步给客户端（含副手的情况） */
	protected void afterBagChanged() {
		if (!isServer()) return;
		ProudSoulStorageProvider.syncBagToClient(this.player, getBag());
	}

	// ------------------------------------------------------------------
	// Shift 存入 / 关界面
	// ------------------------------------------------------------------

	/**
	 * Shift 点击玩家背包里的耀魂 -> 存进袋子。
	 * <p>
	 * 客户端不做预测：服务端会在同一 tick 内把清空后的槽位同步下来。
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		if (!isServer()) return ItemStack.EMPTY;
		if (index < 0 || index >= this.inventorySlots.size()) return ItemStack.EMPTY;

		Slot slot = this.getSlot(index);
		if (slot == null) return ItemStack.EMPTY;

		ItemStack item = slot.getStack();
		if (!ProudSoulStorage.isProudSoul(item)) return ItemStack.EMPTY;

		IProudSoulStorage storage = getStorage();
		if (storage == null) return ItemStack.EMPTY;

		int left = storage.storeItem(item, item.getCount());
		if (left <= 0) {
			slot.putStack(ItemStack.EMPTY);
		} else {
			item.setCount(left);
			slot.onSlotChanged();
		}
		afterBagChanged();
		return ItemStack.EMPTY;
	}

	/**
	 * 关界面时把鼠标上还拿着的耀魂还回去。
	 * <p>
	 * 依次尝试三条路，保证物品<b>不可能凭空消失</b>：
	 * <ol>
	 * <li>塞进玩家背包。这里刻意用容器的 {@link #mergeItemStack} 而不是
	 *     {@code InventoryPlayer#addItemStackToInventory}：后者在<b>创造模式</b>下、
	 *     东西塞不进背包时会直接把整个物品堆 {@code setCount(0)} 销毁
	 *     （见 {@code InventoryPlayer.add()} 里的
	 *     {@code getCount() == i && capabilities.isCreativeMode} 分支）。</li>
	 * <li>还剩的放回袋子（从哪儿来回哪儿去）。</li>
	 * <li>只有"袋子已经不在手上"或"鼠标上不是耀魂"这种真放不下的情况，才丢到地上。</li>
	 * </ol>
	 */
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		if (isServer()) {
			ItemStack carried = playerIn.inventory.getItemStack();
			if (!carried.isEmpty()) {
				playerIn.inventory.setItemStack(ItemStack.EMPTY);

				// 1) 玩家背包（0..35 = 27 主背包 + 9 快捷栏）
				this.mergeItemStack(carried, 0, PLAYER_INVENTORY_SLOTS, true);

				// 2) 放回袋子
				if (!carried.isEmpty()) {
					IProudSoulStorage storage = getStorage();
					if (storage != null && storage.canStore(carried)) {
						carried.setCount(storage.storeItem(carried, carried.getCount()));
					}
				}

				// 3) 真的都放不下才丢出来
				if (!carried.isEmpty()) {
					playerIn.dropItem(carried, false);
					carried.setCount(0);
				}

				// 袋子或背包槽位可能变了：立刻同步给客户端
				afterBagChanged();
			}
		} else if (!playerIn.inventory.getItemStack().isEmpty()) {
			// 客户端只清空，不生成“掉落物”实体：真正的归还由服务端完成
			playerIn.inventory.setItemStack(ItemStack.EMPTY);
		}
		super.onContainerClosed(playerIn);
	}
}
