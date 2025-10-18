package com.tantacat.silverlighting.config;

import java.util.Collections;
import java.util.Set;

import com.tantacat.silverlighting.SilverLightingMain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGuiFactory implements IModGuiFactory{

	@Override
	public void initialize(Minecraft minecraftInstance) {
		
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new GuiConfig(parentScreen, ConfigElement.from(ConfigGeneral.class).getChildElements(), SilverLightingMain.MODID, false, false, null, null);
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return Collections.EMPTY_SET;
	}

	
	
}
