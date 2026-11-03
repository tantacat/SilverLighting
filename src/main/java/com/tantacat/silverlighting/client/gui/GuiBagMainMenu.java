package com.tantacat.silverlighting.client.gui;

import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.network.PacketGuiButtonPressed;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主界面：固定展示六种耀魂的数量，并提供存取。
 *
 * <p>展示格是画出来的（虚拟格子），位置与原来完全一致（26/44, 17/35/53）。
 * 数量直接从袋子（也就是客户端手上那个物品的标签）里读，所以永远是最新的，
 * 而且不受原版"数量写成 byte"的限制。
 */
@SideOnly(Side.CLIENT)
public class GuiBagMainMenu extends GuiBagBase {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ModMain.MODID,
			"textures/gui/bag/init.png");

	/** 主界面固定展示的六种耀魂 */
	private final List<ItemStack> souls = createNormalSouls();

	public GuiBagMainMenu(ContainerBagMainMenu container) {
		super(container, BagPage.MAIN, 176, 166);
	}

	@Override
	protected int getGridX() {
		return 26;
	}

	@Override
	protected int getGridY() {
		return 17;
	}

	@Override
	protected int getColumns() {
		return 2;
	}

	@Override
	protected int getRows() {
		return 3;
	}

	/** 六种耀魂各占一格：数量为 0 时画灰色占位图标（与原实现一致） */
	@Override
	protected List<Cell> buildCells() {
		List<ItemStack> entries = getEntries();
		List<Cell> out = new ArrayList<Cell>(6);

		for (int i = 0; i < 6; i++) {
			ItemStack template = i < souls.size() ? souls.get(i) : ItemStack.EMPTY;
			if (template == null || template.isEmpty()) {
				out.add(null);
				continue;
			}
			// 每帧用副本：物品的 tooltip 可能会就地给物品塞一个空标签
			// （SlashBlade 的 getItemTagCompound），共享模板的话会让匹配条件变掉。
			template = template.copy();

			int stored = 0;
			for (int e = 0; e < entries.size(); e++) {
				ItemStack entry = entries.get(e);
				if (entry.isEmpty()) continue;
				if (ItemHandlerHelper.canItemStacksStack(template, entry)) {
					stored = entry.getCount();
					break;
				}
			}

			int count = adjustCount(template, stored);
			out.add(new Cell(template, count, count <= 0));
		}
		return out;
	}

	@Override
	public void initGui() {
		super.initGui();
		int left = this.guiLeft;
		int top = this.guiTop;

		this.addButton(new GuiButtonExt(0, left + 83 + 32, top + 18, 36, 17,
				I18n.translateToLocal("silverlighting.proudsoulbag.button.enchanted")));
		this.addButton(new GuiButtonExt(1, left + 83 + 32, top + 18 + 33, 36, 17,
				I18n.translateToLocal("silverlighting.proudsoulbag.button.special")));
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
	protected void drawPageTooltip(int mouseX, int mouseY) {
		drawInfoWhenOutside(mouseX, mouseY, "silverlighting.proudsoulbag.main.info");
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		// 交给服务端重新开界面（袋子拿在副手时也能正确打开，见 PacketGuiButtonPressed）
		ModMain.network.sendToServer(new PacketGuiButtonPressed(1, button.id));
	}
}
