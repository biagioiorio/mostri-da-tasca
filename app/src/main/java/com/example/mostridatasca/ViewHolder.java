package com.example.mostridatasca;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    private TextView myTextView;

    public ViewHolder(View itemView) {
        super(itemView);
        myTextView = itemView.findViewById(R.id.textView);
    }

    public void setText(String text) {
        myTextView.setText(text);
    }
}