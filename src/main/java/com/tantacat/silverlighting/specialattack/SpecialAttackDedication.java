package com.tantacat.silverlighting.specialattack;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.common.entity.EntityDedicationVoid;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.registers.RegisterVoices;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialattack.ISuperSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class SpecialAttackDedication extends SpecialAttackBase implements ISuperSpecialAttack{
	
	public int id;
	
	public SpecialAttackDedication()
	{
		id = toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "dedication";
	}

	@Override
	public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
		
		if (player.world.isRemote) return;
		
		String name = ItemAnimaSheath.CurrentItemName.get(stack.getTagCompound());	
		int max_damage2tick = Math.min(stack.getMaxDamage(), 3000) * 20;
		ItemSlashBlade.setComboSequence(stack.getTagCompound(), ItemSlashBlade.ComboSequence.SlashDim);
		switch(name)
		{
		case "silverlighting.silverlighting":
		{
			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, max_damage2tick / 20, 5));
			player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, max_damage2tick / 2, 1));
			player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, max_damage2tick, 1));
			player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, max_damage2tick / 2, 1));
			player.addPotionEffect(new PotionEffect(MobEffects.SPEED, max_damage2tick / 2, 0));
			player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, max_damage2tick / 2, (int)(player.getMaxHealth() * 0.1)));
			if (player.isCreative()) 
				break;
			RegisterVoices.instance.sendMessage(player, "charged", stack);
			stack.setItemDamage(stack.getMaxDamage());
			ItemAnimaSheath.damageItem(stack, 1, player);
			break;
		}
		case "silverlighting.puresilver":
		{
			int current_damage = stack.getMaxDamage() - stack.getItemDamage();
			NBTTagCompound tag_stack = stack.getTagCompound();
			if (current_damage >= 120 || ItemAnimaSheath.ProudSoul.get(tag_stack, 0) * 2 >= 120 - current_damage)
			{
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, max_damage2tick / 20, 2));
				player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, max_damage2tick / 2, 0));
				player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, max_damage2tick / 2, 1));
				player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, max_damage2tick / 2, 0));
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, max_damage2tick / 2, 0));
				if (player.isCreative()) 
					break;
				stack.setItemDamage(stack.getMaxDamage());
				ItemAnimaSheath.ProudSoul.add(tag_stack, -(120 - current_damage) * 2);
			}
			else 
			{
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, max_damage2tick / 20, 4));
				player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, max_damage2tick / 2, 0));
				player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, max_damage2tick, 1));
				player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, max_damage2tick / 2, 0));
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, max_damage2tick / 2, 1));
				player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, max_damage2tick / 2, (int)(player.getMaxHealth() * 0.1)));
				
				EntityDedicationVoid dedication_void = new EntityDedicationVoid(player.world, player, 8, true);
				Vec3d pos = player.getPositionVector().add(player.getLookVec()); 
				pos = pos.addVector(0, 0, 0);
				dedication_void.setPosition(pos.x, pos.y, pos.z);
				Vec3d pos_ = player.getPositionVector();
				AxisAlignedBB bb = new AxisAlignedBB(pos_.x - 8, pos_.y - 8, pos_.z - 8,
						pos_.x + 8, pos_.y + 8, pos_.z + 8);
				dedication_void.setEntityBoundingBox(bb);
				dedication_void.setLifeTime(8 * 20);
				player.world.spawnEntity(dedication_void);
				
				if (player.isCreative()) 
					break;
				stack.setItemDamage(stack.getMaxDamage());
				ItemAnimaSheath.damageItem(stack, 1, player);
			}
			break;
		}
		default:
		{
			double time = 20 * Math.log(stack.getMaxDamage() + 1);
			player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, (int)time * 20, 0));
			player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, (int)time * 20, 0));
			break;
		}
		}
	}

	@Override
	public void doSuperSpecialAttack(ItemStack stack, EntityPlayer player) {
		
		if (player.world.isRemote) return;
		
		NBTTagCompound tag_blade = stack.getTagCompound();
		if (ItemAnimaSheath.CurrentItemName.get(tag_blade).equals("silverlighting.puresilver"))
		{
			int id_hope = RegisterSAs.instance.hope.id;
			ItemAnimaSheath.SpecialAttackType.set(tag_blade, id_hope);
		}
	}
}
