package com.lzy.quant.common;

import com.lzy.quant.bean.KLine;
import com.lzy.quant.bean.MACD;
import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;

import java.util.Collection;
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
public class Policy {

    private static final int IS_NONE = 0;
    private static final int IS_UP = 1;
    private static final int IS_DOWN = 2;

    /**
     * 标准的金叉和死叉
     */
    public static void policy1(EntrySet entrySet) {
        int status = IS_NONE;
        Entry preEntry = null;
        for (Entry entry : entrySet.getEntryList()) {
            if (preEntry != null) {
                if (entry.getDiff() >= entry.getDea() && status != IS_UP) {
                    entry.setMacdBuy(Entry.BUY);
                    status = IS_UP;
                } else if (entry.getDiff() <= entry.getDea() && status != IS_DOWN) {
                    entry.setMacdBuy(Entry.SALE);
                    status = IS_DOWN;
                }
            }
            preEntry = entry;
        }
    }

    public static void policy1(Collection<KLine> kLines) {
        int status = IS_NONE;
        KLine preLine = null;
        for (KLine line : kLines) {
            if (preLine != null) {
                MACD macd = line.macd;
                if (macd.dif >= macd.dea && status != IS_UP) {
                    line.buy = Entry.BUY;
                    status = IS_UP;
                } else if (macd.dif <= macd.dea && status != IS_DOWN) {
                    line.buy = Entry.SALE;
                    status = IS_DOWN;
                }
            }
            preLine = line;
        }
    }

    /**
     * 转折点
     */
    public static void policy2(EntrySet entrySet) {
        int status = IS_NONE;
        Entry preEntry = null;
        for (Entry entry : entrySet.getEntryList()) {
            if (preEntry != null) {
                if (entry.getDiff() >= preEntry.getDiff()) {
                    if (status != IS_UP) {
                        entry.setMacdBuy(Entry.BUY);
                        status = IS_UP;
                    }
                } else if (entry.getDiff() <= preEntry.getDiff()) {
                    if (status != IS_DOWN) {
                        entry.setMacdBuy(Entry.SALE);
                        status = IS_DOWN;
                    }
                }
            }
            preEntry = entry;
        }
    }

    public static void policy2(List<KLine> kLines) {
        int status = IS_NONE;
        KLine preLine = null;
        for (KLine line : kLines) {
            if (preLine != null) {
                if (line.macd.dif >= preLine.macd.dif) {
                    if (status != IS_UP) {
                        line.buy = Entry.BUY;
                        status = IS_UP;
                    }
                } else if (line.macd.dif <= preLine.macd.dif) {
                    if (status != IS_DOWN) {
                        line.buy = Entry.SALE;
                        status = IS_DOWN;
                    }
                }
            }
            preLine = line;
        }
    }

    /**
     * 转折点过滤
     */
    public static void policy3(EntrySet entrySet) {
        int status = IS_NONE;
        Entry preEntry = null;
        Entry preEntry2 = null;
        for (Entry entry : entrySet.getEntryList()) {
            if (preEntry != null && preEntry2 != null) {
                if (entry.getDiff() >= preEntry.getDiff() && status != IS_UP) {
                    entry.setMacdBuy(Entry.BUY);
                    status = IS_UP;
                } else if (entry.getDiff() <= preEntry.getDiff() && preEntry.getDiff() <= preEntry2.getDiff() && status != IS_DOWN) {
                    entry.setMacdBuy(Entry.SALE);
                    status = IS_DOWN;
                }
            }
            preEntry2 = preEntry;
            preEntry = entry;
        }
    }

    /**
     * 收益率日志
     */
    public static void log(EntrySet entrySet) {
        // 第一次从买开始算
        float price = 0;
        float win = 0;
        int count = 0;
        for (Entry entry : entrySet.getEntryList()) {
            if (entry.isMacdBuy()) {
                price = entry.getClose();
                count++;
                System.out.println(entry.getXLabel() + " 买入：" + price);
            } else if (entry.isMacdSale()) {
                if (price != 0) {
                    float priceSale = entry.getClose();
                    float winSale = (priceSale - price) / price;
                    win += winSale;
                    count++;
                    System.out.println(entry.getXLabel() + " 卖出：" + priceSale + " 收益：" + winSale);
                }
            }
        }
        System.out.println("总计收益：" + win);
        System.out.println("交易次数：" + count);
        System.out.println("净收益：" + (win - count * 0.002));
    }
}
