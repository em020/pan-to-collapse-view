package com.edmd.myapplication;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

public class RecyclerViewWithinScrollViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_within_scroll_view);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FooAdapter(8, null));

        View foo2 =  findViewById(R.id.view_foo_2);
        foo2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.d("edmund", String.format(Locale.US, "onTouch, action = %d, x = %d, y = %d",event.getAction(), (int)event.getX(), (int)event.getY()));

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Rect rect = new Rect();
                    v.getHitRect(rect);
                    Log.d("edmund", "hit rect = " + rect.toShortString());
                    v.getLocalVisibleRect(rect);
                    Log.d("edmund", "local visible rect = " + rect.toShortString());
                }

                return true;
            }
        });
    }


}
