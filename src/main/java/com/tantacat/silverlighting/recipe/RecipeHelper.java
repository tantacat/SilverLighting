package com.tantacat.silverlighting.recipe;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

public class RecipeHelper {

    public static NBTTagCompound inheritBladeData(ItemStack target, final ItemStack source) {
        if (!(target.getItem() instanceof ItemSlashBlade)) return source.getTagCompound();
        if (!(source.getItem() instanceof ItemSlashBlade)) return source.getTagCompound();

        NBTTagCompound targetTag = ItemSlashBlade.getItemTagCompound(target);
        NBTTagCompound sourceTag = ItemSlashBlade.getItemTagCompound(source);

        ItemSlashBlade.KillCount.add(targetTag, ItemSlashBlade.KillCount.get(sourceTag));
        ItemSlashBlade.ProudSoul.add(targetTag, ItemSlashBlade.ProudSoul.get(sourceTag));
        ItemSlashBlade.RepairCount.add(targetTag, ItemSlashBlade.RepairCount.get(sourceTag));

        Map<Enchantment, Integer> sourceEnchant = EnchantmentHelper.getEnchantments(source);
        Map<Enchantment, Integer> targetEnchant = EnchantmentHelper.getEnchantments(target);
        for (Map.Entry<Enchantment, Integer> ench : sourceEnchant.entrySet()) {
            if (ench.getValue() > targetEnchant.getOrDefault(ench.getKey(), 0)) {
                targetEnchant.put(ench.getKey(), ench.getValue());
            }
        }
        EnchantmentHelper.setEnchantments(targetEnchant, target);

        if (sourceTag.hasUniqueId("Owner")) {
            targetTag.setUniqueId("Owner", sourceTag.getUniqueId("Owner"));
        }
        if(sourceTag.hasKey(ItemSlashBlade.adjustXStr)) {
            targetTag.setFloat(ItemSlashBlade.adjustXStr,sourceTag.getFloat(ItemSlashBlade.adjustXStr));
        }
        if(sourceTag.hasKey(ItemSlashBlade.adjustYStr)) {
            targetTag.setFloat(ItemSlashBlade.adjustYStr,sourceTag.getFloat(ItemSlashBlade.adjustYStr));
        }
        if(sourceTag.hasKey(ItemSlashBlade.adjustZStr)) {
            targetTag.setFloat(ItemSlashBlade.adjustZStr,sourceTag.getFloat(ItemSlashBlade.adjustZStr));
        }

        return targetTag;
    }
}
