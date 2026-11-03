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
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class BladeAnimaSheathPhos {

	private BladeAnimaSheathPhos() {};
	
	public static BladeAnimaSheathPhos instance = new BladeAnimaSheathPhos();
	
	public void registerBlade()
	{
		String name = "animasheath_phos";
		ItemStack animasheath_phos = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_phos.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_phos, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.IsDefaultBewitched.set(tag, true);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 90);
		ItemAnimaSheath.ModelName.set(tag, "minecraft/phos");
		ItemAnimaSheath.TextureName.set(tag, "minecraft/phos");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.recrystal.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, 0x09CEFF);
		SpecialEffects.addEffect(animasheath_phos, RegisterSEs.instance.PseudoForm);
		animasheath_phos.addEnchantment(Enchantments.UNBREAKING, 5);
		DamageProfileHelper.addDamageProfile(animasheath_phos, new DamageProfile(name, 2.1f, 0, 1.0f, 0));
		BoostProfileHelper.addBoostProfile(animasheath_phos, RegisterBoosts.instance.Shardstorm.profile);

		RegisterBlades.instance.registerCustomItemStack(name, animasheath_phos);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "animasheath_phos";
		ItemStack animasheath_phos = RegisterBlades.instance.getCustomBlade(name);

		ItemStack SphereBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1);
		SphereBladeSoul.setTagCompound(new NBTTagCompound());
		ItemAnimaSheath.SpecialAttackType.set(SphereBladeSoul.getTagCompound(), RegisterSAs.instance.recrystal.id);
		ProudSoulIngredient SASphere = new ProudSoulIngredient(SphereBladeSoul);

		ProudSoulIngredient TrapezohedronBladeSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1));

		ItemStack brokenbamboo = new ItemStack(RegisterItems.brokenbamboo);

		BladeIngredient animasheath_diamond = new BladeIngredient(RegisterBlades.instance.getCustomBlade("animasheath_diamond"));

		ItemStack forgerheart = new ItemStack(RegisterItems.forgerheart);

		ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);
		diamondSword.setItemDamage(diamondSword.getMaxDamage());
		Ingredient damagedSword = Ingredient.fromStacks(diamondSword);

		RecipeBladeResult recipe = new RecipeBladeResult(new ResourceLocation(ModMain.MODID, name + "_recipe"), animasheath_phos,
					"789",
					"456",
					"123",
					'7', SASphere              , '8', Items.END_CRYSTAL, '9', Blocks.DIAMOND_BLOCK,
					'4', TrapezohedronBladeSoul, '5', brokenbamboo     , '6',"oreDiamond",
					'1', animasheath_diamond   , '2', forgerheart      , '3', damagedSword         );
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
}
