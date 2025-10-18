package com.tantacat.silverlighting.blade;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterBlades;
import com.tantacat.silverlighting.registers.RegisterItems;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;
import com.tantacat.silverlighting.util.RecipeWithNBTHelper;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BladeAnimaSheathIron {

	private BladeAnimaSheathIron() {};
	
	public static BladeAnimaSheathIron instance = new BladeAnimaSheathIron();
	
	public void registerBlade()
	{
		String name = "animasheath_iron";
		ItemStack animasheath_iron = new ItemStack(RegisterItems.instance.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_iron.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_iron, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 40);
		ItemAnimaSheath.ModelName.set(tag, "minecraft/edge");
		ItemAnimaSheath.TextureName.set(tag, "minecraft/iron");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.murderous.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, 0xDFDFDF);
		animasheath_iron.addEnchantment(Enchantments.SHARPNESS, 5);
		DamageProfileHelper.addDamageProfile(animasheath_iron, new DamageProfile(name, 2.3f, 0, 1.0f, 0));

		RegisterBlades.instance.registerCustomItemStack(name, animasheath_iron);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "animasheath_iron";
		ItemStack animasheath_iron = RegisterBlades.instance.getCustomBlade(name);
		ItemStack ProudSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1);
		ItemStack brokenbamboo = new ItemStack(RegisterItems.instance.brokenbamboo);
		
		ItemStack balkon_iron = new ItemStack(SlashBlade.wrapBlade);
		balkon_iron.setTagCompound(new NBTTagCompound());
		SlashBlade.wrapBlade.setWrapItem(balkon_iron, new ItemStack(Items.WOODEN_SWORD));
		NBTTagCompound tag_balkon_iron = balkon_iron.getTagCompound();
		ItemAnimaSheath.CurrentItemName.set(tag_balkon_iron, "wrap.reforged.iron_katana");
		ItemAnimaSheath.TextureName.set(tag_balkon_iron, "BalkonIron");
		ItemAnimaSheath.KillCount.set(tag_balkon_iron, 1000);
		
		ItemStack IngotBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1);
		IngotBladeSoul.addEnchantment(Enchantments.SHARPNESS, 1);
		
		ItemStack animasheath = RegisterBlades.instance.getCustomBlade("animasheath");
		NBTTagCompound tag_animasheath = animasheath.getTagCompound();
		ItemAnimaSheath.ProudSoul.set(tag_animasheath, 2000);
		ItemAnimaSheath.KillCount.set(tag_animasheath, 200);
		RecipeWithNBTHelper recipe = new RecipeWithNBTHelper(name + "_recipe", 3, 3, animasheath_iron,
			new Object[]
			{
				"789",
				"456",
				"123",
				'7',ItemStack.EMPTY, '8',brokenbamboo, '9',IngotBladeSoul,
				'4',ProudSoul      , '5',balkon_iron , '6',brokenbamboo,	
				'1',animasheath    , '2',ProudSoul   , '3',ItemStack.EMPTY		
			});
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
