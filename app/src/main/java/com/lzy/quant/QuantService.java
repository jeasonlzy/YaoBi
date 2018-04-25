package com.lzy.quant;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lzy.quant.bean.KLine;
import com.lzy.quant.bean.Period;
import com.lzy.quant.callback.QuantCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2018/4/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class QuantService extends Service {

    private List<String> symbols = new ArrayList<>();
    private List<String> periods = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 在API11之后构建Notification的方式
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent nfIntent = new Intent(this, MainActivity.class);

        PendingIntent activity = PendingIntent.getActivity(this, 0, nfIntent, 0);
        builder.setContentIntent(activity)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("量化前台")    // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("廖子尧")      // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(110, notification);// 开始前台服务

        asyncData();

        asyncByPeriod();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 60秒执行一次
     */
    private void asyncByPeriod() {
        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                for (String symbol : symbols) {
//                    QuantUtils.getKlineData(symbol, Period.MIN_1, 10, new QuantCallback() {
//                        @Override
//                        public void onSuccess() {
//                            //MACD
//                        }
//                    });
//                }
//            }
//        }, 0, Period.MIN_1_MS);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (String symbol : symbols) {
                    QuantUtils.getKlineData(symbol, Period.MIN_5, 10, new QuantCallback() {
                        @Override
                        public void onSuccess(List<KLine> kLines) {
                            //MACD
                        }
                    });
                }
            }
        }, 0, Period.MIN_5_MS);
    }

    private void asyncData() {
        symbols.add("eosusdt");
        periods.add(Period.MIN_1);
        periods.add(Period.MIN_5);
        periods.add(Period.MIN_15);
        periods.add(Period.MIN_30);
        QuantUtils.asyndData(symbols, periods);
    }
}
