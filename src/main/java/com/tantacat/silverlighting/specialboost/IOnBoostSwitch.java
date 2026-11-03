package com.tantacat.silverlighting.specialboost;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IOnBoostSwitch {

	void onBoostOpen(ItemStack blade, EntityPlayer player);
	void onBoostClose(ItemStack blade, EntityPlayer player);
}
