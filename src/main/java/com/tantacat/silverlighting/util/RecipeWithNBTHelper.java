package com.tantacat.silverlighting.util;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.util.NBTHelper.EnchantMode;
import com.tantacat.silverlighting.util.NBTHelper.ItemType;

import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeWithNBTHelper extends ShapedOreRecipe
{
	public final ItemStack result;
	public ItemStack[] items;
	
	public RecipeWithNBTHelper(String name, int width, int height, ItemStack result, Object... items)
	{
		super(new ResourceLocation(SilverLightingMain.MODID, name), result, items);
		this.result = this.output;
		this.items = new ItemStack[width * height];
		int i = 0;
		for (Ingredient n : this.input)
		{
			if (n.getMatchingStacks().length == 0)
				this.items[i++] = ItemStack.EMPTY;
			else
				this.items[i++] = n.getMatchingStacks()[0];
		}
		for (int j = 0; j < this.items.length; j++)
		{
			ItemStack item = this.items[j];
			if (!item.hasTagCompound())
				item.setTagCompound(new NBTTagCompound());
		}
	}
	
	@Override
	public ItemStack getRecipeOutput() //通过Recipe得到的产物
	{
		ItemStack result = this.result.copy();
		NBTTagCompound tag_result = result.getTagCompound();
		for (ItemStack item : items)
		{
			if (!item.hasTagCompound())
				item.setTagCompound(new NBTTagCompound());
			
			if (item.getItem() instanceof ItemSlashBlade)
				tag_result = NBTHelper.instance.mergeNBTTagCompound(EnchantMode.merge, tag_result, item.getTagCompound());
		}
		result.setTagCompound(tag_result);
		return result;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() //Recipe中的物品
    {
        return super.getIngredients();
    }
	
	/*
	for SlashBlade: ItemSlashBlade, CurrentItemName, damage, kill, proud, refine, enchant
	for ProudSoul: ItemProudSoul, metadata, kill, proud, refine, enchant, SA, SE
	for Item: ItemType, metadata, kill, proud, refine, enchant
	*/
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		boolean result = true;
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack item_inv = inv.getStackInSlot(i).copy();
			ItemStack item_recipe = items[i].copy();
			
			if (!item_inv.hasTagCompound())
				item_inv.setTagCompound(new NBTTagCompound());
			
			if (item_recipe.getItem() instanceof ItemSlashBlade)
			{
				if (!(item_inv.getItem() instanceof ItemSlashBlade) ||
					item_recipe.getItemDamage() != item_inv.getItemDamage() ||
					!NBTHelper.instance.fitNBTTagCompound(ItemType.SlashBlade,
							item_recipe.getTagCompound(), item_inv.getTagCompound()))
				{
					result = false;
					break;
				}
			}
			else if (item_recipe.getItem() instanceof ItemProudSoul)
			{
				if (!(item_inv.getItem() instanceof ItemProudSoul) || 
					item_inv.getMetadata() != item_recipe.getMetadata() ||
					!NBTHelper.instance.fitNBTTagCompound(ItemType.ProudSoul, 
							item_recipe.getTagCompound(), item_inv.getTagCompound()))
				{
					result = false;
					break;
				}
			}
			else
			{
				if (!(item_inv.getItem().getClass() == item_recipe.getItem().getClass()) ||
					item_recipe.getMetadata() != 0 && item_inv.getMetadata() != item_recipe.getMetadata() ||
					!NBTHelper.instance.fitNBTTagCompound(ItemType.Other, 
							item_recipe.getTagCompound(), item_inv.getTagCompound()))
				{
					result = false;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) //通过inv得到的产物
	{
		ItemStack result = this.result.copy();
		NBTTagCompound tag_result = result.getTagCompound();
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack item = inv.getStackInSlot(i);
			
			if (!item.hasTagCompound())
				item.setTagCompound(new NBTTagCompound());
			
			if (item.getItem() instanceof ItemSlashBlade)
				tag_result = NBTHelper.instance.mergeNBTTagCompound(EnchantMode.merge, tag_result, item.getTagCompound());
		}
		result.setTagCompound(tag_result);
		return result;
	}
}
