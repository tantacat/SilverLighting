package com.tantacat.silverlighting.registers;

import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.common.entity.*;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class RegisterEntities {

	private RegisterEntities() {};
	
	public static RegisterEntities instance = new RegisterEntities();
	
	public void init()
	{
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(ModMain.MODID, "entitydedicationvoid"),
	                EntityDedicationVoid.class,
	                "EntityDedicationVoid",
	                1,
	                ModMain.instance,
	                64,
	                1,
	                false);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(ModMain.MODID, "entityleafswim"),
	                EntityLeafSwim.class,
	                "EntityLeafSwim",
	                2,
	                ModMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(ModMain.MODID, "entitydestory"),
	                EntityDestory.class,
	                "EntityDestory",
	                3,
	                ModMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(ModMain.MODID, "entitybreak"),
	                EntityBreak.class,
	                "EntityBreak",
	                4,
	                ModMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(ModMain.MODID, "entitymurderous"),
	                EntityMurderous.class,
	                "EntityMurderous",
	                5,
	                ModMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(ModMain.MODID, "entityhope"),
	                EntityHope.class,
	                "EntityHope",
	                6,
	                ModMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(ModMain.MODID, "entityhealingsummonsword"),
	                EntityHealingSummonSword.class,
	                "EntityHealingSummonSword",
	                7,
	                ModMain.instance,
	                64,
	                1,
	                true);
		 EntityRegistry.registerModEntity(
	                new ResourceLocation(ModMain.MODID, "entityunswerving"),
	                EntityUnswerving.class,
	                "EntityUnswerving",
	                8,
	                ModMain.instance,
	                64,
	                1,
	                true);
		EntityRegistry.registerModEntity(
				new ResourceLocation(ModMain.MODID, "entityrecrystaldrive"),
				EntityRecrystalDrive.class,
				"EntityRecrystalDrive",
				9,
				ModMain.instance,
				64,
				1,
				true);
	}
	
}
