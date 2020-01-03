package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        setDatiFake();
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
            Log.d("JSON","problema");
        }
        Log.d("JSON","jsonbody: " + jsonBody.toString());


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
                        Log.d("Volley", "Stringa: " + response.toString());


                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TextView textUsername = (TextView) findViewById(R.id.textUsername);
                        textUsername.setText("Errore di rete riprovare più tardi");
                        Log.d("Volley", "Errore");
                    }
                }
        );
        queue.add(getRequest);

    }

    public void setDatiFake(){
        /**
         * @author Betto
         */

        final JSONObject jsonBody = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        try {
            jsonBody.put("session_id", sharedPreferences.getString(SESSION_ID_KEY, ""));
            jsonBody.put("username", "Ceredone");
            jsonBody.put("img", "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");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSON2","problema");
        }
        Log.d("JSON2","jsonbody: " + jsonBody.toString());


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "setprofile.php";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley2", "Errore");
                    }
                }
        );
        queue.add(getRequest);
    }

    public void onButtonModificaClick(View v) {
        Log.d(" Pulsante: ", " Modifica Profilo ");
        Intent intent = new Intent(getApplicationContext(), ModificaProfilo.class);
        startActivity(intent);
    }

}

