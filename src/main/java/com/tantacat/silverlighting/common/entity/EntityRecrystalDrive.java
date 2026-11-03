package com.tantacat.silverlighting.common.entity;

import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.entity.EntityDrive;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class EntityRecrystalDrive extends EntityDrive {

    public EntityRecrystalDrive(World par1World) {
        super(par1World);
    }

    public EntityRecrystalDrive(World par1World, EntityLivingBase entityLiving, float AttackLevel, boolean multiHit, float roll) {
        super(par1World, entityLiving, AttackLevel, multiHit, roll);
    }

    public EntityRecrystalDrive(World par1World, EntityLivingBase entityLiving, float AttackLevel, boolean multiHit) {
        super(par1World, entityLiving, AttackLevel, multiHit);
    }

    public EntityRecrystalDrive(World par1World, EntityLivingBase entityLiving, float AttackLevel) {
        super(par1World, entityLiving, AttackLevel);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.ticksExisted < this.getLifeTime() && this.isDead) {
            this.isDead = false;
        }

        this.posX -= this.motionX;
        this.posY -= this.motionY;
        this.posZ -= this.motionZ;
        this.setPosition(this.posX, this.posY, this.posZ);

        double range = 1.5F;
        AxisAlignedBB bb = new AxisAlignedBB(this.posX - range, this.posY - range, this.posZ - range, this.posX + range, this.posY + range, this.posZ + range);
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this.getThrower(), bb, EntitySelectorAttackable.getInstance());
        if (!list.isEmpty()) {
            double scale = 0.075D / Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            this.motionX *= scale;
            this.motionY *= scale;
            this.motionZ *= scale;
        }
        else {
            double scale = 1.5D / Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            this.motionX *= scale;
            this.motionY *= scale;
            this.motionZ *= scale;
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.setPosition(this.posX, this.posY, this.posZ);

        for (Entity cur : list) {
            if (!(cur instanceof EntityLivingBase)) continue;
            StunManager.setFreeze((EntityLivingBase)cur, 20);
            cur.motionX = 0;
            cur.motionY = 0;
            cur.motionZ = 0;
        }
    }
}
