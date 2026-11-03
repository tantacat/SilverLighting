package com.tantacat.silverlighting.client.gui;

import net.minecraft.entity.player.EntityPlayer;

/**
 * 主界面的容器：固定展示六种耀魂（展示格由 {@link GuiBagMainMenu} 在客户端画出来）。
 */
public class ContainerBagMainMenu extends ContainerBagBase {

	public ContainerBagMainMenu(EntityPlayer player) {
		super(player, BagPage.MAIN);
	}
}
