package com.lzy.quant.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.lzy.quant.common.Utils;
import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;

import java.io.Serializable;
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
public class KLine implements Serializable, Comparable<KLine> {

    public static final String ID = "id";
    public static final String TS = "ts";
    public static final String SYMBOL = "symbol";
    public static final String PERIOD = "period";
    public static final String OPEN = "open";
    public static final String CLOSE = "close";
    public static final String LOW = "low";
    public static final String HIGH = "high";
    public static final String AMOUNT = "amount";
    public static final String COUNT = "count";
    public static final String VOL = "vol";
    public static final String BUY = "buy";

    public long id;         // K线id
    public String ts;       // K线时间
    public String symbol;   // 交易对 btcusdt, bchbtc, rcneth ...
    public String period;   // K线类型 1min, 5min, 15min, 30min, 60min, 1day, 1mon, 1week, 1year
    public String open;     // 开盘价
    public String close;    // 收盘价,当K线为最晚的一根时，是最新成交价
    public String low;      // 最低价
    public String high;     // 最高价
    public String amount;   // 成交量
    public String count;    // 成交笔数
    public String vol;      // 成交额, 即 sum(每一笔成交价 * 该笔的成交量)

    public int buy;

    public MACD macd = new MACD();

    public static KLine parseCursorToBean(Cursor cursor) {
        KLine kLine = new KLine();
        kLine.id = cursor.getLong(cursor.getColumnIndex(ID));
        kLine.ts = cursor.getString(cursor.getColumnIndex(TS));
        kLine.symbol = cursor.getString(cursor.getColumnIndex(SYMBOL));
        kLine.period = cursor.getString(cursor.getColumnIndex(PERIOD));
        kLine.open = cursor.getString(cursor.getColumnIndex(OPEN));
        kLine.close = cursor.getString(cursor.getColumnIndex(CLOSE));
        kLine.low = cursor.getString(cursor.getColumnIndex(LOW));
        kLine.high = cursor.getString(cursor.getColumnIndex(HIGH));
        kLine.amount = cursor.getString(cursor.getColumnIndex(AMOUNT));
        kLine.count = cursor.getString(cursor.getColumnIndex(COUNT));
        kLine.vol = cursor.getString(cursor.getColumnIndex(VOL));
        kLine.buy = cursor.getInt(cursor.getColumnIndex(BUY));

        kLine.macd.fastEma = cursor.getFloat(cursor.getColumnIndex(MACD.FAST_EMA));
        kLine.macd.slowEma = cursor.getFloat(cursor.getColumnIndex(MACD.SLOW_EMA));
        kLine.macd.dif = cursor.getFloat(cursor.getColumnIndex(MACD.DIF));
        kLine.macd.dea = cursor.getFloat(cursor.getColumnIndex(MACD.DEA));
        kLine.macd.macd = cursor.getFloat(cursor.getColumnIndex(MACD.MACD));
        return kLine;
    }

    public static ContentValues getContentValues(KLine kLine) {
        ContentValues values = new ContentValues();
        values.put(ID, kLine.id);
        values.put(TS, kLine.ts);
        values.put(SYMBOL, kLine.symbol);
        values.put(PERIOD, kLine.period);
        values.put(OPEN, kLine.open);
        values.put(CLOSE, kLine.close);
        values.put(LOW, kLine.low);
        values.put(HIGH, kLine.high);
        values.put(AMOUNT, kLine.amount);
        values.put(COUNT, kLine.count);
        values.put(VOL, kLine.vol);
        values.put(BUY, kLine.buy);

        values.put(MACD.FAST_EMA, kLine.macd.fastEma);
        values.put(MACD.SLOW_EMA, kLine.macd.slowEma);
        values.put(MACD.DIF, kLine.macd.dif);
        values.put(MACD.DEA, kLine.macd.dea);
        values.put(MACD.MACD, kLine.macd.macd);
        return values;
    }

    public static Entry toViewChart(KLine line) {
        float open = Utils.parseFloat(line.open);
        float high = (float) Utils.parseDouble(line.high);
        float low = (float) Utils.parseDouble(line.low);
        float close = (float) Utils.parseDouble(line.close);
        int volume = (int) Utils.parseDouble(line.amount);
        return new Entry(open, high, low, close, volume, line.ts);
    }

    public static EntrySet toViewChart(List<KLine> lines) {
        EntrySet entrySet = new EntrySet();
        if (lines == null) {
            return entrySet;
        }
        for (KLine line : lines) {
            float open = (float) Utils.parseDouble(line.open);
            float high = (float) Utils.parseDouble(line.high);
            float low = (float) Utils.parseDouble(line.low);
            float close = (float) Utils.parseDouble(line.close);
            int volume = (int) Utils.parseDouble(line.amount);
            Entry entry = new Entry(open, high, low, close, volume, line.ts);
            entrySet.addEntry(entry);
        }
        return entrySet;
    }

    public static List<KLine> fillData(List<KLine> data, String symbol, String period) {
        if (data != null) {
            for (KLine line : data) {
                if (line != null) {
                    line.period = period;
                    line.symbol = symbol;
                }
            }
        }
        return data;
    }

    @Override
    public String toString() {
        return "KLine{" +
                "id=" + id +
                ", ts='" + ts + '\'' +
                ", symbol='" + symbol + '\'' +
                ", period='" + period + '\'' +
                ", open='" + open + '\'' +
                ", close='" + close + '\'' +
                ", low='" + low + '\'' +
                ", high='" + high + '\'' +
                ", amount='" + amount + '\'' +
                ", count='" + count + '\'' +
                ", vol='" + vol + '\'' +
                ", buy=" + buy +
                ", macd=" + macd +
                '}';
    }

    @Override
    public int compareTo(@NonNull KLine o) {
        return Long.valueOf(id).compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KLine line = (KLine) o;

        if (id != line.id) return false;
        if (symbol != null ? !symbol.equals(line.symbol) : line.symbol != null) return false;
        return period != null ? period.equals(line.period) : line.period == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (period != null ? period.hashCode() : 0);
        return result;
    }
}
