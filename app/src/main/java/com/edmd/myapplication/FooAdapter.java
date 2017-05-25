package com.edmd.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by yzsh-sym on 2017/5/25.
 */

public class FooAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int count;
    private View.OnClickListener onItemViewClickListener;

    public FooAdapter(int count, View.OnClickListener onItemViewClickListener) {
        this.count = count;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new FooVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FooVH)holder).textView.setText("hello, " + position);
    }

    @Override
    public int getItemCount() {
        return count;
    }


    private class FooVH extends RecyclerView.ViewHolder {

        TextView textView;

        FooVH(View itemView) {
            super(itemView);
            itemView.setOnClickListener(onItemViewClickListener);

            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}