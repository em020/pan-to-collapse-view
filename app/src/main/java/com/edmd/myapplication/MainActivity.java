package com.edmd.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarFucker fucker = new StatusBarFucker();
        fucker.setWindowExtend(1);
        fucker.setStatusBarColor(Color.TRANSPARENT);
        fucker.fuck(getWindow());


        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FooAdapter(100, null));


        RecyclerView collapsingPartRecyclerView = (RecyclerView) findViewById(R.id.collapsing_part_recycler_view);
        collapsingPartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        collapsingPartRecyclerView.setAdapter(new FooAdapter(8, null));
    }




}
