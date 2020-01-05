package com.example.mostridatasca;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
    private LayoutInflater mInflater;

    public MyAdapter(Context context) {
        /**
         * @author Betto Matteo
         */
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /**
         * @author Betto Matteo
         */
        View view = mInflater.inflate(R.layout.single_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        /**
         * @author Betto Matteo
         */
        String text = Model.getInstance().get(position);
        holder.setText(position + 1 + " " + text);
    }
    @Override
    public int getItemCount() {
        /**
         * @author Betto Matteo
         */
        return Model.getInstance().getSize();
    }
}