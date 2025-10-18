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

public class BladeAnimaSheathPhos {

	private BladeAnimaSheathPhos() {};
	
	public static BladeAnimaSheathPhos instance = new BladeAnimaSheathPhos();
	
	public void registerBlade()
	{
		String name = "animasheath_phos";
		ItemStack animasheath_phos = new ItemStack(RegisterItems.instance.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_phos.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_phos, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, SilverLightingMain.MODID + "." + name);
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
		
		ItemStack accessory1 = new ItemStack(Items.END_CRYSTAL);
		Block block = Blocks.DIAMOND_BLOCK;
		ItemStack TrapezohedronBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1);
		ItemStack brokenbamboo = new ItemStack(RegisterItems.instance.brokenbamboo);
		ItemStack accessory2 = new ItemStack(Blocks.DIAMOND_ORE);
		ItemStack animasheath_diamond = RegisterBlades.instance.getCustomBlade("animasheath_diamond");
		ItemStack bagua = new ItemStack(RegisterItems.instance.forgerheart);
		ItemStack specialItem = new ItemStack(Items.DIAMOND_SWORD);
		specialItem.setItemDamage(specialItem.getMaxDamage());
		RecipeWithNBTHelper recipe = new RecipeWithNBTHelper(name + "_recipe", 3, 3, animasheath_phos,
				new Object[]
				{
					"789",
					"456",
					"123",
					'7',SphereBladeSoul       , '8',accessory1  , '9',block,
					'4',TrapezohedronBladeSoul, '5',brokenbamboo, '6',accessory2,
					'1',animasheath_diamond   , '2',bagua       , '3',specialItem
				});
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
