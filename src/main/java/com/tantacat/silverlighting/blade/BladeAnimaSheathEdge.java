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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BladeAnimaSheathEdge {

	private BladeAnimaSheathEdge() {};
	
	public static BladeAnimaSheathEdge instance = new BladeAnimaSheathEdge();
	
	public void registerBlade()
	{
		String name = "animasheath_edge";
		ItemStack animasheath_edge = new ItemStack(RegisterItems.instance.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_edge.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_edge, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, SilverLightingMain.MODID + "." + name);
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
		
		ItemStack accessory1 = new ItemStack(Blocks.ANVIL);
		Block block = Blocks.IRON_BLOCK;
		ItemStack TrapezohedronBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1);
		ItemStack brokenbamboo = new ItemStack(RegisterItems.instance.brokenbamboo);
		ItemStack accessory2 = new ItemStack(Blocks.WOOL);
		
		ItemStack animasheath_iron = RegisterBlades.instance.getCustomBlade("animasheath_iron");
		NBTTagCompound tag_animasheath_iron = animasheath_iron.getTagCompound();
		ItemAnimaSheath.RepairCount.set(tag_animasheath_iron, 20);
		ItemAnimaSheath.KillCount.set(tag_animasheath_iron, 2000);

		ItemStack bagua = new ItemStack(RegisterItems.instance.forgerheart);
		ItemStack specialItem = RegisterBlades.instance.getMcItemStack("skull");
		RecipeWithNBTHelper recipe = new RecipeWithNBTHelper(name + "_recipe", 3, 3, animasheath_edge,
				new Object[]
				{
					"789",
					"456",
					"123",
					'7',SphereBladeSoul       , '8',accessory1  , '9',block,
					'4',TrapezohedronBladeSoul, '5',brokenbamboo, '6',accessory2,
					'1',animasheath_iron      , '2',bagua       , '3',specialItem
				});
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
