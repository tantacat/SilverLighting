package com.tantacat.silverlighting.registers;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.client.render.RenderLeafSwim;
import com.tantacat.silverlighting.common.entity.EntityBreak;
import com.tantacat.silverlighting.common.entity.EntityDestory;
import com.tantacat.silverlighting.common.entity.EntityLeafSwim;
import com.tantacat.silverlighting.common.entity.EntityMurderous;
import com.tantacat.silverlighting.common.entity.EntityUnswerving;

import mods.flammpfeil.slashblade.client.model.BladeModel;
import mods.flammpfeil.slashblade.client.renderer.entity.InvisibleRender;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.tileentity.DummyTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RegisterRenders {

	private RegisterRenders() {};
	
	public static RegisterRenders instance = new RegisterRenders();
	public final ModelResourceLocation modelLoc = new ModelResourceLocation("flammpfeil.slashblade:model/named/blade.obj");
	public RenderLeafSwim render_leafswim_at_player = new RenderLeafSwim();
	
	public void init()
	{		
		RenderingRegistry.registerEntityRenderingHandler(
                EntityLeafSwim.class,
                new IRenderFactory<EntityLeafSwim>() {
                    @Override
                    public Render<? super EntityLeafSwim> createRenderFor(RenderManager manager)
                    {
                        return new InvisibleRender(manager);
                    }
                });
		RenderingRegistry.registerEntityRenderingHandler(
				EntityDestory.class,
                new IRenderFactory<EntityDestory>() {
                    @Override
                    public Render<? super EntityDestory> createRenderFor(RenderManager manager)
                    {
                        return new InvisibleRender(manager);
                    }
                });
		RenderingRegistry.registerEntityRenderingHandler(
				EntityBreak.class,
                new IRenderFactory<EntityBreak>() {
                    @Override
                    public Render<? super EntityBreak> createRenderFor(RenderManager manager)
                    {
                        return new InvisibleRender(manager);
                    }
                });
		RenderingRegistry.registerEntityRenderingHandler(
				EntityMurderous.class,
                new IRenderFactory<EntityMurderous>() {
                    @Override
                    public Render<? super EntityMurderous> createRenderFor(RenderManager manager)
                    {
                        return new InvisibleRender(manager);
                    }
                });
		RenderingRegistry.registerEntityRenderingHandler(
				EntityUnswerving.class,
                new IRenderFactory<EntityUnswerving>() {
                    @Override
                    public Render<? super EntityUnswerving> createRenderFor(RenderManager manager)
                    {
                        return new InvisibleRender(manager);
                    }
                });
	}
	
	public void registerRenderLayer(FMLInitializationEvent event)
	{
		for (RenderPlayer renderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
	        renderer.addLayer(render_leafswim_at_player);
	    }
	}
	
	public void registerModelRender()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void regist_items_model(ModelRegistryEvent event)
	{
		
		final String head = "silverlighting:icon/"; 
		
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.sl_blade, 0, modelLoc);
	    ForgeHooksClient.registerTESRItemStack(RegisterItems.instance.sl_blade, 0, DummyTileEntity.class);
		OBJLoader.INSTANCE.addDomain(SilverLightingMain.MODID);
		
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 0,
				new ModelResourceLocation(RegisterItems.instance.brokenbamboo.getRegistryName(), "inventory"));
		
		
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 1,
				new ModelResourceLocation(new ResourceLocation(head+"rank_d"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 2,
				new ModelResourceLocation(new ResourceLocation(head+"rank_c"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 3,
				new ModelResourceLocation(new ResourceLocation(head+"rank_b"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 4,
				new ModelResourceLocation(new ResourceLocation(head+"rank_a"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 5,
				new ModelResourceLocation(new ResourceLocation(head+"rank_s"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 6,
				new ModelResourceLocation(new ResourceLocation(head+"rank_ss"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 7,
				new ModelResourceLocation(new ResourceLocation(head+"rank_sss"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 8,
				new ModelResourceLocation(new ResourceLocation(head+"stand"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 9,
				new ModelResourceLocation(new ResourceLocation(head+"soul_eater"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 10,
				new ModelResourceLocation(new ResourceLocation(head+"hundred_kill"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 11,
				new ModelResourceLocation(new ResourceLocation(head+"thousand_kill"), "inventory"));
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.brokenbamboo, 12,
				new ModelResourceLocation(new ResourceLocation(head+"slash"), "inventory"));
		
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.anticrystal, 0,
				new ModelResourceLocation("silverlighting:anticrystal.obj"));
		
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.forgerheart, 0,
				new ModelResourceLocation("silverlighting:forgerheart.obj"));
		
		ModelLoader.setCustomModelResourceLocation(RegisterItems.instance.proudsoulbag, 0,
				new ModelResourceLocation(RegisterItems.instance.proudsoulbag.getRegistryName(), "inventory"));
		
	
	}
	
	@SubscribeEvent
    public void onModelBake(ModelBakeEvent event){
        event.getModelRegistry().putObject(modelLoc, new BladeModel());
    }
	
	//隐藏附魔
	@SubscribeEvent
	public void onToolTipsShow(ItemTooltipEvent event)
	{
		if (event.getEntityPlayer() == null) return;
		if (!event.getEntityPlayer().world.isRemote) return;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
        {
			ItemStack item = event.getItemStack();
			if (!(item.getItem() instanceof ItemSlashBlade)) return;
			
			List<String> list = new ArrayList<String>();
			
			if (item.hasTagCompound())
	        {
				NBTTagList nbttaglist = item.getEnchantmentTagList();

                for (int j = 0; j < nbttaglist.tagCount(); ++j)
                {
                    NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(j);
                    int k = nbttagcompound.getShort("id");
                    int l = nbttagcompound.getShort("lvl");
                    Enchantment enchantment = Enchantment.getEnchantmentByID(k);

                    if (enchantment != null)
                    {
                        list.add(enchantment.getTranslatedName(l));
                    }
                }
	        }
			event.getToolTip().removeAll(list);
        }
	}
}
