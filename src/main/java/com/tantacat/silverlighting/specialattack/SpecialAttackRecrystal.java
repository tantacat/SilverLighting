package com.tantacat.silverlighting.specialattack;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;

import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityDrive;
import mods.flammpfeil.slashblade.item.ItemSlashBlade.ComboSequence;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SpecialAttackRecrystal extends SpecialAttackBase{

	public int id;
	
	public SpecialAttackRecrystal() {
		id = toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "recrystal";
	}

	@Override
	public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
		
		if (player.world.isRemote) return;
		
		boolean drop_diamond = false;
		if (stack.getItem() instanceof ItemSlashBladeWrapper)
		{
			ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
			NBTTagCompound innertag = new NBTTagCompound();
			innerblade.setTagCompound(innertag);
			int maxDamage = stack.getMaxDamage();
			if (maxDamage >= 64)
			{
				ItemAnimaSheath.CustomMaxDamage.set(innertag, maxDamage - 64);
				SlashBlade.wrapBlade.setWrapItem(stack, innerblade);
				drop_diamond = true;
			}
		}
		else
		{
			int maxDamage = ItemAnimaSheath.CustomMaxDamage.get(stack.getTagCompound());
			if (maxDamage >= 64)
			{
				ItemAnimaSheath.CustomMaxDamage.set(stack.getTagCompound(), maxDamage - 64);
				drop_diamond = true;
			}
		}
		
		if (drop_diamond)
		{
			player.entityDropItem(new ItemStack(Items.DIAMOND), 0);
			if (ItemAnimaSheath.CurrentItemName.get(stack.getTagCompound()).equals("silverlighting.animasheath_phos"))
			{
				player.setAbsorptionAmount(player.getAbsorptionAmount() + 4);
				float damage = ItemAnimaSheath.AttackAmplifier.get(stack.getTagCompound());
				EntityDrive entityDrive = new EntityDrive(player.world, player, damage, true, 90.0f - ComboSequence.Iai.swingDirection);
	            if (entityDrive != null) {
	                entityDrive.setInitialSpeed(1.5f);
	                entityDrive.setLifeTime(10);
	                player.world.spawnEntity(entityDrive);
	            }
			}
		}
	}
	
}
