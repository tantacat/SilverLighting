package com.tantacat.silverlighting.network;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;

import io.netty.buffer.ByteBuf;
import mods.flammpfeil.slashblade.entity.EntityDrive;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketShardstorm implements IMessage 
{

	public String type;
	
	public PacketShardstorm() {}
	public PacketShardstorm(String flag) 
	{
		type = flag;
	}
	
	public static class Handler implements IMessageHandler<PacketShardstorm, IMessage>
	{

		@Override
		public IMessage onMessage(PacketShardstorm message, MessageContext ctx) {
			if (ctx.side == Side.SERVER)
			{
				 EntityPlayerMP player = ctx.getServerHandler().player;
				 World world = player.world;
				 player.getServer().addScheduledTask(() ->{
					 
				     ItemStack main = player.getHeldItemMainhand();
				     ItemStack off = player.getHeldItemOffhand();
				     ItemStack blade = findAnimaSheathPhos(main, off);
					 NBTTagCompound tag_blade = blade.getTagCompound();
				     
				     if (!blade.isEmpty())
				     {
				    	 if (message.type.equals("summonsword"))
						 {
							 float damage = 3 + blade.getMaxDamage() * 0.05f;
							 for (int i = 0; i < 2; i++)
							 {
								 EntitySummonedSwordBase sowrd = new EntitySummonedSwordBase(player.world, player, damage);
								 sowrd.setThrower(player);
								 
								 if (ItemAnimaSheath.SummonedSwordColor.exists(tag_blade))
									 sowrd.setColor(ItemAnimaSheath.SummonedSwordColor.get(tag_blade));
								 
								 world.spawnEntity(sowrd);
							 }
						 }
						 else if (message.type.equals("drive"))
						 {
							 float damage = ItemAnimaSheath.AttackAmplifier.get(blade.getTagCompound(), 4) * 0.2f
									 + blade.getMaxDamage() * 0.02f;
							 int num = Math.min(6, (int)(2 + blade.getMaxDamage() * 0.01));
							 for (int i = 1; i <= num; i++)
							 {
								 EntityDrive drive = new EntityDrive(world, player, damage, true,
										 ItemAnimaSheath.getComboSequence(blade.getTagCompound()).swingDirection);
								 if (drive != null) 
								 {
									 drive.setInitialSpeed(1.5f);
									 drive.setLifeTime(10);
									 
									 double range = (Math.random() - 0.5f) * 4;
									 double pitch = Math.random() * Math.PI;
									 double yaw = Math.random() * Math.PI;
									 double y = range * Math.cos(pitch);
									 double x = range * Math.sin(pitch) * Math.cos(yaw);
									 double z = range * Math.sin(pitch) * Math.sin(yaw);
									 drive.posX += x;
									 drive.posY += y;
									 drive.posZ += z;
									 
									 drive.setThrower(player);
						             world.spawnEntity(drive);
						         }
							 }
						 }
				     }
					 
				 });
			}
			return null;
		}

		private ItemStack findAnimaSheathPhos(ItemStack main, ItemStack off) {
			ItemStack blade = ItemStack.EMPTY;
			String name = "silverlighting.animasheath_phos";
			if (main.hasTagCompound() && ItemAnimaSheath.CurrentItemName.get(main.getTagCompound()).equals(name))
				blade = main;
			else if (off.hasTagCompound() && ItemAnimaSheath.CurrentItemName.get(off.getTagCompound()).equals(name))
				blade = off;
			return blade;
		}
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		type = ByteBufUtils.readUTF8String(buf);
	}
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, type);
	}
	
}
 