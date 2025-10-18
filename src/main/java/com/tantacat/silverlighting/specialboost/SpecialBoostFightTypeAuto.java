package com.tantacat.silverlighting.specialboost;

import com.tantacat.silverlighting.network.PacketSpecialBoostHandler;
import com.tantacat.silverlighting.util.BoostProfile;
import com.tantacat.silverlighting.util.BoostProfile.BoostType;
import com.tantacat.silverlighting.util.BoostProfileHelper;

import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialBoostFightTypeAuto {

	@SubscribeEvent
	public void onSlashBladeUpdate(SlashBladeEvent.OnUpdateEvent event)
	{	
		if (!(event.entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.entity;
		if (player.world.isRemote) return;
		ItemStack blade = event.blade;
		if (BoostProfileHelper.hasBoostProfile(blade))
		{
			if (blade == player.getHeldItemMainhand() || blade == player.getHeldItemOffhand()) return;
			BoostProfile boost = BoostProfileHelper.getBoostProfiles(blade).get(0);
			if (boost.getType() == BoostType.fight && boost.getEnable())
				PacketSpecialBoostHandler.switchBoost(blade, boost, player);
		}
	}
	
	public void register()
	{
		SlashBladeHooks.EventBus.register(this);
	}
	
}
