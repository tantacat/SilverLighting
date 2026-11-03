package com.tantacat.silverlighting.network;

import com.tantacat.silverlighting.registers.RegisterVoices;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSwitchVoice implements IMessage{

	public boolean recive_voice;
	
	public PacketSwitchVoice() {};
	public PacketSwitchVoice(boolean flag)
	{
		this.recive_voice = flag;
	}
	
	public static class Handler implements IMessageHandler<PacketSwitchVoice, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSwitchVoice message, MessageContext ctx) {
			if (ctx.side == Side.SERVER)
			{
				EntityPlayerMP player = ctx.getServerHandler().player;
				player.getServerWorld().addScheduledTask(new Runnable() {	
					@Override
					public void run() {
						RegisterVoices.instance.RECIVE_VOICE.set(player.getEntityData(), message.recive_voice);
					}
				});
			}
			return null;
		}
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.recive_voice = ByteBufUtils.readVarShort(buf) == 1;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarShort(buf, this.recive_voice ? 1 : 0);
	}

}
