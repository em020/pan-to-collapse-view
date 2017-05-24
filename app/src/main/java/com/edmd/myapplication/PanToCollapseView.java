package com.edmd.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
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
 * 上滑收缩顶部View
 * Created by yiminsun on 16/05/2017.
 */

public class PanToCollapseView extends ViewGroup {

    private final static float GOLDEN = 0.618f;

    private float maxOffset = 300;

    private float offset = 300;
    private float offsetOnDown;
    private float yOnDown;

    private boolean laidOut;

    private View collapsingPart;
    private View panningPart;

    public PanToCollapseView(Context context) {
        super(context);
    }

    public PanToCollapseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public PanToCollapseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PanToCollapseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PanToCollapseView);

        int collapsingPartLayoutId = array.getResourceId(R.styleable.PanToCollapseView_collapsing_part_layout, -1);

        if (collapsingPartLayoutId == -1) {
            throw new RuntimeException("必须提供collapsing_part_layout");
        } else {
            collapsingPart = inflate(getContext(), collapsingPartLayoutId, this);
        }

        int panningPartLayoutId = array.getResourceId(R.styleable.PanToCollapseView_panning_part_layout, -1);

        if (panningPartLayoutId == -1) {
            throw new RuntimeException("必须提供panning_part_layout");
        } else {
            panningPart = inflate(getContext(), panningPartLayoutId, this);
        }


        array.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        final int pWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int pHeight = MeasureSpec.getSize(heightMeasureSpec);


        setMeasuredDimension(pWidth, pHeight);


        final int widthSpecFull = MeasureSpec.makeMeasureSpec(pWidth, MeasureSpec.EXACTLY);
        final int heightSpecFull = MeasureSpec.makeMeasureSpec(pHeight, MeasureSpec.EXACTLY);
        final int heightSpecCollapsing = MeasureSpec.makeMeasureSpec((int) (pHeight * (1-GOLDEN)), MeasureSpec.EXACTLY);


        /*View child = getChildAt(0);*/
        measureChild(collapsingPart, widthSpecFull, heightSpecCollapsing);
        measureChild(panningPart, widthSpecFull, heightSpecFull);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (!laidOut) {
            //首次layout时，按比例定初始offset
            laidOut = true;

            maxOffset = (b - t) * (1- GOLDEN);
            offset = maxOffset;
        }

        int top;
        int bottom;

        top = (int) (t + offset);
        bottom = (int) (b + offset);
        panningPart.layout(l, top, r, bottom);

        collapsingPart.layout(l, t, r, (int) (t + (b - t) * (1 - GOLDEN)));
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
                } else if (offset > maxOffset) {
                    offset = maxOffset;
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
