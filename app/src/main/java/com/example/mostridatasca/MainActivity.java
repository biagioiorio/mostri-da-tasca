package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS_NAME = "sharedPrefs";   // Nome delle SharedPreferences
    public static final String SESSION_ID_KEY = "sessionId";    // Chiave del session_id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "OnCreate");
        getSessionId();
    }


    public void saveSessionId() {
        /**
         * @author Biagio Iorio
         * Metodo che salva in modo persistente il session_id dell'utente,
         * se non è già presente, nelle SharedPreferences
         */
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // editor.putString(SESSION_ID_KEY, "sessionid2");

        String sessionId = sharedPreferences.getString(SESSION_ID_KEY, "");
        // editor.remove(SESSION_ID_KEY);
        editor.apply();
        Log.d("MainActivity", String.valueOf(sessionId.isEmpty()));
    }

    public String getSessionId() {
        /**
         * Ritorna il valore del session_id memorizzato nelle SharedPreferences
         * con la chiave SESSION_ID_KEY. Se non è presente vuol dire che l'utente è nuovo:
         * faccio una chiamata di rete (register) al server per ottenere il session_id
         */

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(SESSION_ID_KEY, "");

        if(sessionId.isEmpty()){
            // Nuovo utente
            Log.d("MainActivity", "SessionId non presente => nuovo utente");
            // Fai chiamata register per ricevere il session id e salvalo nelle shared preferences
        }

        return sessionId;
    }
}