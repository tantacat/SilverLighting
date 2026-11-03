package com.tantacat.silverlighting.recipe;

import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Map;

public class BladeIngredient extends Ingredient {

    public BladeIngredient(ItemStack... p_i47503_1_) {
        super(p_i47503_1_);
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if (stack == null) return false;
        if (!isBlade(stack)) return false;
        if (stack.getItemDamage() != 0) return false;

        for (ItemStack target : this.getMatchingStacks()) {
            if (!isBlade(target)) continue;

            if (!isSameBlade(target, stack)) continue;
            if (!meetKillCount(target, stack)) continue;
            if (!meetProudSoul(target, stack)) continue;
            if (!meetRepairCount(target, stack)) continue;
            if (!meetEnchant(target, stack)) continue;
            return true;
        }

        return false;
    }

    boolean isBlade(ItemStack stack) {
        return stack.getItem() instanceof ItemSlashBlade;
    }

    boolean isSameBlade(ItemStack target, ItemStack stack) {
        if (target.getItem() != stack.getItem()) return false;

        if (target.getItem() instanceof ItemSlashBladeNamed) {
            return ItemSlashBladeNamed.CurrentItemName.get(target.getTagCompound())
                    .equals(ItemSlashBladeNamed.CurrentItemName.get(stack.getTagCompound()));
        }

        return true;
    }

    boolean meetKillCount(ItemStack target, ItemStack stack) {
        return ItemSlashBlade.KillCount.get(target.getTagCompound()) <= ItemSlashBlade.KillCount.get(stack.getTagCompound());
    }

    boolean meetProudSoul(ItemStack target, ItemStack stack) {
        return ItemSlashBlade.ProudSoul.get(target.getTagCompound()) <= ItemSlashBlade.ProudSoul.get(stack.getTagCompound());
    }

    boolean meetRepairCount(ItemStack target, ItemStack stack) {
        return ItemSlashBlade.RepairCount.get(target.getTagCompound()) <= ItemSlashBlade.RepairCount.get(stack.getTagCompound());
    }

    boolean meetEnchant(ItemStack target, ItemStack stack) {
        Map<Enchantment, Integer> targetEnchent = EnchantmentHelper.getEnchantments(target);
        for (Map.Entry<Enchantment, Integer> ench : targetEnchent.entrySet()) {
            if (ench.getValue() > EnchantmentHelper.getEnchantmentLevel(ench.getKey(), stack))
                return false;
        }
        return true;
    }
}
