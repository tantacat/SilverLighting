package com.tantacat.silverlighting.common.entity;

import java.util.UUID;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityLeafSwim extends Entity{
	
	public int lifetime;
	
	public EntityLeafSwim(World worldIn) {
		super(worldIn);
	}
	
	public EntityLeafSwim(World world, UUID id, int time)
	{
		super(world);
		setPlayerID(id);
		setlifetime(time);
	}
	
	@Override
	public void onUpdate()
	{		
		super.onUpdate();
						
		EntityPlayer player = this.world.getPlayerEntityByUUID(getPlayerID());
		if (player == null) {setDead();return;}
		
		if (world.isRemote)
			player.getEntityData().setBoolean("renderleafswim", true);
		
		if (this.ticksExisted % 20 == 0 && this.getPositionVector().distanceTo(player.getPositionVector()) > 30)
			this.setPosition(player.posX, player.posY, player.posZ);
		
		if (player.motionY < 0)
		{
			player.motionY = 0;
			player.fallDistance = 0;
		}	
		
		if (ticksExisted >= getlifetime())
		{
			if (world.isRemote)
			{
				if (world.getEntities(this.getClass(), Predicates.alwaysTrue()).size() == 1 && 
						player.getEntityData().hasKey("renderleafswim"))
					player.getEntityData().setBoolean("renderleafswim", false);
			}
			setDead();
		}
		
	} 
	
	static DataParameter<Optional<UUID>> player_id = EntityDataManager.createKey(EntityLeafSwim.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	
	@Override
	protected void entityInit() {
		Optional<UUID> id = Optional.fromNullable(UUID.randomUUID());
		this.getDataManager().register(player_id, id);
	}

	public UUID getPlayerID()
	{
		return this.getDataManager().get(player_id).get();
	}
	
	public void setPlayerID(UUID value)
	{
		Optional<UUID> id = Optional.fromNullable(value);
		this.getDataManager().set(player_id, id);
	}
	
	public int getlifetime()
	{
		return lifetime;
	}
	
	public void setlifetime(int time)
	{
		lifetime = time;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasUniqueId("playerid"))
			setPlayerID(compound.getUniqueId("playerid"));
		if (compound.hasKey("lifetime"))
			setlifetime(compound.getInteger("lifetime"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		UUID id = this.getPlayerID();
		compound.setUniqueId("playerid", id);
		compound.setInteger("lifetime", lifetime);
	}

	
	
}
