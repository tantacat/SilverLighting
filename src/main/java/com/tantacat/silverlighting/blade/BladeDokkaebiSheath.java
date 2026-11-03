package com.tantacat.silverlighting.blade;

import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterBlades;
import com.tantacat.silverlighting.registers.RegisterItems;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BladeDokkaebiSheath {

	private BladeDokkaebiSheath() {};
	
	public static BladeDokkaebiSheath instance = new BladeDokkaebiSheath();

	public void registerBlade()
	{
		String name = "dokkaebisheath";
		ItemStack dokkaebisheath = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		dokkaebisheath.setTagCompound(tag);
		
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.ModelName.set(tag, "silver/dokkaebisheath");
		ItemAnimaSheath.TextureName.set(tag, "silver/dokkaebisheath");
		ItemAnimaSheath.IsDefaultBewitched.set(tag, true);
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.noutou.id);
		DamageProfileHelper.addDamageProfile(dokkaebisheath, new DamageProfile(name, 5.20f, 0, 1.314f, 0));

		RegisterBlades.instance.registerCustomItemStack(name, dokkaebisheath);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		
	}
	
}
