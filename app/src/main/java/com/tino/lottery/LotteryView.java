package com.tino.lottery;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 抽奖View
 */
public class LotteryView extends RecyclerView {

    private LotteryAdapter adapter;

    private Context mContext;

    public LotteryView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public LotteryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 5) return 2;
                else return 1;
            }
        });
        setLayoutManager(layoutManager);
        addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = UIUtil.dip2px(mContext, 5);
                outRect.bottom = UIUtil.dip2px(mContext, 5);
            }
        });
        adapter = new LotteryAdapter(mContext);
        setAdapter(adapter);
    }

    public void refresh(int refreshType) {
        adapter.setRefreshType(refreshType);
        adapter.notifyDataSetChanged();
    }

    public void setEventListener(LotteryAdapter.EventListener eventListener) {
        if (null != adapter) {
            adapter.setEventListener(eventListener);
        }
    }
}