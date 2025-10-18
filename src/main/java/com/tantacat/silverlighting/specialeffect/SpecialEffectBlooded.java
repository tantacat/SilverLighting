package com.tantacat.silverlighting.specialeffect;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterSEs;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.IRemovable;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects.State;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialEffectBlooded implements ISpecialEffect, IRemovable{

	@SubscribeEvent
	public void onSlashBladeUpdate(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		ItemStack blade = event.blade;
		EntityPlayer player = (EntityPlayer)event.entity;
		
		if (!(blade.getItem() instanceof ItemAnimaSheath)) return;
		
		if (SpecialEffects.isEffective(player, blade, RegisterSEs.instance.Blooded) == State.Effective)
		{
			int killcount = ItemSlashBlade.KillCount.get(blade.getTagCompound(), 0);
			float extra = ((int)(killcount / 1000)) * 0.5f;
			DamageProfileHelper.replaceDamageProfile(blade, getEffectKey(),
						new DamageProfile(getEffectKey(), 0, extra, 0.5f, 0));
		}
		else
			DamageProfileHelper.removeDamageProfile(blade, getEffectKey());
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
	public void register() {
		SpecialEffects.register(this);
		SlashBladeHooks.EventBus.register(this);
	}

	@Override
	public int getDefaultRequiredLevel() {
		return 35;
	}

	@Override
	public String getEffectKey() {
		return "Blooded";
	}

	
	
}
