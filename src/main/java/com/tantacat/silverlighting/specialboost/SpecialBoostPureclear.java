package com.tantacat.silverlighting.specialboost;

import java.lang.reflect.Field;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.common.entity.EntityHealingSummonSword;
import com.tantacat.silverlighting.registers.RegisterBoosts;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.util.BoostProfile;
import com.tantacat.silverlighting.util.BoostProfile.BoostType;
import com.tantacat.silverlighting.util.BoostProfileHelper;

import mods.flammpfeil.slashblade.entity.EntitySummonedBlade;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import mods.flammpfeil.slashblade.event.ScheduleEntitySpawner;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialBoostPureclear implements IOnBoostSwitch 
{

	public BoostProfile profile = new BoostProfile(getId(), false, BoostType.help);
			
	@SubscribeEvent
	public void onSlashBladeUpdate(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.entity;
		ItemStack blade = event.blade;
		
		if (player.world.isRemote) return;
		
		if (!BoostProfileHelper.isBoostEffective(blade, getId())) return;
		
		int SAid = ItemAnimaSheath.SpecialAttackType.get(blade.getTagCompound());
		if (SAid == RegisterSAs.instance.hope.id)
		{
			if (player.getHealth() <= player.getMaxHealth() * 0.25f)
			{
				if (ItemAnimaSheath.ProudSoul.tryAdd(blade.getTagCompound(), -50, false))
					player.heal(1);
			}
		}
		else if (SAid == RegisterSAs.instance.dedication.id)
		{
			if (player.ticksExisted % 5 != 0) return;
			
			World world = player.world;
			NBTTagCompound tag = blade.getTagCompound();
			int attack_type = tag.getInteger("RangeAttackType");
			int range_rain = attack_type == 0 ? 8 : 6;
			float blade_attack = ItemAnimaSheath.AttackAmplifier.get(tag, 4);
			int power_level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, blade);
			float damage_rain = (1 + 0.12f + 0.15f * power_level) * blade_attack;
			int num_per = attack_type == 0 ? 5 : 4; 
			
			for (int i = 0; i < num_per; i++)
			{
				EntityHealingSummonSword pho_sword = new EntityHealingSummonSword(world, player, damage_rain, player.getRNG().nextFloat() * 360.0f, i , player.getEntityId());
				if (pho_sword != null)
				{		
					Field field_blade;
					try {
						field_blade = EntitySummonedSwordBase.class.getDeclaredField("blade");
						field_blade.setAccessible(true);
						field_blade.set(pho_sword, blade);
					} catch(Exception e) {}
					
					int offset_y = i % 4;
					Vec3d pos = getRandomPos(range_rain, player);
					
					pho_sword.posX = pos.x;
					pho_sword.posY = pos.y + 4 + offset_y;
					pho_sword.posZ = pos.z;
					
					pho_sword.setDriveVector(1);
					
					pho_sword.setLifeTime(30 + i);
					pho_sword.setInterval(0);
					
					if (ItemSlashBlade.SummonedSwordColor.exists(tag))
						pho_sword.setColor(ItemSlashBlade.SummonedSwordColor.get(tag));
					
	                ScheduleEntitySpawner.getInstance().offer(pho_sword);
				}
			}
			
			float damage_blade = blade_attack * 0.2f;
			if (attack_type != 0 && player.ticksExisted % 10 == 0)
			{
				EntitySummonedBlade pho_blade = new EntitySummonedBlade(world, player, damage_blade, 90.0f);
				if (pho_blade != null) 
				{	
					Field field_blade;
					try {
						field_blade = EntitySummonedSwordBase.class.getDeclaredField("blade");
						field_blade.setAccessible(true);
						field_blade.set(pho_blade, blade);
					} catch(Exception e) {}
					
					pho_blade.setLifeTime(100);
					pho_blade.setInterval(10);

					Vec3d pos = getRandomPos(range_rain, player);
					pho_blade.posX = pos.x;
					pho_blade.posY = pos.y + 2;
					pho_blade.posZ = pos.z;
					
                    if (ItemSlashBlade.SummonedSwordColor.exists(tag))
                    	pho_blade.setColor(ItemSlashBlade.SummonedSwordColor.get(tag));

                    ScheduleEntitySpawner.getInstance().offer(pho_blade);
                }
			}
		}
		else
		{
			if (player.getRNG().nextFloat() <= 0.01)
				player.heal(1);
			if (player.ticksExisted % (20*30) == 0)
			{
				player.setAbsorptionAmount(player.getAbsorptionAmount() - 5);
				player.setAbsorptionAmount(player.getAbsorptionAmount() + 5);
			}
		}
	}
	
	@SubscribeEvent
	public void onSlashBladeImpact(SlashBladeEvent.ImpactEffectEvent event)
	{
		if (!(event.user instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.user;
		ItemStack blade = event.blade;
		EntityLivingBase target = event.target;
		
		boolean hasBoost = false;
		for (ItemStack n : player.inventoryContainer.inventoryItemStacks)
		{
			if (BoostProfileHelper.isBoostEffective(blade, getId()))
			{
				hasBoost = true;
				break;
			}
		}

		if (!hasBoost || target == null) return;
		try
		{
			Field lastDamage = EntityLivingBase.class.getDeclaredField("lastDamage");
			lastDamage.setAccessible(true);
			float amount = lastDamage.getFloat(target) * 0.03f;
			player.heal(amount);
		}
		catch (Exception e) {}
		
	}
	
	@Override
	public void onBoostOpen(EntityPlayer player) {
		
	}

	@Override
	public void onBoostClose(EntityPlayer player) {
		
	}
	
	public void register()
	{
		RegisterBoosts.instance.BoostsHasSwitch.put(getId(), this);
		SlashBladeHooks.EventBus.register(this);
	}
			
	public String getId()
	{
		return "Pureclear";
	}
	
	private Vec3d getRandomPos(float range, EntityPlayer player)
	{
		float length = (range * player.getRNG().nextFloat() * 1.5f);
		length = length * 7 / 8 + 1;
		double x = player.posX;
		double y = player.posY;
		double z = player.posZ;
		double angel = player.getRNG().nextFloat() * 2 * Math.PI;
		x += length * Math.sin(angel);
		z += length * Math.cos(angel);
		return new Vec3d(x,y,z);
	}
}
