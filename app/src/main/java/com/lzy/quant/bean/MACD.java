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
    public Double fastEma;  // 短期（例如12日）收盘价指数移动平均值（Exponential Moving Average）
    public Double slowEma;  // 长期（例如26日）收盘价指数移动平均值（Exponential Moving Average）
    public Double dif;      // 短期EMA和长期EMA的离差值 (Difference)
    public Double dea;      // DIF线的M日指数平滑移动平均线(Difference Exponential Average）
    public Double macd;     // DIF线与DEA线的差

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
