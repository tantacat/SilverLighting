package com.tantacat.silverlighting.common.entity;

import java.util.List;

import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityDestory extends Entity implements IThrowableEntity{

	/**
	 * 一个向玩家的视线方向移动的实体，随移动距离增大，破坏范围增大，范围大致上为粒子剑气的范围
	 * @param central 视线向量，也即剑气中心向量
	 * @param left 剑气左侧向量
	 * @param right 剑气右侧向量
	 * @param distance 实体最多移动的距离
	 * @param current_distance 实体当前移动的距离
	 */
	public Vec3d central;
	public Vec3d left;
	public Vec3d right;
	public float distance;
	public float current_distance = 0;
	public EntityPlayer thower;
	
	public EntityDestory(World worldIn) {
		super(worldIn);
	}
	
	public EntityDestory(World world, Vec3d central, float pitch, float yaw, float rotation, float distance)
	{
		super(world);
		this.central = central;
		this.left = getVector(pitch, yaw - 30);
		this.right = getVector(pitch, yaw + 30);
		this.distance = distance;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if (this.left == null) return;
		current_distance += 0.5f;
		Vec3d position = getPositionVector();
		Vec3d leftpos = position.add(left.scale(current_distance));
		Vec3d centralpos = position.add(central.scale(current_distance));
		Vec3d rightpos = position.add(right.scale(current_distance));
		
		AxisAlignedBB bb_left = new AxisAlignedBB(leftpos.x, leftpos.y, leftpos.z, 
				centralpos.x, centralpos.y, centralpos.z);
		AxisAlignedBB bb_right = new AxisAlignedBB(centralpos.x, centralpos.y, centralpos.z, 
				rightpos.x, rightpos.y, rightpos.z);
		
		spawnParticleLine(world, leftpos, centralpos, EnumParticleTypes.CRIT, distance, 0);
		spawnParticleLine(world, centralpos, rightpos, EnumParticleTypes.CRIT, distance, 0);
		
		if (current_distance >= distance)
			setDead();
	}
	
	@Override
	protected void entityInit() {
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("central"))
		{
			NBTTagCompound central = compound.getCompoundTag("central");
			this.central = new Vec3d(central.getDouble("x"), central.getDouble("y"), central.getDouble("z"));
		}
		if (compound.hasKey("left"))
		{
			NBTTagCompound left = compound.getCompoundTag("left");
			this.left = new Vec3d(left.getDouble("x"), left.getDouble("y"), left.getDouble("z"));
		}
		if (compound.hasKey("right"))
		{
			NBTTagCompound right = compound.getCompoundTag("right");
			this.right = new Vec3d(right.getDouble("x"), right.getDouble("y"), right.getDouble("z"));
		}
		if (compound.hasKey("distance"))
			distance = compound.getFloat("distance");
		if (compound.hasKey("current_distance"))
			current_distance = compound.getFloat("current_distance");
		if (compound.hasUniqueId("playerid"))
			thower = this.world.getPlayerEntityByUUID(compound.getUniqueId("playerid"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		NBTTagCompound central = new NBTTagCompound();
		central.setDouble("x", this.central.x);
		central.setDouble("y", this.central.y);
		central.setDouble("z", this.central.z);
		compound.setTag("central", central);
		
		NBTTagCompound left = new NBTTagCompound();
		left.setDouble("x", this.left.x);
		left.setDouble("y", this.left.y);
		left.setDouble("z", this.left.z);
		compound.setTag("left", left);
		
		NBTTagCompound right = new NBTTagCompound();
		right.setDouble("x", this.right.x);
		right.setDouble("y", this.right.y);
		right.setDouble("z", this.right.z);
		compound.setTag("right", right);
		
		compound.setFloat("distance", distance);
		compound.setFloat("current_distance", current_distance);
		
		compound.setUniqueId("playerid", thower.getUniqueID());
	}

	
	/**
     * 在两点间生成粒子线
     * 
     * @param world 世界对象
     * @param player 玩家（用于确定生成粒子的维度）
     * @param start 起点坐标
     * @param end 终点坐标
     * @param particleType 粒子类型
     * @param density 粒子密度（每方块距离生成多少粒子）
     * @param speed 粒子速度（0为静态）
     */
	public void spawnParticleLine(
            World world, 
            Vec3d start, 
            Vec3d end, 
            EnumParticleTypes particleType, 
            double density,
            double speed) {
        
        // 计算两点间距离
        double distance = start.distanceTo(end);
        
        // 计算需要生成的粒子总数
        int particleCount = Math.max(1, (int)(distance * density));
        
        // 计算步长向量
        Vec3d step = end.subtract(start).scale(1.0 / particleCount);
        
        // 在客户端生成粒子
        for (int i = 0; i <= particleCount; i++) {
            // 计算当前位置
            Vec3d pos = start.add(step.scale(i));
            
            // 生成粒子（使用随机偏移使效果更自然）
            world.spawnParticle(
                particleType,
                pos.x + (world.rand.nextDouble() - 0.5) * 0.1,
                pos.y + (world.rand.nextDouble() - 0.5) * 0.1,
                pos.z + (world.rand.nextDouble() - 0.5) * 0.1,
                (world.rand.nextDouble() - 0.5) * speed,
                world.rand.nextDouble() * speed,
                (world.rand.nextDouble() - 0.5) * speed
            );
            
            AxisAlignedBB bb = new AxisAlignedBB(new BlockPos((int)pos.x, (int)pos.y, (int)pos.z));
            List<Entity> entity = world.getEntitiesInAABBexcluding(thower, bb, EntitySelectorAttackable.getInstance());
            for (Entity n : entity)
            {
            	((EntityLiving)n).addVelocity(central.x, central.y, central.z);
            }
            
            for (int x = 0; x < 2; x ++)
            {
            	for (int y = 0; y < 2; y++)
            	{
            		for (int z = 0; z < 2; z++)
            		{
            			BlockPos block1 = new BlockPos((int)pos.x + x, (int)pos.y + y, (int)pos.z + z);
            			BlockPos block2 = new BlockPos((int)pos.x - x, (int)pos.y - y, (int)pos.z - z);
                        if (world.getBlockState(block1).getBlockHardness(world, block1) < 2) 
                        	world.destroyBlock(block1, true);
                        if (world.getBlockState(block2).getBlockHardness(world, block2) < 2) 
                        	world.destroyBlock(block2, true);
            		}
            	}
            }
            
        }
    }

	/*
	 * 将left和right视为另外两个视线向量，yaw大体上直接增减角度，pitch根据抬头和低头的角度向下或向上偏
	 */
	protected Vec3d getVector(float pitch, float yaw)
    {
		pitch -= pitch / 180;
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }
	
	@Override
	public Entity getThrower() {
		return this.thower;
	}

	@Override
	public void setThrower(Entity entity) {
		this.thower = (EntityPlayer)entity;
	}
}
