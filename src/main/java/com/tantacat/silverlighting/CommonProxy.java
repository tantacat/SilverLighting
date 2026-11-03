package com.tantacat.silverlighting;

import com.tantacat.silverlighting.client.gui.GuiBagHandler;
import com.tantacat.silverlighting.registers.*;
import com.tantacat.silverlighting.util.CacheManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event)
    {
    	RegisterItems.instance().init();
    	RegisterEntities.instance.init();
    	RegisterSAs.instance.init();
    	RegisterSEs.instance.init();
    	RegisterBoosts.instance.init();
    }

    public void init(FMLInitializationEvent event)
    {
		RegisterBlades.instance.init();
		RegisterEvents.instance.init();
		MinecraftForge.EVENT_BUS.register(CacheManager.instance);
    	RegisterAdvancements.instance.init();
    	RegisterVoices.instance.init();
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(ModMain.instance, new GuiBagHandler());
    }
    
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }
	
}
