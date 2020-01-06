package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FightEat extends AppCompatActivity {

    private static final String TAG = "Debug - FightEat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_eat);

        final Intent intent = getIntent();
        if (intent.hasExtra("id") && intent.hasExtra("type") && intent.hasExtra("name") && intent.hasExtra("img") && intent.hasExtra("size")){
            final String id = intent.getStringExtra("id");
            final String type = intent.getStringExtra("type");

            TextView nome = (TextView)findViewById(R.id.textView_caramellaMostro);
            nome.setText(intent.getStringExtra("name"));
            TextView dimensione = (TextView)findViewById(R.id.textView_dimensione);
            switch (intent.getStringExtra("size")){
                case "S": dimensione.setText("Dimensione: piccola");
                break;
                case "M": dimensione.setText("Dimensione: media");
                break;
                case "L": dimensione.setText("Dimensione: grande");
                break;
            }

            Button mappa = (Button)findViewById(R.id.button_ritirata);
            mappa.setText("MAPPA");
            mappa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });

            Button azione = (Button)findViewById(R.id.button_azione);
            azione.setEnabled(false);

            // abilita il pulsante affronta/mangia se il simbolo è vicino all'utente
            boolean isNear = intent.getBooleanExtra("isNear", false);
            Log.d(TAG,"isNear: " + isNear);
            if (isNear) azione.setEnabled(true);

            if (intent.getStringExtra("type").equals("MO")) {
                azione.setText("AFFRONTA");
            }else{
                azione.setText("MANGIA");
            }

            azione.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(getApplicationContext(), Esito.class);
                    intent1.putExtra("id", id);
                    intent1.putExtra("type", type);
                    startActivity(intent1);
                }
            });


            Bitmap bitmap = intent.getParcelableExtra("img");
            ImageView immagine = (ImageView)findViewById(R.id.imageView_caramellaMostro);
            immagine.setImageBitmap(bitmap);


        }
    }
}
