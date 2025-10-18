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
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BladeAnimaSheathWood {

	private BladeAnimaSheathWood() {};
	
	public static BladeAnimaSheathWood instance = new BladeAnimaSheathWood();
	
	public void registerBlade()
	{
		String name = "animasheath_wood";
		ItemStack animasheath_wood = new ItemStack(RegisterItems.instance.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		animasheath_wood.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(animasheath_wood, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, SilverLightingMain.MODID + "." + name);
		ItemAnimaSheath.CustomMaxDamage.set(innertag, 40);
		ItemAnimaSheath.ModelName.set(tag, "minecraft/drape");
		ItemAnimaSheath.TextureName.set(tag, "minecraft/wood");
		ItemAnimaSheath.SpecialAttackType.set(tag, RegisterSAs.instance.leafswim.id);
		ItemAnimaSheath.SummonedSwordColor.set(tag, 0xEFC381);
		animasheath_wood.addEnchantment(Enchantments.FEATHER_FALLING, 2);
		DamageProfileHelper.addDamageProfile(animasheath_wood, new DamageProfile(name, 1.0f, 0, 1.0f, 0));
		
		RegisterBlades.instance.registerCustomItemStack(name, animasheath_wood);
		RegisterBlades.instance.SlNamedBlades.add(name);
	}
	
	public void registerRecipe()
	{
		String name = "animasheath_wood";
		ItemStack animasheath_wood = RegisterBlades.instance.getCustomBlade(name);
		ItemStack ProudSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1);
		ItemStack brokenbamboo = new ItemStack(RegisterItems.instance.brokenbamboo);
		
		ItemStack balkon_wood = new ItemStack(SlashBlade.wrapBlade);
		balkon_wood.setTagCompound(new NBTTagCompound());
		SlashBlade.wrapBlade.setWrapItem(balkon_wood, new ItemStack(Items.WOODEN_SWORD));
		NBTTagCompound tag_balkon_wood = balkon_wood.getTagCompound();
		ItemAnimaSheath.CurrentItemName.set(tag_balkon_wood, "wrap.reforged.wooden_katana");
		ItemAnimaSheath.TextureName.set(tag_balkon_wood, "BalkonWood");
		ItemAnimaSheath.ProudSoul.set(tag_balkon_wood, 1000);
		ItemAnimaSheath.RepairCount.set(tag_balkon_wood, 10);
		
		ItemStack IngotBladeSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1);
		IngotBladeSoul.addEnchantment(Enchantments.FEATHER_FALLING, 1);
		
		ItemStack animasheath = RegisterBlades.instance.getCustomBlade("animasheath");
		NBTTagCompound tag_animasheath = animasheath.getTagCompound();
		ItemAnimaSheath.ProudSoul.set(tag_animasheath, 2000);
		ItemAnimaSheath.KillCount.set(tag_animasheath, 200);
		RecipeWithNBTHelper recipe = new RecipeWithNBTHelper(name + "_recipe", 3, 3, animasheath_wood,
				new Object[]
				{
					"789",
					"456",
					"123",
					'7',ItemStack.EMPTY, '8',brokenbamboo, '9',IngotBladeSoul,
					'4',ProudSoul      , '5',balkon_wood , '6',brokenbamboo,	
					'1',animasheath    , '2',ProudSoul   , '3',ItemStack.EMPTY		
				});
		SlashBlade.addRecipe(name + "_recipe", recipe);
	}
	
}
