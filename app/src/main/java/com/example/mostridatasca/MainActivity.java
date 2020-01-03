package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;


//TODO: Aggiungere pulsante per centrare la camera sulla posizione dell'utente
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, Style.OnStyleLoaded, PermissionsListener {

    private String sessionId;
    private RequestQueue queue;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private Style style;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private LocationEngine locationEngine;
    private LocationListeningCallback locationListeningCallback;
    private Location location;

    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

    public static final String SHARED_PREFS_NAME = "sharedPrefs";   // Nome delle SharedPreferences
    public static final String SESSION_ID_KEY = "sessionId";        // Chiave del session_id

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0; // serve per identificare i permessi in caso volessi gestirli

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibWF0dGVvYmV0dG8iLCJhIjoiY2szNGF1OGgwMDBhNjNucWRzY29oaTU3OCJ9.G066wR9mYwJUPmWcD_vrwQ");
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "OnCreate");

        queue = Volley.newRequestQueue(this);

        setSessionId();
        // ATTENZIONE LA CHIAMATA DI RETE È ASINCRONA. Ci dobbiamo assicurare che sia stato già settato il session_id.
        Log.d("MainActivity", "session_id settato -> " + this.sessionId);
        // TODO: settare uno username nel caso di un nuovo utente.

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        permissionsManager = new PermissionsManager(this);
        locationListeningCallback = new LocationListeningCallback(this);

        getMonstersAndCandies();

    }


    //================================================================================
    // SessionId
    //================================================================================
    public void setSessionId() {
        /**
         * @author Biagio Iorio
         * Setta il valore del session_id con il valore memorizzato nelle SharedPreferences
         * con la chiave SESSION_ID_KEY. Se è vuoto dire che l'utente è nuovo:
         * faccio una chiamata di rete (nel metodo setSessionIdFromServer())
         * al server per settare il session_id.
         */

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        this.sessionId = sharedPreferences.getString(SESSION_ID_KEY, "");
        // this.sessionId = "";  // DEBUG: decommentare per simulare un nuovo utente.
        if (this.sessionId.isEmpty()) { // Nuovo utente
            Log.d("MainActivity", "SessionId non presente. Nuovo utente. Contatto il server...");
            setSessionIdFromServer();
        }
    }

    private void setSessionIdFromServer() {
        /**
         * @author Biagio Iorio
         * Fa la chiamata 'Register' al server e salva il valore ricevuto (session_id)
         * nelle SharedPreferences con il metodo saveSessionId(sessionId).
         */

        //RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "register.php";

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
                        Log.d("MainActivity", "session_id (from server): " + sessionId);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MainActivity", "sessionid-request Errore:"+error.getMessage());
                        // TODO: gestire l'errore
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
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
        Log.d("MainActivity", "session_id salvato nelle sharedPreferences");
    }


    //================================================================================
    // Map
    //================================================================================
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        Log.d("MainActivity","Map ready");
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.DARK, this);
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {
        // la mappa è pronta, si possono modificare le sue proprietà
        Log.d("MainActivity","Style loaded");
        this.style = style;
        enableLocationComponent(); // Visualizza il pallino blu dell'utente
    }

    /***
     * Serve per mostrare la posizione dell'utente sulla mappa
     */
    private void enableLocationComponent() {

        if(PermissionsManager.areLocationPermissionsGranted(this)){
            Log.d("MainActivity","Permessi già ottenuti");
            // Permessi già ottenuti

            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, this.style).useDefaultLocationEngine(false).build());
            locationComponent.setLocationComponentEnabled(true);    // Rende il pallino visibile
            locationComponent.setCameraMode(CameraMode.TRACKING);   // La camera si centra sul pallino
            locationComponent.setRenderMode(RenderMode.COMPASS);    // Forma del pallino

            initLocationEngine();   // Inizializza il location engine
        } else {
            Log.d("MainActivity","Permessi non ottenuti");
            // Chiedo i permessi
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Inizializza il LocationEngine e i suoi parametri per ottenere aggiornamenti sulla posizione del device
     * (es: ogni quanto aggiornare la posizione, accuratezza, ...)
     */
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        // Imposto una callback da chiamare a ogni nuova posizione (locationListeningCallback)
        locationEngine.requestLocationUpdates(request, locationListeningCallback, getMainLooper());
        locationEngine.getLastLocation(locationListeningCallback);
    }


    //================================================================================
    // PermissionsManager
    //================================================================================
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        /***
         * Questo metodo viene chiamato quando l'utente rifiuta i permessi una volta
         * e gli vengono richiesti per la seconda volta. Possiamo scrivere spiegazioni aggiuntive
         * sul perchè deve accettare i permessi.
         */
    }

    @Override
    public void onPermissionResult(boolean granted) {
        /***
         * Viene chiamato quando l'utente risponde alla richiesta dei permessi.
         * Sia se accetta, sia se rifiuta. L'argomento granted è TRUE se l'utente ha dato i permessi
         * e FALSE se non li ha dati.
         */
        if(granted){
            Log.d("MainActivity","Permessi appena ottenuti");
            enableLocationComponent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //================================================================================
    // Monsters and candies
    //================================================================================
    private void getMonstersAndCandies() {
        /**
         * @author Biagio Iorio
         * Fa la chiamata 'getMap' al server
         */

        //RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "getmap.php";

        final JSONObject jsonBody = new JSONObject();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        try {
            jsonBody.put("session_id", sharedPreferences.getString(SESSION_ID_KEY, ""));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("MainActivity","getMonstersAndCandies() - JSON: problema");
        }
        Log.d("MainActivity","getMonstersAndCandies() - JSON body: " + jsonBody.toString());

        // prepare the request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MainActivity", "getMonstersAndCandies() - Response: " + response.toString());
                        parseMonstersAndCandiesResponse(response);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MainActivity", "getMonstersAndCandies - Error: " + error.toString());
                        // TODO: gestire l'errore
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }

    private void parseMonstersAndCandiesResponse(JSONObject response) {
        JSONArray monstersAndCandiesArray = new JSONArray();
        try {
            monstersAndCandiesArray = response.getJSONArray("mapobjects");
            //Log.d("MainActivity","parseMonstersAndCandiesResponse"+monstersAndCandies.getJSONObject(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < monstersAndCandiesArray.length(); i++) {
            JSONObject jsonobject = null;
            String id, type, size, name;
            double lat, lon;
            id = type = size = name = "";
            lat = lon = 0.0;
            try {
                jsonobject = monstersAndCandiesArray.getJSONObject(i);
                id = jsonobject.getString("id");
                lat = jsonobject.getDouble("lat");
                lon = jsonobject.getDouble("lon");
                type = jsonobject.getString("type");
                size = jsonobject.getString("size");
                name = jsonobject.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("MainActivity",id+", "+lat+", "+lon+", "+type+", "+size+", "+name);

        }

    }


    //================================================================================
    // Intent
    //================================================================================
    public void onButtonProfiloClick(View v) {
        Log.d("Pulsante: ", "Profilo");
        Intent intent = new Intent(getApplicationContext(), Profilo.class);
        startActivity(intent);
    }

    public void onButtonElencoClick(View v) {
        Log.d("Pulsante: ", "Elenco");
        Intent intent = new Intent(getApplicationContext(), TopPlayers.class);
        startActivity(intent);
    }


    //================================================================================
    // Ciclo di vita - metodi
    //================================================================================
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(locationListeningCallback);
        }
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }



    //================================================================================
    // Location Listening Callback - per aggiornamenti sulla posizione
    //================================================================================
    private static class LocationListeningCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MainActivity> activityWeakReference;
        private MainActivity mainActivity;

        LocationListeningCallback(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
            mainActivity = activity;
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            /***
             * onSuccess viene chiamato ogni volta che cambia la posizione dell'utente
             */
            Log.d("MainActivity","LocationListeningCallback: Location changed.");
            mainActivity.location = result.getLastLocation();   // Aggiorno la variabile location della mainActivity con la posizione attuale
            if (mainActivity.mapboxMap != null && result.getLastLocation() != null) {
                mainActivity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            // The LocationEngineCallback interface's method which fires when the device's location can not be captured
            Log.d("MainActivity","LocationListeningCallback: Location can not be captured.");
        }
    }

}

















