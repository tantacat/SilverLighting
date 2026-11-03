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

public class BladeAnimaSheathGold {

	private BladeAnimaSheathGold() {};
	
	public static BladeAnimaSheathGold instance = new BladeAnimaSheathGold();
	
	public void registerBlade()
	{
		String name = "animasheath_gold";
		ItemStack animasheath_gold = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_gold.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_gold, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 40);
		ItemAnimaSheath.ModelName.set(tag, "minecraft/gleam");
		ItemAnimaSheath.TextureName.set(tag, "minecraft/gold");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.spell.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, 0xFFC516);
		animasheath_gold.addEnchantment(Enchantments.THORNS, 2);
		DamageProfileHelper.addDamageProfile(animasheath_gold, new DamageProfile(name, 0.8f, 0, 1.0f, 0));

		RegisterBlades.instance.registerCustomItemStack(name, animasheath_gold);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "animasheath_gold";
		ItemStack animasheath_gold = RegisterBlades.instance.getCustomBlade(name);

		ProudSoulIngredient ProudSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));

		ItemStack brokenbamboo = new ItemStack(RegisterItems.brokenbamboo);
				
		ItemStack stackBalkon_gold = new ItemStack(SlashBlade.wrapBlade);
		stackBalkon_gold.setTagCompound(new NBTTagCompound());
		SlashBlade.wrapBlade.setWrapItem(stackBalkon_gold, new ItemStack(Items.WOODEN_SWORD));
		NBTTagCompound tag_balkon_gold = stackBalkon_gold.getTagCompound();
		ItemAnimaSheath.CurrentItemName.set(tag_balkon_gold, "wrap.reforged.golden_katana");
		ItemAnimaSheath.TextureName.set(tag_balkon_gold, "BalkonGold");
		ItemAnimaSheath.ProudSoul.set(tag_balkon_gold, 1000);
		ItemAnimaSheath.RepairCount.set(tag_balkon_gold, 10);
		BladeIngredient balkon_gold = new BladeIngredient(stackBalkon_gold);
		
		ItemStack IngotBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1);
		IngotBladeSoul.addEnchantment(Enchantments.THORNS, 1);
		ProudSoulIngredient enchantIngot = new ProudSoulIngredient(IngotBladeSoul);
		
		ItemStack stackAnimasheath = RegisterBlades.instance.getCustomBlade("animasheath");
		NBTTagCompound tag_animasheath = stackAnimasheath.getTagCompound();
		ItemAnimaSheath.ProudSoul.set(tag_animasheath, 2000);
		ItemAnimaSheath.KillCount.set(tag_animasheath, 200);
		BladeIngredient animasheath = new BladeIngredient(stackAnimasheath);

		RecipeReplaceSheath recipe = new RecipeReplaceSheath(new ResourceLocation(ModMain.MODID, name + "_recipe"),  animasheath_gold,
				"789",
				"456",
				"123",
				'7',ItemStack.EMPTY, '8',brokenbamboo, '9',enchantIngot,
				'4',ProudSoul      , '5',balkon_gold , '6',brokenbamboo,	
				'1',animasheath    , '2',ProudSoul   , '3',ItemStack.EMPTY);
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
