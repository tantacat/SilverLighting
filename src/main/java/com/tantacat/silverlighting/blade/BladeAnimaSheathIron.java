package com.tantacat.silverlighting.blade;

import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.recipe.BladeIngredient;
import com.tantacat.silverlighting.recipe.ProudSoulIngredient;
import com.tantacat.silverlighting.recipe.RecipeReplaceSheath;
import com.tantacat.silverlighting.registers.RegisterBlades;
import com.tantacat.silverlighting.registers.RegisterItems;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class BladeAnimaSheathIron {

	private BladeAnimaSheathIron() {};
	
	public static BladeAnimaSheathIron instance = new BladeAnimaSheathIron();
	
	public void registerBlade()
	{
		String name = "animasheath_iron";
		ItemStack animasheath_iron = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_iron.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_iron, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
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

		ProudSoulIngredient ProudSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));

		ItemStack brokenbamboo = new ItemStack(RegisterItems.brokenbamboo);
		
		ItemStack stackBalkon_iron = new ItemStack(SlashBlade.wrapBlade);
		stackBalkon_iron.setTagCompound(new NBTTagCompound());
		SlashBlade.wrapBlade.setWrapItem(stackBalkon_iron, new ItemStack(Items.WOODEN_SWORD));
		NBTTagCompound tag_balkon_iron = stackBalkon_iron.getTagCompound();
		ItemAnimaSheath.CurrentItemName.set(tag_balkon_iron, "wrap.reforged.iron_katana");
		ItemAnimaSheath.TextureName.set(tag_balkon_iron, "BalkonIron");
		ItemAnimaSheath.KillCount.set(tag_balkon_iron, 1000);
		BladeIngredient balkon_iron = new BladeIngredient(stackBalkon_iron);
		
		ItemStack IngotBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1);
		IngotBladeSoul.addEnchantment(Enchantments.SHARPNESS, 1);
		ProudSoulIngredient enchantIngot = new ProudSoulIngredient(IngotBladeSoul);
		
		ItemStack stackAnimasheath = RegisterBlades.instance.getCustomBlade("animasheath");
		NBTTagCompound tag_animasheath = stackAnimasheath.getTagCompound();
		ItemAnimaSheath.ProudSoul.set(tag_animasheath, 2000);
		ItemAnimaSheath.KillCount.set(tag_animasheath, 200);
		BladeIngredient animasheath = new BladeIngredient(stackAnimasheath);

		RecipeReplaceSheath recipe = new RecipeReplaceSheath(new ResourceLocation(ModMain.MODID, name + "_recipe"), animasheath_iron,
				"789",
				"456",
				"123",
				'7',ItemStack.EMPTY, '8',brokenbamboo, '9',enchantIngot,
				'4',ProudSoul      , '5',balkon_iron , '6',brokenbamboo,	
				'1',animasheath    , '2',ProudSoul   , '3',ItemStack.EMPTY);
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
