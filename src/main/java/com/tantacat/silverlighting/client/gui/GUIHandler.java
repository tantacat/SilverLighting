package com.tantacat.silverlighting.client.gui;

import javax.annotation.Nullable;

import com.tantacat.silverlighting.common.Item.ItemProudSoulBag;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler{

	private GUIHandler() {}
	
	public static GUIHandler instance = new GUIHandler();
	
	public final int GUIProudSoulBag = 1;
	
	
	@Nullable
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		EnumHand hand = x == 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.hasTagCompound() && stack.getItem() instanceof ItemProudSoulBag)
			stack.setTagCompound(new NBTTagCompound());
		
		Object Gui = null;
		switch(ID) 
		{
		case 1:
			Gui = new ContainerProudSoulBag(player, stack);
			break;
		default:
			break;
		}
		return Gui;
	}

	@Nullable
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		EnumHand hand = x == 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.hasTagCompound() && stack.getItem() instanceof ItemProudSoulBag)
			stack.setTagCompound(new NBTTagCompound());
		
		Object Gui = null;
		switch(ID) 
		{
		case 1:
			Gui = new GUIProudSoulBag(new ContainerProudSoulBag(player, stack));
			break;
		default:
			break;
		}
		return Gui;
	}

}
