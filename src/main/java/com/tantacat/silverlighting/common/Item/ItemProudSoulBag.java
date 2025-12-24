package com.tantacat.silverlighting.common.Item;

import com.tantacat.silverlighting.SilverLightingMain;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemProudSoulBag extends Item {
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
		int x = handIn == EnumHand.MAIN_HAND ? 1 : 0;
		playerIn.openGui(SilverLightingMain.instance, 1, worldIn, x, 0, 0);
        return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }
	
	@Override
	 public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!stack.hasTagCompound())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
			nbt.setInteger("max_page", Enchantment.REGISTRY.getKeys().size() / 27 + 1);
			for (int i = 1; i < nbt.getInteger("max_page"); i++)
				nbt.setTag("page"+i, new NBTTagList());
		}
	}
}
