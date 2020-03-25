package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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

import org.json.JSONException;
import org.json.JSONObject;

public class Profilo extends AppCompatActivity {

    public static final String SHARED_PREFS_NAME = "sharedPrefs";   // Nome delle SharedPreferences
    public static final String SESSION_ID_KEY = "sessionId";        // Chiave del session_id
    public static final String TAG = " Debug - Profilo ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        Log.d(TAG, Model.getInstance().moncanToString());

        //================================================================================
        // Intent
        //================================================================================
        Button pulsanteIndietro = (Button)findViewById(R.id.button_indietro);
        Button pulsanteModificaProfilo = (Button)findViewById(R.id.button_modifica_profilo);

        pulsanteIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, " Pulsante premuto: Indietro ");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        pulsanteModificaProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Pulsante premuto: Modifica profilo ");
                Intent intent = new Intent(getApplicationContext(), ModificaProfilo.class);
                startActivity(intent);
            }
        });

        setContenutoUtente();

    }

    public void setContenutoUtente(){
        /**
         * @author Betto
         */
        final JSONObject jsonBody = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        try {
            jsonBody.put("session_id", sharedPreferences.getString(SESSION_ID_KEY, ""));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG," JSON errore ");
        }
        Log.d(TAG,"JSON jsonbody: " + jsonBody.toString());

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "getprofile.php";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            byte[] decodedString = Base64.decode(response.getString("img"), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            ImageView imgUser = (ImageView) findViewById(R.id.imgUser);
                            imgUser.setImageBitmap(decodedByte);

                            TextView textUsername = (TextView) findViewById(R.id.textUsername);
                            textUsername.setText("USERNAME: " + response.getString("username"));

                            TextView textXp = (TextView) findViewById(R.id.textXp);
                            textXp.setText("XP: " + response.getString("xp"));

                            TextView textPv = (TextView) findViewById(R.id.textPv);
                            textPv.setText("LP: " + response.getString("lp"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "Volley --> Stringa risposta: " + response.toString());


                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TextView textUsername = (TextView) findViewById(R.id.textUsername);
                        textUsername.setText("Errore di rete riprovare più tardi");
                        Log.d(TAG, "Volley --> Errore");
                    }
                }
        );
        queue.add(getRequest);
    }

}

