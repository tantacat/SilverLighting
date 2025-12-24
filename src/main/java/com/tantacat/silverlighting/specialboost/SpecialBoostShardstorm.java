package com.tantacat.silverlighting.specialboost;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.network.PacketShardstorm;
import com.tantacat.silverlighting.registers.RegisterBoosts;
import com.tantacat.silverlighting.util.BoostProfile;
import com.tantacat.silverlighting.util.BoostProfile.BoostType;
import com.tantacat.silverlighting.util.BoostProfileHelper;

import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade.ComboSequence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class SpecialBoostShardstorm implements IOnBoostSwitch
{

	public BoostProfile profile = new BoostProfile(getId(), false, BoostType.fight);
	private int drive_count = 1;
	private int summonsowrd_count = 1;
	
	@SubscribeEvent
	public void onKeyBoardInput(InputEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if(player == null) return;
        if(mc.isGamePaused()) return;
        if(!mc.inGameHasFocus) return;
        if(mc.currentScreen != null) return;
		        
        ItemStack blade = ItemStack.EMPTY;
        ItemStack main = player.getHeldItemMainhand();
        ItemStack off = player.getHeldItemOffhand();
        if (BoostProfileHelper.isBoostEffective(main, getId()))
        	blade = main;
        else if (BoostProfileHelper.isBoostEffective(off, getId()))
        	blade = off;
        if (blade.isEmpty()) return;
        
		if (event instanceof KeyInputEvent)
		{
			if (summonsowrd_count == 1)
				SilverLightingMain.network.sendToServer(new PacketShardstorm("summonsword"));
			summonsowrd_count = (summonsowrd_count + 1) % 2;
		}
		if (mc.gameSettings.keyBindUseItem.isPressed()) 
		{
			if (ItemAnimaSheath.getComboSequence(blade.getTagCompound()) != ComboSequence.Saya1
					&& ItemAnimaSheath.getComboSequence(blade.getTagCompound()) != ComboSequence.Saya2)
			{
				if (drive_count == 1)
					SilverLightingMain.network.sendToServer(new PacketShardstorm("drive"));
				drive_count = (drive_count + 1) % 2;	
			}
		}
	}
	
	public void register()
	{
		RegisterBoosts.instance.BoostsHasSwitch.put(getId(), this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public String getId()
	{
		return "Shardstorm";
	}

	@Override
	public void onBoostOpen(ItemStack blade, EntityPlayer player) {
		if (blade.getItem() instanceof ItemSlashBladeWrapper)
		{
			ItemStack innerblade = new ItemStack(SlashBlade.bladeNamed);
			NBTTagCompound innertag = new NBTTagCompound();
			innerblade.setTagCompound(innertag);
			int maxDamage = blade.getMaxDamage();
			ItemAnimaSheath.CustomMaxDamage.set(innertag, (int)(maxDamage * 0.8f));
			SlashBlade.wrapBlade.setWrapItem(blade, innerblade);
		}
		else
		{
			int maxDamage = ItemAnimaSheath.CustomMaxDamage.get(blade.getTagCompound());
			ItemAnimaSheath.CustomMaxDamage.set(blade.getTagCompound(), (int)(maxDamage * 0.8f));
		}
	}

	@Override
	public void onBoostClose(ItemStack blade, EntityPlayer player) {
		
	}
	
}
