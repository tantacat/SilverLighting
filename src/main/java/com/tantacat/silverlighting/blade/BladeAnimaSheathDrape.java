package com.tantacat.silverlighting.blade;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterBlades;
import com.tantacat.silverlighting.registers.RegisterBoosts;
import com.tantacat.silverlighting.registers.RegisterItems;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.registers.RegisterSEs;
import com.tantacat.silverlighting.util.BoostProfileHelper;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;
import com.tantacat.silverlighting.util.RecipeWithNBTHelper;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BladeAnimaSheathDrape {

	private BladeAnimaSheathDrape() {};
	
	public static BladeAnimaSheathDrape instance = new BladeAnimaSheathDrape();
	
	public void registerBlade()
	{
		String name = "animasheath_drape";
		ItemStack animasheath_drape = new ItemStack(RegisterItems.instance.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_drape.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_drape, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, SilverLightingMain.MODID + "." + name);
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
		
		ItemStack accessory1 = new ItemStack(Items.GOLD_NUGGET);
		Block block = Blocks.LEAVES;
		ItemStack TrapezohedronBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1);
		ItemStack brokenbamboo = new ItemStack(RegisterItems.instance.brokenbamboo);
		ItemStack accessory2 = accessory1;
		
		ItemStack animasheath_wood = RegisterBlades.instance.getCustomBlade("animasheath_wood");
		NBTTagCompound tag_animasheath_wood = animasheath_wood.getTagCompound();
		ItemAnimaSheath.RepairCount.set(tag_animasheath_wood, 10);
		ItemAnimaSheath.ProudSoul.set(tag_animasheath_wood, 2000);
		ItemAnimaSheath.KillCount.set(tag_animasheath_wood, 500);
		
		ItemStack bagua = new ItemStack(RegisterItems.instance.forgerheart);
		ItemStack specialItem = new ItemStack(Items.FEATHER);
		RecipeWithNBTHelper recipe = new RecipeWithNBTHelper(name + "_recipe", 3, 3, animasheath_drape,
				new Object[]
				{
					"789",
					"456",
					"123",
					'7',SphereBladeSoul       , '8',accessory1  , '9',block,
					'4',TrapezohedronBladeSoul, '5',brokenbamboo, '6',accessory2,
					'1',animasheath_wood      , '2',bagua       , '3',specialItem
				});
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
