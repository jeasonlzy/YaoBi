package com.lzy.quant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.quant.bean.HuoBi;
import com.lzy.quant.bean.KLine;
import com.lzy.quant.bean.Period;
import com.lzy.quant.callback.JsonCallback;
import com.lzy.quant.common.AppUtils;
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

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewActivity extends AppCompatActivity {

    @BindView(R.id.kLineView)
    InteractiveKLineView kLineView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);

        initUI();
        loadKLineData();
    }

    private void initUI() {
        kLineView.setEnableLeftRefresh(false);
        kLineView.setEnableLeftRefresh(false);
        KLineRender kLineRender = (KLineRender) kLineView.getRender();

        final int paddingTop = AppUtils.dpTopx(this, 10);
        final int stockMarkerViewHeight = AppUtils.dpTopx(this, 15);

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

    private void loadKLineData() {
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
                        EntrySet entrySet = KLine.toViewChart(kLines);
                        entrySet.computeStockIndex();
                        kLineView.setEntrySet(entrySet);
                        kLineView.notifyDataSetChanged();
                    }
                });
    }
}
