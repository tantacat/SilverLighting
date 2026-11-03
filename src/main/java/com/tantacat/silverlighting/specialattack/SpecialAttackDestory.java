package com.tantacat.silverlighting.specialattack;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.common.entity.EntityDestory;

import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class SpecialAttackDestory extends SpecialAttackBase{

	public int id;
	
	public SpecialAttackDestory() {
		id = toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "destory";
	}

	@Override
	public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
		
		int proudsoul = ItemAnimaSheath.ProudSoul.get(stack.getTagCompound(), 0);
		if (proudsoul < 10) return ;
		
		int distance = 2 + Math.min(5, proudsoul / 20000);
		EntityDestory destory = new EntityDestory(player.world, player.getLookVec().normalize(),
				player.rotationPitch, player.rotationYawHead, 60, distance);
		destory.setPosition(player.posX, player.posY + 1, player.posZ);
		destory.setThrower(player);
		player.world.spawnEntity(destory);
		
		int reduce = (int)Math.max(10, proudsoul * 0.002f);
		ItemAnimaSheath.ProudSoul.add(stack.getTagCompound(), -reduce);
	}
	
}
