package com.lzy.quant.common;

import android.text.TextUtils;

import com.lzy.quant.bean.KLine;

import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2018/4/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class Helper {
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

    /**
     * 获取收盘价平均值
     */
    public static double avgClose(List<KLine> lines, int period) {
        if (lines == null || lines.size() < period) {
            return 0;
        }
        double sum = 0;
        for (int i = 0; i < period; i++) {
            sum += Helper.parseDouble(lines.get(i).close);
        }
        return sum / period;
    }

    /**
     * 获取MACD中的DIF的平均值
     */
    public static double avgDif(List<KLine> lines, int period) {
        if (lines == null) {
            return 0;
        }
        // 找出数据中第一个有dif值的index
        int firstIndex = period;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).macd.dif != null) {
                firstIndex = i;
            }
        }
        // 平均值只能在有值的索引后第period开始计算，否者不计算
        int startIndex = firstIndex + period;
        if (lines.size() < startIndex) {
            return 0;
        }
        double sum = 0;
        for (int i = startIndex; i < period; i++) {
            sum += lines.get(i).macd.dif;
        }
        return sum / period;
    }
}
