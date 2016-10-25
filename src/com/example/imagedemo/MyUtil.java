package com.example.imagedemo;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

public class MyUtil {

	private static String TAG = "MyUtil";
    /**
     * 唯一的toast
     */
    private static Toast mToast = null;

	public synchronized static void showToast(Context mContext, int srcId,
			String more) {
		String str = "";
		try {
			str = mContext.getResources().getString(srcId);
		} catch (Exception e) {
		}
		if (!TextUtils.isEmpty(more))
			str = str + more;
		
		 if (mToast != null) {
            //mToast.cancel();
        } else {
        	mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        }
        mToast.setText(str);
        mToast.show();
	}

	public synchronized static void showToast(Context mContext, String more,
			int srcId, String more1) {
		String str = "";
		try {
			str = mContext.getResources().getString(srcId);
		} catch (Exception e) {
		}
		if (!TextUtils.isEmpty(more))
			str = more + str;
		if (!TextUtils.isEmpty(more1))
			str = str + more1;
		 if (mToast != null) {
	            //mToast.cancel();
	        } else {
	        	mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
	        }
	        mToast.setText(str);
	        mToast.show();
	}

	public static String getString(Context mContext, int srcId) {
		return mContext.getResources().getString(srcId);
	}

	public static void TOAST(Context context, String msg) {
		if (Constant.isDebug)
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void LOG_V(String TAG, String msg) {
		if (Constant.isDebug)
			Log.v(TAG, msg);
	}

	public static void LOG_I(String TAG, String msg) {
		if (Constant.isDebug)
			Log.i(TAG, msg);
	}

	public static void LOG_D(String TAG, String msg) {
		if (Constant.isDebug)
			Log.d(TAG, msg);
	}

	public static void LOG_W(String TAG, String msg) {
		if (Constant.isDebug)
			Log.w(TAG, msg);
	}

	public static void LOG_E(String TAG, String msg) {
		if (Constant.isDebug)
			Log.e(TAG, msg);
	}

	public static void sysout(String clazz, String text) {
		if (Constant.isDebug)
			System.out.println(clazz + " " + text);
	}

	public static int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}



}
