package com.lzy.quant.common;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.quant.bean.HuoBi;
import com.lzy.quant.bean.KLine;
import com.lzy.quant.bean.Noticed;
import com.lzy.quant.callback.JsonCallback;
import com.lzy.quant.db.KLineManager;
import com.lzy.quant.db.NoticeManager;
import com.wordplat.ikvstockchart.entry.Entry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2018/4/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class NetUtils {

    private static KLineManager lineManager = KLineManager.getInstance();
    private static NoticeManager noticeManager = NoticeManager.getInstance();

    public static void asyndData(List<String> symbols, List<String> periods) {
        if (symbols == null || periods == null) {
            return;
        }
        final int size = 2000;
        for (final String symbol : symbols) {
            for (final String period : periods) {
                OkGo.<HuoBi<List<KLine>>>get(Urls.history_kline)
                        .params("symbol", symbol)
                        .params("period", period)
                        .params("size", size)
                        .execute(new JsonCallback<HuoBi<List<KLine>>>() {
                            @Override
                            public void onSuccess(Response<HuoBi<List<KLine>>> response) {
                                TreeSet<KLine> set = new TreeSet<>();
                                // 添加原有的数据库中的
                                List<KLine> query = lineManager.query(symbol, period, 500);
                                set.addAll(query);
                                // 添加网络新加的（如果数据库中有，这里就添加不进去了，继续使用数据库的数据）
                                set.addAll(response.body().data);
                                ArrayList<KLine> list = new ArrayList<>(set);

                                QuantUtils.fillData(list, symbol, period);
                                QuantUtils.computeKLineMACD(list);
                                Policy.policy1(list);
                                // 同步的数据全部不提醒
                                Collection<Noticed> noticed = QuantUtils.notNoticed(list);
                                noticeManager.replace(noticed);

                                // 更新数据库
                                lineManager.replace(set);
                            }
                        });
            }
        }
    }

    public static void getKlineData(final String symbol, final String period, final int size) {
        if (symbol == null || period == null) {
            return;
        }
        OkGo.<HuoBi<List<KLine>>>get(Urls.history_kline)
                .params("symbol", symbol)
                .params("period", period)
                .params("size", size)
                .execute(new JsonCallback<HuoBi<List<KLine>>>() {
                    @Override
                    public void onSuccess(Response<HuoBi<List<KLine>>> response) {
                        TreeSet<KLine> set = new TreeSet<>();
                        // 添加原有的数据库中的
                        List<KLine> kLineList = lineManager.query(symbol, period, 500);
                        set.addAll(kLineList);
                        // 添加网络新加的（如果数据库中有，这里就添加不进去了，继续使用数据库的数据）
                        set.addAll(response.body().data);

                        ArrayList<KLine> list = new ArrayList<>(set);
                        QuantUtils.fillData(list, symbol, period);
                        QuantUtils.computeKLineMACD(list);
                        Policy.policy1(list);
                        // 更新数据库
                        lineManager.replace(list);
                        // 增量的数据提醒最新的点
                        KLine line = QuantUtils.noticed(list);
                        Noticed notice = noticeManager.query(line.id + "", line.symbol, line.period);
                        if (notice == null || !notice.noticed) {
                            if (line.buy == Entry.BUY) {
                                System.out.println("提醒: " + symbol + "在" + period + "形成金叉，最低价" + line.close + ", 注意买入！");
                            } else if (line.buy == Entry.SALE) {
                                System.out.println("提醒: " + symbol + "在" + period + "形成死叉，最低价" + line.close + ",注意卖出！");
                            }
                            notice = new Noticed();
                            notice.period = line.period;
                            notice.symbol = line.symbol;
                            notice.id = line.id;
                            notice.noticed = true;
                            noticeManager.replace(notice);
                        }
                    }
                });
    }
}
