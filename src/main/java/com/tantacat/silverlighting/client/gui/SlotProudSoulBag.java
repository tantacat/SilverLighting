package com.tantacat.silverlighting.client.gui;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SlotProudSoulBag extends Slot{

	public SlotProudSoulBag(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	
	@Override
	public boolean canTakeStack(EntityPlayer playerIn)
    {
		ItemStack item = this.getStack();
		ItemStack tinysoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1);
        return item.isItemEqual(tinysoul) && item.getEnchantmentTagList().tagCount() == 1
        		&& ((NBTTagCompound)item.getEnchantmentTagList().get(0)).getShort("lvl") == 1;
    }
}
