package com.lzy.quant.common;

import com.lzy.quant.bean.KLine;
import com.lzy.quant.bean.Noticed;
import com.lzy.quant.db.NoticeManager;
import com.wordplat.ikvstockchart.entry.Entry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
public class QuantUtils {

    public static SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());

    public static void fillData(Collection<KLine> data, String symbol, String period) {
        if (data != null) {
            Date date = new Date();
            for (KLine line : data) {
                if (line != null) {
                    line.period = period;
                    line.symbol = symbol;
                    date.setTime(line.id * 1000);
                    line.ts = format.format(date);
                }
            }
        }
    }

    public static void computeEntryMACD(List<Entry> entries) {
        computeEntryMACD(entries, 12, 26, 9);
    }

    public static void computeEntryMACD(List<Entry> entries, int fast, int slow, int dif) {
        float emaFast = 0;
        float emaSlow = 0;
        float diff = 0;
        float dea = 0;
        float macd = 0;

        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);

            if (i == 0) {
                emaFast = entry.getClose();
                emaSlow = entry.getClose();
            } else {
                // EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
                // EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                emaFast = emaFast * (fast - 1) / (fast + 1) + entry.getClose() * 2f / (fast + 1);
                emaSlow = emaSlow * (slow - 1) / (slow + 1) + entry.getClose() * 2f / (slow + 1);
            }

            // DIF = EMA（12） - EMA（26） 。
            // 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
            // 用（DIF-DEA）*2 即为 MACD 柱状图。
            diff = emaFast - emaSlow;
            dea = dea * (dif - 1) / (dif + 1) + diff * 2f / (dif + 1);
            macd = (diff - dea) * 2f;

            entry.setDiff(diff);
            entry.setDea(dea);
            entry.setMacd(macd);
        }
    }

    public static void computeKLineMACD(List<KLine> kLines) {
        computeKLineMACD(kLines, 12, 26, 9);
    }

    public static void computeKLineMACD(List<KLine> kLines, int fast, int slow, int dif) {
        float fastEma = 0;
        float slowEma = 0;
        float diff = 0;
        float dea = 0;
        float macd = 0;

        for (int i = 0; i < kLines.size(); i++) {
            KLine line = kLines.get(i);
            float close = (float) Utils.parseDouble(line.close);

            if (i == 0) {
                fastEma = close;
                slowEma = close;
            } else {
                // EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
                // EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                fastEma = fastEma * (fast - 1) / (fast + 1) + close * 2f / (fast + 1);
                slowEma = slowEma * (slow - 1) / (slow + 1) + close * 2f / (slow + 1);
            }

            // DIF = EMA（12） - EMA（26） 。
            // 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
            // 用（DIF-DEA）*2 即为 MACD 柱状图。
            diff = fastEma - slowEma;
            dea = dea * (dif - 1) / (dif + 1) + diff * 2f / (dif + 1);
            macd = (diff - dea) * 2f;

            line.macd.fastEma = fastEma;
            line.macd.slowEma = slowEma;
            line.macd.dif = diff;
            line.macd.dea = dea;
            line.macd.macd = macd;
        }
    }

    public static Collection<Noticed> notNoticed(List<KLine> kLines) {
        List<Noticed> data = new ArrayList<>();
        for (KLine line : kLines) {
            if (line.buy == Entry.BUY || line.buy == Entry.SALE) {
                Noticed noticed = new Noticed();
                noticed.id = line.id;
                noticed.period = line.period;
                noticed.symbol = line.symbol;
                noticed.noticed = true;
                data.add(noticed);
            }
        }
        return data;
    }

    private static NoticeManager noticeManager = NoticeManager.getInstance();

    public static KLine noticed(List<KLine> kLines) {
        if (kLines == null) {
            return null;
        }
        for (int i = kLines.size() - 1; i >= 0; i--) {
            KLine line = kLines.get(i);
            if (line.buy == Entry.BUY || line.buy == Entry.SALE) {
                return line;
            }
        }
        return null;
    }
}