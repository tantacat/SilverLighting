package com.tantacat.silverlighting.client.gui;

import java.io.IOException;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.network.PacketGuiButtonPressed;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GUIProudSoulBag extends GuiContainer{
	
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");
	
	public GUIProudSoulBag(Container inventorySlotsIn) 
	{
		super(inventorySlotsIn);		
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override 
    public void initGui()
    {
		super.initGui();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.addButton(new GuiButton(0, offsetX, offsetY + 3 * 18 + 7, 9, 9,"<"));
		this.addButton(new GuiButton(1, offsetX + 18 * 9 + 6, offsetY + 3 * 18 + 7, 9, 9,">"));
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) 
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, 3 * 18 + 17);
        this.drawTexturedModalRect(i, j + 3 * 18 + 17, 0, 126, this.xSize, 96);
        
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
		this.fontRenderer.drawString(new TextComponentTranslation("silverlighting.proudsoulbag").getFormattedText(), 8, 6, 4210752);
        ContainerProudSoulBag container = (ContainerProudSoulBag)this.inventorySlots;
        this.fontRenderer.drawString(container.current_page + "/" + container.max_page, 125, this.ySize - 96 + 2, 4210752);
		
    }
	
	@Override
    protected void actionPerformed(GuiButton button) throws IOException {
		((ContainerProudSoulBag)this.inventorySlots).onButtonPressed(button.id);
        SilverLightingMain.network.sendToServer(new PacketGuiButtonPressed(button.id));
    }
}
