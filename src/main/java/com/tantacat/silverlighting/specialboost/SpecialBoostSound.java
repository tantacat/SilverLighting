package com.tantacat.silverlighting.specialboost;

import java.util.List;

import com.google.common.collect.Lists;
import com.tantacat.silverlighting.util.BoostProfile;
import com.tantacat.silverlighting.util.BoostProfile.BoostType;
import com.tantacat.silverlighting.util.BoostProfileHelper;

import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialBoostSound {

	public BoostProfile profile = new BoostProfile(getId(), false, BoostType.help);
	
	@SubscribeEvent
	public void onSlashBladeUpdate(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.entity;
		ItemStack blade = event.blade;
		
		if (BoostProfileHelper.isBoostEffective(blade, getId()))
		{
			Vec3d pos = player.getPositionVector();
			AxisAlignedBB bb = new AxisAlignedBB(pos.x - 5, pos.y - 5, pos.z - 5,
					pos.x + 5, pos.y + 5, pos.z + 5);
			List<Entity> entity = Lists.newArrayList(); 
			entity.addAll(player.world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance()));		
			double velocity_player = Math.sqrt(player.motionX * player.motionX 
					+ player.motionY * player.motionY 
					+ player.motionZ * player.motionZ);
			double speed_player = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
			if (velocity_player > speed_player)
			{
				for (Entity n : entity)
				{
					if (n instanceof EntityPlayer)
						((EntityPlayer) n).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 0, 2 * 20));
					else if (n instanceof EntityLivingBase)
						StunManager.setStun((EntityLivingBase)n, 20);
				}
			}
			
			entity.addAll(player.world.getEntitiesInAABBexcluding(player, bb, EntitySelectorDestructable.getInstance()));		
			for (Entity n : entity)
			{
				if (n instanceof EntitySummonedSwordBase && ((EntitySummonedSwordBase)n).getThrower() == player)
					continue;
				Vec3d last = new Vec3d(n.lastTickPosX - player.posX, n.lastTickPosY - player.posY, n.lastTickPosZ - player.posZ);
				Vec3d now = new Vec3d(n.posX - player.posX, n.posY - player.posY, n.posZ - player.posZ);
				if (Math.abs(last.x) >= 4.99 || Math.abs(last.y) >= 4.9 || Math.abs(last.z) >= 4.99 &&
						Math.abs(now.x) < 4.99 && Math.abs(now.y) < 4.99 && Math.abs(now.z) < 4.99)
					player.playSound(SoundEvents.UI_BUTTON_CLICK, 1, 2);
				
				double velocity_n = Math.sqrt(n.motionX * n.motionX + n.motionY * n.motionY + n.motionZ * n.motionZ);
				if (velocity_n > velocity_player)
					n.setVelocity(0, 0, 0);
			}
			
			
		}
	}
	
	public void register()
	{
		SlashBladeHooks.EventBus.register(this);
	}
	
	public String getId()
	{
		return "Sound";
	}
	
}
