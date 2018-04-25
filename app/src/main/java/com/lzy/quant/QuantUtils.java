package com.lzy.quant;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.quant.bean.HuoBi;
import com.lzy.quant.bean.KLine;
import com.lzy.quant.callback.JsonCallback;
import com.lzy.quant.callback.QuantCallback;
import com.lzy.quant.common.MACDUtils;
import com.lzy.quant.db.KLineManager;

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
public class QuantUtils {

    private static KLineManager lineManager = KLineManager.getInstance();

    public static void asyndData(List<String> symbols, List<String> periods) {
        if (symbols == null || periods == null) {
            return;
        }
        for (final String symbol : symbols) {
            for (final String period : periods) {
                OkGo.<HuoBi<List<KLine>>>get(Urls.history_kline)
                        .params("symbol", symbol)
                        .params("period", period)
                        .params("size", 2000)
                        .execute(new JsonCallback<HuoBi<List<KLine>>>() {
                            @Override
                            public void onSuccess(Response<HuoBi<List<KLine>>> response) {
                                List<KLine> kLines = fillData(response.body().data, symbol, period);
                                lineManager.replace(kLines);
                            }
                        });
            }
        }
    }

    public static void getKlineData(final String symbol, final String period, int size, final QuantCallback callback) {
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
                        List<KLine> kLines = fillData(response.body().data, symbol, period);
                        lineManager.replace(kLines);
                        MACDUtils.fillMACD(kLines, 12, 26, 9);
                        callback.onSuccess(kLines);
                    }
                });
    }

    private static List<KLine> fillData(List<KLine> data, String symbol, String period) {
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
}
