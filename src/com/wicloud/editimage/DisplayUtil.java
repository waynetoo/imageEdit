package com.wicloud.editimage;

import android.content.Context;

/**
 * Android大小单位转换工具类
 * 
 */
public class DisplayUtil {

	/**
	 * dip转换成px
	 * 
	 * @param context
	 * @param dipValue dip值
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px转换成dip
	 * 
	 * @param context
	 * @param pxValue px值
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * pt转换成px
	 * 
	 * @param ptValue pt值
	 * @return
	 */
	public static float pt2px(float ptValue) {
		return (ptValue * 4 / 3);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param context
	 * @param pxValue px值
	 * @return
	 */
	public static float px2sp(Context context, float pxValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		System.out.println(fontScale + "  fontScale");
		return (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param context
	 * @param spValue sp值
	 * @return
	 */
	public static float sp2px(Context context, float spValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (spValue * fontScale + 0.5f);
	}

	/**
	 * pt转换成sp
	 *
	 * @param context
	 * @param ptValue pt值
	 * @return
	 */
	public static float pt2sp(Context context, float ptValue) {
		return px2sp(context, pt2px(ptValue));

	}

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @param scale DisplayMetrics类中属性density
	 * @return
	 */
	@Deprecated
	public static int px2dip(float pxValue, float scale) {
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @param scale DisplayMetrics类中属性density
	 * @return
	 */
	@Deprecated
	public static int dip2px(float dipValue, float scale) {
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale DisplayMetrics类中属性scaledDensity
	 * @return
	 */
	@Deprecated
	public static int px2sp(float pxValue, float fontScale) {
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale DisplayMetrics类中属性scaledDensity
	 * @return
	 */
	@Deprecated
	public static int sp2px(float spValue, float fontScale) {
		return (int) (spValue * fontScale + 0.5f);
	}

}
