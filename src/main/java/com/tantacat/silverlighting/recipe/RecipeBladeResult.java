package com.tantacat.silverlighting.recipe;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

public class RecipeBladeResult extends ShapedOreRecipe {
    public RecipeBladeResult(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
        super(group, result, recipe);
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        ItemStack result = this.output.copy();
        for (Ingredient i : this.input) {
            if (i instanceof BladeIngredient) {
                RecipeHelper.inheritBladeData(result, i.getMatchingStacks()[0]);
            }
        }
        return result;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1) {
        ItemStack result = this.output.copy();
        for (int i = 0; i < var1.getSizeInventory(); i++) {
            ItemStack item = var1.getStackInSlot(i);
            if (item.getItem() instanceof ItemSlashBlade) {
                RecipeHelper.inheritBladeData(result, item);
            }
        }
        return result;
    }
}
