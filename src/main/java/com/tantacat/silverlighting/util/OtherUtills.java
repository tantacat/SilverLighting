package com.tantacat.silverlighting.util;

import java.util.Random;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;

public class OtherUtills {

	public static boolean consumePlayerXP(EntityPlayer el, int descExp) 
	{
		boolean result = true;
		if(0 < descExp){
            for(;descExp > 0;descExp--){
                if(((EntityPlayer)el).experienceLevel <= 0) {result = false; break;}

                if( 0 < ((EntityPlayer)el).experienceTotal)
                    ((EntityPlayer)el).addExperience(-1);

                if(((EntityPlayer)el).experience < 0){
                    if(((EntityPlayer)el).experienceLevel <= 0){
                        ((EntityPlayer)el).experience = 0;
                    }else{
                        //el.experienceLevel--;
                        ((EntityPlayer)el).addExperienceLevel(-1);
                        ((EntityPlayer)el).experience = 1.0f - (0.9f/((EntityPlayer)el).xpBarCap());
                    }
                }
            }
        }
		return result;
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
