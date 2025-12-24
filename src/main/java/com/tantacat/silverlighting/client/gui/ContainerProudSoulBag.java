package com.tantacat.silverlighting.client.gui;

import javax.annotation.Nonnull;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ContainerProudSoulBag extends Container {
	
	public int max_page;
	public int current_page = 1;
	public ItemStack bag;
	public IInventory inventory = new InventoryBasic("silverlighting.proudsoulbag", false, 3 * 9);
	public EntityPlayer player;
	
	public ContainerProudSoulBag(EntityPlayer player, ItemStack stack)
	{		
		this.bag = stack;
		this.player = player;
		
		int enchantment_count = Enchantment.REGISTRY.getKeys().size();
		int page_count = enchantment_count / 27;
		int last_page_count = enchantment_count % 27;
		this.max_page = page_count + 1;			
		
		resetInventorySlots();		
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
		return playerIn.getHeldItemMainhand().isItemEqual(bag) || playerIn.getHeldItemOffhand().isItemEqual(bag);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
    {
		super.onContainerClosed(playerIn);
		savePage(current_page);
    }
	
	private void savePage(int page) {
		NBTTagCompound bag_nbt = bag.getTagCompound();
		NBTTagList page_nbt = new NBTTagList();
		ItemStack tinysoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1);
		for (int i = 0; i < 3 * 9; i++)
		{
			ItemStack item = this.inventorySlots.get(i).getStack();
			if (item.isItemEqual(tinysoul) && item.isItemEnchanted())
				page_nbt.appendTag(item.writeToNBT(new NBTTagCompound()));
		}
		bag_nbt.setTag("page" + current_page, page_nbt);
		bag_nbt.setInteger("max_page", max_page);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 3 * 9)
            {
                if (!this.mergeItemStack(itemstack1, 3 * 9, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 3 * 9, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
	
	public void resetInventorySlots()
	{
		this.inventorySlots.clear();
		
        int i = (3 - 4) * 18;
		for (int j = 0; j < 3; ++j)
        {
            for (int k = 0; k < 9; ++k)
            {
            	int count = k + j * 9;
            	ItemStack item = getItemStack(current_page, count);
            	Slot slot = new Slot(this.inventory, count, 8 + k * 18, 18 + j * 18);
            	slot.putStack(item);
            	this.addSlotToContainer(slot);
            }
        }
				
        for (int l = 0; l < 3; ++l)
        {
            for (int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlotToContainer(new SlotProudSoulBag(this.player.inventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }
        for (int i1 = 0; i1 < 9; ++i1)
        {
			addSlotToContainer(new SlotProudSoulBag(this.player.inventory, i1, 8 + i1 * 18, 161 + i));
        }
	}
	
	private ItemStack getItemStack(int page, int index)
	{
		NBTTagCompound bag_nbt = bag.getTagCompound();
		NBTTagList page_nbt = bag_nbt.getTagList("page"+page, 10);
		return new ItemStack(page_nbt.getCompoundTagAt(index));
	}

	public void onButtonPressed(int ButtonID)
	{
		savePage(current_page);
		int new_page = this.current_page;
		switch (ButtonID)
		{
		case 0:
			new_page = (this.current_page - 1 == 0) ? this.max_page : this.current_page - 1;
			break;
		case 1:
			new_page = (this.current_page == this.max_page) ? 1 : this.current_page + 1;
			break;
		default:
			break;
		}
		this.current_page = new_page;
		resetInventorySlots();
	}
}
