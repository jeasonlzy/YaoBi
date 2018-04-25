package com.lzy.quant.common;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2018/4/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class Urls {
    public static final String BASE_URL_HUOBI = "https://api.huobipro.com";

    /**
     * 行情API
     * GET /market/history/kline 获取K线数据
     * 请求参数:
     * 参数名称	是否必须	类型	    描述	        默认值	取值范围
     * symbol	true	string	交易对		btcusdt, bchbtc, rcneth ...
     * period	true	string	K线类型		1min, 5min, 15min, 30min, 60min, 1day, 1mon, 1week, 1year
     * size	false	integer	获取数量	150	[1,2000]
     */
    public static final String history_kline = BASE_URL_HUOBI + "/market/history/kline";

}
