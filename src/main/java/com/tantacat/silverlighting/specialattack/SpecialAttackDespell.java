package com.tantacat.silverlighting.specialattack;

import java.util.Iterator;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterSAs;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.specialattack.ISuperSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class SpecialAttackDespell extends SpecialAttackBase implements ISuperSpecialAttack{

	public int id;
	
	public SpecialAttackDespell() 
	{
		id = toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "despell";
	}

	@Override
	public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
		if (player.world.isRemote) return;
		NBTTagList enchants = stack.getEnchantmentTagList();
		Iterator<NBTBase> iterator = enchants.iterator(); 
		while (iterator.hasNext())
		{
			if (ItemAnimaSheath.ProudSoul.tryAdd(stack.getTagCompound(), -200, false))
			{
				NBTTagCompound enchant = (NBTTagCompound)(iterator.next());
				ItemStack ProudSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1);
				ProudSoul.addEnchantment(Enchantment.getEnchantmentByID(enchant.getShort("id")), enchant.getShort("lvl"));
				player.entityDropItem(ProudSoul, 0);
				iterator.remove();
			}
			else
				break;
		}
		stack.getTagCompound().setTag("ench", enchants);
	}

	@Override
	public void doSuperSpecialAttack(ItemStack stack, EntityPlayer player) {
		ItemAnimaSheath.SpecialAttackType.set(stack.getTagCompound(), RegisterSAs.instance.spell.id);
	}

}
