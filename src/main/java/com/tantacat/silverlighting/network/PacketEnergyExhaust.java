package com.tantacat.silverlighting.network;

import com.tantacat.silverlighting.util.OtherUtills;
import io.netty.buffer.ByteBuf;
import mods.flammpfeil.slashblade.capability.BladeCapabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketEnergyExhaust implements IMessage {

    int index;

    public PacketEnergyExhaust() {}
    public PacketEnergyExhaust(int index) {this.index = index;}

    @Override
    public void fromBytes(ByteBuf buf) {
        index = ByteBufUtils.readVarShort(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarShort(buf, index);
    }

    public static class Handler implements IMessageHandler<PacketEnergyExhaust, IMessage> {
        @Override
        public IMessage onMessage(PacketEnergyExhaust message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        EntityPlayer player = Minecraft.getMinecraft().player;
                        ItemStack blade = player.inventory.getStackInSlot(message.index);
                        OtherUtills.changeBladeEnergyCap(blade, BladeCapabilityProvider.ENERGY, null, s -> {
                            s.extractEnergy(s.getEnergyStored(), false);
                        });
                    }
                });
            }
            return null;
        }
    }
}
