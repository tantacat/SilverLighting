package com.tantacat.silverlighting.network;

import com.tantacat.silverlighting.client.gui.ContainerProudSoulBag;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketGuiButtonPressed implements IMessage{

	public int button_id;
	
	public PacketGuiButtonPressed() {}
	public PacketGuiButtonPressed(int id)
	{
		this.button_id = id;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.button_id = ByteBufUtils.readVarShort(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarShort(buf, button_id);
	}

	public static class Handler implements IMessageHandler<PacketGuiButtonPressed, IMessage>{
		@Override
		public IMessage onMessage(PacketGuiButtonPressed message, MessageContext ctx) {
			
			if (ctx.side == Side.SERVER)
			{
				EntityPlayer player = ctx.getServerHandler().player;
				player.getServer().addScheduledTask(new Runnable() {
					@Override
					public void run() {
						if (player.openContainer instanceof ContainerProudSoulBag)
						{
							ContainerProudSoulBag container = (ContainerProudSoulBag)player.openContainer;
							container.onButtonPressed(message.button_id);
						}
					}
				});
			}	
			return null;
		}
	}
	
}
