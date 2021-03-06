package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mostridatasca.com.example.mostridatasca.models.MonsterCandy;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.mostridatasca.MainActivity.SHARED_PREFS_NAME;
import static com.example.mostridatasca.Profilo.SESSION_ID_KEY;

public class Esito extends AppCompatActivity {

    public static final String TAG = " Debug - Esito ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esito);

        final Intent intent = getIntent();
        if (intent.hasExtra("id")) {

            final MonsterCandy monsterCandy = Model.getInstance().getMoncanById(intent.getStringExtra("id"));
            final JSONObject jsonBody = new JSONObject();
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

            try {
                jsonBody.put("session_id", sharedPreferences.getString(SESSION_ID_KEY, ""));
                jsonBody.put("target_id", monsterCandy.getId());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "JSON, problema");
            }
            Log.d(TAG, "JSON, jsonbody: " + jsonBody.toString());


            RequestQueue queue = Volley.newRequestQueue(this);
            String url = getString(R.string.base_url) + "fighteat.php";

            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                TextView frase = (TextView) findViewById(R.id.textView_frase);
                                if (monsterCandy.getType().equals("CA")){
                                    frase.setText("Buon appetito!");
                                }else{
                                    if (response.getString("died").equals("false")){
                                        frase.setText("Complimenti hai vinto!");
                                    }else{
                                        frase.setText("Mi spiace sei morto");
                                    }

                                }

                                TextView textXp = (TextView) findViewById(R.id.textXp);
                                textXp.setText("XP: " + response.getString("xp"));
                                Log.d(TAG, "XP: " + response.getString("xp"));

                                TextView textPv = (TextView) findViewById(R.id.textPv);
                                textPv.setText("LP: " + response.getString("lp"));
                                Log.d(TAG, "LP: " + response.getString("lp"));


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "Volley, Stringa: " + response.toString());


                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            TextView textUsername = (TextView) findViewById(R.id.textUsername);
                            textUsername.setText("Errore di rete riprovare più tardi");
                            Log.d(TAG, "Volley Errore");
                        }
                    }
            );
            queue.add(getRequest);
        }

        Button mappa = (Button)findViewById(R.id.button_mappa);
        mappa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
