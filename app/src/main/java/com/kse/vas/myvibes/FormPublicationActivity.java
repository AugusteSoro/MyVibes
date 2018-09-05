package com.kse.vas.myvibes;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormPublicationActivity extends AppCompatActivity {

    EditText etTitre;
    EditText etResume;
    EditText etPublication;
    Button btPublication;
    JSONObject jsonObject;
    ProgressBar pbChargement;
    ImageView ivAvatar;
    public static final int RESULT_LOAD_IMAGE = 1;

    byte[] Byte;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_publication);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        etTitre = (EditText) findViewById(R.id.etTitre);
        etResume = (EditText) findViewById(R.id.etResume);
        etPublication = (EditText) findViewById(R.id.etPublication);
        btPublication = (Button) findViewById(R.id.btPublication);
        pbChargement = (ProgressBar)findViewById(R.id.pbChargement);

        btPublication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new EnvoiPublication().execute();

            }

        });

        ivAvatar = (ImageView)findViewById(R.id.ivAvatar);

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeryIntent =  new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(galeryIntent, RESULT_LOAD_IMAGE);
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Log.i("SelectedImage", selectedImage.toString());

            ivAvatar.setImageURI(selectedImage);


            //Convertir URI en Byte
            InputStream iStream = null;
            try {
                iStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Byte = getBytes(iStream);

            } catch (IOException e) {
                e.printStackTrace();
            }

            setImageViewWithByteArray(ivAvatar,Byte);

        }
    }

    //Afficher une image à partir d'un tableau de byte
    public static void setImageViewWithByteArray(ImageView view, byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        view.setImageBitmap(bitmap);
    }

    //Conversion URI en Byte
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    //Insertion de donnees
    private class EnvoiPublication extends AsyncTask<Void, String, Publication[]> {


        @Override
        protected Publication[] doInBackground(Void... voids) {


            int id = 556;
            String titre = etTitre.getText().toString();
            String resume = etResume.getText().toString();
            String publi = etPublication.getText().toString();

            Log.i("Employé", "empid "+id+"empage "+titre+"empname "+resume+"empsalary "+publi);

            jsonObject = new JSONObject();

            try {
                jsonObject.put("empid",id);
                jsonObject.put("empname",titre);
                jsonObject.put("empage",new Integer(25));
                jsonObject.put("empsalary",new Double(25000.0));
                jsonObject.put("photo",Byte);


                Log.i("objetJson", String.valueOf(jsonObject));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String valeur = null;
            publishProgress(valeur);


            //URL
            String API = "http://192.168.1.17:8080/emprest/ajouter";

            //String json = "{\"empid\":896,\"empage\":25,\"empname\":\"android\",\"empsalary\":125000.0}";
            String json = String.valueOf(jsonObject);



            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(API)
                    .post(body)
                    .build();

            Publication publication = null;

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponsePublication", response.toString());
                int messageReponse = response.code();
                if(messageReponse == 404){
                    valeur = "erreur";
                    publishProgress(valeur);
                }
                else if (messageReponse == 200){
                    valeur = "reussite";
                    publishProgress(valeur);

                }


            } catch (IOException e) {
                e.printStackTrace();
            }


            return new Publication[0];
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pbChargement.setVisibility(View.VISIBLE);
            if(values[0] == "erreur"){
                Toast.makeText(FormPublicationActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();
            }
            else if (values[0] == "reussite"){
                Toast.makeText(FormPublicationActivity.this, R.string.enr_reussi, Toast.LENGTH_SHORT).show();
                reset();
            }

        }

        @Override
        protected void onPostExecute(Publication[] publications) {
            super.onPostExecute(publications);
            pbChargement.setVisibility(View.INVISIBLE);

        }

        private void reset() {
            etTitre.setText("");
            etResume.setText("");
            etPublication.setText("");
            finish();
        }


    }




}