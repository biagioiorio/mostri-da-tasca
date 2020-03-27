package com.example.mostridatasca;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderTopPlayers extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView textView_playerName;
    public ImageView imgView_playerImg;
    public static final String TAG = " Debug - ViewHolderTopPlayers ";

    public ViewHolderTopPlayers(View itemView) {
        super(itemView);
        textView_playerName = itemView.findViewById(R.id.textView_singlerow); //creo variabile, la coolego alla textView in single_row
        imgView_playerImg = itemView.findViewById(R.id.imageView_list);
        itemView.setOnClickListener(this);
    }

    public ImageView getImage(){ return this.imgView_playerImg;}

    public void setText(String text) {
        textView_playerName.setText(text);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "OnClik on Element: " + textView_playerName.getText().toString() + " with position: " + getAdapterPosition());
    }
}
