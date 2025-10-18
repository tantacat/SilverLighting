package com.tantacat.silverlighting.common.entity;

import java.lang.reflect.Field;
import java.util.UUID;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;

import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.entity.EntityHeavyRainSwords;
import mods.flammpfeil.slashblade.entity.EntitySummonedBlade;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import mods.flammpfeil.slashblade.event.ScheduleEntitySpawner;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class EntityHope extends EntityBladeStand {
	
	public UUID player_id;
	
	public EntityHope(World world)
	{
		super(world);
	}
	
	public EntityHope(World p_i1582_1_, double x, double y, double z, ItemStack blade) {
		super(p_i1582_1_, x, y, z, blade);
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if (world.isRemote) return;
		
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		EntityPlayer player = server.getPlayerList().getPlayerByUUID(player_id);
		
		if (player == null) return;
		
		ItemStack blade = getBlade();
		NBTTagCompound tag = blade.getTagCompound();
		
		if (ticksExisted % 5 != 0) return;
		if (ItemSlashBlade.ProudSoul.get(tag, 0) < 5) return;
		
		int attack_type = tag.getInteger("RangeAttackType");
		
		float blade_attack = ItemAnimaSheath.AttackAmplifier.get(tag, 4);
		int power_level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, blade);
		float damage_rain = (1 + 0.12f + 0.15f * power_level) * blade_attack;
		float damage_blade = blade_attack * 0.2f;
		float range_rain = 8;
		float range_blade = 6;
		
		if (attack_type == 0)
		{
			for (int i = 0; i < 21; i++)
			{
				EntityHeavyRainSwords pho_sword = new EntityHeavyRainSwords(world, player, damage_rain, this.rand.nextFloat() * 360.0f, i ,this.getEntityId());
				if (pho_sword != null)
				{
					Field field_blade;
					try {
						field_blade = EntitySummonedSwordBase.class.getDeclaredField("blade");
						field_blade.setAccessible(true);
						field_blade.set(pho_sword, blade);
					} catch(Exception e) {}
						
					int offset_y = i % 4;
					Vec3d pos = getRandomPos(range_rain);
					
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
		}
		else
		{
			for (int i = 0; i < 16; i++)
			{
				EntityHeavyRainSwords pho_sword = new EntityHeavyRainSwords(world, player, damage_rain, this.rand.nextFloat() * 360.0f, i ,this.getEntityId());
				if (pho_sword != null)
				{
					Field field_blade;
					try {
						field_blade = EntitySummonedSwordBase.class.getDeclaredField("blade");
						field_blade.setAccessible(true);
						field_blade.set(pho_sword, blade);
					} catch(Exception e) {}
					
					int offset_y = i % 4;
					Vec3d pos = getRandomPos(range_rain);
					
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
			for (int i = 0; i < 2; i++)
			{
				EntitySummonedBlade pho_blade = new EntitySummonedBlade(world, player, damage_blade, 90.0f);
				if (pho_blade != null) {
					Field field_blade;
					try {
						field_blade = EntitySummonedSwordBase.class.getDeclaredField("blade");
						field_blade.setAccessible(true);
						field_blade.set(pho_blade, blade);
					} catch(Exception e) {}
					
					pho_blade.setLifeTime(100);
					pho_blade.setInterval(10);

					Vec3d pos = getRandomPos(range_blade);
					pho_blade.posX = pos.x;
					pho_blade.posY = pos.y + 2;
					pho_blade.posZ = pos.z;
					
                    if (ItemSlashBlade.SummonedSwordColor.exists(tag))
                    	pho_blade.setColor(ItemSlashBlade.SummonedSwordColor.get(tag));

                    ScheduleEntitySpawner.getInstance().offer(pho_blade);
                }
			}	
		}
		
		ItemSlashBlade.ProudSoul.add(tag, -5);
	}

	private Vec3d getRandomPos(float range)
	{
		float length = (range * rand.nextFloat() * 1.5f);
		length = length * 7 / 8 + 1;
		double x = posX;
		double y = posY;
		double z = posZ;
		double angel = rand.nextFloat() * 2 * Math.PI;
		x += length * Math.sin(angel);
		z += length * Math.cos(angel);
		return new Vec3d(x,y,z);
	}
	
	@Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) 
	{
		super.readEntityFromNBT(p_70037_1_);
		this.player_id = p_70037_1_.getUniqueId("Player");
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) 
	{
		super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setUniqueId("Player", player_id);
    }
}
