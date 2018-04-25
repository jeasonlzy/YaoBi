/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.quant.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lzy.okgo.OkGo;
import com.lzy.quant.bean.KLine;
import com.lzy.quant.bean.MACD;
import com.lzy.quant.bean.Noticed;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DBHelper extends SQLiteOpenHelper {

    private static final String DB_CACHE_NAME = "huobi.db";
    private static final int DB_CACHE_VERSION = 3;
    static final String TABLE_KLINE = "kline";
    static final String TABLE_NOTICE = "notice";

    static final Lock lock = new ReentrantLock();
    private static DBHelper dbHelper = new DBHelper();

    private TableEntity kLineTableEntity = new TableEntity(TABLE_KLINE);
    private TableEntity noticeTableEntity = new TableEntity(TABLE_NOTICE);

    public static DBHelper getInstance() {
        return dbHelper;
    }

    private DBHelper() {
        this(OkGo.getInstance().getContext());
    }

    private DBHelper(Context context) {
        super(context, DB_CACHE_NAME, null, DB_CACHE_VERSION);

        kLineTableEntity.addColumn(new ColumnEntity(KLine.ID, "VARCHAR"))
                .addColumn(new ColumnEntity(KLine.SYMBOL, "VARCHAR"))
                .addColumn(new ColumnEntity(KLine.PERIOD, "VARCHAR"))
                .addColumn(new ColumnEntity(KLine.OPEN, "VARCHAR"))
                .addColumn(new ColumnEntity(KLine.CLOSE, "VARCHAR"))
                .addColumn(new ColumnEntity(KLine.LOW, "VARCHAR"))
                .addColumn(new ColumnEntity(KLine.HIGH, "VARCHAR"))
                .addColumn(new ColumnEntity(KLine.AMOUNT, "VARCHAR"))
                .addColumn(new ColumnEntity(KLine.COUNT, "VARCHAR"))
                .addColumn(new ColumnEntity(KLine.VOL, "VARCHAR"))
                .addColumn(new ColumnEntity(KLine.BUY, "VARCHAR"))
                .addColumn(new ColumnEntity(MACD.FAST_EMA, "VARCHAR"))
                .addColumn(new ColumnEntity(MACD.SLOW_EMA, "VARCHAR"))
                .addColumn(new ColumnEntity(MACD.DIF, "VARCHAR"))
                .addColumn(new ColumnEntity(MACD.DEA, "VARCHAR"))
                .addColumn(new ColumnEntity(MACD.MACD, "VARCHAR"))
                .unionPrimaryKey(KLine.ID, KLine.SYMBOL, KLine.PERIOD);

        noticeTableEntity.addColumn(new ColumnEntity(Noticed.ID, "VARCHAR"))
                .addColumn(new ColumnEntity(Noticed.SYMBOL, "VARCHAR"))
                .addColumn(new ColumnEntity(Noticed.PERIOD, "VARCHAR"))
                .addColumn(new ColumnEntity(Noticed.NOTICED, "VARCHAR"))
                .unionPrimaryKey(Noticed.ID, Noticed.SYMBOL, Noticed.PERIOD);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(kLineTableEntity.buildTableString());
        db.execSQL(noticeTableEntity.buildTableString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DBUtils.isNeedUpgradeTable(db, kLineTableEntity))
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KLINE);
        if (DBUtils.isNeedUpgradeTable(db, noticeTableEntity))
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTICE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
