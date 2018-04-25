package com.lzy.quant.callback;

import com.lzy.quant.bean.KLine;

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
public interface QuantCallback {
    void onSuccess(List<KLine> kLines);
}
