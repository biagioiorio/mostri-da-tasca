package com.example.mostridatasca;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
    /**
     * @author Betto Matteo
     */
    private LayoutInflater mInflater;

    public MyAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.single_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {     //come gestire la singola riga "position"
        String text = Model.getInstance().getPlayerName(position);
        holder.setText(position + 1 + " " + text);


        byte[] decodedString = Base64.decode(Model.getInstance().getImg(position), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        holder.myImageView.setImageBitmap(decodedByte);


    }

    @Override
    public int getItemCount() {
        return Model.getInstance().getPlayersSize();   //numero di elementi da stampare
    }
}