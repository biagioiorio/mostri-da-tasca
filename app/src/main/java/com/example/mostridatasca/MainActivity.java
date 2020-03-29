package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.widget.Button;

import com.example.mostridatasca.com.example.mostridatasca.models.MonsterCandy;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, Style.OnStyleLoaded, PermissionsListener {

    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private static final long UPDATE_MONSTERS_AND_CANDIES_DELAY = 120000;
    private static final double FIGHT_EAT_DISTANCE = 50000.0;

    public static final String SHARED_PREFS_NAME = "sharedPrefs";   // Nome delle SharedPreferences
    public static final String SESSION_ID_KEY = "sessionId";       // Chiave del session_id

    public static final String TAG = " Debug - MainActivity ";

    private SharedPreferences sharedPreferences;
    private String sessionId;
    private String testSessionId;
    private RequestQueue queue;
    private Integer numberOfRequests;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private Style style;
    private SymbolManager symbolManager;
    private OnSymbolClickListener onSymbolClickListener;

    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationListeningCallback locationListeningCallback;
    private Location location;

    private Handler handler;
    private Runnable runnableCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibWF0dGVvYmV0dG8iLCJhIjoiY2szNGF1OGgwMDBhNjNucWRzY29oaTU3OCJ9.G066wR9mYwJUPmWcD_vrwQ");
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate");

        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        queue = Volley.newRequestQueue(this);
        numberOfRequests = 0;

        Model.getInstance().clearMoncan();

        setSessionId();
        Log.d(TAG, "session_id settato -> " + this.sessionId);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        permissionsManager = new PermissionsManager(this);
        locationListeningCallback = new LocationListeningCallback(this);

        //================================================================================
        // Intent
        //================================================================================
        Button pulsanteProfilo = (Button)findViewById(R.id.button_profilo);
        Button pulsanteTopPlayers = (Button)findViewById(R.id.button_topPlayers);

        pulsanteProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, " Pulsante Profilo premuto ");
                Intent intent = new Intent(getApplicationContext(), Profilo.class);
                startActivity(intent);
            }
        });

        pulsanteTopPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, " Pulsante TopPlayers premuto ");
                Intent intent = new Intent(getApplicationContext(), TopPlayers.class);
                startActivity(intent);
            }
        });

    }


    //================================================================================
    // SessionId
    //================================================================================
    public void setSessionId() {
        /**
         * Setta il valore del session_id con il valore memorizzato nelle SharedPreferences
         * con la chiave SESSION_ID_KEY. Se è vuoto dire che l'utente è nuovo:
         * faccio una chiamata di rete (nel metodo setSessionIdFromServer())
         * al server per settare il session_id.
         */

        this.sessionId = sharedPreferences.getString(SESSION_ID_KEY, "");
        this.testSessionId = getResources().getString(R.string.test_session_id);
        if (!this.testSessionId.isEmpty()){
            saveSessionId(this.testSessionId);
        }else{
            if (this.sessionId.isEmpty()) { // Nuovo utente
                Log.d(TAG, "SessionId non presente. Nuovo utente. Contatto il server...");
                setSessionIdFromServer();
            }
        }
    }


    public void saveSessionId(String sessionId) {
        /**
         * Salva in modo persistente il session_id dell'utente,
         * se non è già presente, nelle SharedPreferences
         */

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SESSION_ID_KEY, sessionId);
        editor.apply();
        Log.d(TAG, "saveSessionId() - session_id salvato nelle sharedPreferences: " + sessionId);
    }


    private void setSessionIdFromServer() {
        /**
         * Fa la chiamata 'Register' al server e salva il valore ricevuto (session_id)
         * nelle SharedPreferences con il metodo saveSessionId(sessionId).
         */

        String url = getString(R.string.base_url) + "register.php";

        // prepare the request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        numberOfRequests--;
                        Log.d(TAG,"setSessionIdFromServer() - Response get. numberOfRequests: " + numberOfRequests);

                        try {
                            sessionId = response.getString("session_id");
                            saveSessionId(sessionId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "session_id (from server): " + sessionId);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "setSessionIdFromServer() Errore:"+error.getMessage());
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
        numberOfRequests++;
        Log.d(TAG,"setSessionIdFromServer() - Request added. numberOfRequests: "+numberOfRequests);
    }


    //================================================================================
    // Map
    //================================================================================
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        Log.d(TAG,"Map ready");
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.DARK, this);
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {
        // la mappa è pronta, si possono modificare le sue proprietà
        Log.d(TAG,"Style loaded");
        this.style = style;

        enableLocationComponent(); // Visualizza il pallino blu dell'utente

        this.symbolManager = new SymbolManager(mapView, mapboxMap, style);
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconIgnorePlacement(true);

        /*  Definisco onSymbolClickListener che verra' poi aggiunto al symbolManager nel metodo
            addSymbolClickListener() dopo che i simboli sono stati scaricati e mostrati sulla mappa */
        onSymbolClickListener = new OnSymbolClickListener() {
            @Override
            public void onAnnotationClick(Symbol symbol) {
                Log.d(TAG,"Simbolo ["+symbol.getIconImage()+"] toccato");   // getIconImage() ritorna l'id del simbolo

                for (int i =0; i<Model.getInstance().getMoncanSize(); i++){
                    MonsterCandy monsterCandy = Model.getInstance().getMoncan(i);

                    if(monsterCandy.getId() == symbol.getIconImage()){
                        LatLng userPosition = new LatLng(location.getLatitude(),location.getLongitude());

                        Intent intent = new Intent(getApplicationContext(), FightEat.class);
                        intent.putExtra("id", monsterCandy.getId());
                        intent.putExtra("isNear", monsterCandy.isNear(userPosition, FIGHT_EAT_DISTANCE));
                        intent.putExtra("distance", monsterCandy.distanceTo(userPosition));
                        startActivity(intent);
                    }
                }
            }
        };

        // Scarica i mostri/caramelle ogni tot secondi
        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Update monsters and candies...");
                getMonstersAndCandies(); // Volley Request
                handler.postDelayed(runnableCode, UPDATE_MONSTERS_AND_CANDIES_DELAY);
            }
        };
        handler.post(runnableCode);

    }

    private void enableLocationComponent() {
        /***
         * Serve per mostrare la posizione dell'utente sulla mappa
         */
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            Log.d(TAG,"Permessi già ottenuti");
            // Permessi già ottenuti

            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, this.style).useDefaultLocationEngine(false).build());
            locationComponent.setLocationComponentEnabled(true);    // Rende il pallino visibile
            locationComponent.setCameraMode(CameraMode.TRACKING);   // La camera si centra sul pallino
            locationComponent.setRenderMode(RenderMode.COMPASS);    // Forma del pallino

            initLocationEngine();   // Inizializza il location engine
        } else {
            Log.d(TAG,"Permessi non ottenuti");
            // Chiedo i permessi
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void initLocationEngine() {
        /**
         * Inizializza il LocationEngine e i suoi parametri per ottenere aggiornamenti sulla posizione del device
         * (es: ogni quanto aggiornare la posizione, accuratezza, ...)
         */
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
            Log.d(TAG,"Permessi appena ottenuti");
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
    private void getMonstersAndCandies() {              //  Fa la chiamata 'getMap' al server

        String url = getString(R.string.base_url) + "getmap.php";

        final JSONObject jsonBody = new JSONObject();

        // Preparo il body con il session_id
        try {
            jsonBody.put("session_id", sharedPreferences.getString(SESSION_ID_KEY, ""));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"getMonstersAndCandies() - JSON: problema");
        }
        Log.d(TAG,"getMonstersAndCandies() - JSON body: " + jsonBody.toString());

        // prepare the request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        numberOfRequests--;
                        Log.d(TAG, "getMonstersAndCandies - Response get: "+numberOfRequests);
                        Log.d(TAG, "getMonstersAndCandies() - Response: " + response.toString());

                        symbolManager.deleteAll();  //Pulisco i markers
                        Log.d(TAG,"Markers deleted.");

                        Log.d(TAG,"parseMonstersAndCandiesResponse(response): ");
                        parseMonstersAndCandiesResponse(response);  // carico i mostri/caramelle nel model

                        for (int i =0; i<Model.getInstance().getMoncanSize(); i++){
                            downloadImage(Model.getInstance().getMoncan(i));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "getMonstersAndCandies - Error: " + error.toString());
                    }
                }
        );
        queue.add(getRequest);  // aggiungo la richiesta alla coda
        numberOfRequests++;
        Log.d(TAG, "getMonstersAndCandies - Request added. numberOfRequests: "+numberOfRequests);
    }


    private void parseMonstersAndCandiesResponse(JSONObject response) { // Prende la risposta di getMonstersAndCandies() [getmap], e la mette nel model

        try {
            Model.getInstance().addMoncanFromJSONArray(this, response.getJSONArray("mapobjects"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void downloadImage(final MonsterCandy monsterCandy) {
        final String targetId = monsterCandy.getId();
        String url = getString(R.string.base_url) + "getimage.php";
        String sessionId = sharedPreferences.getString(SESSION_ID_KEY, "");

        // Preparo il body con il session_id
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("session_id", sessionId);
            jsonBody.put("target_id", targetId);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"downloadImage() - JSON: problema");
        }

        // prepare the request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        numberOfRequests--;
                        Log.d(TAG, "downloadImage - Response get. numberOfRequests: " + numberOfRequests);
                        Log.d(TAG, "downloadImage() - " + monsterCandy.getId());
                        String img_base64 = "";

                        try {
                            img_base64 = response.getString("img");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        byte[] decodedString = Base64.decode(img_base64, Base64.DEFAULT);
                        Bitmap img_bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        monsterCandy.setImg(img_bitmap);    // setto la proprietà img dell'oggetto monsterCandy con l'immagine appena scaricata

                        showMonsterCandyOnMap(monsterCandy);  // visualizzo l'oggetto monsterCandy sulla mappa

                        if(numberOfRequests==0) addSymbolClickListener();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "downloadImage - Error: " + error.toString());
                    }
                }
        );
        queue.add(getRequest);  // aggiungo la richiesta alla coda
        numberOfRequests++;
        Log.d(TAG, "downloadImage - Request added. numberOfRequests: "+numberOfRequests);
    }


    public void showMonsterCandyOnMap(MonsterCandy monsterCandy) {      // Visualizza l'oggetto monsterCandy sulla mappa

        Log.d(TAG,"showMonsterCandyOnMap("+monsterCandy.getId()+")");
        style.addImage(monsterCandy.getId(), monsterCandy.getImg());
        symbolManager.create(new SymbolOptions()
                .withLatLng(new LatLng(monsterCandy.getLat(), monsterCandy.getLon()))
                .withIconImage(monsterCandy.getId())
                .withIconSize(0.8f));
    }


    private void addSymbolClickListener() {
        /***
         * Aggiunge il symbolClickListener definito nell'onStyleLoaded()
         * a tutti i simboli sulla mappa.
         */
        symbolManager.removeClickListener(onSymbolClickListener);
        symbolManager.addClickListener(onSymbolClickListener);
        Log.d(TAG,"addSymbolClickListener() - Symbol click listener aggiunto.");
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
            Log.d(TAG,"LocationListeningCallback: Location changed.");
            mainActivity.location = result.getLastLocation();   // Aggiorno la variabile location della mainActivity con la posizione attuale
            Model.getInstance().setLocation(mainActivity.location);
            Log.d(TAG,"LocationListeningCallback: Model.location updated: "+Model.getInstance().getLocation().toString());
            if (mainActivity.mapboxMap != null && result.getLastLocation() != null) {
                mainActivity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            // The LocationEngineCallback interface's method which fires when the device's location can not be captured
            Log.d(TAG,"LocationListeningCallback: Location can not be captured.");
        }
    }

}

















