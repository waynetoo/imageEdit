package com.wicloud.eidtimage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.media.AudioManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;


/**
 * Terminal Util
 * @author chenj
 * @date 2014-7-30
 */
public class TerminalUtils {

	/**
	 * 产品名称
	 * @Description
	 * @return
	 */
	public static String getProductName() {
		return android.os.Build.MODEL;
	}

	/**
	 * 硬件制造商
	 * @return
	 */
	public static String getManufacturerName() {
		return android.os.Build.MANUFACTURER;
	}

	/**
	 * 设备名称(制造商+产品名称)
	 * @return samsung GT-I9300
	 */
	public static String getPhoneName() {
		return new StringBuffer(android.os.Build.MANUFACTURER).append(" ").append(android.os.Build.MODEL).toString();
	}

	/**
	 * 获取IMSI
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		return tm.getSubscriberId();
	}

	/**
	 * 获取IMEI
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		return tm.getDeviceId();
	}

	/**
	 * SDK Version
	 * @return
	 */
	public static int getSdkVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * SDK Version name
	 * @return
	 */
	public static String getSdkVersionName() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 版本号
	 * @param context
	 * @param defVersionCode
	 * @return
	 */
	public static int versionCode(Context context, int defVersionCode) {
		try {
			PackageManager packageMgr = context.getPackageManager();
			String packageName = context.getPackageName();
			if (packageMgr == null || TextUtils.isEmpty(packageName)) {
				return defVersionCode;
			}

			PackageInfo packageInfo = packageMgr.getPackageInfo(packageName, 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			return defVersionCode;
		}
	}

	/**
	 * 版本名
	 * @param context
	 * @param defVersionName
	 * @return
	 */
	public static String versionName(Context context, String defVersionName) {
		try {
			PackageManager packageMgr = context.getPackageManager();
			String packageName = context.getPackageName();
			if (packageMgr == null || TextUtils.isEmpty(packageName)) {
				return defVersionName;
			}

			PackageInfo packageInfo = packageMgr.getPackageInfo(packageName, 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			return defVersionName;
		}
	}

	/**
	 * Get application name.
	 * @return
	 */
	public static String getApplicationName(Context context) {
		String name = "";
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			name = context.getString(pi.applicationInfo.labelRes);
		} catch (PackageManager.NameNotFoundException e) {
		}

		return name;
	}

	/**
	 * 返回包详细
	 * @param context
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context) {
		PackageManager packageMgr = context.getPackageManager();
		String packageName = context.getPackageName();
		if (packageMgr == null || TextUtils.isEmpty(packageName)) {
			return null;
		}

		try {
			return packageMgr.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * Returns the country code for this locale
	 * @param context
	 * @return 当前语言环境地区的国家代码
	 */
	public static String countryCode(Context context) {
		if (null == context) return "";

		return context.getResources().getConfiguration().locale.getCountry();
	}

	/**
	 * 比较国家代码
	 * @param context
	 * @param countryCode 国家代码
	 * @return true
	 */
	public static boolean equalsCountryCode(Context context, String countryCode) {
		return StringUtils.equals(countryCode, countryCode(context));
	}

	/**
	 * 比较国家代码
	 * @param context
	 * @param countryCode Locale
	 * @return
	 */
	public static boolean equalsCountryCode(Context context, Locale countryCode) {
		return StringUtils.equals(countryCode.getCountry(), countryCode(context));
	}

	/**
	 * 是否为简体中文
	 * @param context
	 * @return
	 */
	public static boolean isChineseCountryCode(Context context) {
		return equalsCountryCode(context, Locale.CHINA.getCountry());
	}

	/**
	 * 设置语言
	 * @param context
	 * @param locale Locale
	 */
	public static void updateConfiguration4Language(Context context, Locale locale) {
		if (null == context || null == locale) {
			return;
		}

		DisplayMetrics dm = new DisplayMetrics(); // 屏幕
		Configuration config = context.getResources().getConfiguration(); // 配置
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);

		config.locale = locale;
		context.getResources().updateConfiguration(config, dm);
	}

	/**
	 * 返回ApplicationInfo
	 * @param context
	 * @return
	 */
	public static ApplicationInfo applicationInfo(Context context) {
		try {
			PackageManager packageMgr = context.getPackageManager();
			String packageName = context.getPackageName();
			if (packageMgr == null || TextUtils.isEmpty(packageName)) {
				return null;
			}

			PackageInfo packageInfo = packageMgr.getPackageInfo(packageName, 0);
			return packageInfo.applicationInfo;
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * 设备密度
	 * @param context
	 * @return
	 */
	public static int terminalDensityDpi(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);

		return dm.densityDpi;
	}

	/**
	 * 终端设备的宽高
	 * @Description
	 * @return
	 */
	public static int[] terminalWH(Context context) {
		int[] wh = new int[2];

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);

		wh[0] = dm.widthPixels;
		wh[1] = dm.heightPixels;

		// int orientation =
		// KTruetouchApplication.mOurApplication.getResources().getConfiguration().orientation;
		// if (orientation == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
		// } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {// 竖屏
		// }

		return wh;
	}

	/**
	 * 终端设备高度
	 * @Description
	 * @return
	 */
	public static int terminalH(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);

		return dm.heightPixels;
	}

	/**
	 * 终端设备的宽度
	 * @Description
	 * @return
	 */
	public static int terminalW(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);

		return dm.widthPixels;
	}

	/**
	 * 终端设备最短边
	 * @param context
	 * @return 7英寸最短边为600dp
	 */
	public static int terminalSmallestScreenWidthDp(Context context) {
		Configuration config = context.getResources().getConfiguration();
		return config.smallestScreenWidthDp;
	}

	public static float computeScale4DensityDpi(Context context, int dpi) {
		return ((float) context.getResources().getDisplayMetrics().densityDpi) / dpi;
	}

	/**
	 * 状态栏的高度
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return statusBarHeight;
	}

	/**
	 * 是否自动旋转
	 * @param context
	 * @return
	 */
	public static boolean isAutoRotate(Context context) {
		if (context == null) return false;

		return Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
	}

	/**
	 * 检测屏幕方向
	 * @param context
	 * @return
	 */
	public static int getScreenOrientation(Context context) {
		Configuration conf = context.getResources().getConfiguration();
		return conf.orientation;
	}

	/**
	 * 是否是横屏
	 * @param context
	 * @return
	 */
	public static boolean isOrientationLandscape(Context context) {
		Configuration conf = context.getResources().getConfiguration();
		if (conf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 屏幕方向
	 * @param context
	 * @return
	 */
	public static int getRotation(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		int rotate = 0;
		switch (d.getRotation()) {
		case Surface.ROTATION_0: // 手机处于正常状态
				rotate = 0;
				break;

		case Surface.ROTATION_90:// 手机旋转90度
				rotate = 1;
				break;

		case Surface.ROTATION_180:// 手机旋转180度
				rotate = 2;
				break;

		case Surface.ROTATION_270:// 手机旋转270度
				rotate = 3;
				break;

			default:
				break;
		}

		return rotate;
	}

	/**
	 * 屏幕方向角度
	 * @param context
	 * @return
	 */
	public static int getRotationAngle(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		int angle = 0;
		switch (d.getRotation()) {
		case Surface.ROTATION_0: // 手机处于正常状态
				angle = 0;
				break;

		case Surface.ROTATION_90:// 手机旋转90度
				angle = 90;
				break;

		case Surface.ROTATION_180:// 手机旋转180度
				angle = 180;
				break;

		case Surface.ROTATION_270:// 手机旋转270度
				angle = 270;
				break;

			default:
				break;
		}

		return angle;
	}

	/**
	 * 屏幕Rect
	 * @param context
	 * @return Rect
	 */
	public Rect getScreenBoundRect(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		Rect rect = new Rect(0, 0, dm.widthPixels, dm.heightPixels);

		return rect;
	}

	/**
	 * 电话号码
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		if (context == null) return "";

		TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		return telephonyMgr.getLine1Number();
	}

	/**
	 * 电话状态
	 * @Description
	 * @param context
	 * @return CALL_STATE_IDLE 无任何状态时; CALL_STATE_OFFHOOK 接起电话时; CALL_STATE_RINGING 电话进来时
	 */
	public static int getCallState(Context context) {
		if (context == null) return 0;

		TelephonyManager telephonMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		return telephonMgr.getCallState();
	}

	/**
	 * 电话详细
	 * @param context
	 * @return
	 */
	@Deprecated
	public static String telephonyInfo(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		String str = "";
		str += "DeviceId(IMEI) = " + tm.getDeviceId() + "\n";
		str += "DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() + "\n";
		str += "Line1Number = " + tm.getLine1Number() + "\n";
		str += "NetworkCountryIso = " + tm.getNetworkCountryIso() + "\n";
		str += "NetworkOperator = " + tm.getNetworkOperator() + "\n";
		str += "NetworkOperatorName = " + tm.getNetworkOperatorName() + "\n";
		str += "NetworkType = " + tm.getNetworkType() + "\n";
		str += "PhoneType = " + tm.getPhoneType() + "\n";
		str += "SimCountryIso = " + tm.getSimCountryIso() + "\n";
		str += "SimOperator = " + tm.getSimOperator() + "\n";
		str += "SimOperatorName = " + tm.getSimOperatorName() + "\n";
		str += "SimSerialNumber = " + tm.getSimSerialNumber() + "\n";
		str += "SimState = " + tm.getSimState() + "\n";
		str += "SubscriberId(IMSI) = " + tm.getSubscriberId() + "\n";
		str += "VoiceMailNumber = " + tm.getVoiceMailNumber() + "\n";

		return str;
	}

	/**
	 * 检查通信是否使用蓝牙SCO
	 * @param context
	 * @return
	 */
	public static boolean isBluetoothScoOn(Context context) {
		if (null == context) {
			return false;
		}

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		return audioManager.isBluetoothScoOn();
	}

	/**
	 * 检查A2DP音频路由到蓝牙耳机是否打开
	 * @param context
	 * @return
	 */
	public static boolean isBluetoothA2dpOn(Context context) {
		if (null == context) {
			return false;
		}

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		return audioManager.isBluetoothA2dpOn();
	}

	/**
	 * 蓝牙是否关闭
	 * @param context
	 * @return
	 */
	public static boolean isBluetoothScoAvailableOffCall(Context context) {
		if (null == context) {
			return false;
		}

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		return audioManager.isBluetoothScoAvailableOffCall();
	}

	/**
	 * 检查麦克风是否静音
	 * @param context
	 * @return
	 */
	public static boolean isMicrophoneMute(Context context) {
		if (null == context) {
			return false;
		}

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		return audioManager.isMicrophoneMute();
	}

	/**
	 * 检查喇叭扩音器是否开着
	 * @param context
	 * @return
	 */
	public static boolean isSpeakerphoneOn(Context context) {
		if (null == context) {
			return false;
		}

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		return audioManager.isSpeakerphoneOn();
	}

	/**
	 * 检查音频路由到有线耳机是否开着
	 * @param context
	 * @return
	 */
	public static boolean isWiredHeadsetOn(Context context) {
		if (null == context) {
			return false;
		}

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		return audioManager.isWiredHeadsetOn();
	}

	/**
	 * 设置听筒模式
	 * @param context
	 * @param useMaxVolume 使用最大音量
	 * @param forceOpenVolume 强制开启音量，当音量为0时有效
	 */
	public static void setReceiverModel(Context context, boolean useMaxVolume, boolean forceOpenVolume) {
		if (null == context) {
			return;
		}

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		/*
		 * if (!audioManager.isSpeakerphoneOn()) { return; }
		 */
		int volumeIndex = 0;
		if (useMaxVolume) {
			volumeIndex = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
		} else {
			volumeIndex = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
			if (volumeIndex <= 0 && forceOpenVolume) {
				volumeIndex = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
			}
		}
		// 关闭扬声器
		audioManager.setSpeakerphoneOn(false);
		audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
		// 设置音量
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volumeIndex, AudioManager.STREAM_VOICE_CALL);
		audioManager.setMode(AudioManager.MODE_IN_CALL);
	}

	/**
	 * 扬声器模式
	 * @param context
	 * @param useMaxVolume 使用最大音量
	 * @param forceOpenVolume 强制开启音量，当音量为0时有效
	 */
	public static void setSpeakerphoneOn(Context context, boolean useMaxVolume, boolean forceOpenVolume) {
		if (null == context) {
			return;
		}

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.isSpeakerphoneOn()) {
			return;
		}

		int volumeIndex = 0;
		if (useMaxVolume) {
			volumeIndex = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
		} else {
			volumeIndex = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
			if (volumeIndex <= 0 && forceOpenVolume) {
				volumeIndex = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
			}
		}
		// 打开扬声器
		audioManager.setSpeakerphoneOn(true);

		// 设置音量
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volumeIndex, AudioManager.STREAM_VOICE_CALL);
		audioManager.setMode(AudioManager.MODE_NORMAL);
	}

	/**
	 * 获取网络类型
	 * @see com.pc.utils.network.NetWorkUtils#getNetworkSubType(android.content.Context)
	 * @Description
	 * @param context
	 * @return NETWORK_TYPE_CDMA 网络类型为CDMA; NETWORK_TYPE_EDGE 网络类型为EDGE; NETWORK_TYPE_EVDO_0 网络类型为EVDO0;
	 *         NETWORK_TYPE_EVDO_A 网络类型为EVDOA; NETWORK_TYPE_GPRS 网络类型为GPRS; NETWORK_TYPE_HSDPA 网络类型为HSDPA
	 *         NETWORK_TYPE_HSPA 网络类型为HSPA; NETWORK_TYPE_HSUPA 网络类型为HSUPA; NETWORK_TYPE_UMTS 网络类型为UMTS
	 *         联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO
	 */
	@Deprecated
	public static int getNetworkType(Context context) {
		if (context == null) return 0;

		TelephonyManager telephonMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		return telephonMgr.getNetworkType();
	}

	/**
	 * 是否为3G网络
	 * <p>
	 * 注意：NETWORK_TYPE_HSPA 和 NETWORK_TYPE_HSUPA 还没有定位是否为联通3G
	 * </p>
	 * @see com.pc.utils.network.NetWorkUtils#is3G(android.content.Context)
	 * @Description
	 * @param context
	 * @return
	 */
	@Deprecated
	public static boolean is3GNetwork(Context context) {
		if (context == null) return false;

		TelephonyManager telephonMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int type = telephonMgr.getNetworkType();
		if (type == TelephonyManager.NETWORK_TYPE_HSDPA // 联通3G
				|| type == TelephonyManager.NETWORK_TYPE_UMTS // 联通3G
				|| type == TelephonyManager.NETWORK_TYPE_EVDO_0 // 电信3G
				|| type == TelephonyManager.NETWORK_TYPE_EVDO_A)// 电信3G
		{
			return true;
		}

		return false;
	}

	/**
	 * 是否是2G网络
	 * @see com.pc.utils.network.NetWorkUtils#is2G(android.content.Context)
	 * @Description
	 * @param context
	 * @return
	 */
	@Deprecated
	public static boolean is2GNetwork(Context context) {
		if (context == null) return false;

		TelephonyManager telephonMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int type = telephonMgr.getNetworkType();
		if (type == TelephonyManager.NETWORK_TYPE_GPRS // 移动和联通2G
				|| type == TelephonyManager.NETWORK_TYPE_CDMA // 电信2G
				|| type == TelephonyManager.NETWORK_TYPE_EDGE) // 联通2G
		{
			return true;
		}

		return false;
	}

	/**
	 * 本机所有安装程序
	 * @param context
	 * @param sort 排序
	 * @return List<ApplicationInfo>
	 */
	public static List<ApplicationInfo> getApplicationInfo(Context context, boolean sort) {
		if (null == context) return null;

		PackageManager appInfo = context.getPackageManager();
		List<ApplicationInfo> listInfo = appInfo.getInstalledApplications(0);

		if (sort) {
			Collections.sort(listInfo, new ApplicationInfo.DisplayNameComparator(appInfo));
		}

		List<ApplicationInfo> data = new ArrayList<ApplicationInfo>();
		try {
			for (int index = 0; index < listInfo.size(); index++) {
				ApplicationInfo content = listInfo.get(index);
				if (content.flags == ApplicationInfo.FLAG_SYSTEM || content.enabled) continue;

				data.add(content);
			}
		} catch (Exception e) {
		}

		return data;
	}


	/**
	 * app是否运行在前台
	 * @param context
	 * @return
	 */
	public static boolean appIsForeground(Context context) {
		if (context == null) return false;
//		if (true) {
//			return isAppOnForeground(context); //使用此方法获取是否在前台，下面的方法当通知中的设置界面在前台时，不能判断。
//		}

		PackageInfo packageInfo = getPackageInfo(context);
		if (null == packageInfo) return false;
		String packageName = packageInfo.packageName;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(2);

		if (tasksInfo != null && !tasksInfo.isEmpty()) {
			int len = tasksInfo.size();
			String topActivityName = tasksInfo.get(0).topActivity.getPackageName();

			// if (android.os.Build.MODEL.equals("MEIZU MX") && len >= 2) { //
			// 魅族
			// if(TTUtil.isScreenLocked()) {
			// topActivityName = tasksInfo.get(1).topActivity.getPackageName();
			// } else {
			// topActivityName = tasksInfo.get(0).topActivity.getPackageName();
			// }
			// }

			// 应用程序位于堆栈的顶层
			if (!StringUtils.isNull(topActivityName) && packageName.equals(topActivityName)) {
				return true;
			}
		}

		// if (tasksInfo.size() > 0) {
		// String topActivityName =
		// tasksInfo.get(0).topActivity.getPackageName();
		// Log.v("toast", "topActivityName is:" + topActivityName);
		//
		// // 应用程序位于堆栈的顶层
		// if (packageName.equals(topActivityName)) {
		// return true;
		// }
		// }

		return false;
	}


	//在进程中去寻找当前APP的信息，判断是否在前台运行
	public static boolean isAppOnForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(
				Context.ACTIVITY_SERVICE);
		String packageName = context.getPackageName();
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 设备是否处于锁屏状态
	 * @param context
	 * @return
	 */
	public static boolean isScreenLocked(Context context) {
		if (context == null) return true;

		KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

		return mKeyguardManager.inKeyguardRestrictedInputMode();
	}

	/**
	 * 计算系统总内存
	 * @param context
	 * @return
	 */
	public static int getTotalMemory(Context context) {
		if (context == null) {
			return 0;
		}

		String str1 = "/proc/meminfo";
		String str2;
		String[] arrayOfString;
		int initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");

			// for (String num : arrayOfString) {
			// // Log.i(str2, num + "\t");
			// }

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() / 1024;// 显示单位为MB

			localBufferedReader.close();
		} catch (IOException e) {
		}

		return initial_memory;
	}

	/**
	 * 返回cpu型号
	 * @return
	 */
	public static String getCpuInfo() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String cpuType = "";
		String[] cpuInfo = {
				"", ""
		}; // 1-cpu型号 //2-cpu频率
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
		}
		cpuType = cpuInfo[0];
		Log.i("Test", "cpuinfo:" + cpuInfo[0] + " --- " + cpuInfo[1]);
		return cpuType;
	}

	/**
	 * 获取CPU序列号
	 * @Description
	 * @return CPU序列号(16位) 读取失败为"0000000000000000"
	 */
	public static String getCPUSerial() {
		String str = "";
		String strCPU = "";
		String cpuAddress = "0000000000000000";
		try {
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo"); // 读取CPU信息
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (int i = 1; i < 100; i++) {// 查找CPU序列号
				str = input.readLine();
				if (str != null) {
					if (str.indexOf("Serial") > -1) {// 查找到序列号所在行
						strCPU = str.substring(str.indexOf(":") + 1, str.length());// 提取序列号
						cpuAddress = strCPU.trim();// 去空格

						break;
					}
				} else {
					break;// 文件结尾
				}
			}
			input.close();
			ir.close();
		} catch (IOException ex) {
		}

		return cpuAddress;
	}

	/**
	 * 资料地址：http://hi.baidu.com/ch_ff/item/e2d74df357f59c0f85d278f9 <br>
	 * 网上方法，未验证
	 * @return
	 */
	public static short readUsage() {
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();
			String[] toks = load.split(" ");
			long idle1 = Long.parseLong(toks[5]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			Thread.sleep(360);

			reader.seek(0);
			load = reader.readLine();
			reader.close();
			toks = load.split(" ");
			long idle2 = Long.parseLong(toks[5]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			return (short) (100 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1)));
		} catch (IOException ex) {
		} catch (Exception e) {
		}

		return 0;
	}

	/**
	 * 获取内存使用率
	 * @param context
	 */
	public static short getMemoryUsage(Context context) {
		if (context == null) {
			return 0;
		}
		short usuge = 0;
		final ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityMgr.getMemoryInfo(memoryInfo);

		// 系统剩余内存
		final long availMem = (memoryInfo.availMem >> 10) >> 10;

		// 已使用内存
		final long usedMen = getTotalMemory(context) - availMem;

		usuge = (short) ((float) usedMen / getTotalMemory(context) * 100);
		return usuge;
	}

	/**
	 * 获取cpu的核数 <br>
	 * Gets the number of cores available in this device, across all processors. Requires: Ability to peruse the
	 * filesystem at "/sys/devices/system/cpu"
	 * @return The number of cores, or 1 if failed to get result
	 */
	public static int getNumCores() {
		try {
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					// Check if filename is "cpu", followed by a single digit
					// number
					if (Pattern.matches("cpu[0-9]", pathname.getName())) {
						return true;
					}
					return false;
				}
			});
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			// Default to return 1 core
			return 1;
		}
	}

	public static int getCPUFrequencyMax() {
		return readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
	}

	private static int readSystemFileAsInt(final String pSystemFile) {
		InputStream in = null;
		String content = "";
		try {
			final Process process = new ProcessBuilder(new String[] {
					"/system/bin/cat", pSystemFile
			}).start();

			in = process.getInputStream();

			final StringBuilder sb = new StringBuilder();
			final Scanner sc = new Scanner(in);
			while (sc.hasNextLine()) {
				sb.append(sc.nextLine());
			}
			content = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(StringUtils.isNull(content)){
			return -1;
		}
		return Integer.parseInt(content);
	}

}
