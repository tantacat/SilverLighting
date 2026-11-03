package com.tantacat.silverlighting.client.gui;

import net.minecraft.client.gui.Gui;

/**
 * 只给界面用的绘制 / 几何辅助（放在这里，避免容器、存储这些双端类去引用客户端专用类型）。
 */
final class BagGuiHelper {

	/** 滚动条滑块的最小高度 */
	static final int MIN_THUMB = 12;

	/** 滚动条/格子命中判定（绝对坐标） */
	static boolean isIn(int x, int y, int width, int height, int mouseX, int mouseY) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	/** 滑块高度：可见行数占总行数的比例（总行数不超过可见行数时占满） */
	static int thumbHeight(int trackHeight, int totalRows, int visibleRows) {
		if (totalRows <= visibleRows || totalRows <= 0) return trackHeight;
		int h = (int) Math.round((double) trackHeight * visibleRows / totalRows);
		return Math.max(MIN_THUMB, Math.min(trackHeight, h));
	}

	/** 滑块位置：把行偏移线性映射到剩余轨道上 */
	static int thumbY(int trackY, int trackHeight, int thumbHeight, int row, int maxRow) {
		if (maxRow <= 0) return trackY;
		int span = Math.max(0, trackHeight - thumbHeight);
		return trackY + (int) Math.round((double) span * row / maxRow);
	}

	/** 拖动滑块时反推行偏移 */
	static int rowForThumbY(int trackY, int trackHeight, int thumbHeight, int mouseY, int maxRow) {
		if (maxRow <= 0) return 0;
		int span = Math.max(0, trackHeight - thumbHeight);
		if (span <= 0) return 0;
		double ratio = (double) (mouseY - trackY) / span;
		int row = (int) Math.round(ratio * maxRow);
		return Math.max(0, Math.min(maxRow, row));
	}

	/** 颜色与界面贴图保持一致的原版灰阶 */
	private static final int THUMB_FILL = 0xFFC6C6C6;
	private static final int THUMB_LIGHT = 0xFFFFFFFF;
	private static final int THUMB_DARK = 0xFF555555;

	/**
	 * 画右侧位置指示器的滑块（只有滑块）。
	 * <p>
	 * 滑槽本身由界面贴图 {@code special.png} 提供，这里<b>不画槽、不铺底</b>，
	 * 以免盖住贴图槽自带的描边与立体感。贴图里那条槽逐像素是：
	 * <pre>
	 *   x=174  #373737  左描边
	 *   x=175..185 #8b8b8b  槽底
	 *   x=186  #373737  右描边
	 *   x=187  #ffffff  窗口边框高光
	 * </pre>
	 * 所以滑块左边界取 {@code x+2}（176）、右边界取 {@code x+width-2}（184），
	 * 与槽的两条描边各留 1px 空隙；纵向同理，滑块在 {@code y .. y+height-1} 之间移动，
	 * 两端都不会碰到槽的上下描边。
	 * <p>
	 * 滑块按原版 GUI 的画法做成凹槽里的"凸起块"：左上 1px 白色高光、右下 1px 深色阴影。
	 *
	 * @param x           滑槽左边界（绝对坐标，贴图里是 174）
	 * @param y           滑块可移动区间的上边界（绝对坐标，贴图里是 19）
	 * @param width       滑槽外宽（贴图里是 13，含左右各 1px 描边）
	 * @param height      滑块可移动区间的高度
	 * @param thumbY      滑块上边界（绝对坐标）
	 * @param thumbHeight 滑块高度（已按可见比例算好）
	 */
	static void drawScrollBar(int x, int y, int width, int height, int thumbY, int thumbHeight) {
		int bottom = y + height;

		int top = Math.max(y, thumbY);
		int bot = Math.min(bottom, thumbY + thumbHeight);
		if (bot - top < 4) return;

		int left = x + 2;                       // 176：与左描边留 1px
		int right = x + width - 2;              // 184：与右描边留 1px

		Gui.drawRect(left, top, right, bot, THUMB_FILL);
		Gui.drawRect(left, top, right, top + 1, THUMB_LIGHT);            // 上高光
		Gui.drawRect(left, top + 1, left + 1, bot - 1, THUMB_LIGHT);     // 左高光
		Gui.drawRect(left + 1, bot - 1, right, bot, THUMB_DARK);         // 下阴影
		Gui.drawRect(right - 1, top + 1, right, bot - 1, THUMB_DARK);    // 右阴影
	}
}
