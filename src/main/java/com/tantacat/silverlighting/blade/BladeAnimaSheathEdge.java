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
import net.minecraftforge.oredict.OreDictionary;

public class BladeAnimaSheathEdge {

	private BladeAnimaSheathEdge() {};
	
	public static BladeAnimaSheathEdge instance = new BladeAnimaSheathEdge();
	
	public void registerBlade()
	{
		String name = "animasheath_edge";
		ItemStack animasheath_edge = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_edge.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_edge, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.IsDefaultBewitched.set(tag, true);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 80);
		ItemAnimaSheath.ModelName.set(tag, "minecraft/edge");
		ItemAnimaSheath.TextureName.set(tag, "minecraft/edge");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.murderous.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, 0xDFDFDF);
		SpecialEffects.addEffect(animasheath_edge, RegisterSEs.instance.Blooded);
		animasheath_edge.addEnchantment(Enchantments.SHARPNESS, 5);
		DamageProfileHelper.addDamageProfile(animasheath_edge, new DamageProfile(name, 2.3f, 0, 1.0f, 0));
		BoostProfileHelper.addBoostProfile(animasheath_edge, RegisterBoosts.instance.Pressure.profile);		
		
		RegisterBlades.instance.registerCustomItemStack(name, animasheath_edge);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "animasheath_edge";
		ItemStack animasheath_edge = RegisterBlades.instance.getCustomBlade(name);
		
		ItemStack SphereBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1);
		SphereBladeSoul.setTagCompound(new NBTTagCompound());
		ItemAnimaSheath.SpecialAttackType.set(SphereBladeSoul.getTagCompound(), RegisterSAs.instance.murderous.id);
		ProudSoulIngredient SASphere = new ProudSoulIngredient(SphereBladeSoul);
		
		Ingredient anvil = Ingredient.fromStacks(new ItemStack(Blocks.ANVIL, 1, OreDictionary.WILDCARD_VALUE));

		ProudSoulIngredient TrapezohedronBladeSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1));

		ItemStack brokenbamboo = new ItemStack(RegisterItems.brokenbamboo);
		
		ItemStack blade = RegisterBlades.instance.getCustomBlade("animasheath_iron");
		NBTTagCompound tag_animasheath_iron = blade.getTagCompound();
		ItemAnimaSheath.RepairCount.set(tag_animasheath_iron, 20);
		ItemAnimaSheath.KillCount.set(tag_animasheath_iron, 2000);
		BladeIngredient animasheath_iron = new BladeIngredient(blade);

		ItemStack forgerheart = new ItemStack(RegisterItems.forgerheart);

		Ingredient skull = Ingredient.fromStacks(new ItemStack(Items.SKULL, 1, OreDictionary.WILDCARD_VALUE));

		RecipeBladeResult recipe = new RecipeBladeResult(new ResourceLocation(name + "_recipe"), animasheath_edge,
					"789",
					"456",
					"123",
					'7', SASphere              , '8', anvil       , '9', Blocks.IRON_BLOCK,
					'4', TrapezohedronBladeSoul, '5', brokenbamboo, '6',"wool",
					'1', animasheath_iron      , '2', forgerheart , '3', skull             );
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
