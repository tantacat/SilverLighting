package com.tantacat.silverlighting.specialboost;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Predicate;
import com.tantacat.silverlighting.common.entity.EntityBreak;
import com.tantacat.silverlighting.registers.RegisterBoosts;
import com.tantacat.silverlighting.util.BoostProfile;
import com.tantacat.silverlighting.util.BoostProfile.BoostType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class SpecialBoostBreak implements IOnBoostSwitch 
{
	public BoostProfile profile = new BoostProfile(getId(), false, BoostType.fight);
	
	@Override
	public void onBoostOpen(EntityPlayer player)
	{
		//获取玩家附近的EntityBreak并清除
		Vec3d pos = player.getPositionVector();
		AxisAlignedBB bb = new AxisAlignedBB(pos.x - 10, pos.y - 10, pos.z - 10,
				pos.x + 10, pos.y + 10, pos.z + 10);
		List<EntityBreak> entitys = player.world.getEntitiesWithinAABB(EntityBreak.class, bb, new Predicate<EntityBreak>() {
			@Override
			public boolean apply(EntityBreak input) {
				return input.getPlayerID() == player.getUniqueID();
			}
		});
		for (EntityBreak n : entitys)
			n.setDead();
		
		//添加EntityBreak
		EntityBreak Break = new EntityBreak(player.world, player.getUniqueID());
		Break.setPosition(player.posX, player.posY, player.posZ);
		player.world.spawnEntity(Break);
	}

	@Override
	public void onBoostClose(EntityPlayer player)
	{
		//获取玩家附近的EntityBreak触发方块破坏效果并清除
		Vec3d pos = player.getPositionVector();
		AxisAlignedBB bb = new AxisAlignedBB(pos.x - 10, pos.y - 10, pos.z - 10,
				pos.x + 10, pos.y + 10, pos.z + 10);
		List<EntityBreak> entitys = player.world.getEntitiesWithinAABB(EntityBreak.class, bb, new Predicate<EntityBreak>() {
			@Override
			public boolean apply(EntityBreak input) {
				return input.getPlayerID() == player.getUniqueID();
			}
		});		
		entitys.forEach(new Consumer<EntityBreak>() {
			@Override
			public void accept(EntityBreak t) {
				t.destoryBlocks(true);
				t.setDead();
			}
		});
	}
	
	public void register() 
	{
		RegisterBoosts.instance.BoostsHasSwitch.put(getId(), this);
	}
	
	public String getId()
	{
		return "Break";
	}
	
}
