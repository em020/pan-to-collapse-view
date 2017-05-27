package com.edmd.myapplication;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * WhatBar, 一个符合中国特色的action bar实现方式
 *
 * Created by yzsh-sym on 2017/4/6.
 */

public class WhatBarManager {

    private Activity activity;

    private FrameLayout frameL;
    private FrameLayout frameM;
    private FrameLayout frameR2;
    private FrameLayout frameR1;
    private View root;

    public enum Position {
        L, M, R2, R1
    }

    private Map<Position, FrameLayout> frameMap;

    private Map<Position, View.OnClickListener> listenerMap;

    private int dp44, dp64, dp160;

    public WhatBarManager(Activity activity) {
        this.activity = activity;

        root = activity.findViewById(R.id.what_bar_root);
        if (root == null) {
            throw new RuntimeException("必须在activity的跟布局中include what_bar_layout，并且在setContentView()方法之后初始化本类");
        }

        frameL = (FrameLayout) root.findViewById(R.id.frame_l);
        frameM = (FrameLayout) root.findViewById(R.id.frame_m);
        frameR2 = (FrameLayout) root.findViewById(R.id.frame_r2);
        frameR1 = (FrameLayout) root.findViewById(R.id.frame_r1);

        frameL.setVisibility(View.GONE);
        frameM.setVisibility(View.GONE);
        frameR2.setVisibility(View.GONE);
        frameR1.setVisibility(View.GONE);

        frameMap = new HashMap<>();
        frameMap.put(Position.L, frameL);
        frameMap.put(Position.M, frameM);
        frameMap.put(Position.R2, frameR2);
        frameMap.put(Position.R1, frameR1);

        listenerMap = new HashMap<>();

        dp44 = (int) UiUtil.dp2px(activity, 44);
        dp64 = (int) UiUtil.dp2px(activity, 64);
        dp160 = (int) UiUtil.dp2px(activity, 160);
    }


    public void setText(Position position, String text) {
        TextView textView = getOrGenTextView(position);
        textView.setText(text);
    }

    public void setText(Position position, String text,
                        @Nullable Integer color,
                        @Nullable Integer textSizeUnit, float textSize,
                        @Nullable Typeface tf, int textStyle) {

        TextView textView = getOrGenTextView(position);
        textView.setText(text);

        if (color != null) {
            textView.setTextColor(color);
        }

        if (textSizeUnit != null) {
            textView.setTextSize(textSizeUnit, textSize);
        }

        textView.setTypeface(tf, textStyle);
    }


    public void setText(Position position, int textResId) {
        TextView textView = getOrGenTextView(position);
        textView.setText(textResId);
    }

    public void setText(Position position, int textResId,
                        @Nullable Integer color,
                        @Nullable Integer textSizeUnit, float textSize,
                        @Nullable Typeface tf, int textStyle) {

        TextView textView = getOrGenTextView(position);
        textView.setText(textResId);

        if (color != null) {
            textView.setTextColor(color);
        }

        if (textSizeUnit != null) {
            textView.setTextSize(textSizeUnit, textSize);
        }

        textView.setTypeface(tf, textStyle);
    }


    public void setListener(Position position, View.OnClickListener listener) {
        listenerMap.put(position, listener);
    }

    public void setBarBackground(int drawableResId) {
        root.setBackgroundResource(drawableResId);
    }

    public void setBarBackgroudColor(int color) {
        root.setBackgroundColor(color);
    }

    public void setFitsSystemWindow() {
        root.setFitsSystemWindows(true);
    }

    public void setFitsSystemWindow(boolean fitsSystemWindow) {
        root.setFitsSystemWindows(fitsSystemWindow);
    }

    public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener listener) {
        ViewCompat.setOnApplyWindowInsetsListener(root, listener);
    }

    public View getViewAt(Position position) {
        FrameLayout frame = frameMap.get(position);
        if (frame.getChildCount() > 0) {
            return frame.getChildAt(0);
        } else {
            return null;
        }
    }

    public void setImage(Position position, int drawableResId) {
        ImageView imageView = getOrGenImageView(position);
        imageView.setImageResource(drawableResId);
    }


    private ImageView getOrGenImageView(final Position position) {

        FrameLayout frame = frameMap.get(position);

        if (frame.getVisibility() != View.VISIBLE) {
            frame.setVisibility(View.VISIBLE);
        }

        if (frame.getChildCount() > 1 && frame.getChildAt(0) instanceof ImageView) {
            return (ImageView) frame.getChildAt(0);
        } else {

            ImageView imageView = new ImageView(activity);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listenerMap.get(position) != null) {
                        listenerMap.get(position).onClick(v);
                    }
                }
            });

            int height = dp44;
            int width;

            if (position == Position.M) {
                width = dp160;
            } else {
                width = dp44;
            }

            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(width, height);
            param.gravity = Gravity.CENTER;
            imageView.setLayoutParams(param);

            frame.removeAllViews();
            frame.addView(imageView);

            return imageView;

        }
    }


    private TextView getOrGenTextView(final Position position) {

        FrameLayout frame = frameMap.get(position);

        if (frame.getVisibility() != View.VISIBLE) {
            frame.setVisibility(View.VISIBLE);
        }

        if (frame.getChildCount() > 0 && frame.getChildAt(0) instanceof TextView) {
            // reuse previous text view
            return (TextView) frame.getChildAt(0);
        } else {
            // create a new one
            TextView textView = new TextView(activity);

            if (position == Position.M) {
                /*textView.setMaxWidth(dp160);*/
            } else {
                textView.setMaxWidth(dp64);
            }
            textView.setMinWidth(dp44);
            textView.setMinHeight(dp44);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(null, 0);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenerMap.get(position) != null) {
                        listenerMap.get(position).onClick(v);
                    }
                }
            });


            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
            params.gravity = Gravity.CENTER;
            textView.setLayoutParams(params);

            frame.removeAllViews();
            frame.addView(textView);

            return textView;

        }
    }



}
