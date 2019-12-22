package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import androidx.annotation.NonNull;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, Style.OnStyleLoaded {

    private String sessionId;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Handler myHandler = new Handler();

    double lt = 51.50550;
    double ln = -0.07520;

    public static final String SHARED_PREFS_NAME = "sharedPrefs";   // Nome delle SharedPreferences
    public static final String SESSION_ID_KEY = "sessionId";        // Chiave del session_id

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0; // serve per identificare i permessi in caso volessi gestirli

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoibWF0dGVvYmV0dG8iLCJhIjoiY2szNGF1OGgwMDBhNjNucWRzY29oaTU3OCJ9.G066wR9mYwJUPmWcD_vrwQ");
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "OnCreate");
        setSessionId();
        // ATTENZIONE LA CHIAMATA DI RETE È ASINCRONA. Ci dobbiamo assicurare che sia stato già settato il session_id.
        Log.d("MainActivity", "session_id settato -> " + this.sessionId);
        // TODO: settare uno username nel caso di un nuovo utente.

        checkGeoPermission();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        ciclo();



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

        RequestQueue queue = Volley.newRequestQueue(this);
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
                        Log.d("MainActivity", "Errore");
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
    // GeoPermission
    //================================================================================
    public void checkGeoPermission(){
        /**
         * @author Matteo Betto
         * controlla se l'app ha i permessi per la geolocalizzazione
         * se non li ha chiama la funzione getGeoPermission() per ottenerli
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Geolocalizzazione","Non ho i permessi per la geolocalizzazione");
            // getGeoPermission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }else{
            Log.d("Geolocalizzazione","Ho i permessi per la geolocalizzazione");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /**
         * @author Matteo Betto
         * override del metodo per gestire il caso in cui vengano o non vengano forniti i permessi
         */
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.d("Geolocalizzazione", "Ora ho i permessi per la Geolocalizzazione");
                } else {
                    Log.d("Geolocalizzazione", "Non ho ancora ottenuti i permessi per la Geolocalizzazione");
                    checkGeoPermission();
                }
                return;
            }
        }
    }

    //================================================================================
    // Map
    //================================================================================
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.DARK, this);
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {
        // la mappa è pronta, si possono modificare le sue proprietà
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(lt, ln))
                .zoom(18)
                .tilt(20)
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);

    }

    //================================================================================
    // Ciclo
    //================================================================================
    private Runnable rCamera = new Runnable() {
        @Override
        public void run() {
            aggiornaCamera(lt=lt+0.0001,ln=ln+0.0001);
            ciclo();
        }
    };

    public void ciclo(){
        myHandler.postDelayed(rCamera,1000);
    }

    public void aggiornaCamera(double lat, double lon){

        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(lat, lon))
                .zoom(18)
                .tilt(20)
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);

    }

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

}

















