package com.example.mostridatasca;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    /**
     * @author Betto Matteo
     */
    private TextView myTextView;
    public ImageView myImageView;
    public static final String TAG = " Debug - ViewHolder ";

    public ViewHolder(View itemView) {
        super(itemView);
        myTextView = itemView.findViewById(R.id.textView); //creo variabile, la coolego alla textView in single_row
        myImageView = itemView.findViewById(R.id.imageView_list);
        itemView.setOnClickListener(this);
    }

    public ImageView getImage(){ return this.myImageView;}

    public void setText(String text) {
        myTextView.setText(text);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "OnClik on Element: " + myTextView.getText().toString() + " with position: " + getAdapterPosition());
    }
}
