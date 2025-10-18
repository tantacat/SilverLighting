package com.tantacat.silverlighting.specialboost;

import java.util.List;

import com.google.common.collect.Lists;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.util.BoostProfile;
import com.tantacat.silverlighting.util.BoostProfile.BoostType;
import com.tantacat.silverlighting.util.BoostProfileHelper;

import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialBoostPressure {

	public BoostProfile profile = new BoostProfile(getId(), false, BoostType.help);
	
	@SubscribeEvent
	public void onSlashBladeUpdate(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.entity;
		if (player.world.isRemote) return;
		ItemStack boost_blade = event.blade;
		NBTTagCompound player_tag = player.getEntityData();
		String flag = "PressureTick";
		
		if (BoostProfileHelper.isBoostEffective(boost_blade, getId()))
		{
			if (!player_tag.hasKey(flag))
				player_tag.setInteger(flag, 0);
			PressureEffect(player, boost_blade, boost_blade.getTagCompound(), player_tag);
			player_tag.setInteger(flag, player_tag.getInteger(flag) + 1);
		}
	}
	
	private void PressureEffect(EntityPlayer player, ItemStack boost_blade, NBTTagCompound boost_tag, NBTTagCompound player_tag) {
		
		ItemStack main_blade = player.getHeldItemMainhand();
		ItemStack off_blade = player.getHeldItemOffhand();
		
		ItemStack fight_blade = ItemStack.EMPTY;
		if (main_blade.getItem() instanceof ItemSlashBlade)
			fight_blade = main_blade;
		else if (off_blade.getItem() instanceof ItemSlashBlade)
			fight_blade = off_blade;
		if (fight_blade == ItemStack.EMPTY) return;
		
		
		EntityLivingBase target = null;
		Entity entity = player.world.getEntityByID(ItemAnimaSheath.TargetEntityId.get(fight_blade.getTagCompound()));
		if (entity instanceof EntityLivingBase)
			target = (EntityLivingBase)entity;
		if (target == null) {player_tag.setInteger("PressureTick", 0);return;}
		
		int tick = player_tag.getInteger("PressureTick");
		if (tick % 20 != 0) return;//最短触发时间
		
		int K = ItemAnimaSheath.KillCount.get(boost_tag, 0);
		K += ItemAnimaSheath.KillCount.get(fight_blade.getTagCompound(), 0);
		
		float H = target.getHealth();
		int k = 0;
		if (target instanceof EntityPlayer)
		{
			ItemStack main = ((EntityPlayer)target).getHeldItemMainhand();
			ItemStack off = ((EntityPlayer)target).getHeldItemOffhand();
			if (main.getItem() instanceof ItemSlashBlade)
				k += ItemAnimaSheath.KillCount.get(main_blade.getTagCompound());
			else if (off.getItem() instanceof ItemSlashBlade)
				k += ItemAnimaSheath.KillCount.get(off_blade.getTagCompound());
		}
		
		float M = K / (H + k);
		
		if (10 <= M && M < 15)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20));
		}
		else if (M < 20)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20));
		}
		else if (M < 25)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 1));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20));
		}
		else if (M < 50)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 2));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 1));
		}
		else if (M < 70)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 3));
			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2 * 20));
		}
		else if (M < 100)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2 * 20));
			target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2 * 20));
		}
		else if (M < 150)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2 * 20));
			target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2 * 20, 1));
			
			target.attackEntityFrom(DamageSource.IN_WALL, 1);
		}
		else if (M < 200)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2 * 20));
			target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2 * 20, 2));
			
			if (tick % 10*20 == 0)
				StunManager.setStun(target, 20);
			
			target.attackEntityFrom(DamageSource.IN_WALL, 1);
		}
		else if (M < 300)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2 * 20));
			target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2 * 20, 2));

			if (tick % 7*20 == 0)
				StunManager.setStun(target, 20);
			
			target.attackEntityFrom(DamageSource.IN_WALL, 1);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.IN_FIRE, 1);
		}
		else if (M < 500)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2 * 20));
			target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2 * 20, 4));
			
			if (tick % 7*20 == 0)
				StunManager.setStun(target, 2 * 20);
			
			target.attackEntityFrom(DamageSource.IN_WALL, 2);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.IN_FIRE, 2);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.FALL, 2);
		}
		else if (M < 700)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2 * 20));
			target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2 * 20, 4));
			
			if (tick % 5*20 == 0)
				StunManager.setStun(target, 20);
			
			if (player.getRNG().nextFloat() <= 0.05)
				dropAllItems(target);
			
			target.attackEntityFrom(DamageSource.IN_WALL, 2);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.IN_FIRE, 2);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.FALL, 2);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.FLY_INTO_WALL, 2);
		}
		else if (M < 800)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2 * 20));
			target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2 * 20, 4));

			if (tick % 3*20 == 0)
				StunManager.setStun(target, 20);
			
			if (player.getRNG().nextFloat() <= 0.35)
				dropAllItems(target);
			
			target.attackEntityFrom(DamageSource.IN_WALL, 3);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.IN_FIRE, 3);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.FALL, 3);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.FLY_INTO_WALL, 3);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.OUT_OF_WORLD, 3);
		}
		else if (M < 900)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2 * 20));
			target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2 * 20, 4));

			if (tick % 3*20 == 0)
				StunManager.setStun(target, 20);
			
			if (player.getRNG().nextFloat() <= 0.50)
				dropAllItems(target);
			
			target.attackEntityFrom(DamageSource.IN_WALL, 5);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.IN_FIRE, 5);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.FALL, 5);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.FLY_INTO_WALL, 5);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.OUT_OF_WORLD, 5);
		}
		else if (M < 1000)
		{
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2 * 20, 4));
			target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2 * 20));
			target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2 * 20, 4));
			
			if (tick % 2*20 == 0)
				StunManager.setStun(target, 20);
			
			if (player.getRNG().nextFloat() <= 0.50)
				dropAllItems(target);
			
			target.attackEntityFrom(DamageSource.IN_WALL, 5);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.IN_FIRE, 5);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.FALL, 5);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.FLY_INTO_WALL, 5);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.OUT_OF_WORLD, 5);
		}
		else
		{
			StunManager.setStun(target, 20);
			
			dropAllItems(target);
			
			float maxH = target.getMaxHealth();
			
			if (!(target instanceof EntityPlayer))
			{
				AttributeModifier AM = new AttributeModifier(getId(), -maxH * 0.01f, 0);
				IAttributeInstance HealthAttribute = target.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
				HealthAttribute.applyModifier(AM);
			}
			
			float damage = Math.max(5, 0.1f * maxH);
			target.attackEntityFrom(DamageSource.IN_WALL, damage);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.IN_FIRE, damage);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.FALL, damage);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.FLY_INTO_WALL, damage);
			target.hurtResistantTime = 0;
			target.attackEntityFrom(DamageSource.OUT_OF_WORLD, damage);
		}
		
		((ItemSlashBlade)fight_blade.getItem()).updateKillCount(fight_blade, target, player);
		ItemAnimaSheath.KillCount.add(boost_tag, -2);
	}
	
	private void dropAllItems(EntityLivingBase target)
	{
		if (target instanceof EntityPlayer)
			((EntityPlayer) target).inventory.dropAllItems();
		else
		{
			List<ItemStack> items = Lists.newArrayList();
			items.add(target.getHeldItemMainhand());
			items.add(target.getHeldItemOffhand());
			items.add(target.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
			items.add(target.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
			items.add(target.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
			items.add(target.getItemStackFromSlot(EntityEquipmentSlot.FEET));

			
			target.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
			target.setItemStackToSlot(EntityEquipmentSlot.CHEST, ItemStack.EMPTY);
			target.setItemStackToSlot(EntityEquipmentSlot.LEGS, ItemStack.EMPTY);
			target.setItemStackToSlot(EntityEquipmentSlot.FEET, ItemStack.EMPTY);
			target.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
			target.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
			
			for (ItemStack n : items)
				target.entityDropItem(n, 0);
			
		}
	}

	public void register()
	{
		SlashBladeHooks.EventBus.register(this);
	}
	
	public String getId()
	{
		return "Pressure";
	}
	
}
