package com.tino.lottery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LotteryActivity extends AppCompatActivity {

    private LotteryView lotteryView;

    public static int points = 5675;
    public static int lotteryType = 1;//1-抽一次,　10-代表10连抽
    private boolean isLottery;//标识是否正在抽奖

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);
        StatusBarUtil.setStatusBarModeLightFullScreen(this, false);
        initView();
    }

    private void initView() {
        findViewById(R.id.back_view).setOnClickListener(view -> finish());
        lotteryView = findViewById(R.id.recycler_view);
        lotteryView.setEventListener((firstX, firstY) -> {
            LotteryDialog lotteryDialog = new LotteryDialog(LotteryActivity.this);
            lotteryDialog.setXY(firstX, firstY);
            lotteryDialog.setEventListener(() -> {
                isLottery = false;
                lotteryView.refresh(2);
            });
            lotteryDialog.show(getSupportFragmentManager(), "LotteryDialog");
        });
        findViewById(R.id.lottery_one_view).setOnClickListener(view -> {
            if (!isLottery) {
                lotteryType = 1;
                lotteryView.refresh(1);
                isLottery = true;
            }
        });
        findViewById(R.id.lottery_ten_view).setOnClickListener(view -> {
            if (!isLottery) {
                lotteryType = 10;
                lotteryView.refresh(1);
                isLottery = true;
            }
        });
    }
}