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

import org.json.JSONException;
import org.json.JSONObject;

public class Profilo extends AppCompatActivity {

    public static final String SHARED_PREFS_NAME = "sharedPrefs";   // Nome delle SharedPreferences
    public static final String SESSION_ID_KEY = "sessionId";        // Chiave del session_id
    public static final String TAG = "Debug - Profilo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * @author Betto Matteo
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        setContenutoUtente();
    }


    //================================================================================
    // Metodi
    //================================================================================
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

                            ImageView imgUser = (ImageView) findViewById(R.id.imageView_fotoProfilo);
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


        //================================================================================
        // Intent
        //================================================================================
        Button buttonMappa = (Button) findViewById(R.id.button_mappa);
        Button buttonModificaProfilo = (Button) findViewById(R.id.button_modificaProfilo);

        buttonMappa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * @author Betto
                 * ritorna alla mappa
                 */
                Log.d(TAG, " buttonMappa premuto ");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        buttonModificaProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * @author Betto
                 * ritorna alla pagina profilo
                 */
                Log.d(TAG, " buttonAnnullaModifiche ");
                Intent intent = new Intent(getApplicationContext(), ModificaProfilo.class);
                startActivity(intent);
                //TODO gestire il caso in cui internet sia assente, mettere un FLAG nell'onErrorResponse
            }
        });

    }

}

