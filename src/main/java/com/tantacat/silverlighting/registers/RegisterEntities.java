package com.tantacat.silverlighting.registers;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.common.entity.EntityBreak;
import com.tantacat.silverlighting.common.entity.EntityDedicationVoid;
import com.tantacat.silverlighting.common.entity.EntityDestory;
import com.tantacat.silverlighting.common.entity.EntityHealingSummonSword;
import com.tantacat.silverlighting.common.entity.EntityHope;
import com.tantacat.silverlighting.common.entity.EntityLeafSwim;
import com.tantacat.silverlighting.common.entity.EntityMurderous;
import com.tantacat.silverlighting.common.entity.EntitySpelling;
import com.tantacat.silverlighting.common.entity.EntityUnswerving;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class RegisterEntities {

	private RegisterEntities() {};
	
	public static RegisterEntities instance = new RegisterEntities();
	
	public void init()
	{
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(SilverLightingMain.MODID, "entitydedicationvoid"),
	                EntityDedicationVoid.class,
	                "EntityDedicationVoid",
	                1,
	                SilverLightingMain.instance,
	                64,
	                1,
	                false);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(SilverLightingMain.MODID, "entityleafswim"),
	                EntityLeafSwim.class,
	                "EntityLeafSwim",
	                2,
	                SilverLightingMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(SilverLightingMain.MODID, "entitydestory"),
	                EntityDestory.class,
	                "EntityDestory",
	                3,
	                SilverLightingMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(SilverLightingMain.MODID, "entitybreak"),
	                EntityBreak.class,
	                "EntityBreak",
	                4,
	                SilverLightingMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(SilverLightingMain.MODID, "entitymurderous"),
	                EntityMurderous.class,
	                "EntityMurderous",
	                5,
	                SilverLightingMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(SilverLightingMain.MODID, "entityspelling"),
	                EntitySpelling.class,
	                "EntitySpelling",
	                6,
	                SilverLightingMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(SilverLightingMain.MODID, "entityhope"),
	                EntityHope.class,
	                "EntityHope",
	                7,
	                SilverLightingMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(SilverLightingMain.MODID, "entityhealingsummonsword"),
	                EntityHealingSummonSword.class,
	                "EntityHealingSummonSword",
	                8,
	                SilverLightingMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(SilverLightingMain.MODID, "entityunswerving"),
	                EntityUnswerving.class,
	                "EntityUnswerving",
	                9,
	                SilverLightingMain.instance,
	                64,
	                1,
	                true);
	}
	
}
