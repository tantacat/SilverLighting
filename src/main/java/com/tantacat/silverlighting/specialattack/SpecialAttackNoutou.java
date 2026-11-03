package com.tantacat.silverlighting.specialattack;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade.ComboSequence;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;

public class SpecialAttackNoutou extends SpecialAttackBase{

	public int id;
	
	public SpecialAttackNoutou()
	{
		id = toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "noutou";
	}

	@Override
	public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
		if (!(stack.getItem() instanceof ItemSlashBladeWrapper)) return;
		
		ItemStack off = player.getHeldItemOffhand();
		if (off.isItemStackDamageable() && !(off.getItem() instanceof ItemSlashBlade))
		{
			if (((ItemSlashBladeWrapper)stack.getItem()).hasWrapedItem(stack))
			{
				stack.setItemDamage(stack.getMaxDamage());
				ItemAnimaSheath.damageItem(stack, 10, player);
			}
			
			ItemSlashBlade.setComboSequence(stack.getTagCompound(), ComboSequence.Battou);
			player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0f, 1.0f);
			
			((ItemSlashBladeWrapper)stack.getItem()).setWrapItem(stack, off);
			stack.setItemDamage(0);
			
			off.shrink(1);
		}
	}

}
