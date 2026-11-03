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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class BladeAnimaSheathGleam {

	private BladeAnimaSheathGleam() {};
	
	public static BladeAnimaSheathGleam instance = new BladeAnimaSheathGleam();
	
	public void registerBlade()
	{
		String name = "animasheath_gleam";
		ItemStack animasheath_gleam = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_gleam.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_gleam, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.IsDefaultBewitched.set(tag, true);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 30);
		ItemAnimaSheath.ModelName.set(tag, "minecraft/gleam");
		ItemAnimaSheath.TextureName.set(tag, "minecraft/gleam");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.spell.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, 0xFFC516);
		SpecialEffects.addEffect(animasheath_gleam, RegisterSEs.instance.SpellWeave);
		animasheath_gleam.addEnchantment(Enchantments.THORNS, 2);
		DamageProfileHelper.addDamageProfile(animasheath_gleam, new DamageProfile(name, 0.8f, 0, 1.0f, 0));
		BoostProfileHelper.addBoostProfile(animasheath_gleam, RegisterBoosts.instance.Spelling.profile);

		RegisterBlades.instance.registerCustomItemStack(name, animasheath_gleam);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "animasheath_gleam";
		ItemStack animasheath_gleam = RegisterBlades.instance.getCustomBlade(name);
		
		ItemStack SphereBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1);
		SphereBladeSoul.setTagCompound(new NBTTagCompound());
		ItemAnimaSheath.SpecialAttackType.set(SphereBladeSoul.getTagCompound(), RegisterSAs.instance.spell.id);
		ProudSoulIngredient SASphere = new ProudSoulIngredient(SphereBladeSoul);

		ProudSoulIngredient TrapezohedronBladeSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1));

		ItemStack brokenbamboo = new ItemStack(RegisterItems.brokenbamboo);

		ItemStack blade = RegisterBlades.instance.getCustomBlade("animasheath_gold");
		NBTTagCompound tag_animasheath_gold = blade.getTagCompound();
		ItemAnimaSheath.RepairCount.set(tag_animasheath_gold, 10);
		ItemAnimaSheath.ProudSoul.set(tag_animasheath_gold, 4000);
		BladeIngredient animasheath_gold = new BladeIngredient(blade);

		ItemStack forgerheart = new ItemStack(RegisterItems.forgerheart);

		RecipeBladeResult recipe = new RecipeBladeResult(new ResourceLocation(ModMain.MODID, name + "_recipe"), animasheath_gleam,
					"789",
					"456",
					"123",
					'7', SASphere              , '8',"gemEmerald" , '9', Blocks.GOLD_BLOCK,
					'4', TrapezohedronBladeSoul, '5', brokenbamboo, '6', Items.FLINT,
					'1', animasheath_gold      , '2', forgerheart , '3', Blocks.ENCHANTING_TABLE);
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
