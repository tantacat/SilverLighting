package com.tantacat.silverlighting.network;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade.SwordType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSpecialShowSpell implements IMessage{
	
	public ItemStack last_blade;
	public ItemStack now_blade;
	
	public PacketSpecialShowSpell() {}
	public PacketSpecialShowSpell(ItemStack last, ItemStack now) 
	{
		last_blade = last;
		now_blade = now;
	}
	
	public static class Handler implements IMessageHandler<PacketSpecialShowSpell, IMessage>
	{	
		@Override
		public IMessage onMessage(PacketSpecialShowSpell message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT)
			{
				// 1. 获取本地线程环境
				Minecraft.getMinecraft().addScheduledTask(new Runnable() {
						@Override
						public void run() {
							EntityPlayer player = Minecraft.getMinecraft().player;
							showToolTips(message.last_blade, message.now_blade, player);
						}
				});
			}
			return null;
		}
		
		private void showToolTips(ItemStack last_blade, ItemStack now_blade, EntityPlayer player)
		{
			List<String> last_tooltips = new ArrayList<String>();
			List<String> now_tooltips = new ArrayList<String>();
			addInformation(last_blade, player, last_tooltips);
			addInformation(now_blade, player, now_tooltips);
			
			int j = 0;
			int i = 0;
			for (; i < last_tooltips.size(); i++)
			{
				String last_line = last_tooltips.get(i);
				String now_line = now_tooltips.get(i + j);
				if (last_line.equals(now_line))
				{
					if (last_line.contains("Kill") || last_line.contains("Proud") || last_line.contains("Refine"))
						player.sendMessage(new TextComponentString(last_line));
				}
				else if (last_line.equals(""))
				{
					player.sendMessage(new TextComponentString(I18n.format("silverlighting.newline" + "§r" + now_line)));
					j++;
				}
				else
					player.sendMessage(new TextComponentString(last_line + "->" + now_line));
					
			}
			
			for (int k = i + j; i < now_tooltips.size(); k++)
				player.sendMessage(new TextComponentString(I18n.format("silverlighting.newline" + "§r " + now_tooltips.get(k))));
			
		}
		
		
		
		
		private void addInformationKillCount(ItemStack stack, List par3List) {
	    	EnumSet<SwordType> swordType = ((ItemSlashBlade)stack.getItem()).getSwordType(stack);
			NBTTagCompound tag = ((ItemSlashBlade)stack.getItem()).getItemTagCompound(stack);

			par3List.add(String.format("%sKillCount : %d", swordType.contains(SwordType.FiercerEdge) ? "§4" : "", ItemSlashBlade.KillCount.get(tag)));
	    }
		
		private void addInformationProudSoul(ItemStack stack, List par3List) {
			EnumSet<SwordType> swordType = ((ItemSlashBlade)stack.getItem()).getSwordType(stack);
			NBTTagCompound tag = ((ItemSlashBlade)stack.getItem()).getItemTagCompound(stack);

			par3List.add(String.format("%sProudSoul : %d", swordType.contains(SwordType.SoulEeater) ? "§5" : "", ItemSlashBlade.ProudSoul.get(tag)));
		}
		
		private void addInformationRepairCount(ItemStack stack, List par3List) {
			NBTTagCompound tag = ((ItemSlashBlade)stack.getItem()).getItemTagCompound(stack);
	        int repair = ItemSlashBlade.RepairCount.get(tag);
	        if(0 < repair){
	            par3List.add(String.format("Refine : %d", repair));
	        }
		}
		
		private void addInformationEnchants(ItemStack stack, List par3List)
		{
			par3List.add("");
			NBTTagList nbttaglist = stack.getEnchantmentTagList();
            for (int j = 0; j < nbttaglist.tagCount(); ++j)
            {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(j);
                int k = nbttagcompound.getShort("id");
                int l = nbttagcompound.getShort("lvl");
                Enchantment enchantment = Enchantment.getEnchantmentByID(k);

                if (enchantment != null)
                {
                    par3List.add(enchantment.getTranslatedName(l));
                }
            }
		}
		
		private void addInformationSpecialEffec(ItemStack stack, EntityPlayer player,List par3List) 
		{
			NBTTagCompound etag = ((ItemSlashBlade)stack.getItem()).getSpecialEffect(stack);

			Set<String> tagKeys = etag.getKeySet();

			if(tagKeys.size() == 0) return;

			int playerLevel = player.experienceLevel;

			for(String key : tagKeys){
				int reqiredLevel = etag.getInteger(key);

				par3List.add(
						I18n.format("slashblade.seffect.name." + key)
						+ "§r "
						+ (reqiredLevel <= playerLevel ? "§c" : "§8") + reqiredLevel);
			}
		}
		
		private void addInformation(ItemStack blade, EntityPlayer player, List par3List)
		{
			addInformationKillCount(blade, par3List);
			addInformationProudSoul(blade, par3List);
			addInformationRepairCount(blade, par3List);
			addInformationSpecialEffec(blade, player, par3List);
			addInformationEnchants(blade, par3List);
		}
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		last_blade = ByteBufUtils.readItemStack(buf);
		now_blade = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeItemStack(buf, last_blade);
		ByteBufUtils.writeItemStack(buf, now_blade);
	}
	
}
