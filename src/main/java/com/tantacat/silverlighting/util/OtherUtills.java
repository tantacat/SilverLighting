package com.tantacat.silverlighting.util;

import java.util.Random;

import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import net.minecraft.advancements.Advancement;
import net.minecraft.enchantment.Enchantment;
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

public class OtherUtills {
		
	public static void removePlayerXP(EntityPlayer player, int expT)
	{
		int exp = player.experienceTotal - expT;
		
		player.setScore(0);
		player.experienceTotal = 0;
		player.experienceLevel = 0;
		player.experience = 0.0f;
		
		if (exp > 0)
			player.addExperience(exp);
		
	}
	
	public static ItemStack addEnchantment(ItemStack item, Enchantment ench, int gift_level, boolean force)
	{
		gift_level /= 10;
		
		NBTTagCompound enchantments = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		NBTTagCompound enchantment = new NBTTagCompound();
		enchantment.setShort("id", (short)Enchantment.getEnchantmentID(ench));
		enchantment.setShort("lvl", (short)1);
		list.appendTag(enchantment);
		enchantments.setTag("ench", list);
		
		ItemStack mainitem = ItemStack.EMPTY;
		if (item.getCount() == 1)
		{
			mainitem = item;
			if (!mainitem.hasTagCompound())
				mainitem.setTagCompound(new NBTTagCompound());
			if (force)
				NBTHelper.instance.addEnchantment(mainitem.getTagCompound(), enchantments);
			else
				NBTHelper.instance.enhanceEnchantment(gift_level, mainitem.getTagCompound(), enchantments);
			return ItemStack.EMPTY;
		}
		else
		{
			mainitem = item.splitStack(1);
			if (!mainitem.hasTagCompound())
				mainitem.setTagCompound(new NBTTagCompound());
			if (force)
				NBTHelper.instance.addEnchantment(mainitem.getTagCompound(), enchantments);
			else
				NBTHelper.instance.enhanceEnchantment(gift_level, mainitem.getTagCompound(), enchantments);
			return mainitem;
		}
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
	
	public static int getSlotFor(EntityPlayer player, ItemStack stack)
	{
		for (int i = 0; i < player.inventory.mainInventory.size(); ++i)
        {
			ItemStack item = player.inventory.mainInventory.get(i);
            if (item == stack)
            	return i;
        }

        return -1;
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
	
}
