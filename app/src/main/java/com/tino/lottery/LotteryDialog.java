package com.tino.lottery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Random;

/**
 * 抽奖弹窗(包含抽奖动画和抽奖结果)
 */
public class LotteryDialog extends DialogFragment {

    private final int RESULT_ITEM_IN_DURATION = 80;//中奖结果项动画持续时间
    private final int TURN_DURATION = 1500;//抽奖动画持续时间
    private final int RESULT_DIALOG_SCALE_IN = 300;//中奖弹窗出现动画持续时间
    private final int COIN_COUNT_ANIM_DURATION = 300;//中学币个数提示动画持续时间
    private int mFirstX;
    private int mFirstY;

    private Context mContext;
    private EventListener mEventListener;

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public LotteryDialog(Context context) {
        mContext = context;
    }

    public void setXY(int firstX, int firstY) {
        mFirstX = firstX;
        mFirstY = firstY;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_lottery, null);
        ImageView turnView = view.findViewById(R.id.turn_view);
        view.findViewById(R.id.sure_view).setOnClickListener(view1 -> dismiss());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) turnView.getLayoutParams();
        params.width = (UIUtil.getScreenWidth(mContext) - UIUtil.dip2px(mContext, 75)) / 4;
        params.height = params.width * 92 / 75;
        params.leftMargin = mFirstX;
        params.topMargin = mFirstY - UIUtil.getStatusBarHeight(mContext);
        turnView.setLayoutParams(params);
        turnView.post(() -> turn(view, turnView));
        Dialog dialog = new Dialog(mContext, R.style.CustomDimAmount4);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnKeyListener((dialog1, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);
        dialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().getAttributes().height = ViewGroup.LayoutParams.MATCH_PARENT;
        return dialog;
    }

    /**
     * 抽奖转动动画
     */
    private void turn(View view, ImageView imageView) {
        int imageWidth = imageView.getWidth();
        int imageHeight = imageView.getHeight();
        int spacing = UIUtil.dip2px(mContext, 5f);
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(TURN_DURATION);
        animator.setIntValues(0, 2 * 11 + new Random().nextInt(10));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int position = (int) animation.getAnimatedValue();
            switch (position % 11) {
                case 0:
                    imageView.setTranslationX(0);
                    imageView.setTranslationY(0);
                    break;
                case 1:
                    imageView.setTranslationX(imageWidth + spacing);
                    imageView.setTranslationY(0);
                    break;
                case 2:
                    imageView.setTranslationX((imageWidth + spacing) * 2);
                    imageView.setTranslationY(0);
                    break;
                case 3:
                    imageView.setTranslationX((imageWidth + spacing) * 3);
                    imageView.setTranslationY(0);
                    break;
                case 4:
                    imageView.setTranslationX((imageWidth + spacing) * 3);
                    imageView.setTranslationY(imageHeight + spacing);
                    break;
                case 5:
                    imageView.setTranslationX((imageWidth + spacing) * 3);
                    imageView.setTranslationY((imageHeight + spacing) * 2);
                    break;
                case 6:
                    imageView.setTranslationX((imageWidth + spacing) * 2);
                    imageView.setTranslationY((imageHeight + spacing) * 2);
                    break;
                case 7:
                    imageView.setTranslationX(imageWidth + spacing);
                    imageView.setTranslationY((imageHeight + spacing) * 2);
                    break;
                case 8:
                    imageView.setTranslationX(0);
                    imageView.setTranslationY((imageHeight + spacing) * 2);
                    break;
                case 9:
                    imageView.setTranslationX(0);
                    imageView.setTranslationY(imageHeight + spacing);
                    break;
            }
        });
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                imageView.setVisibility(View.GONE);
                resultDialogScaleIn(view);
            }
        });
    }

    /**
     * 中奖结果弹窗进入动画
     */
    private void resultDialogScaleIn(View view) {
        RelativeLayout resultLayout = view.findViewById(R.id.result_layout);
        resultLayout.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(resultLayout, "scaleX", 0f, 1f).setDuration(RESULT_DIALOG_SCALE_IN);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(resultLayout, "scaleY", 0f, 1f).setDuration(RESULT_DIALOG_SCALE_IN);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(scaleX).with(scaleY);
        scaleX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                resultItemIn(view);
            }
        });
        animatorSet.start();
    }

    /**
     * 中奖结果项添加动画
     */
    private void resultItemIn(View view) {
        HorizontalScrollView scrollView = view.findViewById(R.id.result_scroll_view);
        LinearLayout resultContainerLayout = view.findViewById(R.id.result_container_layout);
        RelativeLayout resultAnimView = view.findViewById(R.id.result_anim_view);

        resultContainerLayout.removeAllViews();
        int total = 1;
        if (LotteryActivity.lotteryType == 10) {
            total = 6;
        }
        for (int i = 0; i < total; i++) {
            int finalI = i;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                RelativeLayout resultLayout = view.findViewById(R.id.result_layout);
                resultLayout.post(() -> {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) resultAnimView.getLayoutParams();
                    params.topMargin = resultLayout.getTop() + scrollView.getTop();
                    resultAnimView.setLayoutParams(params);
                    resultAnimView.setVisibility(View.VISIBLE);
                });
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(resultAnimView, "scaleX", finalI == 0 ? 3f : 2f, 1f).setDuration(RESULT_ITEM_IN_DURATION);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(resultAnimView, "scaleY", finalI == 0 ? 3f : 2f, 1f).setDuration(RESULT_ITEM_IN_DURATION);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(resultAnimView, "alpha", 0.2f, 1f).setDuration(RESULT_ITEM_IN_DURATION);
                animatorSet.setInterpolator(new LinearInterpolator());
                AnimatorListenerAdapter animatorListener = new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        View resultView = LayoutInflater.from(mContext).inflate(R.layout.lottery_result_item_view, null);
                        resultContainerLayout.addView(resultView);
                        int deviation = (int) (mContext.getResources().getDimension(R.dimen.lottery_result_item_width) - mContext.getResources().getDimension(R.dimen.lottery_result_item_image_width) - UIUtil.dip2px(mContext, 15));
                        if (finalI != 0) {
                            LinearLayout.LayoutParams params0 = (LinearLayout.LayoutParams) resultView.getLayoutParams();
                            params0.leftMargin = -deviation;
                            resultView.setLayoutParams(params0);
                        }
                        LottieAnimationView lottieAnimationView = resultView.findViewById(R.id.lottery_anim_view);
                        lottieAnimationView.playAnimation();
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
                        params.width = ((int) mContext.getResources().getDimension(R.dimen.lottery_result_item_width)) * (finalI + 1) - deviation * finalI;
                        scrollView.setLayoutParams(params);
                        scrollView.postDelayed(() -> scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT), 10);
                        resultAnimView.setVisibility(View.GONE);
                        if (finalI < 5) {
                            resultAnimView.setTranslationX(resultAnimView.getTranslationX() + ((int) mContext.getResources().getDimension(R.dimen.lottery_result_item_image_width)) / 2);
//                            Log.i("iiiiiiiiii", "TranslationX:" + resultAnimView.getTranslationX());
                        }
                    }
                };
                animatorSet.play(scaleX).with(scaleY).with(alpha);
                scaleX.addListener(animatorListener);
                animatorSet.start();
            }, i * (RESULT_ITEM_IN_DURATION * 2 + 100));
            if (i == 0) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    TextView coinCountTextView = view.findViewById(R.id.coin_count_text_view);
                    coinCountTextView.setVisibility(View.VISIBLE);
                    AnimatorSet animatorSet = new AnimatorSet();
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(coinCountTextView, "scaleX", 0f, 1f).setDuration(COIN_COUNT_ANIM_DURATION);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(coinCountTextView, "scaleY", 0f, 1f).setDuration(COIN_COUNT_ANIM_DURATION);
                    animatorSet.setInterpolator(new LinearInterpolator());
                    animatorSet.play(scaleX).with(scaleY);
                    animatorSet.start();
                    if (mEventListener != null) {
                        mEventListener.onShowResult();
                    }
                }, RESULT_ITEM_IN_DURATION);
            }
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }

    public interface EventListener {
        void onShowResult();//通知将抽奖卡片翻转过来
    }

}