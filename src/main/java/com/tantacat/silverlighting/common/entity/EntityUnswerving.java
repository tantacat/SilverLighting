package com.tantacat.silverlighting.common.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityUnswerving extends Entity{

	public UUID player_id;
	public List<ItemStack> blades = new ArrayList<ItemStack>();
	
	public EntityUnswerving(World worldIn) {
		super(worldIn);
	}
	
	public EntityUnswerving(World world, UUID player_id, List<ItemStack> blades)
	{
		super(world);
		this.player_id = player_id;
		this.blades = blades;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if (world.isRemote) return;
		
		if (player_id == null) 
		{
			setDead();
			return;
		}
		
		EntityPlayer player = world.getPlayerEntityByUUID(player_id);
		
		if (player == null || !player.isEntityAlive()) return;
		
		for (int i = 0; i < blades.size(); i++)
		{
			double angel = Math.PI * 2 / blades.size() * i;
			double xoffset = 1.5 * Math.sin(angel);
			double zoffset = 1.5 * Math.cos(angel);
			Vec3d pos = player.getPositionVector().addVector(xoffset, 0, zoffset);
			EntityItem item = new EntityItem(world, pos.x, pos.y, pos.z, blades.get(i));
			item.addTag("SB.DeathDrop");
			world.spawnEntity(item);
		}
		
		setDead();
	}
	
	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasUniqueId("Player"))
			this.player_id = compound.getUniqueId("Player");
		if (compound.hasKey("Blades"))
		{
			NBTTagList nbt_list = compound.getTagList("Blades", 10);
			for (int i = 0; i < nbt_list.tagCount(); i++)
			{
				NBTTagCompound bladeTag = nbt_list.getCompoundTagAt(i);
				ItemStack blade = new ItemStack(bladeTag);
				blade.deserializeNBT(bladeTag);
				blades.add(blade);
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setUniqueId("Player", player_id);
		NBTTagList blades = new NBTTagList();
		for (ItemStack blade : this.blades)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			blades.appendTag(blade.writeToNBT(nbt));
		}
		compound.setTag("Blades", blades);
	}

}
