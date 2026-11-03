package com.tantacat.silverlighting.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import mezz.jei.api.JEIPlugin;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@JEIPlugin
public class SlashBladeJEIPlugin implements IModPlugin{
	
	public SlashBladeJEIPlugin() {}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) 
	{
		
		for (Item item : ForgeRegistries.ITEMS.getValues())
		{
			if (item instanceof ItemSlashBladeNamed)
				subtypeRegistry.registerSubtypeInterpreter(item, new ISlashBladeSubtypeInterpreter());
		}

		/*
		subtypeRegistry.registerSubtypeInterpreter(SlashBlade.bladeNamed, new ISlashBladeSubtypeInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(SlashBlade.wrapBlade, new ISlashBladeSubtypeInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(RegisterItems.sl_blade, new ISlashBladeSubtypeInterpreter());
		
		if (Loader.isModLoaded("negorerouse"))
			subtypeRegistry.registerSubtypeInterpreter(NrBlades.NR_BLADE, new ISlashBladeSubtypeInterpreter());
		*/
	}

	public static class ISlashBladeSubtypeInterpreter implements ISubtypeInterpreter
	{
		@Override
		public String apply(ItemStack param1ItemStack) {
			if (param1ItemStack.hasTagCompound())
			{
				NBTTagCompound tag = param1ItemStack.getTagCompound();
				if (ItemSlashBladeNamed.CurrentItemName.exists(tag))
					return ItemSlashBladeNamed.CurrentItemName.get(tag);
				else
					return param1ItemStack.getItem().getRegistryName().toString();
			}
			else
				return param1ItemStack.getItem().getRegistryName().toString();
		}
	}
}
