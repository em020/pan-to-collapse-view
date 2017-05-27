package com.edmd.myapplication;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.lang.reflect.InvocationTargetException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarFucker fucker = new StatusBarFucker();
        fucker.setWindowExtend(1);
        fucker.setStatusBarColor(Color.TRANSPARENT);
        fucker.setUseDarkNotiIcon(false);
        fucker.fuck(getWindow());

        PanToCollapseView p2cView = (PanToCollapseView) findViewById(R.id.pan_to_collapse_view);
        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView collapsingPartRecyclerView = (RecyclerView) findViewById(R.id.collapsing_part_recycler_view);
        final View whatBarRoot = findViewById(R.id.what_bar_root);
        final View frameCollapsing = findViewById(R.id.frame_collapsing_part);

        Point p = getAppUsableScreenSize(this);
        p2cView.setMaxOffset(p.y * (1f - 0.618f));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FooAdapter(100, null));

        collapsingPartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        collapsingPartRecyclerView.setAdapter(new FooAdapter(8, null));

        p2cView.setOnPanListener(new PanToCollapseView.OnPanListener() {
            @Override
            public void onPan(float currentOffset, float lastOffset, float minOffset, float maxOffset, float maxMaxOffset) {
                Log.d("edmund", String.format("onPan, cur=%f, last=%f, min=%f, max=%f, maxmax=%f", currentOffset, lastOffset, minOffset, maxOffset, maxMaxOffset));

                if (lastOffset > minOffset && currentOffset <= minOffset) {
                    // 到顶事件
                    ObjectAnimator animator = ObjectAnimator.ofFloat(whatBarRoot, "alpha", 0f, 1f);
                    animator.setDuration(200);
                    animator.start();

                    StatusBarFucker fucker = new StatusBarFucker();
                    fucker.setUseDarkNotiIcon(true);
                    fucker.setStatusBarColor(Color.TRANSPARENT);
                    fucker.fuck(getWindow());
                }

                if (lastOffset <= minOffset && currentOffset > minOffset) {
                    // 离顶事件
                    ObjectAnimator animator = ObjectAnimator.ofFloat(whatBarRoot, "alpha", 1f, 0f);
                    animator.setDuration(200);
                    animator.start();

                    StatusBarFucker fucker = new StatusBarFucker();
                    fucker.setUseDarkNotiIcon(false);
                    fucker.setStatusBarColor(Color.TRANSPARENT);
                    fucker.fuck(getWindow());
                }
            }
        });

        whatBarRoot.setAlpha(0f);

        WhatBarManager wbm = new WhatBarManager(this);
        wbm.setText(WhatBarManager.Position.L, "hello");
        wbm.setText(WhatBarManager.Position.M, "WORLD!");

        ViewCompat.setOnApplyWindowInsetsListener(whatBarRoot, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                v.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
                return insets;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(frameCollapsing, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                v.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
                return insets;
            }
        });
    }




    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {

            display.getRealSize(size);

        } else if (Build.VERSION.SDK_INT >= 14) {

            try {

                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        }

        return size;
    }

}
