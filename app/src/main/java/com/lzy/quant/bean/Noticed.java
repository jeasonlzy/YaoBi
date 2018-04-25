package com.lzy.quant.bean;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2018/4/26
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class Noticed {
    public static final String ID = "id";
    public static final String SYMBOL = "symbol";
    public static final String PERIOD = "period";
    public static final String NOTICED = "noticed";

    public long id;         // K线id
    public String symbol;   // 交易对 btcusdt, bchbtc, rcneth ...
    public String period;   // K线类型 1min, 5min, 15min, 30min, 60min, 1day, 1mon, 1week, 1year
    public boolean noticed; // 是否已经提醒过了

    public static Noticed parseCursorToBean(Cursor cursor) {
        Noticed noticed = new Noticed();
        noticed.id = cursor.getLong(cursor.getColumnIndex(ID));
        noticed.symbol = cursor.getString(cursor.getColumnIndex(SYMBOL));
        noticed.period = cursor.getString(cursor.getColumnIndex(PERIOD));
        noticed.noticed = cursor.getInt(cursor.getColumnIndex(NOTICED)) == 1;
        return noticed;
    }

    public static ContentValues getContentValues(Noticed noticed) {
        ContentValues values = new ContentValues();
        values.put(ID, noticed.id);
        values.put(SYMBOL, noticed.symbol);
        values.put(PERIOD, noticed.period);
        values.put(NOTICED, noticed.noticed);
        return values;
    }

    @Override
    public String toString() {
        return "Noticed{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", period='" + period + '\'' +
                ", noticed=" + noticed +
                '}';
    }
}
