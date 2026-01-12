package com.tantacat.silverlighting.specialeffect;

import java.util.List;

import com.tantacat.silverlighting.registers.RegisterSEs;

import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.IRemovable;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects.State;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialEffectPneuma implements ISpecialEffect, IRemovable{

	@SubscribeEvent
	public void onSlashBladeUpdate(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		ItemStack blade = event.blade;
		EntityPlayer player = (EntityPlayer)event.entity;
		if (SpecialEffects.isEffective(player, blade, RegisterSEs.instance.Pneuma) == State.Effective)
		{
			if (blade != player.getHeldItemMainhand() && blade != player.getHeldItemOffhand()) 
				return;
			
			World world = player.world;
			Vec3d pos = player.getPositionVector();
			AxisAlignedBB bb = new AxisAlignedBB(pos.x - 16, pos.y - 16, pos.z - 16,
					pos.x + 16, pos.y + 16, pos.z + 16);
			List<Entity> entitys = world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());
			
			for (Entity n : entitys)
			{
				if (n instanceof EntityLivingBase)
					((EntityLivingBase) n).addPotionEffect(new PotionEffect(MobEffects.GLOWING, 3 * 20));
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerHurtEvent(LivingDamageEvent event)
	{	
		if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer)event.getEntityLiving();
		ItemStack mainblade = player.getHeldItemMainhand();
		ItemStack offblade = player.getHeldItemOffhand();
		if (!(mainblade.getItem() instanceof ItemSlashBlade) && !(offblade.getItem() instanceof ItemSlashBlade)) return;
		
		if (SpecialEffects.isEffective(player, mainblade, RegisterSEs.instance.Pneuma) == State.Effective ||
				SpecialEffects.isEffective(player, offblade, RegisterSEs.instance.Pneuma) == State.Effective)
		{
			String damageType = event.getSource().damageType;
			if (damageType.equals(DamageSource.IN_WALL.damageType) || damageType.equals(DamageSource.DROWN.damageType) ||
					damageType.equals(DamageSource.FALL.damageType))
				event.setCanceled(true);
		}
	}
	
	@Override
	public void register() {
		SpecialEffects.register(this);
		MinecraftForge.EVENT_BUS.register(this);
		SlashBladeHooks.EventBus.register(this);
	}

	@Override
	public int getDefaultRequiredLevel() {
		return 20;
	}

	@Override
	public String getEffectKey() {
		return "Pneuma";
	}

	@Override
	public boolean canCopy(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canRemoval(ItemStack stack) {
		return true;
	}

	
	
}
