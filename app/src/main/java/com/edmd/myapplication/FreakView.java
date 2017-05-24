package com.edmd.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yiminsun on 16/05/2017.
 */

public class FreakView extends ViewGroup {

//    private int baseYOffset;
//    private int newYOffset;

    private float offset = 300;
    private float offsetOnDown;
    private float yOnDown;

    public FreakView(Context context) {
        super(context);
    }

    public FreakView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FreakView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FreakView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int wLimit= MeasureSpec.getSize(widthMeasureSpec);
//        int hLimit = MeasureSpec.getSize(heightMeasureSpec);
//
//        setMeasuredDimension(wLimit, hLimit);
//
//
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure();
//        }


        final int pWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int pHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(pWidth, pHeight);




        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(pWidth, MeasureSpec.EXACTLY);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(pHeight, MeasureSpec.EXACTLY);


        View child = getChildAt(0);
        measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top;
        int bottom;

        /*final int offset = newYOffset;*/

        View child = getChildAt(0);

        top = (int) (t + offset);
        bottom = (int) (b + offset);
        child.layout(l, top, r, bottom);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean shouldIntercept = false;

        int action  = MotionEventCompat.getActionMasked(ev);

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                yOnDown = ev.getY();
                offsetOnDown = offset;
                Log.d("edmund", "CASE-DOWN, shouldIntercept = " + shouldIntercept);
                break;

            case MotionEvent.ACTION_MOVE:

                if (offset > 0) {

                    shouldIntercept = true;
                    Log.d("edmund", "if-0, shouldIntercept = " + shouldIntercept);

                } else {

                    if (ev.getY() < yOnDown) {

                        shouldIntercept = false;
                        Log.d("edmund", "if-1, shouldIntercept = " + shouldIntercept);

                    } else if (ev.getY() > yOnDown) {

                        // check scroll to determine
                        boolean canScrollUpwards = innerCanChildScrollVertically(getChildAt(0), -1);
                        shouldIntercept = !canScrollUpwards;
                        Log.d("edmund", "if-2, shouldIntercept = " + shouldIntercept);
                    }
                }
                break;
        }


        Log.d("edmund", "returning from onIntercept, value = " + shouldIntercept);
        return shouldIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        boolean ret = false;

        int action  = MotionEventCompat.getActionMasked(ev);

        Log.d("edmund", "onTouchEvent, action = " + action);

        switch (action) {

            case MotionEvent.ACTION_MOVE:
                offset = ev.getY() - yOnDown + offsetOnDown;
                if (offset < 0) {
                    offset = 0;
                } else if (offset > 300) {
                    offset = 300;
                }
                requestLayout();
                ret = true;

                break;

        }

        return false;

    }






    private boolean innerCanChildScrollVertically(View view, int direction) {
        if (view instanceof ViewGroup) {
            final ViewGroup vGroup = (ViewGroup) view;
            View child;
            boolean result;
            for (int i = 0; i < vGroup.getChildCount(); i++) {
                child = vGroup.getChildAt(i);
                if (child instanceof View) {
                    result = ViewCompat.canScrollVertically(child, direction);
                } else {
                    result = innerCanChildScrollVertically(child, direction);
                }

                if (result) {
                    return true;
                }
            }
        }

        return ViewCompat.canScrollVertically(view, direction);
    }

}
