package com.tantacat.silverlighting.common.entity;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import mods.flammpfeil.slashblade.entity.EntityDrive;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityBreak extends Entity{

	public UUID player_id;
	public List<BlockPos> blocks = Lists.newArrayList();
	
	public EntityBreak(World worldIn) {
		super(worldIn);
	}
	
	public EntityBreak(World world, UUID id)
	{
		super(world);
		setPlayerID(id);
	}

	@Override
	public void onUpdate()
	{
		if (world.isRemote) return;
		
		EntityPlayer player = this.world.getPlayerEntityByUUID(getPlayerID());
		if (player == null) {setDead();return;}
		
		if (this.ticksExisted % 20 == 0 && this.getPositionVector().distanceTo(player.getPositionVector()) > 8)
			this.setPosition(player.posX, player.posY, player.posZ);
		
		List<EntityDrive> drives = this.world.getEntities(EntityDrive.class, new Predicate<EntityDrive>() {
			@Override
			public boolean apply(EntityDrive input) {
				return input.getThrower() == player;
			}
		});
		List<EntitySummonedSwordBase> swords = this.world.getEntities(EntitySummonedSwordBase.class, new Predicate<EntitySummonedSwordBase>() {
			@Override
			public boolean apply(EntitySummonedSwordBase input) {
				return input.getThrower() == player;
			}
		});
		for (Entity n : drives)
		{
			world.getCollisionBoxes(n, n.getEntityBoundingBox()).forEach(new Consumer<AxisAlignedBB>() {
				@Override
				public void accept(AxisAlignedBB t) {
					for (BlockPos blockpos : BlockPos.getAllInBox((int)t.minX, (int)t.minY, (int)t.minZ, (int)t.maxX, (int)t.maxY, (int)t.maxZ))
					{
						if (blockpos != null && !world.isAirBlock(blockpos))
						{
							if (!tryDestoryBlock(blockpos, true))
							{
								IBlockState blockstate = world.getBlockState(blockpos);
								if (blockstate.getBlockHardness(world, blockpos) >= 2)
									blocks.add(blockpos);
							}
						}
					}
				}
			});
		}
		for (Entity n : swords)
		{
			world.getCollisionBoxes(n, n.getEntityBoundingBox()).forEach(new Consumer<AxisAlignedBB>() {
				@Override
				public void accept(AxisAlignedBB t) {
					for (BlockPos blockpos : BlockPos.getAllInBox((int)t.minX, (int)t.minY, (int)t.minZ, (int)t.maxX, (int)t.maxY, (int)t.maxZ))
					{
						if (blockpos != null && !world.isAirBlock(blockpos))
						{
							if (!tryDestoryBlock(blockpos, true))
							{
								IBlockState blockstate = world.getBlockState(blockpos);
								if (blockstate.getBlockHardness(world, blockpos) >= 2)
									blocks.add(blockpos);
							}
						}
					}
				}
			});
		}
	}
	
	public boolean tryDestoryBlock(BlockPos pos, boolean flag)
	{
		if (blocks.contains(pos))
		{
			this.world.destroyBlock(pos, flag);
			blocks.remove(pos);
			return true;
		}
		return false;
	}
	
	public void destoryBlocks(boolean flag)
	{
		for (BlockPos n : blocks)
			this.world.destroyBlock(n, flag);
		blocks.clear();
	}		
	
	public void setBlockCache(List<BlockPos> blocks)
	{
		this.blocks = blocks;
	}
	
	public List<BlockPos> getBlockCache()
	{
		return this.blocks;
	}
		
	@Override
	protected void entityInit() {
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasUniqueId("playerid"))
			setPlayerID(compound.getUniqueId("playerid"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setUniqueId("playerid", player_id);
	}

	public UUID getPlayerID()
	{
		return player_id;
	}
	
	public void setPlayerID(UUID value)
	{
		player_id = value;
	}

}
