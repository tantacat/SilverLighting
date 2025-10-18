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
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BladePureSilver {

	private BladePureSilver() {};
	
	public static BladePureSilver instance = new BladePureSilver();
	
	public void registerBlade()
	{
		String name = "puresilver";
		ItemStack puresilver = new ItemStack(RegisterItems.instance.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		puresilver.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(puresilver, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.TrueItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.IsDefaultBewitched.set(tag, true);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 120);
		ItemAnimaSheath.ModelName.set(tag, "silver/puresilver");
		ItemAnimaSheath.TextureName.set(tag, "silver/puresilver");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.hope.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, 0xF5FFFA);
		SpecialEffects.addEffect(puresilver, RegisterSEs.instance.SoulForge);
		puresilver.addEnchantment(Enchantments.UNBREAKING, 3);
		DamageProfileHelper.addDamageProfile(puresilver, new DamageProfile(name, 2.2f, 0, 1.0f, 0));
		BoostProfileHelper.addBoostProfile(puresilver, RegisterBoosts.instance.Pureclear.profile);
		
		RegisterBlades.instance.registerCustomItemStack(name, puresilver);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "puresilver";
		ItemStack puresilver = RegisterBlades.instance.getCustomBlade(name);
		ItemStack brokenbamboo = new ItemStack(RegisterItems.instance.brokenbamboo);
		
		ItemStack SphereProudSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1);
		SphereProudSoul.setTagCompound(new NBTTagCompound());
		ItemAnimaSheath.SpecialAttackType.set(SphereProudSoul.getTagCompound(), RegisterSAs.instance.dedication.id);
		
		ItemStack silverlighting = RegisterBlades.instance.getCustomBlade("silverlighting");
		NBTTagCompound tag_silverlighting = silverlighting.getTagCompound();
		ItemAnimaSheath.ProudSoul.set(tag_silverlighting, 3000);
		ItemAnimaSheath.RepairCount.set(tag_silverlighting, 20);
		
		ItemStack CrystalProudSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.CrystalBladeSoulStr, 1);
		
		RecipeWithNBTHelper recipe = new RecipeWithNBTHelper(name + "_recipe", 3, 3, puresilver,
			new Object[]
			{
				"789",
				"456",
				"123",
				'7',Blocks.GOLD_BLOCK, '8',brokenbamboo    , '9',brokenbamboo,
				'4',Items.NETHER_STAR, '5',SphereProudSoul , '6',Items.DIAMOND,	
				'1',silverlighting   , '2',CrystalProudSoul, '3',Blocks.GLASS 			
			});
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
}
