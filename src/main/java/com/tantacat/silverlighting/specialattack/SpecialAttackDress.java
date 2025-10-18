package com.tantacat.silverlighting.specialattack;

import mods.flammpfeil.slashblade.ItemSlashBladeDetune;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SpecialAttackDress extends SpecialAttackBase{

	public int id;
	
	public SpecialAttackDress() 
	{
		id = toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "dress";
	}

	@Override
	public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
		ItemStack offhand = player.getHeldItemOffhand();
		
		if (offhand.getItem() instanceof ItemSlashBladeDetune)
		{
			ItemSlashBlade.TextureName.set(stack.getTagCompound(),
					((ItemSlashBladeDetune)offhand.getItem()).getModelTexture().getResourcePath().replaceFirst("model/", "").replaceFirst(".png", "")); 
			ItemSlashBlade.ModelName.set(stack.getTagCompound(), "blade");
		}
		else if (offhand.getItem() instanceof ItemSlashBlade)
		{
			NBTTagCompound offtag = offhand.getTagCompound();
			NBTTagCompound maintag = stack.getTagCompound();
			
			ItemSlashBlade.ModelName.set(maintag, ItemSlashBlade.ModelName.exists(offtag) ? 
					ItemSlashBlade.ModelName.get(offtag) : ItemSlashBlade.ModelName.set(maintag, "blade"));
			
			ItemSlashBlade.TextureName.set(maintag, ItemSlashBlade.TextureName.exists(offtag) ? 
					ItemSlashBlade.TextureName.get(offtag) : ItemSlashBlade.TextureName.set(maintag, "blade"));
		}
		
	}
	
}
