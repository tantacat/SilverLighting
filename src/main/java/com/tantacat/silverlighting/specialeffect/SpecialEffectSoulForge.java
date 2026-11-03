package com.tantacat.silverlighting.specialeffect;

import com.tantacat.silverlighting.registers.RegisterSEs;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.IRemovable;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects.State;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialEffectSoulForge implements ISpecialEffect, IRemovable {

	private static final String TAG = "silverlighting.soulforge";

	@SubscribeEvent
	public void onAttack(SlashBladeEvent.ImpactEffectEvent event) {
		if (!(event.user instanceof EntityPlayer)) return;
		if (event.sequence.useScabbard) return;

		EntityPlayer player = (EntityPlayer)event.user;
		ItemStack blade = event.blade;
		if (ItemSlashBlade.IsBroken.get(blade.getTagCompound())) return;
		if (SpecialEffects.isEffective(player, blade, RegisterSEs.instance.SoulForge) != State.Effective) return;
		if (blade.getTagCompound().getBoolean("Unbreakable") && !blade.getTagCompound().hasKey(TAG)) return;

		if (blade.getItemDamage() == blade.getMaxDamage()
				&& ItemSlashBlade.ProudSoul.tryAdd(blade.getTagCompound(), -2, false)) {
			blade.getTagCompound().setBoolean("Unbreakable", true);
			blade.getTagCompound().setBoolean(TAG, true);
		}
	}
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTick(SlashBladeEvent.OnUpdateEvent event) {
		if (!(event.entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.entity;
		ItemStack blade = event.blade;

		if (SpecialEffects.isEffective(player, blade, RegisterSEs.instance.SoulForge) != State.Effective) return;
		if (blade.getTagCompound().hasKey(TAG) && blade.getTagCompound().getBoolean("Unbreakable")) {
			blade.getTagCompound().removeTag(TAG);
			blade.getTagCompound().removeTag("Unbreakable");
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
