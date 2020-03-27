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

import com.example.mostridatasca.com.example.mostridatasca.models.MonsterCandy;

public class FightEat extends AppCompatActivity {

    private static final String TAG = "Debug - FightEat";
    private MonsterCandy monsterCandy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_eat);
        Log.d(TAG," Activity creata ");

        final Intent intent = getIntent();
        if (intent.hasExtra("id") && intent.hasExtra("isNear") && intent.hasExtra("distance")){

            monsterCandy = Model.getInstance().getMoncanById(intent.getStringExtra("id"));
            Log.d(TAG," monstercandy: " + monsterCandy.toString());

            TextView nome = (TextView)findViewById(R.id.textView_caramellaMostro);
            nome.setText(monsterCandy.getName());

            TextView dimensione = (TextView)findViewById(R.id.textView_dimensione);
            switch (monsterCandy.getSize()){
                case "S": dimensione.setText("Dimensione: piccola");
                break;
                case "M": dimensione.setText("Dimensione: media");
                break;
                case "L": dimensione.setText("Dimensione: grande");
                break;
            }

            //================================================================================
            // Intent
            //================================================================================
            Button mappa = (Button)findViewById(R.id.button_ritirata);
            mappa.setText("    MAPPA    ");
            mappa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentMappa = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intentMappa);
                }
            });

            Button azione = (Button)findViewById(R.id.button_azione);
            azione.setEnabled(false);

            // abilita il pulsante affronta/mangia se il simbolo è vicino all'utente
            boolean isNear = intent.getBooleanExtra("isNear", false);
            Log.d(TAG,"isNear: " + isNear);
            if (isNear) azione.setEnabled(true);

            if (monsterCandy.getType().equals("MO")) {
                azione.setText("    AFFRONTA    ");
            }else{
                azione.setText("    MANGIA    ");
            }

            azione.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentAzione = new Intent(getApplicationContext(), Esito.class);
                    intentAzione.putExtra("id", monsterCandy.getId());
                    intentAzione.putExtra("type", monsterCandy.getType());
                    startActivity(intentAzione);
                }
            });


            Bitmap bitmap = monsterCandy.getImg();
            ImageView immagine = (ImageView)findViewById(R.id.imageView_caramellaMostro);
            immagine.setImageBitmap(bitmap);


        }
    }
}
