package com.tantacat.silverlighting.common.entity;

import java.util.UUID;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.util.BoostProfileHelper;

import mods.flammpfeil.slashblade.entity.EntitySpiralSwords;
import mods.flammpfeil.slashblade.event.ScheduleEntitySpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class EntitySpelling extends Entity{
	
	public NBTTagCompound last_ench;
	public UUID player_id;
	
	public EntitySpelling(World worldIn) {
		super(worldIn);
	}
	
	public EntitySpelling(World world, UUID id, NBTTagCompound tag)
	{
		super(world);
		setPlayerID(id);
		last_ench = tag;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (this.world.isRemote) return;
		EntityPlayer player = this.world.getPlayerEntityByUUID(getPlayerID());
		if (player == null) return;
		
		if (this.ticksExisted % 20 == 0 && this.getPositionVector().distanceTo(player.getPositionVector()) > 8)
			this.setPosition(player.posX, player.posY, player.posZ);
		
		ItemStack blade = ItemStack.EMPTY;
		if (BoostProfileHelper.isBoostEffective(player.getHeldItemMainhand(), "Spelling"))
			blade = player.getHeldItemMainhand();
		else if (BoostProfileHelper.isBoostEffective(player.getHeldItemOffhand(), "Spelling"))
			blade = player.getHeldItemOffhand();
		
		if (blade.isEmpty()) {setDead(); return;}
		
		NBTTagList now = blade.getEnchantmentTagList();
		NBTTagList last = last_ench.getTagList("ench", 10);
		if (!last.equals(now))
		{
			NBTTagCompound tag_blade = blade.getTagCompound();
			int currentTime = (int)player.getEntityWorld().getWorldTime();
            final int holdLimit = 20 * 20;

            if(player.getEntityData().hasKey("SB.SPHOLDID")){
                if(currentTime < (player.getEntityData().getInteger("SB.SPHOLDID") + holdLimit)){
                	player.getEntityData().removeTag("SB.SPHOLDID");
            		last_ench.setTag("ench", now.copy());
                	return;
                }
            }
            int count = 24;
            float magicDamage = ItemAnimaSheath.AttackAmplifier.get(tag_blade, 4);
            float arc = 360.0f / count;
            player.getEntityData().setInteger("SB.SPHOLDID", currentTime);
            for (int i = 0; i < count; i++) {
                float offset = i * arc;
                EntitySpiralSwords summonedSword = new EntitySpiralSwords(player.world, player, magicDamage, 0, offset);
                if (summonedSword != null) {
                    summonedSword.setHoldId(currentTime);
                    summonedSword.setInterval(holdLimit);
                    summonedSword.setLifeTime(holdLimit);
                    if (ItemAnimaSheath.SummonedSwordColor.exists(tag_blade))
                        summonedSword.setColor(ItemAnimaSheath.SummonedSwordColor.get(tag_blade));
                    ScheduleEntitySpawner.getInstance().offer(summonedSword);
                    //w.spawnEntity(entityDrive);
                }
            }
		}
		last_ench.setTag("ench", now.copy());
	}
		
	@Override
	protected void entityInit() {
		
	}

	public UUID getPlayerID()
	{
		return player_id;
	}
	
	public void setPlayerID(UUID value)
	{
		player_id = value;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasUniqueId("playerid"))
			setPlayerID(compound.getUniqueId("playerid"));
		if (compound.hasKey("last_ench"))
			this.last_ench = compound.getCompoundTag("last_ench");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		UUID id = this.getPlayerID();
		compound.setUniqueId("playerid", id);
		compound.setTag("last_ench", last_ench);
	}
	
}
