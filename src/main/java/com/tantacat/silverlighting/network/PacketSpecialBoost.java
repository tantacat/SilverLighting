package com.tantacat.silverlighting.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSpecialBoost implements IMessage {

	public String bladeid;
	
	public PacketSpecialBoost( ) {};
	public PacketSpecialBoost(String bladeid)
	{
		this.bladeid = bladeid;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		bladeid = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, bladeid);
	}

}
