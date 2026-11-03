package com.tantacat.silverlighting.recipe;

import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecipeForgerHeart extends ShapedOreRecipe {

    public RecipeForgerHeart(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
        super(group, result, recipe);
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
        if (!super.matches(inv, world)) return false;
        List<Integer> SA = new ArrayList<>();
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack SASphere = inv.getStackInSlot(i).copy();
            if (!(SASphere.getItem() instanceof ItemProudSoul)) continue;

            int id = ItemSlashBlade.SpecialAttackType.get(SASphere.getTagCompound());
            if (SA.contains(id)) return false;
            SA.add(id);
        }
        return true;
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> result = NonNullList.create();
        Collections.addAll(result, super.getIngredients().toArray(new Ingredient[0]));

        List<ItemStack> items = new ArrayList<>();
        Collections.addAll(items, result.get(0).getMatchingStacks().clone());
        Collections.shuffle(items);

        for (int i = 0; i < result.size(); i++) {
            if (i == 4) continue;
            result.set(i, Ingredient.fromStacks(items.toArray(new ItemStack[0])));
            items.add(items.remove(0));
        }
        return result;
    }
}
