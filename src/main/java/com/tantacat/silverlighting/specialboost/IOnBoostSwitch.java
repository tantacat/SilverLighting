package com.tantacat.silverlighting.specialboost;

import net.minecraft.entity.player.EntityPlayer;

public interface IOnBoostSwitch {

	void onBoostOpen(EntityPlayer player);
	void onBoostClose(EntityPlayer player);
}
