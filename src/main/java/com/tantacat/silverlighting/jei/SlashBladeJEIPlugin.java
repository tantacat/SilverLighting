package com.tantacat.silverlighting.jei;

import com.tantacat.silverlighting.registers.RegisterItems;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import mezz.jei.api.JEIPlugin;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import moflop.mods.negorerouse.init.NrBlades;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;

@JEIPlugin
public class SlashBladeJEIPlugin implements IModPlugin{
	
	public SlashBladeJEIPlugin() {};
	
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) 
	{
		subtypeRegistry.registerSubtypeInterpreter(SlashBlade.bladeNamed, new ISlashBladeSubtypeInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(SlashBlade.wrapBlade, new ISlashBladeSubtypeInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(RegisterItems.instance.sl_blade, new ISlashBladeSubtypeInterpreter());
		
		if (Loader.isModLoaded("negorerouse"))
			subtypeRegistry.registerSubtypeInterpreter(NrBlades.NR_BLADE, new ISlashBladeSubtypeInterpreter());
	};
	
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
