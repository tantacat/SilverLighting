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

public class BladeAnimaSheathDiamond {

	private BladeAnimaSheathDiamond() {};
	
	public static BladeAnimaSheathDiamond instance = new BladeAnimaSheathDiamond();
	
	public void registerBlade()
	{
		String name = "animasheath_diamond";
		ItemStack animasheath_diamond = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_diamond.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_diamond, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 40);
		ItemAnimaSheath.ModelName.set(tag, "minecraft/diamond");
		ItemAnimaSheath.TextureName.set(tag, "minecraft/diamond");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.recrystal.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, 0x09CEFF);
		animasheath_diamond.addEnchantment(Enchantments.UNBREAKING, 5);
		DamageProfileHelper.addDamageProfile(animasheath_diamond, new DamageProfile(name, 2.1f, 0, 1.0f, 0));

		RegisterBlades.instance.registerCustomItemStack(name, animasheath_diamond);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "animasheath_diamond";
		ItemStack animasheath_diamond = RegisterBlades.instance.getCustomBlade(name);

		ProudSoulIngredient ProudSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));

		ItemStack brokenbamboo = new ItemStack(RegisterItems.brokenbamboo);
						
		ItemStack stackBalkon_diamond = new ItemStack(SlashBlade.wrapBlade);
		stackBalkon_diamond.setTagCompound(new NBTTagCompound());
		SlashBlade.wrapBlade.setWrapItem(stackBalkon_diamond, new ItemStack(Items.WOODEN_SWORD));
		NBTTagCompound tag_balkon_diamond = stackBalkon_diamond.getTagCompound();
		ItemAnimaSheath.CurrentItemName.set(tag_balkon_diamond, "wrap.reforged.diamond_katana");
		ItemAnimaSheath.TextureName.set(tag_balkon_diamond, "BalkonDiamond");
		ItemAnimaSheath.ProudSoul.set(tag_balkon_diamond, 3500);
		ItemAnimaSheath.KillCount.set(tag_balkon_diamond, 500);
		BladeIngredient balkon_diamond = new BladeIngredient(stackBalkon_diamond);
		
		ItemStack IngotBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1);
		IngotBladeSoul.addEnchantment(Enchantments.UNBREAKING, 1);
		ProudSoulIngredient enchantIngot = new ProudSoulIngredient(IngotBladeSoul);
		
		ItemStack stackAnimasheath = RegisterBlades.instance.getCustomBlade("animasheath");
		NBTTagCompound tag_animasheath = stackAnimasheath.getTagCompound();
		ItemAnimaSheath.ProudSoul.set(tag_animasheath, 2000);
		ItemAnimaSheath.KillCount.set(tag_animasheath, 200);
		BladeIngredient animasheath = new BladeIngredient(stackAnimasheath);

		RecipeReplaceSheath recipe = new RecipeReplaceSheath(new ResourceLocation(ModMain.MODID, name + "_recipe"), animasheath_diamond,
				"789",
				"456",
				"123",
				'7',ItemStack.EMPTY, '8',brokenbamboo  , '9',enchantIngot,
				'4',ProudSoul      , '5',balkon_diamond, '6',brokenbamboo,	
				'1',animasheath    , '2',ProudSoul     , '3',ItemStack.EMPTY);
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
