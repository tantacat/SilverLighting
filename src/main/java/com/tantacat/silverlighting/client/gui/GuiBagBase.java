package com.tantacat.silverlighting.client.gui;

import com.google.common.collect.Lists;
import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.capability.ProudSoulBagData;
import com.tantacat.silverlighting.common.Item.ItemProudSoulBag;
import com.tantacat.silverlighting.network.PacketBagAction;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 耀魂袋界面的公共基类：把"袋子里的东西"画成一格格<b>虚拟格子</b>（不是原版槽位）。
 *
 * <p>为什么用虚拟格子而不是容器槽位：
 * <ul>
 * <li>袋子内容就在物品标签里，客户端的袋子上<b>本来就有完整内容</b>
 *     （原版 {@code PacketBuffer.writeItemStack} 发的是 {@code getNBTShareTag}），
 *     所以展示、搜索、翻页都能本地即时完成，不需要任何"内容同步包"，也不会和真实数据错位；</li>
 * <li>格子不再是槽位，就不会被原版的双击收集 / 拖拽 / 数字键交换绕过存储改到实物；</li>
 * <li>布局可以随意变化（筛选、翻页、数量变化）而<b>点击永远按物品身份生效</b>
 *     （见 {@link PacketBagAction}）。</li>
 * </ul>
 *
 * <p>鼠标事件：在 GUI 内、但不在任何槽位上的点击，原版 {@code Container.slotClick} 会以
 * {@code slotId = -1} 直接返回（{@code Container.java:278-281}），既不丢物品也不发包；
 * 只有点到 GUI 外面才会以 {@code -999} 丢东西。所以虚拟格子是安全的，
 * 我们只是在 {@link #mouseClicked} 里先拦下落在格子上的点击。
 *
 * <p>服务端权威 + 客户端预测：点击时客户端按同样的规则先动自己鼠标上的那一叠
 * （所以光标立刻就有东西，不用等往返），服务端处理完会用
 * {@code SPacketSetSlot(-1,-1,光标)} 覆盖成真实结果。
 */
@SideOnly(Side.CLIENT)
public abstract class GuiBagBase extends GuiContainer {

	/** 一个展示格 */
	protected static final class Cell {
		/** 画什么 / 点击时用它的物品身份（数量不参与） */
		public final ItemStack stack;
		/** 真实数量（含本地乐观调整） */
		public final int count;
		/** 灰色占位：袋子里没有这种耀魂时画个图标 */
		public final boolean ghost;

		public Cell(ItemStack stack, int count, boolean ghost) {
			this.stack = stack == null ? ItemStack.EMPTY : stack;
			this.count = count;
			this.ghost = ghost;
		}
	}

	/** 本地乐观调整：点击后立刻把数量减下去，等服务端的更新到了就清掉 */
	private static final class Pending {
		ItemStack key;
		int amount;
		long tick;
	}

	/**
	 * 六种基础耀魂模板。
	 * <p>
	 * 主界面固定展示它们；特殊页要把它们排除掉（"特殊"= 既不附魔也不是这六种）。
	 */
	protected static List<ItemStack> createNormalSouls() {
		List<ItemStack> souls = new ArrayList<ItemStack>(6);
		souls.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));
		souls.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1));
		souls.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1));
		souls.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1));
		souls.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.CrystalBladeSoulStr, 1));
		souls.add(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1));
		return souls;
	}

	/** 乐观调整最多保留多久（tick），服务端没回应就自动放弃，避免界面一直显示错的数字 */
	private static final long PENDING_TIMEOUT = 100L;

	protected final ContainerBagBase container;
	protected final BagPage page;

	/** 当前这一帧画出来的格子：点击也用这一份，保证"点到的就是你看到的" */
	private final List<Cell> cells = new ArrayList<Cell>();
	/** 袋子上一次被同步过来的标签（换个对象就说明内容更新了） */
	private NBTTagCompound lastTag;
	private boolean entriesLoaded = false;
	private List<ItemStack> entries = Collections.emptyList();
	private final List<Pending> pending = new ArrayList<Pending>();

	protected GuiBagBase(ContainerBagBase container, BagPage page, int xSize, int ySize) {
		super(container);
		this.container = container;
		this.page = page;
		this.xSize = xSize;
		this.ySize = ySize;
	}

	// ------------------------------------------------------------------
	// 子类实现：格子几何 + 内容
	// ------------------------------------------------------------------

	/** 格子区域左上角（相对 GUI） */
	protected abstract int getGridX();

	protected abstract int getGridY();

	/** 每行几格 */
	protected abstract int getColumns();

	/** 共几行 */
	protected abstract int getRows();

	/** 依据当前内容算出每格画什么（返回长度可以小于格子总数，缺的当空格） */
	protected abstract List<Cell> buildCells();

	/** 画在悬浮提示<b>之下</b>的界面元素（滚动条等，绝对坐标） */
	protected void drawPageOverlay(int mouseX, int mouseY) {
	}

	/** 画在悬浮提示<b>之上</b>的界面元素（例如界面外的 Shift 说明） */
	protected void drawPageTooltip(int mouseX, int mouseY) {
	}

	// ------------------------------------------------------------------
	// 数据
	// ------------------------------------------------------------------

	/** 玩家手上那个袋子 */
	protected ItemStack getBagStack() {
		EntityPlayer player = this.mc == null ? null : this.mc.player;
		if (player == null) return ItemStack.EMPTY;

		ItemStack main = player.getHeldItemMainhand();
		if (main.getItem() instanceof ItemProudSoulBag) return main;
		ItemStack off = player.getHeldItemOffhand();
		if (off.getItem() instanceof ItemProudSoulBag) return off;
		return ItemStack.EMPTY;
	}

	/**
	 * 袋子内容（客户端本地解析，按标签对象身份缓存）。
	 * <p>
	 * 服务端每次改动物品标签都会换一个新的 compound 对象，所以"对象变了"就等于"内容更新了"，
	 * 于是每帧都不用重新解析 NBT。
	 */
	protected List<ItemStack> getEntries() {
		ItemStack bag = getBagStack();
		NBTTagCompound tag = bag.isEmpty() ? null : bag.getTagCompound();

		if (!entriesLoaded || tag != lastTag) {
			entriesLoaded = true;
			lastTag = tag;
			entries = ProudSoulBagData.readEntries(bag);
			pending.clear();
		} else {
			clearExpiredPending();
		}
		return entries;
	}

	/** 数量的真实值再减去还没被服务端确认的乐观调整 */
	protected int adjustCount(ItemStack entry, int storedCount) {
		int adjust = 0;
		for (int i = 0; i < pending.size(); i++) {
			Pending p = pending.get(i);
			if (ItemHandlerHelper.canItemStacksStack(p.key, entry)) adjust += p.amount;
		}
		return Math.max(0, storedCount - adjust);
	}

	private void addPending(ItemStack key, int amount) {
		if (amount <= 0) return;
		Pending p = new Pending();
		p.key = key.copy();
		p.key.setCount(1);
		p.amount = amount;
		p.tick = this.mc == null || this.mc.player == null ? 0L : this.mc.player.ticksExisted;
		pending.add(p);
	}

	private void clearExpiredPending() {
		if (pending.isEmpty()) return;
		long now = this.mc == null || this.mc.player == null ? 0L : this.mc.player.ticksExisted;
		for (int i = pending.size() - 1; i >= 0; i--) {
			if (now - pending.get(i).tick > PENDING_TIMEOUT) pending.remove(i);
		}
	}

	// ------------------------------------------------------------------
	// 绘制
	// ------------------------------------------------------------------

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// 每帧先算好格子：绘制和点击用同一份，避免"看到的是这一帧、点到的是下一帧"
		rebuildCells();

		this.drawDefaultBackground();
		// 占位图标必须画在界面贴图<b>之前</b>：贴图盖上去会把它们压暗，这就是"袋子里没有这种耀魂"
		// 的视觉提示。（不能靠 GlStateManager.color 调暗：renderItemIntoGUI 内部会把颜色重置成
		// 白色，见 RenderItem.renderItemModelIntoGUI。）
		drawGhostCells();

		super.drawScreen(mouseX, mouseY, partialTicks);

		// 界面元素（滚动条等）画在悬浮提示<b>之下</b>
		drawPageOverlay(mouseX, mouseY);

		// 原版槽位（玩家背包）的悬浮提示要自己调（GuiContainer 不会帮我们调）
		this.renderHoveredToolTip(mouseX, mouseY);
		// 虚拟格子的悬浮提示
		this.renderCellTooltip(mouseX, mouseY);
		// 子类画在悬浮提示<b>之上</b>的东西（例如界面外的 Shift 说明）
		drawPageTooltip(mouseX, mouseY);
	}

	private void rebuildCells() {
		List<Cell> built = buildCells();
		this.cells.clear();
		if (built != null) this.cells.addAll(built);
	}

	/** 当前格子里第 index 格（越界返回 null） */
	protected Cell getCell(int index) {
		if (index < 0 || index >= this.cells.size()) return null;
		return this.cells.get(index);
	}

	/**
	 * 画"袋子里没有这种耀魂"的灰色占位图标。
	 * <p>
	 * 与原实现一致：画在界面贴图之前，靠贴图把它压暗（所以这里用绝对坐标；
	 * 此时还没有进入 GuiContainer 的 guiLeft/guiTop 平移）。
	 */
	private void drawGhostCells() {
		int columns = getColumns();
		int rows = getRows();
		if (columns <= 0 || rows <= 0) return;

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		for (int i = 0; i < columns * rows; i++) {
			Cell cell = getCell(i);
			if (cell == null || !cell.ghost || cell.stack.isEmpty()) continue;

			int x = this.guiLeft + getGridX() + (i % columns) * 18;
			int y = this.guiTop + getGridY() + (i / columns) * 18;
			this.itemRender.renderItemIntoGUI(cell.stack, x, y);
		}

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	/**
	 * 画真正有内容的格子。
	 * <p>
	 * 放在 {@code drawGuiContainerForegroundLayer} 里是有讲究的：
	 * 原版 {@code GuiContainer.drawScreen} 的顺序是「贴图 -> 槽位物品 -> 前景层 -> 鼠标上的物品」，
	 * 所以放在这里既在贴图之上（满亮度），又永远<b>在鼠标上那一叠之下</b>。
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawCells(mouseX, mouseY);
	}

	private void drawCells(int mouseX, int mouseY) {
		int columns = getColumns();
		int rows = getRows();
		if (columns <= 0 || rows <= 0) return;

		RenderHelper.enableGUIStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableDepth();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		for (int i = 0; i < columns * rows; i++) {
			Cell cell = getCell(i);
			// 占位图标已经在贴图之前画过了，这里只画真正有内容的格子
			if (cell == null || cell.stack.isEmpty() || cell.ghost) continue;

			int x = getGridX() + (i % columns) * 18;
			int y = getGridY() + (i / columns) * 18;

			this.itemRender.renderItemAndEffectIntoGUI(cell.stack, x, y);
			// 数量自己画：原版槽位同步把数量写成 byte，超过 127 会溢出（这里没有这个限制）
			if (cell.count > 1) {
				this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, cell.stack, x, y,
						String.valueOf(cell.count));
			}
		}

		// 与选中效果（和原版槽位一模一样的高亮）：盖在物品之上，通过 colorMask 只做叠加
		int hovered = getCellIndexAt(mouseX, mouseY);
		if (hovered >= 0 && getCell(hovered) != null) {
			int x = getGridX() + (hovered % columns) * 18;
			int y = getGridY() + (hovered / columns) * 18;
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.colorMask(true, true, true, false);
			this.drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
		}

		GlStateManager.disableDepth();
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
	}

	/** 鼠标所在的格子下标（不在格子上返回 -1） */
	protected int getCellIndexAt(int mouseX, int mouseY) {
		int columns = getColumns();
		int rows = getRows();
		for (int i = 0; i < columns * rows; i++) {
			int x = getGridX() + (i % columns) * 18;
			int y = getGridY() + (i / columns) * 18;
			if (isPointInRegion(x, y, 16, 16, mouseX, mouseY)) return i;
		}
		return -1;
	}

	private void renderCellTooltip(int mouseX, int mouseY) {
		if (this.mc == null || this.mc.player == null) return;
		// 与 renderHoveredToolTip 保持一致：鼠标上拿着东西时不显示提示
		if (!this.mc.player.inventory.getItemStack().isEmpty()) return;

		int index = getCellIndexAt(mouseX, mouseY);
		if (index < 0) return;

		Cell cell = getCell(index);
		if (cell == null || cell.stack.isEmpty() || cell.ghost) return;   // 占位/空格子没有提示
		// 必须用副本：物品的 tooltip 可能会就地改物品自己的标签
		// （例如 SlashBlade 的 ItemSlashBlade.getItemTagCompound 会给没有标签的物品塞一个空标签），
		// 那会破坏"无标签 vs 空标签"的匹配，导致格子里的东西消失。
		this.renderToolTip(cell.stack.copy(), mouseX, mouseY);
	}

	/** 鼠标在界面外且按住 Shift 时显示一段说明（与原实现一致） */
	protected void drawInfoWhenOutside(int mouseX, int mouseY, String langKey) {
		boolean inside = mouseX > this.guiLeft && mouseX < this.guiLeft + this.xSize
				&& mouseY > this.guiTop && mouseY < this.guiTop + this.ySize;
		if (inside || !isShiftKeyDown()) return;
		this.drawHoveringText(Lists.newArrayList(I18n.translateToLocal(langKey).split(" ")), mouseX, mouseY);
	}

	// ------------------------------------------------------------------
	// 点击
	// ------------------------------------------------------------------

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (handleCellClick(mouseX, mouseY, mouseButton)) return;
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * 处理落在虚拟格子上的点击。
	 *
	 * @return true 表示这次点击属于格子（已处理，不再交给原版）
	 */
	protected boolean handleCellClick(int mouseX, int mouseY, int mouseButton) {
		int index = getCellIndexAt(mouseX, mouseY);
		if (index < 0) return false;

		Cell cell = getCell(index);
		// 空格子 / 占位格：吃掉这次点击即可（原版在这个位置本来也什么都不做）
		if (cell == null || cell.stack.isEmpty() || cell.ghost || cell.count <= 0) return true;
		if (mouseButton < ContainerBagBase.BUTTON_LEFT || mouseButton > ContainerBagBase.BUTTON_MIDDLE) return true;

		boolean shift = isShiftKeyDown();
		// Shift + 中键不做事（原版 QUICK_MOVE 只对应左右键）
		if (shift && mouseButton == ContainerBagBase.BUTTON_MIDDLE) return true;

		sendBagAction(cell, mouseButton, shift);
		return true;
	}

	/** 发一条点击指令，并做一次本地预测 */
	protected void sendBagAction(Cell cell, int button, boolean shift) {
		ItemStack key = cell.stack.copy();
		key.setCount(1);        // 数量恒为 1：原版物品序列化把数量写成 byte

		predict(cell, key, button, shift);
		ModMain.network.sendToServer(new PacketBagAction(key, button, shift));
	}

	/**
	 * 本地预测：规则与服务端 {@code ContainerBagBase#handleBagAction} 完全一致。
	 * <p>
	 * 预测只影响"鼠标上那一叠"（这正是原版 {@code Container#slotClick} 返回值的用途），
	 * 服务端的真实结果会随后覆盖它，所以即使预测错了也不会多出或吞掉物品。
	 */
	protected void predict(Cell cell, ItemStack key, int button, boolean shift) {
		EntityPlayer player = this.mc == null ? null : this.mc.player;
		if (player == null) return;
		// Shift 的搬运量取决于背包剩余空间，本地不预测（服务端会直接把结果发下来）
		if (shift) return;

		ItemStack hand = player.inventory.getItemStack();
		int got;

		if (!hand.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(hand, key)) return;
			if (hand.getCount() >= hand.getMaxStackSize()) return;
			got = Math.min(1, cell.count);
			if (got <= 0) return;
			hand.grow(got);
		} else {
			got = Math.min(ContainerBagBase.amountForButton(button), cell.count);
			if (got <= 0) return;
			ItemStack take = key.copy();
			take.setCount(got);
			player.inventory.setItemStack(take);
		}
		addPending(key, got);
	}
}
