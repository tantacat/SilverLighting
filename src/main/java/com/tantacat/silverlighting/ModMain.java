package com.tantacat.silverlighting;

import com.tantacat.silverlighting.network.*;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ModMain.MODID, name = ModMain.NAME, version = ModMain.VERSION,
guiFactory = "com.tantacat.silverlighting.config.ConfigGuiFactory", dependencies = "required-after:flammpfeil.slashblade")
public class ModMain
{
    public static final String MODID = "silverlighting";
    public static final String NAME = "SilverLighting";
    public static final String VERSION = "1.12.2-2.0.3";
    
    @Mod.Instance
    public static ModMain instance;

    @SidedProxy(clientSide = "com.tantacat.silverlighting.ClientProxy", serverSide = "com.tantacat.silverlighting.CommonProxy")
    public static CommonProxy proxy;
    
    public static SimpleNetworkWrapper network;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    	network.registerMessage(new PacketSpecialBoost.Handler(), PacketSpecialBoost.class, 1, Side.SERVER);
    	network.registerMessage(new PacketSpecialShowSpell.Handler(), PacketSpecialShowSpell.class, 2, Side.CLIENT);
    	network.registerMessage(new PacketShardstorm.Handler(), PacketShardstorm.class, 3, Side.SERVER);
    	network.registerMessage(new PacketSwitchVoice.Handler(), PacketSwitchVoice.class, 4, Side.SERVER);
    	network.registerMessage(new PacketSendVoice.Handler(), PacketSendVoice.class, 5, Side.CLIENT);
    	network.registerMessage(new PacketGuiButtonPressed.Handler(), PacketGuiButtonPressed.class, 6, Side.SERVER);
        network.registerMessage(new PacketBagAction.Handler(), PacketBagAction.class, 7, Side.SERVER);
    	network.registerMessage(new PacketEnergyExhaust.Handler(), PacketEnergyExhaust.class, 8, Side.CLIENT);
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
