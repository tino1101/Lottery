package com.tino.lottery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LotteryAdapter extends RecyclerView.Adapter {

    private final int TYPE_NORMAL = 0;
    private final int TYPE_CENTER = 1;

    private Context mContext;

    private int mRefreshType;//刷新类型 0-正常刷新 1-卡片翻转到背面 2-卡片翻转到正面　3-刷新积分

    private EventListener mEventListener;

    private int firstX = 0;
    private int firstY = 0;

    private Pair<Integer, String>[] lotteryInfo = new Pair[]{
            Pair.create(R.drawable.one_coin_icon, "1学币"),
            Pair.create(R.drawable.thank_icon, "谢谢参与"),
            Pair.create(R.drawable.five_coins_icon, "5学币"),
            Pair.create(R.drawable.one_coin_icon, "1学币"),
            Pair.create(R.drawable.fudai_icon, "88学币"),
            null,
            Pair.create(R.drawable.two_coins_icon, "2学币"),
            Pair.create(R.drawable.two_coins_icon, "2学币"),
            Pair.create(R.drawable.ten_coins_icon, "10学币"),
            Pair.create(R.drawable.one_coin_icon, "1学币"),
            Pair.create(R.drawable.thank_icon, "谢谢参与")
    };

    public LotteryAdapter(Context context) {
        mContext = context;
    }

    /**
     * 翻转卡片
     */
    public void setRefreshType(int refreshType) {
        mRefreshType = refreshType;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CENTER) {
            return new CenterHolder(LayoutInflater.from(mContext).inflate(R.layout.lottery_item_center, parent, false));
        } else {
            return new NormalHolder(LayoutInflater.from(mContext).inflate(R.layout.lottery_item_normal, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (mRefreshType) {
            case 0:
                if (holder instanceof CenterHolder) {
                    CenterHolder centerHolder = (CenterHolder) holder;
                    centerHolder.init(LotteryActivity.points);
                } else if (holder instanceof NormalHolder) {
                    NormalHolder normalHolder = (NormalHolder) holder;
                    normalHolder.init(position);
                }
                break;
            case 1:
            case 2:
                if (holder instanceof CenterHolder) {
                    CenterHolder centerHolder = (CenterHolder) holder;
                    if (mRefreshType == 2) {
                        if (LotteryActivity.lotteryType == 1) {
                            LotteryActivity.points -= 10;
                        } else {
                            LotteryActivity.points -= 100;
                        }
                    }
                    centerHolder.init(LotteryActivity.points);
                } else if (holder instanceof NormalHolder) {
                    NormalHolder normalHolder = (NormalHolder) holder;
                    normalHolder.init(position);
                    if (mRefreshType == 1 || mRefreshType == 2) {
                        int delay = 0;
                        if (position == 1 || position == 4) {
                            delay = 150;
                        } else if (position == 2 || position == 7) {
                            delay = 200;
                        } else if (position == 3 || position == 8) {
                            delay = 250;
                        } else if (position == 6 || position == 9) {
                            delay = 300;
                        } else if (position == 10) {
                            delay = 350;
                        }
                        normalHolder.itemView.postDelayed(() -> {
                            if (position == 0) {
                                int[] location = new int[2];
                                normalHolder.itemView.getLocationOnScreen(location);
                                firstX = location[0];
                                firstY = location[1];
                                Log.i("iiiiiiiiii", "x:" + firstX + "-----y:" + firstY);
                            }
                            ObjectAnimator o1 = ObjectAnimator.ofFloat(normalHolder.rootLayout, "rotationY", 0f, 90f);
                            o1.setDuration(100);
                            o1.start();
                            ObjectAnimator o2 = ObjectAnimator.ofFloat(normalHolder.rootLayout, "rotationY", -90f, 0f);
                            o2.setStartDelay(100);
                            o2.setDuration(100);
                            o2.start();
                            o2.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    if (mRefreshType == 1) {
                                        normalHolder.ivBg.setBackgroundResource(R.drawable.card_back_bg);
                                        normalHolder.ivLottery.setVisibility(View.GONE);
                                        normalHolder.tvName.setVisibility(View.GONE);
                                    } else {
                                        normalHolder.ivBg.setBackgroundResource(R.drawable.card_front_bg);
                                        normalHolder.ivLottery.setVisibility(View.VISIBLE);
                                        normalHolder.tvName.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (position == 10 && mRefreshType == 1) {
                                        if (null != mEventListener) {
                                            mEventListener.onRotateToBackFinish(firstX, firstY);
                                        }
                                    }
                                }
                            });
                        }, delay);
                    }
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 11;
    }

    public class NormalHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rootLayout;
        public ImageView ivBg;
        public ImageView ivLottery;
        public TextView tvName;

        public NormalHolder(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.root_layout);
            ivBg = itemView.findViewById(R.id.iv_bg);
            ivLottery = itemView.findViewById(R.id.iv_lottery);
            tvName = itemView.findViewById(R.id.tv_lottery_name);
        }

        public void init(int position) {
            ivLottery.setImageResource(lotteryInfo[position].first);
            tvName.setText(lotteryInfo[position].second);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivBg.getLayoutParams();
            params.width = (UIUtil.getScreenWidth(mContext) - UIUtil.dip2px(mContext, 75)) / 4;
            params.height = params.width * 92 / 75;
            ivBg.setLayoutParams(params);
        }
    }

    public class CenterHolder extends RecyclerView.ViewHolder {
        public TextView pointsTextView;

        public CenterHolder(View itemView) {
            super(itemView);
            pointsTextView = itemView.findViewById(R.id.my_points_value_view);
        }

        public void init(int count) {
            pointsTextView.setText(String.valueOf(count));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 5) return TYPE_CENTER;
        return TYPE_NORMAL;
    }

    public interface EventListener {
        void onRotateToBackFinish(int firstX, int firstY);//翻转到背面结束，通知准备抽奖转动动画
    }
}