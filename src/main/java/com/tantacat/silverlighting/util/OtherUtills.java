package com.tantacat.silverlighting.util;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.capability.BladeCapabilityProvider;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import net.minecraft.advancements.Advancement;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class OtherUtills {
		
	public static void removePlayerXP(EntityPlayer player, int expT)
	{
		int exp = getXPTotal(player) - expT;
		
		if (exp < 0) return;
		
		player.experienceTotal = 0;
		player.experienceLevel = 0;
		player.experience = 0.0f;
		
		player.addExperience(exp);
				
	}

    public static int getXPTotal(EntityPlayer player) {
        return (int) (getXPValueFromLevel(player.experienceLevel) + getXPValueToNextLevel(player.experienceLevel) * player.experience);
    }

    public static int getXPValueFromLevel(int xpLevel) {
        int val;
        if (xpLevel > 31) {
            val = (int) (4.5d * Math.pow(xpLevel, 2d) - 162.5d * xpLevel + 2220d);
        } else if (xpLevel > 16) {
            val = (int) (2.5d * Math.pow(xpLevel, 2d) - 40.5d * xpLevel + 360d);
        } else {
            val = (int) (Math.pow(xpLevel, 2d) + 6d * xpLevel);
        }
        return val;
    }

    public static int getXPValueToNextLevel(int xpLevel) {
        int val;
        if (xpLevel > 30) {
            val = 9 * xpLevel - 158;
        } else if (xpLevel > 15) {
            val = 5 * xpLevel - 38;
        } else {
            val = 2 * xpLevel + 7;
        }

        return val;
    }
	
	public static ItemStack addEnchantment(ItemStack item, Enchantment ench, int gift_level, boolean force)
	{
		gift_level /= 10;

		boolean isSingle = item.getCount() == 1;
		ItemStack singleItem = isSingle ? item : item.splitStack(1);

		Map<Enchantment, Integer> itemEnchant = EnchantmentHelper.getEnchantments(item);
		if (itemEnchant.getOrDefault(ench, 0) == 0 || force) {
			singleItem.addEnchantment(ench, 1);
		}
		else {
			itemEnchant.put(ench, Math.min(ench.getMaxLevel() + gift_level, itemEnchant.get(ench) + 1));
			EnchantmentHelper.setEnchantments(itemEnchant, singleItem);
		}

		if (isSingle) {
			return ItemStack.EMPTY;
		}
		return singleItem;
	}
	
	public static boolean isNamedBlade(ItemStack blade, String name)
	{
		String bladename = ItemSlashBladeNamed.CurrentItemName.get(blade.getTagCompound(), "");
		return name.equals(bladename);
	}
	
	public static int getSumEnchantmentLevel(ItemStack item)
	{
		int sum = 0;
		NBTTagList enchants = item.getEnchantmentTagList();
		for (NBTBase n : enchants)
		{
			NBTTagCompound enchant = (NBTTagCompound)n;
			sum += enchant.getShort("lvl");
		}
		return sum;
	}

	public static void grantAdvancement(EntityPlayerMP player, ResourceLocation loc)
	{
		if (player == null) return;
		Advancement adv = player.world.getMinecraftServer().getAdvancementManager().getAdvancement(loc);
		if (adv == null) return;
		if (player.getAdvancements().getProgress(adv).isDone()) return;
		
		String commond = String.format(
	            "advancement grant %s only %s",
	            player.getName(),
	            loc.toString());
		
        MinecraftServer server = player.world.getMinecraftServer();
		boolean commandFeedback = server.getEntityWorld().getGameRules().getBoolean("sendCommandFeedback");
        try {
            // 临时禁用命令反馈
        	server.getEntityWorld().getGameRules().setOrCreateGameRule("sendCommandFeedback", "false");
        	server.commandManager.executeCommand(server, commond);   
        } finally {
            // 恢复命令反馈设置
        	server.getEntityWorld().getGameRules().setOrCreateGameRule("sendCommandFeedback", Boolean.toString(commandFeedback));
        }
	}
	
	public static boolean isDirtyDead(EntityLivingBase entity)
	{
		return entity.getHealth() <= 0 && entity.deathTime == 0;
	}
	
	public static void damageItemIgnoreUnbreaking(ItemStack stack, int amount, EntityLivingBase entityIn)
	{
		
		Random zeroRandom = new Random() {
		    @Override
		    public int nextInt(int bound) {
		        if (bound <= 0) {
		            throw new IllegalArgumentException("bound must be positive");
		        }
		        return 0;
		    }
		    
		    @Override
		    public int nextInt() {
		        return 0;
		    }
		};
		
		if (!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).capabilities.isCreativeMode)
        {
            if (stack.isItemStackDamageable())
            {
                if (stack.attemptDamageItem(amount, zeroRandom, entityIn instanceof EntityPlayerMP ? (EntityPlayerMP)entityIn : null))
                {
                    entityIn.renderBrokenItemStack(stack);
                    stack.shrink(1);

                    if (entityIn instanceof EntityPlayer)
                    {
                        EntityPlayer entityplayer = (EntityPlayer)entityIn;
                        entityplayer.addStat(StatList.getObjectBreakStats(stack.getItem()));
                    }

                    stack.setItemDamage(0);
                }
            }
        }

	}

	public static <T> void changeBladeEnergyCap(ItemStack blade, net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing, Consumer<IEnergyStorage> consumer)
	{
		SpecialEffects.addEffect(blade, SpecialEffects.HFCustom);
		IEnergyStorage storage = blade.getCapability(BladeCapabilityProvider.ENERGY, null);
		consumer.accept(storage);
		ItemAnimaSheath.getSpecialEffect(blade).removeTag(SpecialEffects.HFCustom.getEffectKey());
	}
}
