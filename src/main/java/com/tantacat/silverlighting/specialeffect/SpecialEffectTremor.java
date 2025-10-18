package com.tantacat.silverlighting.specialeffect;

import java.util.function.Consumer;

import com.tantacat.silverlighting.registers.RegisterSEs;

import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.IRemovable;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects.State;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialEffectTremor implements ISpecialEffect, IRemovable{

	@SubscribeEvent
	public void onImpactEffect(SlashBladeEvent.ImpactEffectEvent event)
	{
		if (!(event.user instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.user;
		ItemStack blade = event.blade;
		EntityLivingBase target = event.target;
		
		if (SpecialEffects.isEffective(player, blade, RegisterSEs.instance.Tremor) != State.Effective) return;
		
		if (player.getRNG().nextFloat() <= 0.25)
		{
			StunManager.setStun(target, 4 * 20);
			
			final float damage = ItemSlashBlade.AttackAmplifier.get(blade.getTagCompound(), 0) + 
					ItemSlashBlade.RepairCount.get(blade.getTagCompound(), 0);
			Iterable<ItemStack> a =  target.getArmorInventoryList();
			a.forEach(new Consumer<ItemStack>() {
				@Override
				public void accept(ItemStack armor) {
					if (armor.getItem() instanceof ItemArmor)
						armor.damageItem((int)damage, target);
				}
			});
				
			if (!(target instanceof EntityPlayer) && !target.getEntityData().hasKey("Tremor"))
			{
				IAttributeInstance armor = target.getEntityAttribute(SharedMonsterAttributes.ARMOR);
				double num = armor.getAttributeValue() * 0.3d;
				AttributeModifier reduce = new AttributeModifier(getEffectKey(), -num, 0);
				armor.applyModifier(reduce);
				target.getEntityData().setBoolean("Tremor", true);
			}
			
			if (player.world.isRemote)
			{
				for (int i = 0; i < 5; i++) {
					target.world.spawnParticle(EnumParticleTypes.CRIT, 
						target.posX + (target.world.rand.nextDouble() - 0.5) * target.width,
						target.posY + target.world.rand.nextDouble() * target.height,
		                target.posZ + (target.world.rand.nextDouble() - 0.5) * target.width,
		                0, 0, 0);
		        }
				
				target.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F, 0.5F);
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
		return "Tremor";
	}

	
	
}
