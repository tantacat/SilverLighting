package com.tantacat.silverlighting.common.Item;

import java.util.List;

import com.tantacat.silverlighting.config.ConfigGeneral;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.EntityBladeStand;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.world.World;

public class ItemAntiProudSoul extends Item{

	@Override
	public Item setCreativeTab(CreativeTabs tab)
    {
        return super.setCreativeTab(tab);
    }
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{ 
		
		if (entity instanceof EntityBladeStand && ConfigGeneral.canUseAntiCrystal)
		{
			EntityBladeStand bladestand = (EntityBladeStand)entity;
			ItemStack blade = bladestand.getBlade();
			NBTTagCompound bladetag = blade.getTagCompound();
			
			NBTTagCompound stacktag;
		    if (stack.hasTagCompound())
		    	stacktag = stack.getTagCompound();
		    else 
		    	stack.setTagCompound(stacktag = new NBTTagCompound());
			
		    int needproudsoul = ItemSlashBlade.ProudSoul.get(stacktag, 0);
		    int needkillcout = ItemSlashBlade.KillCount.get(stacktag, 0);
		    int needrepaircount = ItemSlashBlade.RepairCount.get(stacktag);
		    
		    if (ItemSlashBlade.ProudSoul.get(bladetag, 0) >= needproudsoul &&
		    	ItemSlashBlade.KillCount.get(bladetag, 0) >= needkillcout &&
		    	ItemSlashBlade.RepairCount.get(bladetag, 0) >= needrepaircount)
		    {
		    	ItemStack crystal = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.CrystalBladeSoulStr, 1);
		    	NBTTagCompound crystaltag = new NBTTagCompound();
		    	crystal.setTagCompound(crystaltag);
		    	crystal.setTagInfo("BIRTH", new NBTTagLong(bladestand.world.getTotalWorldTime()));
		    	
		    	if (bladetag.hasKey("SB.SEffect"))
		    	{
		    		crystaltag.setTag("SB.SEffect", bladetag.getTag("SB.SEffect"));
				    bladetag.removeTag("SB.SEffect");
		    	}
		    	bladetag.setBoolean("isSealed", true);
		    	
			    ItemSlashBlade.ProudSoul.add(bladetag, -needproudsoul);
			    ItemSlashBlade.KillCount.add(bladetag, -needkillcout);
			    ItemSlashBlade.RepairCount.add(bladetag, -needrepaircount);
			    
			    stack.shrink(1);
			    
			    World world = player.world;
			    EntityItem ItemEntity = new EntityItem(world, bladestand.posX, bladestand.posY + 2.0, bladestand.posZ, crystal);
			    ItemEntity.setDefaultPickupDelay();
			    ItemEntity.setGlowing(true);
			    
			    
			    if (world.isRemote)
			    {
			    	player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 50, 50);
				    player.playSound(SoundEvents.ENTITY_GHAST_AMBIENT, 20, 100);
			    }
			    else
			    	world.spawnEntity(ItemEntity);
			        
		    }
		    
		}
		
		return super.onLeftClickEntity(stack, player, entity);
	}
	
	@Override
	public void addInformation(ItemStack stack, World world, List par3List, ITooltipFlag inFlag) {
		
		super.addInformation(stack, world, par3List, inFlag);
		
		if (!(stack.getItem() instanceof ItemAntiProudSoul)) return;
		
	    NBTTagCompound tag;
	    if (stack.hasTagCompound())
	    	tag = stack.getTagCompound();
	    else 
	    	stack.setTagCompound(tag = new NBTTagCompound());
	    
	    if(tag.hasKey("killCount")) {
	       	par3List.add(String.format("§4KillCount:%d", tag.getInteger("killCount")));
	    }
	    if(tag.hasKey("ProudSoul")) {
	       	par3List.add(String.format("§4ProudSoul:%d", tag.getInteger("ProudSoul")));
	    }
	    if(tag.hasKey("RepairCounter")) {
	       	par3List.add(String.format("§4Refine:%d", tag.getInteger("RepairCounter")));
	    }
		
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack) {
		return true;
	}
	
}
