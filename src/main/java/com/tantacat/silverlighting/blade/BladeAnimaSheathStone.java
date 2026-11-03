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

public class BladeAnimaSheathStone {

	private BladeAnimaSheathStone() {};
	
	public static BladeAnimaSheathStone instance = new BladeAnimaSheathStone();
	
	public void registerBlade()
	{
		String name = "animasheath_stone";
		ItemStack animasheath_stone = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_stone.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_stone, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 40);
		ItemAnimaSheath.ModelName.set(tag, "minecraft/spite");
		ItemAnimaSheath.TextureName.set(tag, "minecraft/stone");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.destory.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, -0x8E8E86);
		animasheath_stone.addEnchantment(Enchantments.POWER, 5);
		DamageProfileHelper.addDamageProfile(animasheath_stone, new DamageProfile(name, 1.5f, 0, 1.0f, 0));
		
		RegisterBlades.instance.registerCustomItemStack(name, animasheath_stone);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "animasheath_stone";
		ItemStack animasheath_stone = RegisterBlades.instance.getCustomBlade(name);

		ProudSoulIngredient ProudSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));

		ItemStack brokenbamboo = new ItemStack(RegisterItems.brokenbamboo);
		
		ItemStack stackBalkon_stone = new ItemStack(SlashBlade.wrapBlade);
		stackBalkon_stone.setTagCompound(new NBTTagCompound());
		SlashBlade.wrapBlade.setWrapItem(stackBalkon_stone, new ItemStack(Items.WOODEN_SWORD));
		NBTTagCompound tag_balkon_stone = stackBalkon_stone.getTagCompound();
		ItemAnimaSheath.CurrentItemName.set(tag_balkon_stone, "wrap.reforged.stone_katana");
		ItemAnimaSheath.TextureName.set(tag_balkon_stone, "BalkonStone");
		ItemAnimaSheath.ProudSoul.set(tag_balkon_stone, 3000);
		BladeIngredient balkon_stone = new BladeIngredient(stackBalkon_stone);
		
		ItemStack IngotBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1);
		IngotBladeSoul.addEnchantment(Enchantments.POWER, 1);
		ProudSoulIngredient enchantIngot = new ProudSoulIngredient(IngotBladeSoul);
		
		ItemStack stackAnimasheath = RegisterBlades.instance.getCustomBlade("animasheath");
		NBTTagCompound tag_animasheath = stackAnimasheath.getTagCompound();
		ItemAnimaSheath.ProudSoul.set(tag_animasheath, 2000);
		ItemAnimaSheath.KillCount.set(tag_animasheath, 200);
		BladeIngredient animasheath = new BladeIngredient(stackAnimasheath);

		RecipeReplaceSheath recipe = new RecipeReplaceSheath(new ResourceLocation(ModMain.MODID, name + "_recipe"), animasheath_stone,
				"789",
				"456",
				"123",
				'7', ItemStack.EMPTY, '8', brokenbamboo, '9', enchantIngot,
				'4', ProudSoul      , '5', balkon_stone, '6', brokenbamboo,
				'1', animasheath    , '2', ProudSoul   , '3', ItemStack.EMPTY);
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
