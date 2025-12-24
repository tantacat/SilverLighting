package com.tantacat.silverlighting;

import com.tantacat.silverlighting.client.gui.GUIHandler;
import com.tantacat.silverlighting.registers.RegisterAdvancements;
import com.tantacat.silverlighting.registers.RegisterBlades;
import com.tantacat.silverlighting.registers.RegisterBoosts;
import com.tantacat.silverlighting.registers.RegisterEntities;
import com.tantacat.silverlighting.registers.RegisterEvents;
import com.tantacat.silverlighting.registers.RegisterItems;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.registers.RegisterSEs;
import com.tantacat.silverlighting.registers.RegisterVoices;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event)
    {
    	RegisterItems.instance.init();
    	RegisterEntities.instance.init();
    	RegisterSAs.instance.init();
    	RegisterSEs.instance.init();
    	RegisterBoosts.instance.init();
    	RegisterBlades.instance.init();    	
    }

    public void init(FMLInitializationEvent event)
    {
    	RegisterEvents.instance.init();
    	RegisterAdvancements.instance.init();
    	RegisterVoices.instance.init();
    	
    	NetworkRegistry.INSTANCE.registerGuiHandler(SilverLightingMain.instance, GUIHandler.instance);
    }
    
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }
	
}
