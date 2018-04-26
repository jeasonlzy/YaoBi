package com.lzy.quant;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.quant.bean.KLine;
import com.lzy.quant.bean.Noticed;
import com.lzy.quant.common.Utils;
import com.lzy.quant.db.KLineManager;
import com.lzy.quant.db.NoticeManager;
import com.wordplat.ikvstockchart.entry.Entry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoticeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private static final long[] PATTERN = {700, 1300, 700, 1300};// 设置震动频率

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private MainAdapter adapter;
    private int count = 0;
    private List<KLine> items;

    private KeyguardManager.KeyguardLock keyguardLock;
    private Vibrator mVibrator;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("");
        keyguardLock.disableKeyguard();
        //震动和音频初始化
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mMediaPlayer = new MediaPlayer();

        setContentView(R.layout.activity_notice);
        ButterKnife.bind(this);
        setTitle("提醒买卖更新列表");

        items = new ArrayList<>();
        List<KLine> data = getData(0, 20);

        adapter = new MainAdapter();
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.isFirstOnly(false);
        adapter.setOnLoadMoreListener(this);
        adapter.addData(data);

        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter);

        System.out.println("onCreate");
        onNotify();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("onNewIntent");
        onNotify();
    }

    /**
     * 播放当前默认铃声并震动
     */
    public void startRingAndVibrator() {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVibrator.vibrate(PATTERN, 2);
        Utils.muteAudioFocus(this, true);
    }

    /**
     * 停止响铃和震动
     */
    public void stopRingAndVibrator() {
        if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
        mVibrator.cancel();
        Utils.muteAudioFocus(this, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingAndVibrator();
    }

    private void onNotify() {
        KLine line = (KLine) getIntent().getSerializableExtra("line");
        if (line == null) {
            return;
        }
        //显示提醒对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage(line.toString());
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stopRingAndVibrator();
            }
        });
        builder.show();

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
        }

        startRingAndVibrator();

        if (line.buy == Entry.BUY) {
            System.out.println("提醒: " + line.symbol + "在" + line.period + "形成金叉，最低价" + line.close + ", 注意买入！");
        } else if (line.buy == Entry.SALE) {
            System.out.println("提醒: " + line.symbol + "在" + line.period + "形成死叉，最低价" + line.close + ", 注意卖出！");
        }
    }

    private List<KLine> getData(int offset, int limit) {
        List<KLine> lines = new ArrayList<>();
        List<Noticed> noticeds = NoticeManager.getInstance().query(offset, limit);
        for (Noticed notice : noticeds) {
            KLine line = KLineManager.getInstance().query(notice.id, notice.symbol, notice.period);
            if (line != null) {
                lines.add(line);
            }
        }
        count = offset + limit;
        return lines;
    }

    @Override
    public void onRefresh() {
        setRefreshing(false);
    }

    @Override
    public void onLoadMoreRequested() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<KLine> data = getData(count, 20);
                adapter.addData(data);
            }
        }, 500);
    }

    private class MainAdapter extends BaseQuickAdapter<KLine> {

        MainAdapter() {
            super(R.layout.item_notice, items);
        }

        @Override
        protected void convert(final BaseViewHolder baseViewHolder, KLine itemModel) {
            if (itemModel.buy == Entry.BUY) {
                baseViewHolder.setText(R.id.option, "买入");
                baseViewHolder.setText(R.id.price, itemModel.open);
                baseViewHolder.setTextColor(R.id.option, getResources().getColor(R.color.up));
                baseViewHolder.setTextColor(R.id.price, getResources().getColor(R.color.up));
            } else if (itemModel.buy == Entry.SALE) {
                baseViewHolder.setText(R.id.option, "卖出");
                baseViewHolder.setText(R.id.price, itemModel.open);
                baseViewHolder.setTextColor(R.id.option, getResources().getColor(R.color.down));
                baseViewHolder.setTextColor(R.id.price, getResources().getColor(R.color.down));
            } else {
                baseViewHolder.setText(R.id.option, "异常");
                baseViewHolder.setText(R.id.price, "");
                baseViewHolder.setTextColor(R.id.option, Color.BLACK);
                baseViewHolder.setTextColor(R.id.price, Color.BLACK);
            }
            baseViewHolder.setText(R.id.deal, itemModel.toString());
        }
    }

    public void setRefreshing(final boolean refreshing) {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(refreshing);
            }
        }, 1000);
    }
}
