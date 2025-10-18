package com.tantacat.silverlighting.blade;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterBlades;
import com.tantacat.silverlighting.registers.RegisterItems;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BladeDokkaebiSheath {

	private BladeDokkaebiSheath() {};
	
	public static BladeDokkaebiSheath instance = new BladeDokkaebiSheath();

	public void registerBlade()
	{
		String name = "dokkaebisheath";
		ItemStack dokkaebisheath = new ItemStack(RegisterItems.instance.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		dokkaebisheath.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(dokkaebisheath, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 50);
		ItemAnimaSheath.IsSealed.set(tag, true);
		ItemAnimaSheath.ModelName.set(tag, "silver/dokkaebisheath");
		ItemAnimaSheath.TextureName.set(tag, "silver/dokkaebisheath");
		DamageProfileHelper.addDamageProfile(dokkaebisheath, new DamageProfile(name, 5.20f, 0, 1.314f, 0));
		
		RegisterBlades.instance.registerCustomItemStack(name, dokkaebisheath);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		
	}
	
}
