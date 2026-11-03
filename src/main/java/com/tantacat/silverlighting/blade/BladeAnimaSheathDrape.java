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
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class BladeAnimaSheathDrape {

	private BladeAnimaSheathDrape() {};
	
	public static BladeAnimaSheathDrape instance = new BladeAnimaSheathDrape();
	
	public void registerBlade()
	{
		String name = "animasheath_drape";
		ItemStack animasheath_drape = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_drape.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_drape, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, ModMain.MODID + "." + name);
		ItemAnimaSheath.IsDefaultBewitched.set(tag, true);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 60);
		ItemAnimaSheath.ModelName.set(tag, "minecraft/drape");
		ItemAnimaSheath.TextureName.set(tag, "minecraft/drape");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.leafswim.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, 0xEFC381);
		SpecialEffects.addEffect(animasheath_drape, RegisterSEs.instance.Pneuma);
		animasheath_drape.addEnchantment(Enchantments.FEATHER_FALLING, 2);
		DamageProfileHelper.addDamageProfile(animasheath_drape, new DamageProfile(name, 1.0f, 0, 1.0f, 0));
		BoostProfileHelper.addBoostProfile(animasheath_drape, RegisterBoosts.instance.Sound.profile);
		
		RegisterBlades.instance.registerCustomItemStack(name, animasheath_drape);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "animasheath_drape";
		ItemStack animasheath_drape = RegisterBlades.instance.getCustomBlade(name);
		
		ItemStack SphereBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1);
		SphereBladeSoul.setTagCompound(new NBTTagCompound());
		ItemAnimaSheath.SpecialAttackType.set(SphereBladeSoul.getTagCompound(), RegisterSAs.instance.leafswim.id);
		ProudSoulIngredient SASphere = new ProudSoulIngredient(SphereBladeSoul);

		ProudSoulIngredient TrapezohedronBladeSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1));

		ItemStack brokenbamboo = new ItemStack(RegisterItems.brokenbamboo);

		ItemStack blade = RegisterBlades.instance.getCustomBlade("animasheath_wood");
		NBTTagCompound tag_animasheath_wood = blade.getTagCompound();
		ItemAnimaSheath.RepairCount.set(tag_animasheath_wood, 10);
		ItemAnimaSheath.ProudSoul.set(tag_animasheath_wood, 2000);
		ItemAnimaSheath.KillCount.set(tag_animasheath_wood, 500);
		BladeIngredient animasheath_wood = new BladeIngredient(blade);
		
		ItemStack forgerheart = new ItemStack(RegisterItems.forgerheart);
		RecipeBladeResult recipe = new RecipeBladeResult(new ResourceLocation(ModMain.MODID, name + "_recipe"), animasheath_drape,
					"789",
					"456",
					"123",
					'7', SASphere              , '8',"nuggetGold" , '9',"treeLeaves",
					'4', TrapezohedronBladeSoul, '5', brokenbamboo, '6',"nuggetGold",
					'1', animasheath_wood      , '2', forgerheart , '3',"feather"    );
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
