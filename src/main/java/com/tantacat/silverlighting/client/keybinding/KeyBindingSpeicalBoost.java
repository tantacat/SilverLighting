package com.tantacat.silverlighting.client.keybinding;

import org.lwjgl.input.Keyboard;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.network.PacketSpecialBoost;
import com.tantacat.silverlighting.util.BoostProfileHelper;

import mods.flammpfeil.slashblade.core.CoreProxyClient;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyBindingSpeicalBoost {

	public KeyBinding key = new KeyBinding("key.silverlighting.specialboost", Keyboard.KEY_ADD, "flammpfeil.slashblade");
	public void register()
	{
	    ClientRegistry.registerKeyBinding(key);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onKeyBoardInput(InputEvent.KeyInputEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if(player == null) return;
        if(mc.isGamePaused()) return;
        if(!mc.inGameHasFocus) return;
        if(mc.currentScreen != null) return;
		        
		if (key.isPressed())
		{
			ItemStack blade = CoreProxyClient.lockon.isKeyDown() ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
			if (blade.getItem() instanceof ItemSlashBlade)
			{
				if (BoostProfileHelper.getBoostProfiles(blade).isEmpty()) return;
				String id = ItemAnimaSheath.CurrentItemName.exists(blade.getTagCompound()) 
						? ItemAnimaSheath.CurrentItemName.get(blade.getTagCompound()) :
							blade.getUnlocalizedName();
				SilverLightingMain.network.sendToServer(new PacketSpecialBoost(id));
			}
		}
	}
}
