package com.tantacat.silverlighting.specialattack;

import java.lang.reflect.Method;
import java.util.List;

import com.tantacat.silverlighting.common.entity.EntityMurderous;

import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialattack.IJustSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpecialAttackMurderous extends SpecialAttackBase implements IJustSpecialAttack
{

	public int id;
	
	public SpecialAttackMurderous() {
		id = toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "murderous";
	}

	@Override
	public void doSpacialAttack(ItemStack stack, EntityPlayer player) 
	{
		
		World world = player.world;
		
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);
        
        Vec3d hitvec = Vec3d.ZERO;
        
        int i = 0;
        for (; i < 12; i++)
        {
        	Vec3d pos = player.getPositionEyes(1).add(player.getLookVec().scale(i));
        	AxisAlignedBB bb = new AxisAlignedBB(pos.x - 1.5D, pos.y - 1.5D, pos.z - 1.5D, pos.x + 1.5D, pos.y + 1.5D, pos.z + 1.5D);
        	List<Entity> target = world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());
        	
        	for (Entity n : target)
        	{
        		if (n instanceof EntityLivingBase)
        		{
        			hitvec = target.get(0).getPositionVector();
            		break;
        		}
        	}
        	
        	if (hitvec != Vec3d.ZERO)
        		break;
        }
        
        if (hitvec == Vec3d.ZERO)
        {
        	ItemSlashBlade.specialAttacks.get(4).doSpacialAttack(stack, player);
        	return;
        }
        
        Vec3d vel = getVector(player.rotationPitch, player.rotationYaw).scale((i + 1) / 2);
        ReflectionAccessHelper.setVelocity(player, vel.x, vel.y, vel.z);
        
        if (!world.isRemote)
        {
        	EntityMurderous entity = new EntityMurderous(world, player.getUniqueID(), hitvec, 
        			stack, player.rotationPitch, player.rotationYaw, false);
            entity.setPosition(player.posX, player.posY, player.posZ);
            entity.setlifetime(20);
            if (entity != null)
                world.spawnEntity(entity);
        }
        
        final int cost = -100;
        if(!ItemSlashBlade.ProudSoul.tryAdd(tag,cost,false)){
        	try {
        		Method damageItem = stack.getItem().getClass().getMethod("damageItem", ItemStack.class, int.class, EntityLivingBase.class);
    			damageItem.invoke(stack.getItem(), stack, 10, player);
        	}
        	catch (Exception e) {}
        }
        player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,20,0,true,false));

        UntouchableTime.setUntouchableTime(player, 10);

        player.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
        ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.HiraTuki);
	}
	
	@Override
	public void doJustSpacialAttack(ItemStack stack, EntityPlayer player) {
		World world = player.world;
		
        NBTTagCompound tag = ItemSlashBlade.getItemTagCompound(stack);
        
        Vec3d hitvec = Vec3d.ZERO;
        
        int i = 0;
        for (; i < 12; i++)
        {
        	Vec3d pos = player.getPositionEyes(1).add(player.getLookVec().scale(i));
        	AxisAlignedBB bb = new AxisAlignedBB(pos.x - 1.5D, pos.y - 1.5D, pos.z - 1.5D, pos.x + 1.5D, pos.y + 1.5D, pos.z + 1.5D);
        	List<Entity> target = world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());
        	if (!target.isEmpty())
        	{
        		hitvec = target.get(0).getPositionVector();
        		break;
        	}
        }
        
        if (hitvec == Vec3d.ZERO)
        {
        	ItemSlashBlade.specialAttacks.get(4).doSpacialAttack(stack, player);
        	return;
        }
        
        Vec3d vel = getVector(player.rotationPitch, player.rotationYaw).scale((i + 1) / 2);
        ReflectionAccessHelper.setVelocity(player, vel.x, vel.y, vel.z);
        
		if (!world.isRemote)
		{
			EntityMurderous entity = new EntityMurderous(world, player.getUniqueID(), hitvec, 
	    			stack, player.rotationPitch, player.rotationYaw, true);
	        entity.setPosition(player.posX, player.posY, player.posZ);
	        entity.setlifetime(20);
	        if (entity != null)
	            world.spawnEntity(entity);
		}
        
        final int cost = -100;
        if(!ItemSlashBlade.ProudSoul.tryAdd(tag,cost,false)){
        	try {
        		Method damageItem = stack.getItem().getClass().getMethod("damageItem", ItemStack.class, int.class, EntityLivingBase.class);
    			damageItem.invoke(stack.getItem(), stack, 1, player);
        	}
        	catch (Exception e) {}
        }
        player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,20,0,true,false));

        UntouchableTime.setUntouchableTime(player, 10);

        player.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
        ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.HiraTuki);
	}

	protected Vec3d getVector(float pitch, float yaw)
    {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }

	
	
}
