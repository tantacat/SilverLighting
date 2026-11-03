package com.tantacat.silverlighting.recipe;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RecipeReplaceSheath extends RecipeBladeResult{
    public RecipeReplaceSheath(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
        super(group, result, recipe);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> result = super.getRemainingItems(inv);
        result.set(4, new ItemStack(SlashBlade.wrapBlade));
        return result;
    }
}
