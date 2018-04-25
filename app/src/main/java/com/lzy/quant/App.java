package com.lzy.quant;

import android.app.Application;
import android.content.Context;

import com.lzy.okgo.OkGo;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2018/4/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        OkGo.getInstance().init(this);
    }
}
