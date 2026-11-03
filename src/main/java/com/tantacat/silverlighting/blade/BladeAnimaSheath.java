package com.tantacat.silverlighting.blade;

import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterBlades;
import com.tantacat.silverlighting.registers.RegisterItems;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BladeAnimaSheath {

	private BladeAnimaSheath() {};
	
	public static BladeAnimaSheath instance = new BladeAnimaSheath();
		
	public void registerBlade()
	{
		String name = "animasheath";
		ItemStack animasheath = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
		//ItemAnimaSheath.CustomMaxDamage.set(innertag, 1);
		tag.setBoolean("Unbreakable", true);
		tag.setInteger("HideFlags", 4);
		ItemAnimaSheath.IsSealed.set(tag, true);
		ItemAnimaSheath.ModelName.set(tag, "silver/sheath");
		ItemAnimaSheath.TextureName.set(tag, "silver/sheath");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.dress.id);		
		DamageProfileHelper.addDamageProfile(animasheath, new DamageProfile(name, 0.5f, 0, 1.0f, 0));
		
		
		RegisterBlades.instance.registerCustomItemStack(name, animasheath);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
	}
	
}
