package com.tantacat.silverlighting;

import com.tantacat.silverlighting.registers.RegisterKeyBindings;
import com.tantacat.silverlighting.registers.RegisterRenders;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy{

	@Override
	public void preInit(FMLPreInitializationEvent event)
    {
    	super.preInit(event);
    	RegisterRenders.instance.init();
    	RegisterRenders.instance.registerModelRender();
    }

	@Override
    public void init(FMLInitializationEvent event)
    {
    	super.init(event);
    	RegisterRenders.instance.registerRenderLayer(event);
    	RegisterKeyBindings.instance.init();
    }
    
	@Override
    public void postInit(FMLPostInitializationEvent event)
    {
    	super.postInit(event);
    }
	
}
