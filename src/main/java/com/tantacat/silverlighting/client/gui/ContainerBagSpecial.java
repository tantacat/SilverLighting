package com.tantacat.silverlighting.client.gui;

import net.minecraft.entity.player.EntityPlayer;

/**
 * 附魔 / 特殊页的容器。
 * <p>
 * 这一页要展示哪些耀魂、搜索词、翻页位置全都由客户端本地计算
 * （内容本来就在物品标签里，客户端有完整数据），所以容器这边不需要任何额外状态，
 * 也不需要"把筛选结果发回服务端"的包。
 */
public class ContainerBagSpecial extends ContainerBagBase {

	public ContainerBagSpecial(EntityPlayer player, BagPage page) {
		super(player, page);
	}
}
