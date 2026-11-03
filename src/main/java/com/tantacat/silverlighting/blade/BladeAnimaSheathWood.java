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

public class BladeAnimaSheathWood {

	private BladeAnimaSheathWood() {};
	
	public static BladeAnimaSheathWood instance = new BladeAnimaSheathWood();
	
	public void registerBlade()
	{
		String name = "animasheath_wood";
		ItemStack animasheath_wood = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_wood.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_wood, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 40);
		ItemAnimaSheath.ModelName.set(tag, "minecraft/drape");
		ItemAnimaSheath.TextureName.set(tag, "minecraft/wood");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.leafswim.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, 0xEFC381);
		animasheath_wood.addEnchantment(Enchantments.FEATHER_FALLING, 2);
		DamageProfileHelper.addDamageProfile(animasheath_wood, new DamageProfile(name, 1.0f, 0, 1.0f, 0));
		
		RegisterBlades.instance.registerCustomItemStack(name, animasheath_wood);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "animasheath_wood";
		ItemStack animasheath_wood = RegisterBlades.instance.getCustomBlade(name);

		ProudSoulIngredient ProudSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));

		ItemStack brokenbamboo = new ItemStack(RegisterItems.brokenbamboo);
		
		ItemStack stackBalkon_wood = new ItemStack(SlashBlade.wrapBlade);
		stackBalkon_wood.setTagCompound(new NBTTagCompound());
		SlashBlade.wrapBlade.setWrapItem(stackBalkon_wood, new ItemStack(Items.WOODEN_SWORD));
		NBTTagCompound tag_balkon_wood = stackBalkon_wood.getTagCompound();
		ItemAnimaSheath.CurrentItemName.set(tag_balkon_wood, "wrap.reforged.wooden_katana");
		ItemAnimaSheath.TextureName.set(tag_balkon_wood, "BalkonWood");
		ItemAnimaSheath.ProudSoul.set(tag_balkon_wood, 1000);
		ItemAnimaSheath.RepairCount.set(tag_balkon_wood, 10);
		BladeIngredient balkon_wood = new BladeIngredient(stackBalkon_wood);

		ItemStack Ingot = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1);
		Ingot.addEnchantment(Enchantments.FEATHER_FALLING, 1);
		ProudSoulIngredient enchantIngot = new ProudSoulIngredient(Ingot);
		
		ItemStack stackAnimasheath = RegisterBlades.instance.getCustomBlade("animasheath");
		NBTTagCompound tag_animasheath = stackAnimasheath.getTagCompound();
		ItemAnimaSheath.ProudSoul.set(tag_animasheath, 2000);
		ItemAnimaSheath.KillCount.set(tag_animasheath, 200);
		BladeIngredient animasheath = new BladeIngredient(stackAnimasheath);

		RecipeReplaceSheath recipe = new RecipeReplaceSheath(new ResourceLocation(ModMain.MODID, name + "_recipe"), animasheath_wood,
					"789",
					"456",
					"123",
					'7', ItemStack.EMPTY, '8', brokenbamboo, '9', enchantIngot,
					'4', ProudSoul      , '5', balkon_wood , '6', brokenbamboo,
					'1', animasheath    , '2', ProudSoul   , '3', ItemStack.EMPTY);
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
