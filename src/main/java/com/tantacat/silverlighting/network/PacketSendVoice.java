package com.tantacat.silverlighting.network;

import io.netty.buffer.ByteBuf;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSendVoice implements IMessage{

	public ItemStack blade;
	public String type;
	public PacketSendVoice() {}
	public PacketSendVoice(String type, ItemStack blade)
	{
		this.type = type;
		this.blade = blade;
	}
	
	public static class Handler implements IMessageHandler<PacketSendVoice, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSendVoice message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT)
			{
				Minecraft.getMinecraft().addScheduledTask(new Runnable() {
					@Override
					public void run() {
						EntityPlayer player = Minecraft.getMinecraft().player;
						ItemStack blade = message.blade;
						String type = message.type;
						String bladename = blade.getDisplayName();
						NBTTagCompound tag = blade.getTagCompound();
						String bladeid = null;
						
						if (ItemSlashBladeNamed.CurrentItemName.exists(tag))
							bladeid = ItemSlashBladeNamed.CurrentItemName.get(tag);
						else
							bladeid = blade.getItem().getRegistryName().toString();
						
						int num = 0;
						StringBuilder key;					
						TextComponentTranslation translate;
						
						StringBuilder text;
						do 
						{
							key = new StringBuilder();
							key.append("silverlighting.voice.").append(type).append(".").append(bladeid).append(".");
							key.append(num);
							translate = new TextComponentTranslation(key.toString());
							text = new StringBuilder(translate.getFormattedText());
							text.delete(text.length()-2, text.length());
							if (text.toString().equals(key.toString()))
								break;
							num ++;
						}
						while (true);
						
						if (num == 0)
							return;
							
						StringBuilder key_text = new StringBuilder();
						int text_i = player.getRNG().nextInt(num);
						key_text.append("silverlighting.voice.").append(type).append(".").append(bladeid).append(".").append(text_i);
						player.sendMessage(new TextComponentString(bladename + ": " + new TextComponentTranslation(key_text.toString()).getFormattedText()));
	
					}
				});
			}
			return null;
		}
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.blade = ByteBufUtils.readItemStack(buf);
		this.type = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeItemStack(buf, blade);
		ByteBufUtils.writeUTF8String(buf, type);
	}

}
