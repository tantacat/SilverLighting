package com.tantacat.silverlighting.config;

import com.tantacat.silverlighting.SilverLightingMain;

import net.minecraftforge.common.config.Config;

@Config(modid = SilverLightingMain.MODID)
@Config.LangKey("config.silverlighting.general")
public class ConfigGeneral {

	@Config.LangKey("config.silverlighting.general.canAttackPlayer")
	public static boolean canAttackPlayer = false;
	
	@Config.LangKey("config.silverlighting.general.canAttackAllMob")
	public static boolean canAttackAllMob = true;
	
	@Config.LangKey("config.silverlighting.general.canUseAntiCrystal")
	public static boolean canUseAntiCrystal = true;
	
	@Config.LangKey("config.silverlighting.general.isUnswervingEnable")
	public static boolean isUnswervingEnable = true;
	
	@Config.LangKey("config.silverlighting.general.canReciveVoice")
	public static boolean canReciveVoice = true;
	
}
