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

public class BladeAnimaSheathSpite {

	private BladeAnimaSheathSpite() {};
	
	public static BladeAnimaSheathSpite instance = new BladeAnimaSheathSpite();
	
	public void registerBlade()
	{
		String name = "animasheath_spite";
		ItemStack animasheath_spite = new ItemStack(RegisterItems.instance.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_spite.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_spite, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, SilverLightingMain.MODID + "." + name);
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
		
		ItemStack accessory1 = new ItemStack(Blocks.GRAVEL);
		Block block = Blocks.STONEBRICK;
		ItemStack TrapezohedronBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1);
		ItemStack brokenbamboo = new ItemStack(RegisterItems.instance.brokenbamboo);
		ItemStack accessory2 = new ItemStack(Items.LEATHER);
		
		ItemStack animasheath_stone = RegisterBlades.instance.getCustomBlade("animasheath_stone");
		NBTTagCompound tag_animasheath_stone = animasheath_stone.getTagCompound();
		ItemAnimaSheath.ProudSoul.set(tag_animasheath_stone, 10000);
		
		ItemStack bagua = new ItemStack(RegisterItems.instance.forgerheart);
		ItemStack specialItem = new ItemStack(Blocks.OBSIDIAN);
		RecipeWithNBTHelper recipe = new RecipeWithNBTHelper(name + "_recipe", 3, 3, animasheath_spite,
				new Object[]
				{
					"789",
					"456",
					"123",
					'7',SphereBladeSoul       , '8',accessory1  , '9',block,
					'4',TrapezohedronBladeSoul, '5',brokenbamboo, '6',accessory2,
					'1',animasheath_stone     , '2',bagua       , '3',specialItem
				});
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
