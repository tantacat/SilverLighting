package com.tantacat.silverlighting.specialeffect;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterSEs;

import mods.flammpfeil.slashblade.specialeffect.IRemovable;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects.State;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialEffectSoulForge implements ISpecialEffect, IRemovable {
	
	@SubscribeEvent
	public void onSlashbladeUpdate(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		ItemStack blade = event.blade;
		EntityPlayer player = (EntityPlayer)event.entity;
		if (SpecialEffects.isEffective(player, blade, RegisterSEs.instance.SoulForge) == State.Effective)
		{
			if (blade == player.getHeldItemMainhand() || blade == player.getHeldItemOffhand()) 
				return;
			NBTTagCompound tag = blade.getTagCompound();
			int damage = blade.getItemDamage();
			if (damage > 0 && ItemAnimaSheath.ProudSoul.get(tag) >= 2)
			{
				blade.setItemDamage(damage - 1);
				ItemAnimaSheath.ProudSoul.add(tag, -2);
			}
		}
	}
	
	@Override
	public boolean canCopy(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canRemoval(ItemStack stack) {
		return true;
	}

	@Override
	public void register() 
	{
		SpecialEffects.register(this);
		SlashBladeHooks.EventBus.register(this);
	}

	@Override
	public int getDefaultRequiredLevel() {
		return 15;
	}

	@Override
	public String getEffectKey() {
		return "SoulForge";
	}

}
