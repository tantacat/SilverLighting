package com.tantacat.silverlighting.client.gui;

/**
 * 耀魂袋的页面。
 * <p>
 * 两个界面（主界面 / 附魔与特殊页）共用同一个容器实现，页面只影响
 * 玩家背包槽位的横向偏移（与旧实现的坐标保持一致，界面上看不出变化）
 * 以及这一页要展示哪些耀魂。
 */
public enum BagPage {

	/** 主界面：固定展示六种耀魂 */
	MAIN(8),
	/** 附魔页：展示带附魔的耀魂 */
	ENCHANTED(9),
	/** 特殊页：展示不带附魔、也不属于六种基础耀魂的耀魂 */
	SPECIAL(9);

	/** 玩家背包槽位的横向偏移（旧实现里主界面是 8，另两个页面是 9） */
	public final int playerSlotX;

	BagPage(int playerSlotX) {
		this.playerSlotX = playerSlotX;
	}

	/** 是不是“附魔 / 特殊”这两个带搜索框和滚动条的页面 */
	public boolean isList() {
		return this != MAIN;
	}

	/**
	 * 按 GUI id 与子 id 解析页面。
	 * <p>
	 * 与原实现保持一致：guiId 0 = 主界面；guiId 1/2 由 subId 决定
	 * （0 = 附魔页，1 = 特殊页）。
	 */
	public static BagPage byGuiId(int guiId, int subId) {
		if (guiId == 0) return MAIN;
		return Math.floorMod(subId, 2) == 0 ? ENCHANTED : SPECIAL;
	}
}
