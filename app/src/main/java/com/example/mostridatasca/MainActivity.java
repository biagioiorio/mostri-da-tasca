package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity{

    private String sessionId;

    public static final String BASE_URL = "https://ewserver.di.unimi.it/mobicomp/mostri/";
    public static final String SHARED_PREFS_NAME = "sharedPrefs";   // Nome delle SharedPreferences
    public static final String SESSION_ID_KEY = "sessionId";    // Chiave del session_id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "OnCreate");
        setSessionId();
    }


    public void setSessionId() {
        /**
         * Setta il valore del session_id con il valore memorizzato nelle SharedPreferences
         * con la chiave SESSION_ID_KEY. Se è vuoto dire che l'utente è nuovo:
         * faccio una chiamata di rete (nel metodo setSessionIdFromServer())
         * al server per ottenere il session_id e lo salvo nelle SharedPreferences
         */

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        this.sessionId = sharedPreferences.getString(SESSION_ID_KEY, "");

        if (this.sessionId.isEmpty()) { // Nuovo utente
            Log.d("MainActivity", "SessionId non presente => nuovo utente");
            setSessionIdFromServer();
        }
    }


    public void saveSessionId(String sessionId) {
        /**
         * @author Biagio Iorio
         * Salva in modo persistente il session_id dell'utente,
         * se non è già presente, nelle SharedPreferences
         */
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SESSION_ID_KEY, sessionId);
        editor.apply();
        Log.d("MainActivity", "session_id salvato nelle sharedPreferences.");
    }


    private void setSessionIdFromServer() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BASE_URL + "register.php";

        // prepare the request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d("MainActivity", "Response: " + response.toString());
                        try {
                            sessionId = response.getString("session_id");
                            saveSessionId(sessionId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("MainActivity", "session_id: " + sessionId);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MainActivity", "Errore");
                        // TODO: gestisci l'errore
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
    }

}