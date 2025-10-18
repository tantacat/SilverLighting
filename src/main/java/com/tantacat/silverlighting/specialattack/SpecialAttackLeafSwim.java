package com.tantacat.silverlighting.specialattack;

import java.util.List;

import com.tantacat.silverlighting.common.entity.EntityLeafSwim;

import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class SpecialAttackLeafSwim extends SpecialAttackBase{

	public int id;
	
	public SpecialAttackLeafSwim() {
		id = toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "leafswim";
	}

	@Override
	public void doSpacialAttack(ItemStack stack, EntityPlayer player) {	
		
		ItemSlashBlade.setComboSequence(stack.getTagCompound(), ItemSlashBlade.ComboSequence.ReturnEdge);
		Vec3d pos = player.getPositionVector();
		AxisAlignedBB bb = new AxisAlignedBB(pos.x - 10, pos.y - 5, pos.z - 10,
				pos.x + 10, pos.y + 7, pos.z + 10);
		List<Entity> list = player.world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());
		for (Entity curEntity : list)
		{
			 float damage = ItemSlashBlade.AttackAmplifier.get(stack.getTagCompound(), 0);
			 curEntity.attackEntityFrom(DamageSource.causeMobDamage(player), damage * 0.5f);
             if (curEntity instanceof EntityLivingBase)
             	ItemSlashBlade.updateKillCount(stack, (EntityLivingBase)curEntity, player);
             ReflectionAccessHelper.setVelocity(curEntity, 0, 0, 0);
             double len = player.getRNG().nextDouble();
             len = 0.3 + (1.5 - 0.3) * len;
             double radians = player.getRNG().nextDouble() * Math.PI;
             double x = len * Math.sin(radians);
             double z = len * Math.cos(radians);
             double y = Math.sqrt(x * x + z * z) * Math.sqrt(2);
             curEntity.addVelocity(x, y, z);
		}
	
		EntityLeafSwim leafswim = new EntityLeafSwim(player.world, player.getUniqueID(), 5 * 20);
		leafswim.setPosition(player.posX, player.posY + 15, player.posZ);
		player.world.spawnEntity(leafswim);
	}
	
}
