package com.edmd.myapplication;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by yiminsun on 23/05/2017.
 */

public class DebugScrollView extends ScrollView {
    public DebugScrollView(Context context) {
        super(context);
    }

    public DebugScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DebugScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DebugScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = super.onInterceptTouchEvent(ev);
        Log.d("edmund", "DebugScrollView: intercept? " + ret);
        return ret;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean ret = super.onTouchEvent(ev);
        Log.d("edmund", "DebugScrollView: onTouchEvent, action= "+ev.getAction() +", ret = " + ret);
        return ret;
    }
}
