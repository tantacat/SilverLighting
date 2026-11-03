package com.tantacat.silverlighting.registers;

import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.common.Item.ItemAntiProudSoul;
import com.tantacat.silverlighting.common.Item.ItemProudSoulBag;
import com.tantacat.silverlighting.recipe.RecipeForgerHeart;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RegisterItems {
	
	public static RegisterItems instance ()
	{
		return new RegisterItems();
	}
	
	//public static RegisterItems instance = new RegisterItems();
	public final CreativeTabs creativetab_silverlighting = new CreativeTabs("silverlighting") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(SlashBlade.bladeSilverBambooLight);
		}
	};
	
	public static Item brokenbamboo = new Item();
	public static ItemAntiProudSoul anticrystal = new ItemAntiProudSoul();
	public static ItemAnimaSheath sl_blade = new ItemAnimaSheath(ToolMaterial.WOOD);
	public static Item forgerheart = new Item()
	{
		@Override
		public boolean hasEffect(ItemStack stack) {
			return true;
		}
	};
	public static ItemProudSoulBag proudsoulbag = new ItemProudSoulBag();
	
	@SubscribeEvent
	public void regist_items(RegistryEvent.Register<Item> event)
	{		
		brokenbamboo.setUnlocalizedName("silverlighting.brokenbamboo")
					.setRegistryName(ModMain.MODID, "brokenbamboo")
					.setCreativeTab(creativetab_silverlighting);
		
		anticrystal.setUnlocalizedName("silverlighting.anticrystal")
				   .setRegistryName(ModMain.MODID, "anticrystal")
				   .setCreativeTab(creativetab_silverlighting);
		
		sl_blade.setUnlocalizedName("silverlighting.sl_blade")
				.setRegistryName(ModMain.MODID, "animasheath")
				.setCreativeTab(creativetab_silverlighting);
		
		forgerheart.setUnlocalizedName("silverlighting.forgerheart")
				   .setRegistryName(ModMain.MODID, "forgerheart")
				   .setCreativeTab(creativetab_silverlighting);
		
		proudsoulbag.setUnlocalizedName("silverlighting.proudsoulbag")
					.setRegistryName(ModMain.MODID, "proudsoulbag")
					.setMaxStackSize(1)
					.setCreativeTab(creativetab_silverlighting);
				   
		event.getRegistry().registerAll(brokenbamboo, anticrystal, forgerheart, proudsoulbag, sl_blade);
	}

	@SubscribeEvent
	public void registRecipe(RegistryEvent.Register<IRecipe> event) {

		NonNullList<ItemStack> SASphere = NonNullList.create();
		for (int i : ItemSlashBlade.specialAttacks.keySet()) {
			ItemStack sphere = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.SphereBladeSoulStr, 1);
			sphere.setTagCompound(new NBTTagCompound());
			ItemSlashBlade.SpecialAttackType.set(sphere.getTagCompound(), i);
			SASphere.add(sphere);
		}
		Ingredient ingredient = Ingredient.fromStacks(SASphere.toArray(new ItemStack[0]));

		ItemStack forgerheart = new ItemStack(RegisterItems.forgerheart);

		RecipeForgerHeart recipe = new RecipeForgerHeart(new ResourceLocation(ModMain.MODID, "forgerheart_recipe"), forgerheart,
				"789",
				"456",
				"123",
				'7', ingredient, '8', ingredient       , '9', ingredient,
				'4', ingredient, '5', Items.NETHER_STAR, '6', ingredient,
				'1', ingredient, '2', ingredient       , '3', ingredient );
		SlashBlade.addRecipe("forgerheart_recipe", recipe);
	}

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
