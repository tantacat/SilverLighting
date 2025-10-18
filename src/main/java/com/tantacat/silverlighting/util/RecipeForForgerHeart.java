package com.tantacat.silverlighting.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterItems;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipeForForgerHeart extends RecipeWithNBTHelper{
	
	public static RecipeForForgerHeart instance = new RecipeForForgerHeart();
	
	private RecipeForForgerHeart()
	{
		super("forgerheart_recipe", 3, 3, new ItemStack(RegisterItems.instance.forgerheart), getRecipe());
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() //Recipe中的物品
    {
		RecipeForForgerHeart recipe = new RecipeForForgerHeart();
		return recipe.input;
    }

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		List<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack inv_item = inv.getStackInSlot(i).copy();
			ItemStack recipe_item = this.input.get(i).getMatchingStacks()[0].copy();
			if (recipe_item.getItem() != inv_item.getItem())
				return false;
			else if (recipe_item.getMetadata() != inv_item.getMetadata())
				return false;
			else if (recipe_item.getItem() == Items.NETHER_STAR)
				continue;
			else if (!inv_item.hasTagCompound())
				return false;
			else if (!ItemAnimaSheath.SpecialAttackType.exists(inv_item.getTagCompound()))
				return false;
			else if (!ids.contains(ItemAnimaSheath.SpecialAttackType.get(inv_item.getTagCompound())))
				ids.add(ItemAnimaSheath.SpecialAttackType.get(inv_item.getTagCompound()));
			else
				return false;
		}
		return true;
	}

	static List<Integer> ids;
	private static Object[] getRecipe()
	{
		ids = new ArrayList<Integer>();
		ItemStack NetherStar = new ItemStack(Items.NETHER_STAR);
		return new Object[]
				{
						"789",
						"456",
						"123",
						'7',RandomSoul(), '8',RandomSoul(), '9',RandomSoul(),
						'4',RandomSoul(), '5',NetherStar  , '6',RandomSoul(),	
						'1',RandomSoul(), '2',RandomSoul(), '3',RandomSoul()		
				};
	}
	private static Object RandomSoul()
	{
		Random random = new Random();
		ItemStack SphereBladeSoul1 = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1);
		SphereBladeSoul1.setTagCompound(new NBTTagCompound());
		Object[] ids_ = ItemSlashBlade.specialAttacks.keySet().toArray();
		int id = (int)ids_[random.nextInt(ids_.length)];
		while (ids.contains(id))
			id = (int)ids_[random.nextInt(ids_.length)];
		
		ids.add(id);
		ItemAnimaSheath.SpecialAttackType.set(SphereBladeSoul1.getTagCompound(), id);
		return SphereBladeSoul1;
	}
	
}
