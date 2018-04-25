package com.lzy.quant.common;

import com.lzy.quant.bean.KLine;

import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2018/4/25
 * 描    述：
 * 修订历史：
 * <p>
 * MACD公式：
 * DIFF : EMA(CLOSE,SHORT) - EMA(CLOSE,LONG);
 * DEA : EMA(DIFF,M);
 * MACD : 2*(DIFF-DEA); //2 为权重
 * <p>
 * 源代码说明：
 * 1、k线的指标值是使用新的List集合来存储的，逻辑为每个点存一个指标的值，
 * 2、KCandleObj 包含开高低收的字段，getNormValue 表示指标的值
 * 3、对于macd指标来说DIFF和DEA为画线逻辑，macd为画矩形逻辑，其中macd的值以0为界点，大于0或者小于0
 * 如果大于0，矩形高度为0到high，如果小于0，矩形高度为low到0。
 * ================================================
 */
public class MACDUtils {

    public static void fillMACD(List<KLine> lines, int fastEMA, int slowEMA, int difEMA) {
        if (lines == null || lines.size() == 0) {
            return;
        }
        int cycle = Math.max(fastEMA, slowEMA);
        if (cycle > lines.size()) {
            return;
        }

        fillFastEMA(lines, fastEMA);
        fillSlowEMA(lines, slowEMA);
        fillDif(lines);
        fillDeaEMA(lines, difEMA);
        fillMacd(lines);
    }

    /**
     * 算出指定索引位置的Ema
     * 对于计算指数平均的初值，如果想作持续性的记录时，可以将第一天的收盘价或需求指数当作指数平均的初值。
     * 若要更精确一些，则可把最近几天的收盘价或需求指数平均，以其平均价位作为初值。
     * 此外。亦可依其所选定的周期单位数，来做为计算平均值的基期数据。
     * <p>
     * 计算平滑系数
     * MACD一个最大的长处，即在于其指标的平滑移动，特别是对一某些剧烈波动的市场，这种平滑移动的特性能够对价格波动作较和缓的描绘，
     * 从而大为提高资料的实用性。不过，在计算EMA前，首先必须求得平滑系数。
     * 所谓的系数，则是移动平均周期之单位数，如几天，几周等等。其公式如下：
     * 平滑系数＝2÷（周期单位数＋1 ）
     * 如
     * 12日EMA的平滑系数 ＝ 2 ÷（12＋1）＝0.1538；
     * 26日EMA平滑系数为 = 2 ÷ (26+1) ＝ 0.0741
     * <p>
     * 计算指数平均值（EMA）
     * 一旦求得平滑系数后，即可用于EMA之运算，公式如下：
     * 今天的指数平均值 ＝ 平滑系数 ×（今天收盘指数－昨天的指数平均值）＋昨天的指数平均值。
     * 依公式可计算出12日EMA
     * 12日EMA＝ 2÷13 ×（今天收盘指数一昨天的指数平均值）＋昨天的指数平均值。
     * ＝（2÷13）×今天收盘指数＋（11÷13）×昨天的指数平均值。
     * 同理，26日EMA亦可计算出：
     * 26日EMA＝（2÷27）×今天收盘指数＋（25÷27）×昨天的指数平均值。
     *
     * @param lines  完整的数据序列
     * @param period 周期
     */
    private static void fillFastEMA(List<KLine> lines, int period) {
        if (lines == null || lines.size() < period) {
            return;
        }

        // 计算初值
        double preEMA = Helper.avgClose(lines, period);
        // 计算EMA
        for (int i = period; i < lines.size(); i++) {
            double todayClose = Helper.parseDouble(lines.get(i).close);
            double currentEma = 2.0d / (period + 1) * (todayClose - preEMA) + preEMA;
            lines.get(i).macd.fastEma = currentEma;
            preEMA = currentEma;
        }
    }

    private static void fillSlowEMA(List<KLine> lines, int period) {
        if (lines == null || lines.size() < period) {
            return;
        }

        // 计算初值
        double preEMA = Helper.avgClose(lines, period);
        // 计算EMA
        for (int i = period; i < lines.size(); i++) {
            double todayClose = Helper.parseDouble(lines.get(i).close);
            double currentEma = 2.0d / (period + 1) * (todayClose - preEMA) + preEMA;
            lines.get(i).macd.slowEma = currentEma;
            preEMA = currentEma;
        }
    }

    /**
     * DIFF : EMA(CLOSE,SHORT) - EMA(CLOSE,LONG);
     *
     * @param lines k线数据
     */
    private static void fillDif(List<KLine> lines) {
        for (KLine line : lines) {
            if (line.macd.fastEma == null || line.macd.slowEma == null) {
                continue;
            }
            line.macd.dif = line.macd.fastEma - line.macd.slowEma;
        }
    }

    /**
     * DEA : EMA(DIFF,M);
     *
     * @param lines  k先数据
     * @param period M周期
     */
    private static void fillDeaEMA(List<KLine> lines, int period) {
        if (lines == null || lines.size() < period) {
            return;
        }

        // 找出数据中第一个有dif值的index
        int firstIndex = period;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).macd.dif != null) {
                firstIndex = i;
                break;
            }
        }
        // 平均值只能在有值的索引后第period开始计算，否者不计算
        int startIndex = firstIndex + period;
        if (lines.size() < startIndex) {
            return;
        }

        // 计算初值
        double preEMA = Helper.avgDif(lines, period);
        // 计算EMA
        for (int i = startIndex; i < lines.size(); i++) {
            double todayClose = lines.get(i).macd.dif;
            double currentEma = 2.0d / (period + 1) * (todayClose - preEMA) + preEMA;
            lines.get(i).macd.dea = currentEma;
            preEMA = currentEma;
        }
    }

    /**
     * MACD线：　DIF线与DEA线的差
     *
     * @param lines k线数据
     */
    private static void fillMacd(List<KLine> lines) {
        for (KLine line : lines) {
            if (line.macd.dif == null || line.macd.dea == null) {
                continue;
            }
            line.macd.macd = line.macd.dif - line.macd.dea;
        }
    }
}