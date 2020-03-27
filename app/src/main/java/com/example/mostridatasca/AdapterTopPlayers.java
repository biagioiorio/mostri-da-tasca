package com.example.mostridatasca;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterTopPlayers extends RecyclerView.Adapter<ViewHolderTopPlayers> {

    private LayoutInflater mInflater;

    public AdapterTopPlayers(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolderTopPlayers onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.single_row, parent, false);
        return new ViewHolderTopPlayers(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderTopPlayers holder, int position) {     //come gestire la singola riga "position"
        String text = Model.getInstance().getPlayer(position).getName();
        holder.setText(position + 1 + " " + text);


        byte[] decodedString = Base64.decode(Model.getInstance().getPlayer(position).getImg(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        holder.imgView_playerImg.setImageBitmap(decodedByte);

    }

    @Override
    public int getItemCount() {
        return Model.getInstance().getPlayersSize();   //numero di elementi da stampare
    }
}