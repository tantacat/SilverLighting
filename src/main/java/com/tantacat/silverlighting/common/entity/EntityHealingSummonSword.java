package com.tantacat.silverlighting.common.entity;

import java.util.List;

import com.google.common.base.Predicate;

import mods.flammpfeil.slashblade.entity.EntityHeavyRainSwords;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityHealingSummonSword extends EntityHeavyRainSwords{

	public EntityHealingSummonSword(World par1World) {
		super(par1World);
	}
	public EntityHealingSummonSword(World par1World, EntityLivingBase entityLiving, float AttackLevel) {
        super(par1World, entityLiving, AttackLevel);
        alreadyHitEntity.remove(thrower);
    }
	public EntityHealingSummonSword(World par1World, EntityLivingBase entityLiving, float AttackLevel, float roll, int interval, int targetId) {
        super(par1World, entityLiving, AttackLevel, roll, interval, targetId);
        alreadyHitEntity.remove(thrower);
    }
	
	@Override
	public RayTraceResult getRayTraceResult()
	{
        Vec3d Vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d Vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult movingobjectposition = this.world.rayTraceBlocks(Vec3d, Vec3d1);
        Vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (movingobjectposition != null)
        {
            IBlockState state = null;
            BlockPos pos = movingobjectposition.getBlockPos();
            if(pos != null)
                state = world.getBlockState(pos);
            if(state != null && state.getCollisionBoundingBox(world, pos) == null)
                movingobjectposition = null;
            else
                Vec3d1 = new Vec3d(movingobjectposition.hitVec.x, movingobjectposition.hitVec.y, movingobjectposition.hitVec.z);
        }

        Entity entity = null;

        AxisAlignedBB bb = this.getEntityBoundingBox().offset(this.motionX, this.motionY, this.motionZ).grow(1.0D, 1.0D, 1.0D);
        AxisAlignedBB bb2 = this.getEntityBoundingBox().grow(1.0D, 1.0D, 1.0D);

        Predicate<Entity>[] selectors = new Predicate[]{EntitySelectorDestructable.getInstance(), EntitySelectorAttackable.getInstance()};
        for(Predicate<Entity> selector : selectors){
            List list = this.world.getEntitiesInAABBexcluding(this, bb, selector);

            if(selector.equals(EntitySelectorAttackable.getInstance()) && getTargetEntityId() != 0){
                Entity target = world.getEntityByID(getTargetEntityId());
                if(target != null){
                    if(target.getEntityBoundingBox().intersects(bb) || target.getEntityBoundingBox().intersects(bb2) )
                        list.add(target);
                }
            }
            list.removeAll(alreadyHitEntity);
            
            double d0 = 0.0D;
            int i;
            float f1;

            for (i = 0; i < list.size(); ++i)
            {
                Entity entity1 = (Entity)list.get(i);

                if(entity1 instanceof EntitySummonedSwordBase)
                    if(((EntitySummonedSwordBase) entity1).getThrower() == this.getThrower())
                        continue;

                if (entity1.canBeCollidedWith())
                {
                    f1 = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.getEntityBoundingBox().grow((double) f1, (double) f1, (double) f1);
                    RayTraceResult movingobjectposition1 = axisalignedbb1.calculateIntercept(Vec3d1, Vec3d);

                    if (movingobjectposition1 != null)
                    {
                        double d1 = Vec3d1.distanceTo(movingobjectposition1.hitVec);

                        if (d1 < d0 || d0 == 0.0D)
                        {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null)
            {
                movingobjectposition = new RayTraceResult(entity);
                movingobjectposition.hitInfo = selector;
                break;
            }
        }
        
        return movingobjectposition;
    }
	
	@Override
    public boolean onImpact(RayTraceResult mop)
    {
		boolean result = true;

        if (mop.entityHit != null)
        {
            Entity target = mop.entityHit;

            if (getThrower() != null && target.getEntityId() == getThrower().getEntityId())
            {
            	EntityLivingBase player = (EntityLivingBase) getThrower();
            	player.heal(1);
            	alreadyHitEntity.add(player);
            }
            else if(mop.hitInfo.equals(EntitySelectorAttackable.getInstance()))
            {
            	attackEntity(target);
            }
            else
            { 
                destructEntity(target);
            }
        }
        else
        {
            if(!world.getCollisionBoxes(this,this.getEntityBoundingBox()).isEmpty())
            {
                if(this.getThrower() != null && this.getThrower() instanceof EntityPlayer)
                    ((EntityPlayer)this.getThrower()).onCriticalHit(this);
                //this.setDead();
                result = false;
            }
        }

        return result;
    }
	
	@Override
    public void setDead() {
        if(this.thrower != null && this.thrower instanceof EntityPlayer)
            ((EntityPlayer)thrower).onCriticalHit(this);
        /*
        if(!this.world.isRemote)
            System.out.println("dead" + this.ticksExisted);
            */

        this.world.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 0.25F, 1.6F);

        AxisAlignedBB bb = this.getEntityBoundingBox().grow(1.0D, 1.0D, 1.0D);
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, bb, EntitySelectorAttackable.getInstance());
        list.removeAll(alreadyHitEntity);
        for(Entity target : list){
            if(blade.isEmpty()) break;
            if(target == null) continue;
            if(target == getThrower()) continue;
            blastAttackEntity(target);
        }

        this.isDead = true;
    }
	
	@Override
    protected void readEntityFromNBT(NBTTagCompound compound) 
	{
		if (compound.hasUniqueId("playerid"))
			setThrower(this.world.getPlayerEntityByUUID(compound.getUniqueId("playerid")));
	}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) 
    {
    	if (getThrower() instanceof EntityPlayer)
    		compound.setUniqueId("playerid", ((EntityPlayer)getThrower()).getUniqueID());
    }
	
}
