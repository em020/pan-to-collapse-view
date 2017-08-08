package com.edmd.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.RectF;
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

    public interface OnPanListener {
        void onPan(float currentOffset, float lastOffset, float minOffset, float maxOffset, float maxMaxOffset);
    }

    private static final String TAG = PanToCollapseView.class.getSimpleName();

    private float MAX_OVER_DRAG_PX;//从maxoffset处，手指再下滑多少，collapsing part就到了最大offset了
    private float MAX_EXCEEDING_OFFSET_PX; // 最大offset超过maxoffset的超出值

    private float MIN_OFFSET = -1;
    private float MAX_OFFSET = 300; // overwritten in constructor

    private float offset = MAX_OFFSET;
    private float offsetOnDown;
    private float yOnDown;
    private float previousOffset;

    private int touchTarget; // 0 - collapsing, 1 - panning
    private View collapsingPartTouchedChild;

    private OnPanListener onPanListener;



    public PanToCollapseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public void setMaxOffset(float maxOffset) {
        this.MAX_OFFSET = maxOffset;
        offset = MAX_OFFSET;
    }

    public void setOnPanListener(OnPanListener onPanListener) {
        this.onPanListener = onPanListener;
    }

    private void initialize(AttributeSet attrs) {

        MAX_OFFSET = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, getContext().getResources().getDisplayMetrics());
        offset = MAX_OFFSET;

        MAX_OVER_DRAG_PX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350f, getContext().getResources().getDisplayMetrics());
        MAX_EXCEEDING_OFFSET_PX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 135f, getContext().getResources().getDisplayMetrics());

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


        int actionBarPartLayout = array.getResourceId(R.styleable.PanToCollapseView_action_bar_part_layout, -1);
        if (actionBarPartLayout == -1) {
            throw new RuntimeException("必须提供action_bar_part_layout");
        } else {
            inflate(getContext(), actionBarPartLayout, this);
        }

        array.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //HADV - how android draw views
        /*Log.d(TAG, "HADV, onMeasure");*/

        int pWidth = MeasureSpec.getSize(widthMeasureSpec);
        int pHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(pWidth, pHeight);


        int matchParentWidthSpec = MeasureSpec.makeMeasureSpec(pWidth, MeasureSpec.EXACTLY);

        int collapsingPartHeightSpec;
        if (offset <= MAX_OFFSET) {
            collapsingPartHeightSpec = MeasureSpec.makeMeasureSpec((int) MAX_OFFSET, MeasureSpec.EXACTLY);
        } else {
            collapsingPartHeightSpec = MeasureSpec.makeMeasureSpec((int) offset, MeasureSpec.EXACTLY);
        }
        View collapsingPart = getChildAt(0);
        measureChild(collapsingPart, matchParentWidthSpec, collapsingPartHeightSpec);


        int panningPartHeightSpec = MeasureSpec.makeMeasureSpec(pHeight, MeasureSpec.EXACTLY);
        View panningPart = getChildAt(1);
        measureChild(panningPart, matchParentWidthSpec, panningPartHeightSpec);


        int actionBarPartHeightSpec = MeasureSpec.makeMeasureSpec((int) MAX_OFFSET, MeasureSpec.AT_MOST);
        View actionBarPart = getChildAt(2);
        measureChild(actionBarPart, matchParentWidthSpec, actionBarPartHeightSpec);

        if (MIN_OFFSET < 0) {
            MIN_OFFSET = actionBarPart.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        /*Log.d(TAG, "HADV, onLayout");*/

        View collapsingPart = getChildAt(0);
        if (offset <= MAX_OFFSET) {
            int fooTop = (int) (t - MAX_OFFSET /2f + offset/2f);
            collapsingPart.layout(l, fooTop, r, fooTop + (int) MAX_OFFSET);
        } else {
            collapsingPart.layout(l, t, r, t + (int)offset);
        }


        int top;
        int bottom;
        View panningPart = getChildAt(1);
        top = (int) (t + offset);
        bottom = (int) (b + offset);
        panningPart.layout(l, top, r, bottom);


        View actionBarPart = getChildAt(2);
        actionBarPart.layout(l, t, r, t + actionBarPart.getMeasuredHeight());

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean shouldIntercept = false;

        int action  = MotionEventCompat.getActionMasked(ev);

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                yOnDown = ev.getY();
                offsetOnDown = offset;
                previousOffset = offset;

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

                    if (offset > MIN_OFFSET) {

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

        /*boolean ret = false;*/

        int action  = MotionEventCompat.getActionMasked(ev);

        /*Log.d(TAG, "onTouchEvent, action = " + action);*/

        switch (action) {

            case MotionEvent.ACTION_MOVE:

                offset = ev.getY() - yOnDown + offsetOnDown;

                if (offset < MIN_OFFSET) {

                    offset = MIN_OFFSET;

                } else if (offset > MAX_OFFSET) {

                    float fingerDis = ev.getY() - yOnDown;//其中有一部分是offset < maxOffset的，刨除该部分

                    float baz = MAX_OFFSET - offsetOnDown;

                    float x = fingerDis - baz;

                    double y = MAX_EXCEEDING_OFFSET_PX / 1.57 * Math.atan(x * 4f / MAX_OVER_DRAG_PX);

                    offset = MAX_OFFSET + (float) y;
                }

                /*int hisSize = ev.getHistorySize();
                StringBuilder sb = new StringBuilder("ev_y: ");

                for (int i = 0; i < hisSize; i++) {
                    float y = ev.getHistoricalY(i);
                    sb.append(String.format("(%d, %f); ", i, y));
                }

                Log.d(TAG, sb.toString());*/

                requestLayout();


                if (onPanListener != null) {
                    onPanListener.onPan(offset, previousOffset, MIN_OFFSET, MAX_OFFSET, MAX_OFFSET + MAX_EXCEEDING_OFFSET_PX);
                }

                previousOffset = offset;

                break;


            case MotionEvent.ACTION_UP:
                if (offset > MAX_OFFSET) {
                    // bounce back
                    ValueAnimator animator = new ValueAnimator();
                    animator.setFloatValues(offset, MAX_OFFSET);
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
