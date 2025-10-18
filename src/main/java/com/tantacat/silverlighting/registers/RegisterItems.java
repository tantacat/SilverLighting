package com.tantacat.silverlighting.registers;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.common.Item.ItemAntiProudSoul;
import com.tantacat.silverlighting.util.RecipeForForgerHeart;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RegisterItems {
	
	private RegisterItems() {}
	
	public static RegisterItems instance = new RegisterItems();
	public final CreativeTabs creativetab_silverlighting = new CreativeTabs("silverlighting") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(SlashBlade.bladeSilverBambooLight);
		}
	};
	
	public Item brokenbamboo = new Item();
	public ItemAntiProudSoul anticrystal = new ItemAntiProudSoul();
	public ItemAnimaSheath sl_blade = new ItemAnimaSheath(ToolMaterial.WOOD);
	public Item forgerheart = new Item();
	
	@SubscribeEvent
	public void regist_items(RegistryEvent.Register<Item> event)
	{		
		brokenbamboo.setUnlocalizedName("silverlighting.brokenbamboo")
					.setRegistryName(SilverLightingMain.MODID, "brokenbamboo")
					.setCreativeTab(creativetab_silverlighting);
		
		anticrystal.setUnlocalizedName("silverlighting.anticrystal")
				   .setRegistryName(SilverLightingMain.MODID, "anticrystal")
				   .setCreativeTab(creativetab_silverlighting);
		
		sl_blade.setUnlocalizedName("silverlighting.sl_blade")
				.setRegistryName(SilverLightingMain.MODID, "animasheath")
				.setCreativeTab(creativetab_silverlighting);
		
		forgerheart.setUnlocalizedName("silverlighting.forgerheart")
				   .setRegistryName(SilverLightingMain.MODID, "forgerheart")
				   .setCreativeTab(creativetab_silverlighting);
				   
		event.getRegistry().registerAll(brokenbamboo, anticrystal, sl_blade, forgerheart);
	}
	
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(this);
		SlashBlade.addRecipe("forgerheart_recipe", RecipeForForgerHeart.instance);
	}
	
}
