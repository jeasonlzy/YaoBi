package com.lzy.quant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.quant.bean.HuoBi;
import com.lzy.quant.bean.KLine;
import com.lzy.quant.callback.JsonCallback;
import com.lzy.quant.common.Policy;
import com.lzy.quant.common.QuantUtils;
import com.lzy.quant.common.Urls;
import com.lzy.quant.common.Utils;
import com.lzy.quant.db.KLineManager;
import com.wordplat.ikvstockchart.InteractiveKLineView;
import com.wordplat.ikvstockchart.drawing.HighlightDrawing;
import com.wordplat.ikvstockchart.drawing.KDJDrawing;
import com.wordplat.ikvstockchart.drawing.MACDDrawing;
import com.wordplat.ikvstockchart.drawing.RSIDrawing;
import com.wordplat.ikvstockchart.drawing.StockIndexYLabelDrawing;
import com.wordplat.ikvstockchart.entry.EntrySet;
import com.wordplat.ikvstockchart.entry.StockKDJIndex;
import com.wordplat.ikvstockchart.entry.StockMACDIndex;
import com.wordplat.ikvstockchart.entry.StockRSIIndex;
import com.wordplat.ikvstockchart.marker.XAxisTextMarkerView;
import com.wordplat.ikvstockchart.marker.YAxisTextMarkerView;
import com.wordplat.ikvstockchart.render.KLineRender;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewActivity extends AppCompatActivity {

    @BindView(R.id.kLineView)
    InteractiveKLineView kLineView;

    private String period;
    private String symbol;
    private int size;
    private boolean test;
    private int policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        period = intent.getStringExtra("period");
        symbol = intent.getStringExtra("symbol");
        size = Utils.parseInt(intent.getStringExtra("size"));
        test = intent.getBooleanExtra("test", false);
        policy = Utils.parseInt(intent.getStringExtra("policy"));

        setTitle(symbol + " " + period + " " + size + " 策略" + policy);

        initUI();
        if (test) {
            loadDBData();
        } else {
            loadNetData();
        }
    }

    private void initUI() {
        kLineView.setEnableLeftRefresh(false);
        kLineView.setEnableLeftRefresh(false);
        KLineRender kLineRender = (KLineRender) kLineView.getRender();

        final int paddingTop = Utils.dp2px(this, 10);
        final int stockMarkerViewHeight = Utils.dp2px(this, 15);

        // MACD
        HighlightDrawing macdHighlightDrawing = new HighlightDrawing();
        macdHighlightDrawing.addMarkerView(new YAxisTextMarkerView(stockMarkerViewHeight));

        StockMACDIndex macdIndex = new StockMACDIndex();
        macdIndex.addDrawing(new MACDDrawing());
        macdIndex.addDrawing(new StockIndexYLabelDrawing());
        macdIndex.addDrawing(macdHighlightDrawing);
        macdIndex.setPaddingTop(paddingTop);
        kLineRender.addStockIndex(macdIndex);

        // RSI
        HighlightDrawing rsiHighlightDrawing = new HighlightDrawing();
        rsiHighlightDrawing.addMarkerView(new YAxisTextMarkerView(stockMarkerViewHeight));

        StockRSIIndex rsiIndex = new StockRSIIndex();
        rsiIndex.addDrawing(new RSIDrawing());
        rsiIndex.addDrawing(new StockIndexYLabelDrawing());
        rsiIndex.addDrawing(rsiHighlightDrawing);
        rsiIndex.setPaddingTop(paddingTop);
        kLineRender.addStockIndex(rsiIndex);

        // KDJ
        HighlightDrawing kdjHighlightDrawing = new HighlightDrawing();
        kdjHighlightDrawing.addMarkerView(new YAxisTextMarkerView(stockMarkerViewHeight));

        StockKDJIndex kdjIndex = new StockKDJIndex();
        kdjIndex.addDrawing(new KDJDrawing());
        kdjIndex.addDrawing(new StockIndexYLabelDrawing());
        kdjIndex.addDrawing(kdjHighlightDrawing);
        kdjIndex.setPaddingTop(paddingTop);
        kLineRender.addStockIndex(kdjIndex);

        kLineRender.addMarkerView(new YAxisTextMarkerView(stockMarkerViewHeight));
        kLineRender.addMarkerView(new XAxisTextMarkerView(stockMarkerViewHeight));
    }

    private void loadDBData() {
        kLineView.post(new Runnable() {
            @Override
            public void run() {
                List<KLine> list = KLineManager.getInstance().query(symbol, period, size);
                showChat(list);
            }
        });
    }

    private void loadNetData() {
        OkGo.<HuoBi<List<KLine>>>get(Urls.history_kline)
                .params("symbol", symbol)
                .params("period", period)
                .params("size", size)
                .execute(new JsonCallback<HuoBi<List<KLine>>>() {
                    @Override
                    public void onSuccess(Response<HuoBi<List<KLine>>> response) {

                        TreeSet<KLine> set = new TreeSet<>();
                        // 添加原有的数据库中的
                        List<KLine> kLineList = KLineManager.getInstance().query(symbol, period, 500);
                        set.addAll(kLineList);
                        // 添加网络新加的（如果数据库中有，这里就添加不进去了，继续使用数据库的数据）
                        set.addAll(response.body().data);

                        ArrayList<KLine> list = new ArrayList<>(set);
                        QuantUtils.fillData(list, symbol, period);
                        QuantUtils.computeKLineMACD(list);

                        showChat(list);
                    }
                });
    }

    private void showChat(List<KLine> list) {
        if (list == null) {
            return;
        }
        EntrySet entrySet = KLine.toViewChart(list);
        entrySet.computeStockIndex();
        if (policy == 1) {
            Policy.policy1(entrySet);
        } else if (policy == 2) {
            Policy.policy2(entrySet);
        } else if (policy == 3) {
            Policy.policy3(entrySet);
        } else {
            Policy.policy1(entrySet);
        }
        kLineView.setEntrySet(entrySet);
        kLineView.notifyDataSetChanged();
    }
}
