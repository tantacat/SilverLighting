package com.tantacat.silverlighting.blade;

import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.recipe.BladeIngredient;
import com.tantacat.silverlighting.recipe.ProudSoulIngredient;
import com.tantacat.silverlighting.recipe.RecipeBladeResult;
import com.tantacat.silverlighting.registers.RegisterBlades;
import com.tantacat.silverlighting.registers.RegisterItems;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class BladeSilverLighting {

	private BladeSilverLighting() {};
	
	public static BladeSilverLighting instance = new BladeSilverLighting();
	
	public void registerBlade()
	{
		String name = "silverlighting";
		ItemStack silverlighting = new ItemStack(RegisterItems.sl_blade, 1, 0);
		NBTTagCompound tag = new NBTTagCompound(); 
		silverlighting.setTagCompound(tag);
		ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
		NBTTagCompound innertag = new NBTTagCompound();
		innerblade.setTagCompound(innertag);
		
		SlashBlade.wrapBlade.setWrapItem(silverlighting, innerblade);
		ItemAnimaSheath.CurrentItemName.set(tag, ModMain.MODID + "." + name);
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

		ProudSoulIngredient IngotBladeSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 1));

		ItemStack brokenbamboo = new ItemStack(RegisterItems.brokenbamboo);

		ProudSoulIngredient SphereBladeSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TrapezohedronBladeSoulStr, 1));

		BladeIngredient sheath = new BladeIngredient(new ItemStack(SlashBlade.wrapBlade));

		RecipeBladeResult recipe1 = new RecipeBladeResult(new ResourceLocation(ModMain.MODID, name + "_recipe1"), silverlighting,
                "789",
                "456",
                "123",
                '7', Blocks.LAPIS_BLOCK, '8',"string"       , '9', brokenbamboo,
                '4', IngotBladeSoul    , '5', brokenbamboo  , '6',"dyeRed",
                '1', sheath            , '2', IngotBladeSoul, '3', SphereBladeSoul);
		SlashBlade.addRecipe(name + "_recipe1", recipe1);
		
	//--------------------------------------------------------------------------------------------------------------------------------
		
		ProudSoulIngredient ProudSoul = new ProudSoulIngredient(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1));
		
		ItemStack sword = SlashBlade.findItemStack("sakura", "shinai", 1);
		if (sword.isEmpty())
			sword = new ItemStack(Items.WOODEN_SWORD);
		
		BladeIngredient animasheath = new BladeIngredient(RegisterBlades.instance.getCustomBlade("animasheath"));//already copy

		NonNullList<ItemStack> bambooList = NonNullList.create();
		bambooList.addAll(OreDictionary.getOres("bamboo"));
		bambooList.addAll(OreDictionary.getOres("cropBamboo"));
		if (bambooList.isEmpty()) {
			bambooList.add(new ItemStack(Items.REEDS));
		}
		Ingredient bamboo = Ingredient.fromStacks(bambooList.toArray(new ItemStack[0]));

		RecipeBladeResult recipe2 = new RecipeBladeResult(new ResourceLocation(ModMain.MODID, name + "_recipe2"), silverlighting,
                "789",
                "456",
                "123",
                '7', ItemStack.EMPTY, '8', ProudSoul, '9', bamboo,
                '4', ProudSoul      , '5', sword    , '6', ProudSoul,
                '1', animasheath    , '2', ProudSoul, '3', ItemStack.EMPTY);
		SlashBlade.addRecipe(name + "_recipe2", recipe2);
	}
}
