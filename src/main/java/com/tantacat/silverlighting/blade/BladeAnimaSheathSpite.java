package com.tantacat.silverlighting.blade;

import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.recipe.BladeIngredient;
import com.tantacat.silverlighting.recipe.ProudSoulIngredient;
import com.tantacat.silverlighting.recipe.RecipeBladeResult;
import com.tantacat.silverlighting.registers.*;
import com.tantacat.silverlighting.util.BoostProfileHelper;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class BladeAnimaSheathSpite {

	private BladeAnimaSheathSpite() {};
	
	public static BladeAnimaSheathSpite instance = new BladeAnimaSheathSpite();
	
	public void registerBlade()
	{
		String name = "animasheath_spite";
		ItemStack animasheath_spite = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_spite.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_spite, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.IsDefaultBewitched.set(tag, true);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 70);
		ItemAnimaSheath.ModelName.set(tag, "minecraft/spite");
		ItemAnimaSheath.TextureName.set(tag, "minecraft/spite");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.destory.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, -0x8E8E86);
		SpecialEffects.addEffect(animasheath_spite, RegisterSEs.instance.Tremor);
		animasheath_spite.addEnchantment(Enchantments.POWER, 5);
		DamageProfileHelper.addDamageProfile(animasheath_spite, new DamageProfile(name, 1.5f, 0, 1.0f, 0));
		BoostProfileHelper.addBoostProfile(animasheath_spite, RegisterBoosts.instance.Break.profile);
		
		RegisterBlades.instance.registerCustomItemStack(name, animasheath_spite);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "animasheath_spite";
		ItemStack animasheath_spite = RegisterBlades.instance.getCustomBlade(name);
		
		ItemStack SphereBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1);
		SphereBladeSoul.setTagCompound(new NBTTagCompound());
		ItemAnimaSheath.SpecialAttackType.set(SphereBladeSoul.getTagCompound(), RegisterSAs.instance.destory.id);
		ProudSoulIngredient SASphere = new ProudSoulIngredient(SphereBladeSoul);

		Ingredient stonebrick = Ingredient.fromStacks(new ItemStack(Blocks.STONEBRICK, 1, OreDictionary.WILDCARD_VALUE));

		ProudSoulIngredient TrapezohedronBladeSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1));

		ItemStack brokenbamboo = new ItemStack(RegisterItems.brokenbamboo);

		ItemStack blade = RegisterBlades.instance.getCustomBlade("animasheath_stone");
		NBTTagCompound tag_animasheath_stone = blade.getTagCompound();
		ItemAnimaSheath.ProudSoul.set(tag_animasheath_stone, 10000);
		BladeIngredient animasheath_stone = new BladeIngredient(blade);
		
		ItemStack forgerheart = new ItemStack(RegisterItems.forgerheart);

		RecipeBladeResult recipe = new RecipeBladeResult(new ResourceLocation(ModMain.MODID, name + "_recipe"), animasheath_spite,
					"789",
					"456",
					"123",
					'7', SASphere              , '8',"gravel"     , '9', stonebrick,
					'4', TrapezohedronBladeSoul, '5', brokenbamboo, '6',"leather",
					'1', animasheath_stone     , '2', forgerheart , '3',"obsidian"  );
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
