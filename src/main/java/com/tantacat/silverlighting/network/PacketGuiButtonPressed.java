package com.tantacat.silverlighting.network;

import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.common.Item.ItemProudSoulBag;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/** 客户端 -> 服务端：界面上的翻页按钮 */
public class PacketGuiButtonPressed implements IMessage{

	public int guiId;
	public int subId;
	
	public PacketGuiButtonPressed() {}
	public PacketGuiButtonPressed(int guiId, int subId)
	{
		this.guiId = guiId;
		this.subId = subId;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.guiId = ByteBufUtils.readVarShort(buf);
		this.subId = ByteBufUtils.readVarShort(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarShort(buf, guiId);
		ByteBufUtils.writeVarShort(buf, subId);
	}

	public static class Handler implements IMessageHandler<PacketGuiButtonPressed, IMessage>{
		@Override
		public IMessage onMessage(final PacketGuiButtonPressed message, MessageContext ctx) {
			
			if (ctx.side == Side.SERVER)
			{
				// 数据包默认在网络线程处理，必须回到服务端主线程再开界面
				final EntityPlayerMP player = ctx.getServerHandler().player;
				WorldServer world = player.getServerWorld();
				if (world != null)
				{
					world.addScheduledTask(new Runnable() {
						@Override
						public void run() {
							// 袋子可能在副手，按实际拿着袋子的那只手重新打开界面
							int hand = player.getHeldItemMainhand().getItem() instanceof ItemProudSoulBag
									? EnumHand.MAIN_HAND.ordinal() : EnumHand.OFF_HAND.ordinal();
							player.openGui(ModMain.instance, message.guiId, player.world, message.subId, hand, 0);
						}
					});
				}
			}	
			return null;
		}
	}
	
}
