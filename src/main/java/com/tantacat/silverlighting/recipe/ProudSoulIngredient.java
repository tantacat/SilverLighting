package com.tantacat.silverlighting.recipe;

import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Map;

public class ProudSoulIngredient extends Ingredient {

    public ProudSoulIngredient (ItemStack... p_i47503_1_) {
        super(p_i47503_1_);
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if (stack == null) return false;
        if (!isProudSoul(stack)) return false;

        for (ItemStack target : this.getMatchingStacks()) {
            if (!isProudSoul(target)) continue;

            ItemStack targetCopy = getFixedProudSoul(target);
            ItemStack stackCopy = getFixedProudSoul(stack);

            if (!meetEnchant(targetCopy, stackCopy)) continue;

            ItemSlashBlade.getItemTagCompound(targetCopy).removeTag("ench");
            ItemSlashBlade.getItemTagCompound(stackCopy).removeTag("ench");
            if (ItemHandlerHelper.canItemStacksStack(targetCopy, stackCopy))
                return true;

            /*catSever?
            if (targetCopy.getMetadata() == stackCopy.getMetadata() && meetEnchant(targetCopy, stackCopy))
                return true;*/
        }

        return false;
    }

    boolean isProudSoul(ItemStack stack) {
        return stack.getItem() instanceof ItemProudSoul;
    }

    boolean meetEnchant(ItemStack target, ItemStack stack) {
        Map<Enchantment, Integer> targetEnchent = EnchantmentHelper.getEnchantments(target);
        for (Map.Entry<Enchantment, Integer> ench : targetEnchent.entrySet()) {
            if (ench.getValue() > EnchantmentHelper.getEnchantmentLevel(ench.getKey(), stack))
                return false;
        }
        return true;
    }

    ItemStack getFixedProudSoul(ItemStack stack) {
        ItemStack copy = stack.copy();
        NBTTagCompound nbt = copy.getTagCompound();
        if (nbt == null) return copy;

        if (nbt.hasKey("SB.SEffect") && nbt.getTag("SB.SEffect").hasNoTags())
            nbt.removeTag("SB.SEffect");
        if (nbt.hasKey("RepairCost"))
            nbt.removeTag("RepairCost");
        if (nbt.hasNoTags())
            copy.setTagCompound(null);

        return copy;
    }
}
