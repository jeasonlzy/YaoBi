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
public class HuoBi<T> {
    public String status;   // 状态 ok
    public String ch;       // market.eosusdt.kline.5min
    public long ts;         // 时间戳 1524593961761
    public T data;          // 数据
    public T tick;          // 数据
}
