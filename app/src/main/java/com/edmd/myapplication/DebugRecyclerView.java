package com.edmd.myapplication;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yiminsun on 23/05/2017.
 */

public class DebugRecyclerView extends RecyclerView {
    public DebugRecyclerView(Context context) {
        super(context);
    }

    public DebugRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DebugRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean ret = super.onTouchEvent(e);
        Log.d("edmund", "RecyclerView - onTouch, action = " + e.getAction() + ", ret = " + ret);
        return ret;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean ret = super.onInterceptTouchEvent(e);
        Log.d("edmund", "RecyclerView - onInterceptTouchEvent, action = " + e.getAction() + ", ret = " + ret);
        return ret;
    }
}
