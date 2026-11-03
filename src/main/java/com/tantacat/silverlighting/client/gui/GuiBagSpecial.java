package com.tantacat.silverlighting.client.gui;

import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.capability.ProudSoulStorage;
import com.tantacat.silverlighting.network.PacketGuiButtonPressed;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 附魔 / 特殊耀魂页：9 x 3 的虚拟格子 + 搜索框 + 右侧的位置指示器（滚动条）。
 *
 * <p>这一页要展示的条目可能比格子多，所以像创造模式物品栏那样分页：
 * 滚轮、点轨道、拖动滑块都能翻页，滑块的相对位置就是当前所在的页。
 *
 * <p>搜索、过滤、翻页全部在客户端本地完成：
 * <ul>
 * <li>内容就在玩家手上那个袋子的物品标签里（原版会把整份标签同步过来），
 *     所以不需要"请求内容 / 回传筛选结果"的网络包；</li>
 * <li>按物品名和 tooltip 匹配这件事本来也只有客户端做得到；</li>
 * <li>点击发的是"哪一种耀魂"（物品身份），与格子下标、排序、翻页都无关，
 *     所以边搜边点也不会取错东西。</li>
 * </ul>
 */
@SideOnly(Side.CLIENT)
public class GuiBagSpecial extends GuiBagBase {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ModMain.MODID,
			"textures/gui/bag/special.png");

	/** 格子布局（与原实现的槽位坐标完全一致） */
	private static final int COLUMNS = 9;
	private static final int ROWS = 3;
	private static final int GRID_X = 9;
	private static final int GRID_Y = 18;

	/**
	 * 右侧位置指示器（滚动条）。
	 * <p>
	 * 这组数字是逐像素量着 {@code textures/gui/bag/special.png} 里那条凹槽取的（hex 实测）：
	 * <pre>
	 *   y=17        凹槽上描边（#373737，横跨 x=174..186）
	 *   x=174       凹槽左描边（#373737）        x=186  凹槽右描边（#373737）
	 *   x=175..185  凹槽内部（#8b8b8b）→ 宽 11px  x=187  窗口边框高光（#ffffff，不能碰）
	 *   y=18..157   凹槽内部（高 140px）
	 *   y=158       凹槽下描边
	 * </pre>
	 * 所以：{@code BAR_W = 13} 是凹槽外宽（x=174..186，左右各含 1px 描边）；
	 * {@code BAR_H = 138} 而不是 140，是为了让滑块在凹槽内部走完全程——
	 * 顶部对齐 {@code BAR_Y=19}、最底部停在 y=157，两端都不会压到上下描边。
	 */
	private static final int BAR_X = 174;
	private static final int BAR_Y = 19;
	private static final int BAR_W = 13;
	private static final int BAR_H = 138;

	private GuiTextField searchField;

	/** 六种基础耀魂（特殊页要把它们排除掉） */
	private final List<ItemStack> normalSouls = createNormalSouls();

	/** 过滤后的可见条目（客户端本地计算，带缓存，避免每帧解析 tooltip） */
	private List<ItemStack> visible = Collections.emptyList();
	private List<ItemStack> visibleFrom = null;
	private String visibleSearch = null;

	/** 当前滚到的行 */
	private int row = 0;
	/** 是否正在拖动滑块 */
	private boolean draggingBar = false;
	private int dragOffset = 0;

	public GuiBagSpecial(ContainerBagSpecial container, BagPage page) {
		super(container, page, 195, 166);
	}

	// ------------------------------------------------------------------
	// 几何
	// ------------------------------------------------------------------

	@Override
	protected int getGridX() {
		return GRID_X;
	}

	@Override
	protected int getGridY() {
		return GRID_Y;
	}

	@Override
	protected int getColumns() {
		return COLUMNS;
	}

	@Override
	protected int getRows() {
		return ROWS;
	}

	// ------------------------------------------------------------------
	// 内容：过滤 + 翻页
	// ------------------------------------------------------------------

	/**
	 * 当前这一页要画的条目（已经按模式与搜索词过滤）。
	 * <p>
	 * 只在"袋子内容变了"或"搜索词变了"时重新计算，因此每帧调用也不会有开销。
	 */
	private List<ItemStack> getVisible() {
		List<ItemStack> entries = getEntries();
		String text = this.searchField == null ? "" : this.searchField.getText();

		if (entries != visibleFrom || !text.equals(visibleSearch)) {
			visibleFrom = entries;
			visibleSearch = text;
			visible = filter(entries, text);
			this.row = clampRow(this.row);
		}
		return visible;
	}

	private List<ItemStack> filter(List<ItemStack> entries, String text) {
		String needle = text == null ? "" : text.toLowerCase(Locale.ROOT).trim();
		List<ItemStack> out = new ArrayList<ItemStack>();

		for (int i = 0; i < entries.size(); i++) {
			ItemStack item = entries.get(i);
			if (item.isEmpty()) continue;
			if (!matchesMode(item)) continue;
			// 匹配时用副本：物品的 tooltip 可能会就地给物品塞一个空标签
			// （SlashBlade 的 getItemTagCompound），共享缓存条目会让匹配条件变掉
			if (!needle.isEmpty() && !containsText(item.copy(), needle)) continue;
			out.add(item);
		}
		return out;
	}

	/** 附魔页 = 有附魔的；特殊页 = 既没附魔也不是那六种基础耀魂 */
	private boolean matchesMode(ItemStack item) {
		if (this.page == BagPage.ENCHANTED) return item.isItemEnchanted();
		return !item.isItemEnchanted() && !isNormalSoul(item);
	}

	private boolean isNormalSoul(ItemStack item) {
		for (int i = 0; i < this.normalSouls.size(); i++) {
			ItemStack soul = this.normalSouls.get(i);
			if (soul == null || soul.isEmpty()) continue;
			// 和原来一样顺手清掉模板里空的 SB.SEffect，保证两侧比较条件一致
			ProudSoulStorage.isProudSoul(soul);
			if (ItemHandlerHelper.canItemStacksStack(soul, item)) return true;
		}
		return false;
	}

	/** 按物品名与 tooltip 匹配（tooltip 只有客户端拿得到） */
	private boolean containsText(ItemStack item, String needle) {
		if (item.getDisplayName().toLowerCase(Locale.ROOT).contains(needle)) return true;

		List<String> info = item.getTooltip(null, TooltipFlags.NORMAL);
		for (int i = 0; i < info.size(); i++) {
			if (info.get(i).toLowerCase(Locale.ROOT).contains(needle)) return true;
		}
		return false;
	}

	@Override
	protected List<Cell> buildCells() {
		List<ItemStack> list = getVisible();
		List<Cell> out = new ArrayList<Cell>(COLUMNS * ROWS);
		int start = this.row * COLUMNS;

		for (int i = 0; i < COLUMNS * ROWS; i++) {
			int index = start + i;
			if (index < 0 || index >= list.size()) {
				out.add(null);
				continue;
			}
			ItemStack entry = list.get(index);
			out.add(new Cell(entry, adjustCount(entry, entry.getCount()), false));
		}
		return out;
	}

	private int totalRows() {
		int size = getVisible().size();
		return (size + COLUMNS - 1) / COLUMNS;
	}

	private int maxRow() {
		return Math.max(0, totalRows() - ROWS);
	}

	private int clampRow(int value) {
		return Math.max(0, Math.min(maxRow(), value));
	}

	private void scrollBy(int delta) {
		this.row = clampRow(this.row + delta);
	}

	// ------------------------------------------------------------------
	// 绘制
	// ------------------------------------------------------------------

	@Override
	public void initGui() {
		super.initGui();
		int left = this.guiLeft;
		int top = this.guiTop;

		this.addButton(new GuiButtonExt(0, left + 8, top + 4, 25, 12,
				I18n.translateToLocal("silverlighting.button.back")));

		this.searchField = new GuiTextField(0, fontRenderer, guiLeft + 82, guiTop + 5, 88, 10);
		this.searchField.setTextColor(-1);
		this.searchField.setMaxStringLength(20);
		this.searchField.setFocused(true);
		this.searchField.setCanLoseFocus(true);
		this.searchField.setEnableBackgroundDrawing(false);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(TEXTURE);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// 先画虚拟格子（基类），再画搜索框
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		GlStateManager.pushMatrix();
		GlStateManager.translate(-this.guiLeft, -this.guiTop, 0);
		this.searchField.drawTextBox();
		GlStateManager.popMatrix();
	}

	/** 条目超过一页时才需要位置指示器：凹槽是贴图的一部分，装得下就留给它自己 */
	private boolean showBar() {
		return totalRows() > ROWS;
	}

	/** 右侧位置指示器：滑块位置 = 当前页在全部条目里的位置（画在悬浮提示之下） */
	@Override
	protected void drawPageOverlay(int mouseX, int mouseY) {
		if (!showBar()) return;

		int totalRows = totalRows();
		int thumbH = BagGuiHelper.thumbHeight(BAR_H, totalRows, ROWS);
		int thumbY = BagGuiHelper.thumbY(this.guiTop + BAR_Y, BAR_H, thumbH, this.row, maxRow());
		BagGuiHelper.drawScrollBar(this.guiLeft + BAR_X, this.guiTop + BAR_Y, BAR_W, BAR_H, thumbY, thumbH);
	}

	/** 界面外的 Shift 说明要画在悬浮提示之上 */
	@Override
	protected void drawPageTooltip(int mouseX, int mouseY) {
		drawInfoWhenOutside(mouseX, mouseY, "silverlighting.proudsoulbag.special.info");
	}

	// ------------------------------------------------------------------
	// 鼠标 / 键盘
	// ------------------------------------------------------------------

	private boolean isOverBar(int mouseX, int mouseY) {
		return showBar()
				&& BagGuiHelper.isIn(this.guiLeft + BAR_X, this.guiTop + BAR_Y, BAR_W, BAR_H, mouseX, mouseY);
	}

	private boolean isOverScrollableArea(int mouseX, int mouseY) {
		return isOverBar(mouseX, mouseY)
				|| BagGuiHelper.isIn(this.guiLeft + GRID_X, this.guiTop + GRID_Y, COLUMNS * 18, ROWS * 18,
						mouseX, mouseY);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int wheel = Mouse.getEventDWheel();
		if (wheel == 0) return;

		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		if (isOverScrollableArea(mouseX, mouseY)) {
			scrollBy(wheel > 0 ? -1 : 1);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		// 滚动条优先处理（它在格子区域之外，不会和格子冲突）
		if (mouseButton == 0 && isOverBar(mouseX, mouseY)) {
			int thumbH = BagGuiHelper.thumbHeight(BAR_H, totalRows(), ROWS);
			int thumbY = BagGuiHelper.thumbY(this.guiTop + BAR_Y, BAR_H, thumbH, this.row, maxRow());

			if (mouseY >= thumbY && mouseY < thumbY + thumbH) {
				this.draggingBar = true;
				this.dragOffset = mouseY - thumbY;
			} else if (mouseY < thumbY) {
				scrollBy(-ROWS);
			} else {
				scrollBy(ROWS);
			}
			return;
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (this.draggingBar) {
			int thumbH = BagGuiHelper.thumbHeight(BAR_H, totalRows(), ROWS);
			this.row = clampRow(BagGuiHelper.rowForThumbY(this.guiTop + BAR_Y, BAR_H, thumbH,
					mouseY - this.dragOffset, maxRow()));
			return;
		}
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		this.draggingBar = false;
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.searchField != null && this.searchField.isFocused()) {
			// Esc 不该被搜索框吃掉，照旧用来关界面
			if (keyCode == Keyboard.KEY_ESCAPE) {
				super.keyTyped(typedChar, keyCode);
				return;
			}
			this.searchField.textboxKeyTyped(typedChar, keyCode);
			// 过滤与翻页都是本地即时的，这里不需要发任何包
			return;
		}
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (this.searchField != null && this.searchField.isFocused()) {
			this.searchField.updateCursorCounter();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		ModMain.network.sendToServer(new PacketGuiButtonPressed(0, 0));
	}
}
