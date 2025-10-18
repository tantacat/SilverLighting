package com.tantacat.silverlighting.specialeffect;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterSEs;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;
import com.tantacat.silverlighting.util.OtherUtills;

import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.specialeffect.IRemovable;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects.State;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialEffectPseudoForm implements ISpecialEffect, IRemovable{

	@SubscribeEvent
	public void onImpact(SlashBladeEvent.ImpactEffectEvent event)
	{
		if (!(event.user instanceof EntityPlayer)) return;
		ItemStack blade = event.blade;
		EntityPlayer player = (EntityPlayer)event.user;
		EntityLivingBase target = event.target;
		
		if (SpecialEffects.isEffective(player, blade, RegisterSEs.instance.PseudoForm) == State.Effective && OtherUtills.isDirtyDead(target))
		{
			if (blade.getItem() instanceof ItemSlashBladeWrapper)
			{
				ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
				NBTTagCompound innertag = new NBTTagCompound();
				innerblade.setTagCompound(innertag);
				int maxDamage = blade.getMaxDamage();
				ItemAnimaSheath.CustomMaxDamage.set(innertag, maxDamage + 1);
				SlashBlade.wrapBlade.setWrapItem(blade, innerblade);
			}
			else
			{
				int maxDamage = ItemAnimaSheath.CustomMaxDamage.get(blade.getTagCompound());
				ItemAnimaSheath.CustomMaxDamage.set(blade.getTagCompound(), maxDamage + 1);
			}
		}
		
		if (blade.getItem() instanceof ItemAnimaSheath) 
		{
			if (SpecialEffects.isEffective(player, blade, RegisterSEs.instance.PseudoForm) == State.Effective)
			{
				int maxDamage = blade.getMaxDamage();
				float fit = (int)maxDamage / 100;
				DamageProfileHelper.replaceDamageProfile(blade, getEffectKey(),
							new DamageProfile(getEffectKey(), 0, 0, 0, fit));
			}
			else 
				DamageProfileHelper.removeDamageProfile(blade, getEffectKey());
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
	public void register() {
		SpecialEffects.register(this);
		SlashBladeHooks.EventBus.register(this);
	}

	@Override
	public int getDefaultRequiredLevel() {
		return 50;
	}

	@Override
	public String getEffectKey() {
		return "PseudoForm";
	}

	
	
}
