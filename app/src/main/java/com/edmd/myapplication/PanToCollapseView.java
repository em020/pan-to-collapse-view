package com.edmd.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 上滑收缩顶部View
 * Created by yiminsun on 16/05/2017.
 */

public class PanToCollapseView extends ViewGroup {

    private static final String TAG = PanToCollapseView.class.getSimpleName();

    private final static float GOLDEN = 0.618f;

    private float maxOverDragPx;//从maxoffset处，手指再下滑多少，collapsing part就到了最大offset了
    private float maxExceedingOffsetPx; // 最大offset超过maxoffset的超出值

    private float maxOffset = 300;
    private float offset = 300;

    private float offsetOnDown;
    private float yOnDown;

    private boolean laidOut;

    private int touchTarget; // 0 - collapsing, 1 - panning

    private View collapsingPartTouchedChild;





    public PanToCollapseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {

        maxOverDragPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350f, getContext().getResources().getDisplayMetrics());
        maxExceedingOffsetPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135f, getContext().getResources().getDisplayMetrics());

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PanToCollapseView);

        int collapsingPartLayoutId = array.getResourceId(R.styleable.PanToCollapseView_collapsing_part_layout, -1);

        if (collapsingPartLayoutId == -1) {
            throw new RuntimeException("必须提供collapsing_part_layout");
        } else {
            inflate(getContext(), collapsingPartLayoutId, this);
        }

        int panningPartLayoutId = array.getResourceId(R.styleable.PanToCollapseView_panning_part_layout, -1);

        if (panningPartLayoutId == -1) {
            throw new RuntimeException("必须提供panning_part_layout");
        } else {
            inflate(getContext(), panningPartLayoutId, this);
        }


        array.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //Log.d(TAG, "HADV, onMeasure");//HADV - how android draw views

        int pWidth = MeasureSpec.getSize(widthMeasureSpec);
        int pHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(pWidth, pHeight);


        int collapsingPartWidthSpec = MeasureSpec.makeMeasureSpec(pWidth, MeasureSpec.EXACTLY);
        int collapsingPartHeightSpec;
        if (offset <= maxOffset) {
            collapsingPartHeightSpec = MeasureSpec.makeMeasureSpec((int) maxOffset, MeasureSpec.EXACTLY);
        } else {
            collapsingPartHeightSpec = MeasureSpec.makeMeasureSpec((int) offset, MeasureSpec.EXACTLY);
        }
        View collapsingPart = getChildAt(0);
        measureChild(collapsingPart, collapsingPartWidthSpec, collapsingPartHeightSpec);


        int panningPartWidthSpec = MeasureSpec.makeMeasureSpec(pWidth, MeasureSpec.EXACTLY);
        int panningPartHeightSpec = MeasureSpec.makeMeasureSpec(pHeight, MeasureSpec.EXACTLY);
        View panningPart = getChildAt(1);
        measureChild(panningPart, panningPartWidthSpec, panningPartHeightSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //Log.d(TAG, "HADV, onLayout");

        if (!laidOut) {
            //首次layout时，按比例定初始offset
            laidOut = true;

            maxOffset = (b - t) * (1- GOLDEN);
            offset = maxOffset;
        }


        View collapsingPart = getChildAt(0);

        if (offset <= maxOffset) {
            int fooTop = (int) (t - maxOffset/2f + offset/2f);
            collapsingPart.layout(l, fooTop, r, fooTop + (int)maxOffset);
        } else {
            collapsingPart.layout(l, t, r, t + (int)offset);
        }

        int top;
        int bottom;

        View panningPart = getChildAt(1);

        top = (int) (t + offset);
        bottom = (int) (b + offset);
        panningPart.layout(l, top, r, bottom);


    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {


        boolean shouldIntercept = false;

        int action  = MotionEventCompat.getActionMasked(ev);

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                yOnDown = ev.getY();
                offsetOnDown = offset;

                View panning = getChildAt(1);
                Rect temp = new Rect();
                panning.getHitRect(temp);
                RectF rf = new RectF(temp);

                if (rf.contains(ev.getX(), ev.getY())) {
                    touchTarget = 1;
                    Log.d(TAG, "onInterceptTouchEvent, CASE ACTION_DOWN, touch target = " + touchTarget);
                } else {
                    touchTarget = 0;
                    Log.d(TAG, "onInterceptTouchEvent, CASE ACTION_DOWN, touch target = " + touchTarget);

                    collapsingPartTouchedChild = null;

                    // 找出最顶层的被点击的view
                    View collapsingPart  = getChildAt(0);

                    if (collapsingPart instanceof ViewGroup) {

                        ViewGroup vg = (ViewGroup) collapsingPart;

                        Log.d(TAG, "onInterceptTouchEvent, CASE ACTION_DOWN, searching touched child...");
                        for (int i = vg.getChildCount() - 1; i >= 0; i--) {

                            View child = vg.getChildAt(i);

                            Rect tempRect = new Rect();
                            child.getHitRect(tempRect);

                            RectF rect = new RectF(tempRect);
                            if (rect.contains(ev.getX(), ev.getY())) {
                                // found!
                                collapsingPartTouchedChild = child;
                                Log.d(TAG, "onInterceptTouchEvent, CASE ACTION_DOWN, touched child found! it is a " + child.getClass().getSimpleName());
                                break;
                            }
                        }
                        // 注意这里寻找点击目标的逻辑：ev的坐标是相对于PanToCollapseView的，也就是最外层的parent，
                        // 而测试hit的rect，是collapsing part里面的childView

                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (touchTarget == 1) {
                    // click falls in panning part

                    if (offset > 0) {

                        shouldIntercept = true;
                        Log.d(TAG, "onInterceptTouchEvent, if-0, shouldIntercept = " + shouldIntercept);

                    } else {

                        if (ev.getY() <= yOnDown) {

                            shouldIntercept = false;
                            Log.d(TAG, "onInterceptTouchEvent, if-1, shouldIntercept = " + shouldIntercept);

                        } else if (ev.getY() > yOnDown) {

                            // check scroll to determine
                            boolean canScrollUpwards = innerCanChildScrollVertically(getChildAt(1), -1);
                            shouldIntercept = !canScrollUpwards;
                            Log.d(TAG, "onInterceptTouchEvent, if-2, shouldIntercept = " + shouldIntercept);
                        }
                    }

                } else if (touchTarget == 0) {
                    // click falls in collapsing part

                    if (collapsingPartTouchedChild == null) {

                        shouldIntercept = true;
                        Log.d(TAG, "onInterceptTouchEvent, if-3, shouldIntercept = " + shouldIntercept);

                    } else {

                        if (ev.getY() > yOnDown) {
                            //此时手指向下拖拽，导致列表“后退”，故此时的方向为负
                            int direction = -1;

                            //不能滚动时，拦截
                            shouldIntercept = !innerCanChildScrollVertically(collapsingPartTouchedChild, direction);
                            Log.d(TAG, "onInterceptTouchEvent, if-4, shouldIntercept = " + shouldIntercept + ", direction = " + direction);
                        } else if (ev.getY() < yOnDown) {
                            //此时手指向上拖拽，导致列表“前进”，故此时的方向为正
                            int direction = 1;

                            //不能滚动时，拦截
                            shouldIntercept = !innerCanChildScrollVertically(collapsingPartTouchedChild, direction);
                            Log.d(TAG, "onInterceptTouchEvent, if-5, shouldIntercept = " + shouldIntercept + ", direction = " + direction);

                        } else {

                            shouldIntercept = false;
                            Log.d(TAG, "onInterceptTouchEvent, if-6, shouldIntercept = " + shouldIntercept);
                        }

                    }

                }

                break;
        }


        Log.d(TAG, "onInterceptTouchEvent, returning from onIntercept, value = " + shouldIntercept);
        return shouldIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        boolean ret = false;

        int action  = MotionEventCompat.getActionMasked(ev);

        /*Log.d(TAG, "onTouchEvent, action = " + action);*/

        switch (action) {

            case MotionEvent.ACTION_MOVE:
                offset = ev.getY() - yOnDown + offsetOnDown;
                if (offset < 0) {
                    offset = 0;
                } else if (offset > maxOffset) {

                    float fingerDis = ev.getY() - yOnDown;//其中有一部分是offset < maxOffset的，刨除该部分

                    float baz = maxOffset - offsetOnDown;

                    float x = fingerDis - baz;

                    double y = maxExceedingOffsetPx / 1.57 * Math.atan(x * 4f / maxOverDragPx);

                    offset = maxOffset + (float) y;
                }

                requestLayout();
                ret = true;

                break;


            case MotionEvent.ACTION_UP:
                if (offset > maxOffset) {
                    // bounce back
                    ValueAnimator animator = new ValueAnimator();
                    animator.setFloatValues(offset, maxOffset);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            offset = (float) animation.getAnimatedValue();
                            requestLayout();
                        }
                    });
                    animator.start();
                }
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
