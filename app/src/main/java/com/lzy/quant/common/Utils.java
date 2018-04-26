package com.lzy.quant.common;

import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * APP工具
 *
 * @author liutao
 */

public class Utils {

    public static float parseFloat(String string) {
        if (TextUtils.isEmpty(string)) {
            return 0;
        }
        try {
            return Float.parseFloat(string);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static double parseDouble(String string) {
        if (TextUtils.isEmpty(string)) {
            return 0;
        }
        try {
            return Double.parseDouble(string);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long parseLong(String string) {
        if (TextUtils.isEmpty(string)) {
            return 0;
        }
        try {
            return Long.parseLong(string);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int parseInt(String string) {
        if (TextUtils.isEmpty(string)) {
            return 0;
        }
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void muteAudioFocus(Context context, boolean bMute) {
        AudioManager am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (bMute) {
            am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        } else {
            am.abandonAudioFocus(null);
        }
    }

    /**
     * 网络是否连接
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return false;
        }
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        return !(netInfo == null || !netInfo.isConnected());
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取App版本号
     */
    public static String getAppVersionName(Context context) {
        String packageName = null;
        String versionName = null;
        int versionCode = 0;
        try {
            packageName = context.getPackageName();
            versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
            versionCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }
}