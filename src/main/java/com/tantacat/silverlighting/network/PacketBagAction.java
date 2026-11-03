package com.tantacat.silverlighting.network;

import com.tantacat.silverlighting.client.gui.ContainerBagBase;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 客户端 -> 服务端：在耀魂袋界面的一个格子上点了一下。
 *
 * <p>只发三样东西：
 * <ul>
 * <li>{@link #key}：点的是<b>哪一种耀魂</b>（数量恒为 1，只用于按物品身份匹配）。
 *     袋子里每种耀魂只有一条记录（相同标识会被合并），所以"物品身份"就是无歧义主键，
 *     不需要下标、id 或版本号——搜索筛选、翻页、内容随时变化都不会取错东西；</li>
 * <li>{@link #button}：左键 / 右键 / 中键；</li>
 * <li>{@link #shift}：是否按住 Shift。</li>
 * </ul>
 *
 * <p>取多少个由服务端自己按按钮和袋子里真实的数量决定，<b>不采信客户端报的数量</b>。
 */
public class PacketBagAction implements IMessage {

	/** 点击的耀魂（数量恒为 1） */
	public ItemStack key = ItemStack.EMPTY;
	/** 0=左键 1=右键 2=中键 */
	public int button;
	/** 是否按住 Shift（尽量搬进背包） */
	public boolean shift;

	public PacketBagAction() {
	}

	public PacketBagAction(ItemStack key, int button, boolean shift) {
		this.key = key == null ? ItemStack.EMPTY : key;
		this.button = button;
		this.shift = shift;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.key = ByteBufUtils.readItemStack(buf);
		this.button = buf.readByte() & 0xFF;
		this.shift = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// 注意：原版物品序列化把数量写成 byte，所以这里发的 key 必须只有 1 个
		ByteBufUtils.writeItemStack(buf, this.key);
		buf.writeByte(this.button);
		buf.writeBoolean(this.shift);
	}

	public static class Handler implements IMessageHandler<PacketBagAction, IMessage> {
		@Override
		public IMessage onMessage(final PacketBagAction message, MessageContext ctx) {
			if (ctx.side == Side.SERVER) {
				// 数据包默认在网络线程处理，必须回到服务端主线程再碰容器
				final EntityPlayerMP player = ctx.getServerHandler().player;
				WorldServer world = player.getServerWorld();
				if (world != null) {
					world.addScheduledTask(new Runnable() {
						@Override
						public void run() {
							if (player.openContainer instanceof ContainerBagBase) {
								((ContainerBagBase) player.openContainer)
										.handleBagAction(message.key, message.button, message.shift);
							}
						}
					});
				}
			}
			return null;
		}
	}
}
