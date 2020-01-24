package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ModificaProfilo extends AppCompatActivity {

    public static final String TAG = " Debug - ModificaProfilo ";
    public static final String SHARED_PREFS_NAME = "sharedPrefs";   // Nome delle SharedPreferences
    public static final String SESSION_ID_KEY = "sessionId";        // Chiave del session_id
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    String immagine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * @author Betto
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_profilo);


        //================================================================================
        // Intent
        //================================================================================
        Button buttonImmagine = (Button) findViewById(R.id.button_cambia_immagine);
        Button buttonConfermaModifiche = (Button) findViewById(R.id.button_conferma_modifiche);
        Button buttonAnnullaModifiche = (Button) findViewById(R.id.button_annulla_modifiche);

        buttonImmagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * @author Betto
                 * apre la galleria
                 */
                openGallery();
            }
        });

        buttonConfermaModifiche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * @author Betto
                 */
                TextView nuovoUsernameTextView = (TextView)findViewById(R.id.textView_nuovo_username);
                String newUsername = nuovoUsernameTextView.getText().toString();

                if (newUsername.length() > 15){
                    Log.d(TAG, " Bottone conferma modifiche > 15 --> " + newUsername);
                    Toast.makeText(getApplicationContext(),"Username oltre 15 lettere",Toast.LENGTH_SHORT).show();
                }else{
                    if(newUsername.isEmpty() && immagine == null){
                        Toast.makeText(getApplicationContext(),"Nessuna modifica",Toast.LENGTH_SHORT).show();
                    }else{

                        final JSONObject jsonBody = new JSONObject();
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

                        try {
                            jsonBody.put("session_id", sharedPreferences.getString(SESSION_ID_KEY, ""));
                            if (!newUsername.isEmpty()) {
                                jsonBody.put("username", newUsername);
                            }
                            if (immagine != null){
                                jsonBody.put("img", immagine);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG,"problema");
                        }
                        Log.d(TAG,"jsonbody: " + jsonBody.toString());


                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url = getString(R.string.base_url) + "setprofile.php";

                        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Intent intent = new Intent(getApplicationContext(), Profilo.class);
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(),"Dati aggiornati correttamente",Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "buttonConfermaModifiche --> onResponse");

                                    }
                                },
                                new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(),"Dati non aggiornati",Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "buttonConfermaModifiche --> onErrorResponse");
                                    }
                                }
                        );
                        queue.add(getRequest);

                    }
                }
            }
        });

        buttonAnnullaModifiche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * @author Betto
                 * ritorna alla pagina profilo
                 */
                Log.d(TAG, " buttonAnnullaModifiche ");
                Intent intent = new Intent(getApplicationContext(), Profilo.class);
                startActivity(intent);
            }
        });

    }


    //================================================================================
    // Ciclo di vita - metodi
    //================================================================================
    private void openGallery(){
        /**
         * @author Betto
         * Intent che fa accedere alla galleria
         */
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        /**
         * @author Betto
         * override del metodo chiamato quando viene selezionata un'immagine dalla galleria
         */
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){

            imageUri = data.getData();
            Log.d(TAG," imageUri --> " + imageUri.toString());
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            /*
            Cursor returnCursor = getContentResolver().query(imageUri, null, null, null, null);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            Log.d(TAG,"Dimesione immagine selezionata" + Long.toString(returnCursor.getLong(sizeIndex)));

            if (returnCursor.getLong(sizeIndex)< 99000) {
            */
            if(encodeImage(bitmap).length()<137000){
                ImageView imageView = (ImageView) findViewById(R.id.imageView_nuova_immagine);
                imageView.setImageBitmap(bitmap);   //da uri --> imageView.setImageURI(imageUri);
                immagine = encodeImage(bitmap);
            }else{
                Toast.makeText(getApplicationContext(),"Immagine troppo pesante",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeImage(Bitmap bm){
        /**
         * @author Betto
         * Prende come input una Bitmap e ritorna la stringa in Base64 corrispondente
         */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }
}










