package com.tantacat.silverlighting.client.gui;

import com.tantacat.silverlighting.common.Item.ItemProudSoulBag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

/**
 * 耀魂袋的界面分发。
 * <p>
 * guiId：0 = 主界面，1 / 2 = 附魔 / 特殊页（由 subId 决定是哪一页，与原实现一致）。
 * 两端拿到的是同一组 guiId/subId（FML 的 OpenGui 消息会把 x/y/z 一起发过来），
 * 因此容器与界面用的页面必然一致。
 */
public class GuiBagHandler implements IGuiHandler {

	/** 找出玩家身上拿着耀魂袋的那只手；没有则返回 null（界面不该打开） */
	@Nullable
	private static EnumHand getBagHand(EntityPlayer player) {
		if (player.getHeldItemMainhand().getItem() instanceof ItemProudSoulBag) return EnumHand.MAIN_HAND;
		if (player.getHeldItemOffhand().getItem() instanceof ItemProudSoulBag) return EnumHand.OFF_HAND;
		return null;
	}

	@Nullable
	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int subId, int handIn, int z) {
		// 不直接信任 handIn：界面按钮重新打开时只会传 0，袋子在副手就会打不开
		if (getBagHand(player) == null) return null;

		switch (guiId) {
		case 0:
			return new ContainerBagMainMenu(player);
		case 1:
		case 2:
			return new ContainerBagSpecial(player, BagPage.byGuiId(guiId, subId));
		default:
			return null;
		}
	}

	@Nullable
	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int subId, int handIn, int z) {
		ItemStack stack = player.getHeldItem(EnumHand.values()[Math.floorMod(handIn, EnumHand.values().length)]);
		if (!(stack.getItem() instanceof ItemProudSoulBag) && getBagHand(player) == null) return null;

		switch (guiId) {
		case 0:
			return new GuiBagMainMenu(new ContainerBagMainMenu(player));
		case 1:
		case 2:
			BagPage page = BagPage.byGuiId(guiId, subId);
			return new GuiBagSpecial(new ContainerBagSpecial(player, page), page);
		default:
			return null;
		}
	}
}
