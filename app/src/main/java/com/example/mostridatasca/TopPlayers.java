package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TopPlayers extends AppCompatActivity {

    public static final String SHARED_PREFS_NAME = "sharedPrefs";   // Nome delle SharedPreferences
    public static final String SESSION_ID_KEY = "sessionId";       // Chiave del session_id
    public static final String TAG = " Debug - TopPlayers ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_players);

        getTopPlayers();
    }

    private void getTopPlayers() {
        /**
         * Fa la chiamata 'ranking' al server
         */
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "ranking.php";

        final JSONObject jsonBody = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        // Preparo il body con il session_id
        try {
            jsonBody.put("session_id", sharedPreferences.getString(SESSION_ID_KEY, ""));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG," Volley_TopPlayer ERRORE --> JsonBody ");
        }
        Log.d(TAG," Volley_TopPlayer JsonBody: " + jsonBody.toString());

        // prepare the request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, " Volley_TopPlayer getTopPlayers --> Response: " + response.toString());
                        //parso nell'arraylist topPlayers
                        JSONArray topPlayers = new JSONArray();
                        try {
                            topPlayers = response.getJSONArray("ranking");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Model.getInstance().addPlayersFromJSONArray(topPlayers);    // Svuota l'arraylist e aggiunge i topPlayers al model

                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        AdapterTopPlayers adapter = new AdapterTopPlayers(getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, " Volley_TopPlayer ERRORE ---> Volley OnErrorResponse " + error.toString());
                        TextView tp = (TextView) findViewById(R.id.textView_TopPlayers);
                        tp.setText("Connessione assente riprovare più tardi");
                    }
                }
        );
        queue.add(getRequest);  // aggiungo la richiesta alla coda
    }


}
