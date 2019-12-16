package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS_NAME = "sharedPrefs";
    public static final String SESSION_ID_KEY = "sessionId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "OnCreate");
        saveSessionId();
    }

    public void saveSessionId() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // editor.putString(SESSION_ID_KEY, "sessionid2");

        String sessionId = sharedPreferences.getString(SESSION_ID_KEY, "");
        // editor.remove(SESSION_ID_KEY);
        editor.apply();
        Log.d("MainActivity", String.valueOf(sessionId.isEmpty()));
    }
}
