package com.kse.vas.myvibes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageActivity extends AppCompatActivity {

    ImageView ivAvatar1;
    byte[] Byte;
    String ipOffline = ConfigApp.ipOffline;
    String ipOnline = ConfigApp.ipOnline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ivAvatar1 = findViewById(R.id.ivAvatar1);

        new RequestPublication().execute();

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

    public static void setImageViewWithByteArray(ImageView view, byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        view.setImageBitmap(bitmap);
    }




    //Afficher les données
    private class RequestPublication extends AsyncTask<Void, String, String[]>
    {


        //ProgressBar progressBar = new ProgressBar(AccueilActivity.this);

        @Override
        protected String[] doInBackground(Void... voids) {

            //URL
            String API = ipOffline+"/clientrest/listallclient";
            //String API = "http://192.168.43.186:8080/emprest/listallemp";
            //String API = "http://192.168.8.101:8080/emprest/listallemp";

            /*Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("http")
                    .appendPath("localhost:8080")
                    .appendPath("emprest")
                    .appendPath("listallemp")
                    .build();

            URL API = null;

            try {
                API = new URL(uriBuilder.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Log.i("Uribuilder", uriBuilder.toString()); */

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API)
                    .build();

            String publication = null;
            String reponse = null;

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponsePublication", response.toString());
                //publishProgress(valeur);
                reponse = response.body().string();
                publishProgress(reponse);



                //publishProgress(publication.toString());

                //TextView tvTest = (TextView)findViewById(R.id.tvTest);

                //tvTest.setText(publication.getEmpname());

            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONArray reponseBody = null;
            String[] name = null;
            try {
                if (reponse != null){
                    reponseBody = new JSONArray(reponse);
                    name = new String[reponseBody.length()];
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




            return name;
            //return new String[0];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            String valeur = values[0];
            try {

                JSONArray reponseBody = new JSONArray(valeur);
                final String[] clientphoto1 = new String[reponseBody.length()];

                int i;
                for(i=0; i < reponseBody.length(); i++) {
                    JSONObject elementi = reponseBody.getJSONObject(i);
                    String clientphoto = elementi.getString("clientphoto");
                    clientphoto1[i] = clientphoto;
                    //Toast.makeText(AccueilActivity.this, "Chargement en cours", Toast.LENGTH_SHORT).show();

                    byte[] photobyte = clientphoto.getBytes();

                    setImageViewWithByteArray(ivAvatar1,photobyte);


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings != null){
                if (strings.length > 1)
                    Toast.makeText(ImageActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(ImageActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(ImageActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();




        }

    }


}
