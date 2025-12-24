package com.tantacat.silverlighting.specialeffect;

import java.lang.reflect.Method;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterSEs;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.IRemovable;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects.State;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialEffectSpellWeak implements ISpecialEffect, IRemovable{

	@SubscribeEvent
	public void onSlashBladeUpdate(SlashBladeEvent.OnUpdateEvent event) throws Exception
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		ItemStack blade = event.blade;
		EntityPlayer player = (EntityPlayer)event.entity;
		
		if (player.world.isRemote) return;
		
		if (ItemSlashBlade.getSpecialEffect(blade).hasKey(getEffectKey()))
		{
			NBTTagCompound tag_blade = blade.getTagCompound();		
			if (blade == player.getHeldItemMainhand() || blade == player.getHeldItemOffhand())
			{
				if (player.getRNG().nextFloat() < 0.01)
				{
					NBTTagList enchants = blade.getEnchantmentTagList();
					if (enchants.tagCount() != 0)
					{
						int lost_i = player.getRNG().nextInt(enchants.tagCount());
						enchants.removeTag(lost_i);
						blade.getTagCompound().setTag("ench", enchants);
					}
				}
			}
			else
			{
				if (ItemAnimaSheath.ProudSoul.tryAdd(tag_blade, -1, false));
				else if (ItemAnimaSheath.KillCount.tryAdd(tag_blade, -1, false));
				else if (ItemAnimaSheath.RepairCount.tryAdd(tag_blade, -1, false));
				else
				{
					Method damageItem = blade.getItem().getClass().getMethod("damageItem", ItemStack.class, int.class, EntityLivingBase.class);
					damageItem.invoke(blade.getItem(), blade, 1, player);
				}
			}
		}
	}
	
	@Override
	public boolean canCopy(ItemStack stack) {
		
		boolean result = false;
		NBTTagCompound stackTag = stack.getTagCompound();
		if (!stackTag.hasUniqueId("Owner")) return false;
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(stackTag.getUniqueId("Owner"));
		
		if (player == null) return false;
		
		if (!(SpecialEffects.isEffective(player, stack, RegisterSEs.instance.SpellWeak) == State.Effective)) 
			return false;
		
		ItemStack off = player.getHeldItemOffhand();
		for (Enchantment n : EnchantmentHelper.getEnchantments(off).keySet())
		{
			if (EnchantmentHelper.getEnchantments(off).get(n).intValue() >= 5)
			{
				result = true;
				break;
			}
		}
		
		return result;
	}

	@Override
	public boolean canRemoval(ItemStack stack) {
		
		boolean result = false;
		NBTTagCompound stackTag = stack.getTagCompound();
		if (!stackTag.hasUniqueId("Owner")) return false;
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(stackTag.getUniqueId("Owner"));
		
		if (player == null) return false;
		
		if (!(SpecialEffects.isEffective(player, stack, RegisterSEs.instance.SpellWeak) == State.Effective)) 
			return false;
		
		ItemStack off = player.getHeldItemOffhand();
		for (Enchantment n : EnchantmentHelper.getEnchantments(off).keySet())
		{
			if (EnchantmentHelper.getEnchantments(off).get(n).intValue() < 5)
			{
				result = true;
				break;
			}
		}
		
		if (result)
		{
			server.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					ItemAnimaSheath.getSpecialEffect(stack).removeTag("SpellWeak");
                    player.getHeldItemOffhand().shrink(1);
                    player.addExperienceLevel(-30);
                   
                    NBTTagCompound bladetag = stack.getTagCompound();
                    if (!bladetag.hasKey("SL.Gift"))
                    	bladetag.setInteger("SL.Gift", 1);
                    else
                    	bladetag.setInteger("SL.Gift", bladetag.getInteger("SL.Gift") + 10);
				}
			});
		}
		
		return result;
	}

	@Override
	public void register() {
		SpecialEffects.register(this);//未执行这一句时，canCopy和canRemoval实际不生效
		SlashBladeHooks.EventBus.register(this);
	}

	@Override
	public int getDefaultRequiredLevel() {
		return 30;
	}

	@Override
	public String getEffectKey() {
		return "SpellWeak";
	}
	
}
