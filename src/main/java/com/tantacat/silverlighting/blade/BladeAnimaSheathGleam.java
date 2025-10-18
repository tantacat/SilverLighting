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

public class BladeAnimaSheathGleam {

	private BladeAnimaSheathGleam() {};
	
	public static BladeAnimaSheathGleam instance = new BladeAnimaSheathGleam();
	
	public void registerBlade()
	{
		String name = "animasheath_gleam";
		ItemStack animasheath_gleam = new ItemStack(RegisterItems.instance.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_gleam.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_gleam, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, SilverLightingMain.MODID + "." + name);
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
		
		ItemStack accessory1 = new ItemStack(Items.EMERALD);
		Block block = Blocks.GOLD_BLOCK;
		ItemStack TrapezohedronBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1);
		ItemStack brokenbamboo = new ItemStack(RegisterItems.instance.brokenbamboo);
		ItemStack accessory2 = new ItemStack(Items.FLINT);
		
		ItemStack animasheath_gold = RegisterBlades.instance.getCustomBlade("animasheath_gold");
		NBTTagCompound tag_animasheath_gold = animasheath_gold.getTagCompound();
		ItemAnimaSheath.RepairCount.set(tag_animasheath_gold, 10);
		ItemAnimaSheath.ProudSoul.set(tag_animasheath_gold, 4000);
		
		ItemStack bagua = new ItemStack(RegisterItems.instance.forgerheart);
		ItemStack specialItem = new ItemStack(Blocks.ENCHANTING_TABLE);
		RecipeWithNBTHelper recipe = new RecipeWithNBTHelper(name + "_recipe", 3, 3, animasheath_gleam,
				new Object[]
				{
					"789",
					"456",
					"123",
					'7',SphereBladeSoul       , '8',accessory1  , '9',block,
					'4',TrapezohedronBladeSoul, '5',brokenbamboo, '6',accessory2,
					'1',animasheath_gold      , '2',bagua       , '3',specialItem
				});
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
