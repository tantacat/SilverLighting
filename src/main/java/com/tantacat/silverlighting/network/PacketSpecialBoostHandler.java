package com.tantacat.silverlighting.network;

import com.tantacat.silverlighting.registers.RegisterBoosts;
import com.tantacat.silverlighting.util.BoostProfile;
import com.tantacat.silverlighting.util.BoostProfile.BoostType;
import com.tantacat.silverlighting.util.BoostProfileHelper;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSpecialBoostHandler implements IMessageHandler<PacketSpecialBoost, IMessage>{

	@Override
	public IMessage onMessage(PacketSpecialBoost message, MessageContext ctx) {
		if (ctx.side == Side.SERVER)
		{
			// 1. 获取服务器线程环境
			EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> {
				// 2. 在主线程中安全操作
	            ItemStack main_stack = player.getHeldItemMainhand();
	            if (!main_stack.isEmpty()) {
	            	// 3. 检查物品有效性（可选）
	                if (main_stack.getItem() instanceof ItemSlashBlade) {
	                	// 4. 修改NBT
	                    if (!main_stack.hasTagCompound()) {
	                    	main_stack.setTagCompound(new NBTTagCompound());
	                        return;
	                    }
	                    if (!BoostProfileHelper.hasBoostProfile(main_stack)) return;
	                    BoostProfile boost = BoostProfileHelper.getBoostProfiles(main_stack).get(0);
	                    //自动关闭的增幅
	                    if (!boost.getEnable())
	                    {
	                    	if (boost.getType() == BoostType.fight || boost.getType() == BoostType.help)
	                    	{
	                    		ItemStack off_stack = player.getHeldItemOffhand();
	 		                    if (BoostProfileHelper.hasBoostProfile(off_stack)) 
	 		                    {
	 		                    	BoostProfile off_boost = BoostProfileHelper.getBoostProfiles(off_stack).get(0);
		 	                    	if (off_boost.getId().equals(boost.getId()) && off_boost.getEnable())
		 	                    		switchBoost(off_stack, off_boost, player);
	 		                    }
	                    	}
	                    	
	                    	if(boost.getType() == BoostType.help)
	                    	{
	                    		for (ItemStack blade : player.inventory.mainInventory)
	 	                    	{
		 		                    if (!BoostProfileHelper.hasBoostProfile(blade)) continue;
		 	                    	BoostProfile blade_boost = BoostProfileHelper.getBoostProfiles(blade).get(0);
	 	                    		if (blade_boost.getId().equals(boost.getId()) && blade_boost.getEnable())
	 	                    			switchBoost(blade, blade_boost, player);
	 	                    	}
	                    	}
	                    	
	                    	if (boost.getType() == BoostType.add)
	                    	{
	                    		ItemStack off_stack = player.getHeldItemOffhand();
	 		                    if (BoostProfileHelper.hasBoostProfile(off_stack)) 
	 		                    {
	 		                    	BoostProfile off_boost = BoostProfileHelper.getBoostProfiles(off_stack).get(0);
		 	                    	if (off_boost.getType() == BoostType.add && off_boost.getEnable())
		 	                    		switchBoost(off_stack, off_boost, player);
	 		                    }
	                    		for (ItemStack blade : player.inventory.mainInventory)
	 	                    	{
		 		                    if (!BoostProfileHelper.hasBoostProfile(blade)) continue;
		 	                    	BoostProfile blade_boost = BoostProfileHelper.getBoostProfiles(blade).get(0);
	 	                    		if (blade_boost.getType() == BoostType.add && blade_boost.getEnable())
	 	                    			switchBoost(blade, blade_boost, player);
	 	                    	}
	                    	}
	                    }
	                    //启用或关闭增幅
	                    switchBoost(main_stack, boost, player);
	                    // 5. 强制同步物品到客户端
	                    player.inventory.markDirty();
	                    player.openContainer.detectAndSendChanges(); // 更新容器
	                }
	            }
	        });
			
		}
		return null;
	}
	
	public static void switchBoost(ItemStack blade, BoostProfile old_boost, EntityPlayer player)
	{
		boolean enable = old_boost.getEnable();
		BoostProfile newboost = new BoostProfile(old_boost.getId(), !old_boost.getEnable(), old_boost.getType());
        BoostProfileHelper.replaceDamageProfile(blade, old_boost.getId(), newboost);
        if (RegisterBoosts.instance.BoostsHasSwitch.containsKey(old_boost.getId()))
        {
        	if (enable)
        		RegisterBoosts.instance.BoostsHasSwitch.get(old_boost.getId()).onBoostClose(blade, player);
        	else
        		RegisterBoosts.instance.BoostsHasSwitch.get(old_boost.getId()).onBoostOpen(blade, player);
        }
	}
	
}
