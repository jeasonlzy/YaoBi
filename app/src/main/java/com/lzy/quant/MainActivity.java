package com.lzy.quant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.lzy.quant.bean.Period;
import com.lzy.quant.db.KLineManager;
import com.lzy.quant.db.NoticeManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.symbol)
    EditText etSymbol;
    @BindView(R.id.size)
    EditText etSize;
    @BindView(R.id.policy)
    EditText etPolicy;
    @BindView(R.id.test)
    CheckBox cbTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        startService(new Intent(this, QuantService.class));
    }

    @OnClick(R.id.min_1)
    public void onMin1Click(View view) {
        start(Period.MIN_1);
    }

    @OnClick(R.id.min_5)
    public void onMin5Click(View view) {
        start(Period.MIN_5);
    }

    @OnClick(R.id.min_15)
    public void onMin15Click(View view) {
        start(Period.MIN_15);
    }

    @OnClick(R.id.min_30)
    public void onMin30Click(View view) {
        start(Period.MIN_30);
    }

    @OnClick(R.id.min_60)
    public void onMin60Click(View view) {
        start(Period.MIN_60);
    }

    @OnClick(R.id.notice)
    public void notice(View view) {
        startActivity(new Intent(this, NoticeActivity.class));
    }

    @OnClick(R.id.clearNotice)
    public void clearNotice(View view) {
        NoticeManager.getInstance().deleteAll();
    }

    @OnClick(R.id.clearKline)
    public void clearKline(View view) {
        KLineManager.getInstance().deleteAll();
    }

    private void start(String period) {
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("symbol", etSymbol.getText().toString());
        intent.putExtra("period", period);
        intent.putExtra("size", etSize.getText().toString());
        intent.putExtra("test", cbTest.isChecked());
        intent.putExtra("policy", etPolicy.getText().toString());
        startActivity(intent);
    }
}
