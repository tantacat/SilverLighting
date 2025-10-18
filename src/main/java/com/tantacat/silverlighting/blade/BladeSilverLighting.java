package com.tantacat.silverlighting.blade;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterBlades;
import com.tantacat.silverlighting.registers.RegisterItems;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;
import com.tantacat.silverlighting.util.RecipeWithNBTHelper;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BladeSilverLighting {

	private BladeSilverLighting() {};
	
	public static BladeSilverLighting instance = new BladeSilverLighting();
	
	public void registerBlade()
	{
		String name = "silverlighting";
		ItemStack silverlighting = new ItemStack(RegisterItems.instance.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		silverlighting.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(silverlighting, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 60);
		ItemAnimaSheath.ModelName.set(tag, "silver/silverlighting");
		ItemAnimaSheath.TextureName.set(tag, "silver/silverlighting");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.dedication.id);
		silverlighting.addEnchantment(Enchantments.SHARPNESS, 3);
		DamageProfileHelper.addDamageProfile(silverlighting, new DamageProfile(name, 2.0f, 0, 1.0f, 0));

		
		RegisterBlades.instance.registerCustomItemStack(name, silverlighting);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "silverlighting";
		ItemStack silverlighting = RegisterBlades.instance.getCustomBlade(name);
		ItemStack IngotBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1);
		ItemStack brokenbamboo = new ItemStack(RegisterItems.instance.brokenbamboo);
		ItemStack SphereBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1);
		
		ItemStack DYE_RED = new ItemStack(Items.DYE);
		DYE_RED.getItem().setDamage(DYE_RED, 1);
		
		RecipeWithNBTHelper recipe1 = new RecipeWithNBTHelper(name + "_recipe1", 3, 3, silverlighting,
			new Object[]
			{
				"789",
				"456",
				"123",
				'7',Blocks.LAPIS_BLOCK  , '8',Items.STRING  , '9',brokenbamboo,
				'4',IngotBladeSoul      , '5',brokenbamboo  , '6',DYE_RED,
				'1',SlashBlade.wrapBlade, '2',IngotBladeSoul, '3',SphereBladeSoul
			});
		SlashBlade.addRecipe(name + "_recipe1", recipe1);
		
	//--------------------------------------------------------------------------------------------------------------------------------
		
		ItemStack ProudSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1);
		
		ItemStack sword = SlashBlade.findItemStack("sakura", "shinai", 1);
		if (sword.isEmpty())
			sword = new ItemStack(Items.WOODEN_SWORD);
		
		ItemStack animasheath = RegisterBlades.instance.getCustomBlade("animasheath");//already copy
		
		ItemStack bamboo = SlashBlade.findItemStack("sakura", "bamboo", 1);
		if (bamboo.isEmpty())
			bamboo = new ItemStack(Items.REEDS);
		
		RecipeWithNBTHelper recipe2 = new RecipeWithNBTHelper(name + "_recipe2", 3, 3, silverlighting,
			new Object[]
			{
				"789",
				"456",
				"123",
				'7',ItemStack.EMPTY, '8',ProudSoul, '9',bamboo,
				'4',ProudSoul      , '5',sword    , '6',ProudSoul,	
				'1',animasheath    , '2',ProudSoul, '3',ItemStack.EMPTY 	
			});
		SlashBlade.addRecipe(name + "_recipe2", recipe2);
	}
}
