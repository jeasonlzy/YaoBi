package com.lzy.quant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.quant.bean.HuoBi;
import com.lzy.quant.bean.KLine;
import com.lzy.quant.bean.Period;
import com.lzy.quant.callback.JsonCallback;
import com.lzy.quant.common.MACDUtils;
import com.lzy.quant.db.KLineManager;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private KLineManager lineManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        lineManager = KLineManager.getInstance();

//        startService(new Intent(this, QuantService.class));
    }

    @OnClick(R.id.min_1)
    public void onMin1Click(View view) {
        final String period = Period.MIN_5;
        final String symbol = "eosusdt";
        OkGo.<HuoBi<List<KLine>>>get(Urls.history_kline)
                .params("symbol", symbol)
                .params("period", period)
                .params("size", 500)
                .execute(new JsonCallback<HuoBi<List<KLine>>>() {
                    @Override
                    public void onSuccess(Response<HuoBi<List<KLine>>> response) {
                        List<KLine> kLines = KLine.fillData(response.body().data, symbol, period);
                        Collections.sort(kLines);

                        lineManager.replace(kLines);
                        MACDUtils.fillMACD(kLines, 2, 4, 2);

                        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                        Date date = new Date();
                        for (KLine kLine : kLines) {
                            date.setTime(kLine.id * 1000);
                            String time = format.format(date);
                            System.out.println(time + " " + kLine.macd);
                        }
                    }
                });
    }

    @OnClick(R.id.min_5)
    public void onMin5Click(View view) {
        startActivity(new Intent(this, ViewActivity.class));
    }
}
