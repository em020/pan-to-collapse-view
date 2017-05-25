package com.edmd.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarFucker fucker = new StatusBarFucker();
        fucker.setWindowExtend(1);
        fucker.setStatusBarColor(Color.TRANSPARENT);
        fucker.setUseDarkNotiIcon(true);
        fucker.fuck(getWindow());

        PanToCollapseView p2cView = (PanToCollapseView) findViewById(R.id.pan_to_collapse_view);
        Point p = getAppUsableScreenSize(this);
        p2cView.setMaxOffset(p.y * (1f - 0.618f));


        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FooAdapter(100, null));


        RecyclerView collapsingPartRecyclerView = (RecyclerView) findViewById(R.id.collapsing_part_recycler_view);
        collapsingPartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        collapsingPartRecyclerView.setAdapter(new FooAdapter(8, null));
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
