package com.lzy.quant.bean;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2018/4/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MACD {

    public static final String FAST_EMA = "fastEma";
    public static final String SLOW_EMA = "slowEma";
    public static final String DIF = "dif";
    public static final String DEA = "dea";
    public static final String MACD = "macd";

    public float fastEma;  // 短期（例如12日）收盘价指数移动平均值（Exponential Moving Average）
    public float slowEma;  // 长期（例如26日）收盘价指数移动平均值（Exponential Moving Average）
    public float dif;      // 短期EMA和长期EMA的离差值 (Difference)
    public float dea;      // DIF线的M日指数平滑移动平均线(Difference Exponential Average）
    public float macd;     // DIF线与DEA线的差

    @Override
    public String toString() {
        return "MACD{" +
                "fastEma=" + fastEma +
                ", slowEma=" + slowEma +
                ", dif=" + dif +
                ", dea=" + dea +
                ", macd=" + macd +
                '}';
    }
}
