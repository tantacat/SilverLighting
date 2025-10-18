package com.tantacat.silverlighting.common.entity;

import java.util.List;
import java.util.Random;

import com.google.common.base.Predicate;

import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.ability.TeleportCanceller;
import mods.flammpfeil.slashblade.entity.EntitySlashDimension;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityDedicationVoid extends EntitySlashDimension{

	public EntityDedicationVoid(World world)
	{
		super(world);
	}
	
	public EntityDedicationVoid(World par1World, EntityLivingBase entityLiving, float AttackLevel, boolean multiHit) {
		super(par1World, entityLiving, AttackLevel, multiHit);
	}
	
	@Override
	public void onUpdate()
	{
		if (!this.world.isRemote)
        {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();

        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;

        if(!world.isRemote)
        {
            if(ticksExisted < 8 && ticksExisted % 2 == 0) {
                this.playSound(SoundEvents.ENTITY_WITHER_HURT, 0.2F, 0.5F + 0.25f * this.rand.nextFloat());
            }

            if(this.getThrower() != null){
                AxisAlignedBB bb = this.getEntityBoundingBox();

                if(this.getThrower() instanceof EntityLivingBase && this.ticksExisted % 2 == 0){
                    EntityLivingBase entityLiving = (EntityLivingBase)this.getThrower();
                    
                    List<Entity> list = this.world.getEntitiesInAABBexcluding(this.getThrower(), bb, EntitySelectorDestructable.getInstance());
                    StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.DestructObject);
                    list.addAll(this.world.getEntitiesInAABBexcluding(this.thrower, bb, new Predicate<Entity>() {
						@Override
						public boolean apply(Entity input) {
							return (input instanceof EntityItem);
						}
					}));
                    
                    for(Entity curEntity : list){
                        if(blade.isEmpty()) break;

                        boolean isDestruction = true;
                        
                        if(!isDestruction)
                            continue;
                        else{
                    		curEntity.addVelocity(-curEntity.motionX, -curEntity.motionY, -curEntity.motionZ);
                        	if (curEntity instanceof EntityArrow && ((EntityArrow)curEntity).isAirBorne)
                        	{
                        		curEntity.setNoGravity(true);
                        		curEntity.setPosition(this.posX, this.posY, this.posZ);
                        		Vec3d velocity = this.getPositionVector().subtract(curEntity.getPositionVector());
                            	velocity.scale(1);
                            	curEntity.addVelocity(velocity.x, velocity.y, velocity.z);
                        	}
                        	else
                        	{
                        		int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, blade);
                            	Vec3d velocity = this.getPositionVector().subtract(curEntity.getPositionVector());
                            	velocity.scale((level + 1));
                            	curEntity.addVelocity(velocity.x, velocity.y, velocity.z);

                        	}                        	
                            for (int var1 = 0; var1 < 10; ++var1)
                            {
                                Random rand = this.getRand();
                                double var2 = rand.nextGaussian() * 0.02D;
                                double var4 = rand.nextGaussian() * 0.02D;
                                double var6 = rand.nextGaussian() * 0.02D;
                                double var8 = 10.0D;
                                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL
                                        , curEntity.posX + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var2 * var8
                                        , curEntity.posY + (double)(rand.nextFloat() * curEntity.height) - var4 * var8
                                        , curEntity.posZ + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var6 * var8
                                        , var2, var4, var6);
                            }
                        }

                        StylishRankManager.doAttack(this.thrower);
                    }
                }
                if(getIsSingleHit() || this.ticksExisted % 2 == 0){
                    List<Entity> list = this.world.getEntitiesInAABBexcluding(this.getThrower(), bb, EntitySelectorAttackable.getInstance());

                    float magicDamage = Math.max(1.0f, AttackLevel);

                    StylishRankManager.setNextAttackType(this.thrower ,StylishRankManager.AttackTypes.SlashDimMagic);

                    for(Entity curEntity : list){
                        if(blade.isEmpty()) break;

                        curEntity.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this.getThrower()), 0.005f);
                        if (curEntity instanceof EntityLivingBase && this.getThrower() instanceof EntityPlayer)
                        	ItemSlashBlade.updateKillCount(this.blade, (EntityLivingBase)curEntity, (EntityPlayer)this.thrower);
                        
                        Vec3d pos = curEntity.getPositionVector();

                        TeleportCanceller.setCancel(curEntity);

                        curEntity.hurtResistantTime = 0;

                        if(!blade.isEmpty() && curEntity instanceof EntityLivingBase)
                            ((ItemSlashBlade)blade.getItem()).hitEntity(blade,(EntityLivingBase)curEntity,(EntityLivingBase)thrower);

                        if(!curEntity.getPositionVector().equals(pos))
                            curEntity.setPositionAndUpdate(pos.x,pos.y,pos.z);

                        curEntity.motionX = 0;
                        curEntity.motionY = 0;
                        curEntity.motionZ = 0;

                        if(3 < this.ticksExisted){
                            if(curEntity instanceof EntityLivingBase) {
                                
                            	int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, blade);
                            	Vec3d velocity = this.getPositionVector().subtract(curEntity.getPositionVector());
                            	velocity = velocity.normalize();
                            	velocity.scale(level + 1);
                            	curEntity.addVelocity(velocity.x, velocity.y, velocity.z);
                            	
                            }
                        }

                    }
                }
            }
        }
        
        if (getLifeTime() - ticksExisted == 3)
            this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1, 1);
        
        if(ticksExisted >= getLifeTime()) {
        	AxisAlignedBB bb = getEntityBoundingBox();
            List<Entity> mob_list = this.world.getEntitiesInAABBexcluding(this.getThrower(), bb, EntitySelectorAttackable.getInstance());
            for (Entity curEntity : mob_list)
            {
            	curEntity.addVelocity(0, 1.25f, 0);
            }
            		
            List<Entity> arrow_list = this.world.getEntitiesInAABBexcluding(this.getThrower(), bb, EntitySelectorDestructable.getInstance());
            for (Entity curEntity : arrow_list)
            {
            	if (curEntity instanceof EntityArrow)
            	{
            		curEntity.setNoGravity(false);
            		curEntity.addVelocity((this.rand.nextDouble() - 0.5) * 2, 
            				0.5, (this.rand.nextDouble() - 0.5) * 2);
            	}
            }
            for (int var1 = 0; var1 < 10; ++var1)
            {
                Random rand = this.getRand();
                double var2 = rand.nextGaussian() * 0.02D;
                double var4 = rand.nextGaussian() * 0.02D;
                double var6 = rand.nextGaussian() * 0.02D;
                double var8 = 10.0D;
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL
                        , this.posX + (double)(rand.nextFloat() * this.width * 2.0F) - (double)this.width - var2 * var8
                        , this.posY + (double)(rand.nextFloat() * this.height) - var4 * var8
                        , this.posZ + (double)(rand.nextFloat() * this.width * 2.0F) - (double)this.width - var6 * var8
                        , var2, var4, var6);
            }
            setDead();
        }
	}
	
}
