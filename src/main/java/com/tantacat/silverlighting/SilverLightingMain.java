package com.tantacat.silverlighting;

import com.tantacat.silverlighting.network.PacketGuiButtonPressed;
import com.tantacat.silverlighting.network.PacketSendVoice;
import com.tantacat.silverlighting.network.PacketShardstorm;
import com.tantacat.silverlighting.network.PacketSpecialBoost;
import com.tantacat.silverlighting.network.PacketSpecialBoostHandler;
import com.tantacat.silverlighting.network.PacketSpecialShowSpell;
import com.tantacat.silverlighting.network.PacketSwitchVoice;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = SilverLightingMain.MODID, name = SilverLightingMain.NAME, version = SilverLightingMain.VERSION, 
guiFactory = "com.tantacat.silverlighting.config.ConfigGuiFactory", dependencies = "after:flammpfeil.slashblade")
public class SilverLightingMain
{
    public static final String MODID = "silverlighting";
    public static final String NAME = "SilverLighting";
    public static final String VERSION = "1.12.2-2.0.0";
    
    @Mod.Instance
    public static SilverLightingMain instance;

    @SidedProxy(clientSide = "com.tantacat.silverlighting.ClientProxy", serverSide = "com.tantacat.silverlighting.CommonProxy")
    public static CommonProxy proxy;
    
    public static SimpleNetworkWrapper network;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    	network.registerMessage(new PacketSpecialBoostHandler(), PacketSpecialBoost.class, 1, Side.SERVER);
    	network.registerMessage(new PacketSpecialShowSpell.Handler(), PacketSpecialShowSpell.class, 2, Side.CLIENT);
    	network.registerMessage(new PacketShardstorm.Handler(), PacketShardstorm.class, 3, Side.SERVER);
    	network.registerMessage(new PacketSwitchVoice.Handler(), PacketSwitchVoice.class, 4, Side.SERVER);
    	network.registerMessage(new PacketSendVoice.Handler(), PacketSendVoice.class, 5, Side.CLIENT);
    	network.registerMessage(new PacketGuiButtonPressed.Handler(), PacketGuiButtonPressed.class, 6, Side.SERVER);
    	proxy.preInit(event);
    	
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	proxy.postInit(event);
    }
}
