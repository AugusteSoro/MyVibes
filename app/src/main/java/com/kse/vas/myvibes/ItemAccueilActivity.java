package com.kse.vas.myvibes;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ItemAccueilActivity extends AppCompatActivity {


    TextView tvNomService;
    TextView tvInfo;
    LinearLayout linearLayoutMessage;
    ImageButton ibVote;
    RatingBar rbVote;
    GridView gridView1;
    JSONObject jsonObject;
    String ipOffline = ConfigApp.ipOffline;
    String ipOnline = ConfigApp.ipOnline;
    int serviceId;
    String DATA;
    File file;
    File directory;
    public final static String APP_PATH_SD_CARD = "/storage/myvibes/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "images";
    private static final String[] STORAGE_PERMISSIONS = { Manifest.permission.WRITE_EXTERNAL_STORAGE};

    CustomAdapterMessage customAdapterMessage;
    List<Publication> publicationArrayList = null;



    //SharedPreferences
    String PREFS = "ALERTE";
    Gson gson = new Gson();
    String PUBLICATION_USER = "PLUBLICATION_USER";
    String PREFS_LAST_ID = "PREFS_LAST_ID";
    SharedPreferences sharedPreferences;



    // Create a handler which can run code periodically
    final int POLL_INTERVAL = 20000; // milliseconds
    final Handler myHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            String num = ObtainNum();
            new RequestServiceSouscrit().execute(num);
            //new VerificationPublication().execute();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    public void StopRunnable(){
        myHandler.removeCallbacks(mRefreshMessagesRunnable);
        Log.i("Stop alerte", "Arrêt des recherches");

    }

    public String ObtainNum()
    {
        sharedPreferences = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        String num = sharedPreferences.getString("PREFS_NUM", null);
        return num;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        //EmojiCompat.init(config);

        setContentView(R.layout.activity_item_accueil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Enregistrer des objets
        DATA = "listPublication.txt";
        directory = getApplicationContext().getFilesDir();
        file = new File(directory, DATA);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //tvNom = (TextView)findViewById(R.id.tvNom);
        //btn_send = (ImageButton)findViewById(R.id.btn_send);
        //messageRecu = (TextView) findViewById(R.id.messageRecu);
        //message = (EditText)findViewById(R.id.message);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvNomService = (TextView)findViewById(R.id.tvNomService);
        linearLayoutMessage = (LinearLayout) findViewById(R.id.LinearLayoutMessage);
        rbVote = (RatingBar)findViewById(R.id.rbVote);
        ibVote = (ImageButton)findViewById(R.id.ibVote);
        gridView1 = (GridView) findViewById(R.id.gvPublication);


        publicationArrayList = new ArrayList<>();




        Intent intent = getIntent();
        String nom = intent.getStringExtra("nom");
        serviceId = intent.getIntExtra("idService", 0);

        int clientIdOnline = intent.getIntExtra("clientIdOnline",0);
        Log.i("clientIdOnline", String.valueOf(clientIdOnline));



        //tvNom.setText(nom);

        //Modifier le nom de la Toolbar
        getSupportActionBar().setTitle(nom);

        //new ReceptionNotification().execute();

        //new ReceptionPublication().execute();
        //new VerificationPublication().execute();




    }



    @Override
    protected void onResume() {
        super.onResume();

        verifyPermissions();

        //Chercher les alertes chaque 2min
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);

        Intent intent = getIntent();
        String nomService = intent.getStringExtra("nom");

        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        String json;
        List<Publication> publications = new ArrayList<>();
        if (sharedPreferences.contains(PUBLICATION_USER)) {
            json = sharedPreferences.getString(PUBLICATION_USER, null);
            JSONObject objetMessage;
            try {
                JSONArray tableau = new JSONArray(json);
                if (tableau.length()>0){
                    tvInfo.setVisibility(View.GONE);

                }
                for (int j = 0; j < tableau.length(); j++) {
                    objetMessage = tableau.getJSONObject(j);
                    String publicationContenu1 = objetMessage.getString("alertemessage");
                    String publicationDateCreation1 = objetMessage.getString("alertedatecreation");
                    Collections.reverse(publications);
                    publications.add(new Publication(publicationContenu1, publicationDateCreation1));

                }
                Collections.reverse(publications);
                customAdapterMessage = new CustomAdapterMessage(getApplicationContext(), nomService, publications);
                gridView1.setAdapter(customAdapterMessage);
                customAdapterMessage.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }



    private class ReceptionNotification extends AsyncTask<Void, String, String[]>
    {


        //ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar1);


        //fonction Okhttp get
        public String run(String API){

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API)
                    .build();

            String publication = null;
            String reponse = null;

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponseMessage", response.toString());
                //publishProgress(valeur);
                reponse = response.body().string();
                publishProgress(reponse);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return reponse;
        }

        @Override
        protected String[] doInBackground(Void... voids) {

            String API = ipOnline+"/notificationrest/listallnotif/"+ serviceId;

            String reponse = run(API);




            JSONArray reponseBody = null;
            String[] message = null;
            try {
                if (reponse != null){
                    reponseBody = new JSONArray(reponse);
                    message = new String[reponseBody.length()];
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




            return message;
            //return new String[0];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            gridView1 = (GridView) findViewById(R.id.gvPublication);
            int publicationid;
            //Appel des informations de la ListView

            String valeur = values[0];
            try {

                JSONArray reponseBody = new JSONArray(valeur);
                final boolean[] tableauNotifLike = new boolean[reponseBody.length()];


                int i;
                for(i=0; i < reponseBody.length(); i++) {
                    JSONObject elementi = reponseBody.getJSONObject(i);

                    boolean notificationlike = elementi.getBoolean("notificationlike");

                    tableauNotifLike[i] = notificationlike;
                    Log.i("tableauNotifLike", String.valueOf(tableauNotifLike));

                }

                new ReceptionPublication().execute();


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

        }

    }

    //Tache Asynchrone de recuperation de message
    //Reception de données
    private class ReceptionPublication1 extends AsyncTask<Void, String, String[]>
    {


        //ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar1);


        //fonction Okhttp get
        public String run(String API){

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API)
                    .build();

            String publication = null;
            String reponse = null;

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponseMessage", response.toString());
                //publishProgress(valeur);
                reponse = response.body().string();
                publishProgress(reponse);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return reponse;
        }

        @Override
        protected String[] doInBackground(Void... voids) {

            String API = ipOnline+"/publicationrest/listallpubli/"+ serviceId;

            String reponse = run(API);




            JSONArray reponseBody = null;
            String[] message = null;
            try {
                if (reponse != null){
                    reponseBody = new JSONArray(reponse);
                    message = new String[reponseBody.length()];
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




            return message;
            //return new String[0];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            gridView1 = (GridView) findViewById(R.id.gvPublication);
            int publicationid;
            //Appel des informations de la ListView
            tvInfo.setVisibility(View.INVISIBLE);

            String valeur = values[0];
            try {

                JSONArray reponseBody = new JSONArray(valeur);
                final int[] tableauId = new int[reponseBody.length()];
                final String[] tableauMessage = new String[reponseBody.length()];
                final String[] tableauDate = new String[reponseBody.length()];
                final int[] tableauNbreVue = new int[reponseBody.length()];
                final String[] tableauImage = new String[reponseBody.length()];
                Bitmap[] bitmaps = null;
                ArrayList<Publication> publication = new ArrayList<>();

                String nomService = null;
                Intent intent = null;
                //Recuperer le nom du service
                        intent = getIntent();
                nomService = intent.getStringExtra("nom");
                //Recuperer l'ID du client
                //Intent intent2 = getIntent();
                //intent.putExtra("clientIdOnline",clientIdOnline);
                int clientIdOnline = intent.getIntExtra("clientIdOnline",0);


                int i;
                for(i=0; i < reponseBody.length(); i++) {
                    JSONObject elementi = reponseBody.getJSONObject(i);

                    publicationid = elementi.getInt("publicationid");
                    String publicationContenu = elementi.get("publicationcontenu").toString();
                    String publicationDateCreation = elementi.get("publicationdatecreation").toString();
                    int publicationNbreVue = elementi.getInt("publicationnbrevue");
                    //String publicationImage = elementi.get("publicationimage").toString();

                    tableauId[i] = publicationid;
                    tableauMessage[i] = publicationContenu;
                    //tableauDate[i] = dateToday;
                    tableauDate[i] = publicationDateCreation;
                    tableauNbreVue[i] = publicationNbreVue;
                    //tableauImage[i] = publicationImage;


                    publicationArrayList.add(new Publication(publicationContenu,publicationDateCreation));


                    sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                    String json;
                    List<Publication> publications = new List<Publication>() {
                        @Override
                        public int size() {
                            return 0;
                        }

                        @Override
                        public boolean isEmpty() {
                            return false;
                        }

                        @Override
                        public boolean contains(Object o) {
                            return false;
                        }

                        @NonNull
                        @Override
                        public Iterator<Publication> iterator() {
                            return null;
                        }

                        @NonNull
                        @Override
                        public Object[] toArray() {
                            return new Object[0];
                        }

                        @NonNull
                        @Override
                        public <T> T[] toArray(@NonNull T[] a) {
                            return null;
                        }

                        @Override
                        public boolean add(Publication publication) {
                            return false;
                        }

                        @Override
                        public boolean remove(Object o) {
                            return false;
                        }

                        @Override
                        public boolean containsAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public boolean addAll(@NonNull Collection<? extends Publication> c) {
                            return false;
                        }

                        @Override
                        public boolean addAll(int index, @NonNull Collection<? extends Publication> c) {
                            return false;
                        }

                        @Override
                        public boolean removeAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public boolean retainAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public void clear() {

                        }

                        @Override
                        public Publication get(int index) {
                            return null;
                        }

                        @Override
                        public Publication set(int index, Publication element) {
                            return null;
                        }

                        @Override
                        public void add(int index, Publication element) {

                        }

                        @Override
                        public Publication remove(int index) {
                            return null;
                        }

                        @Override
                        public int indexOf(Object o) {
                            return 0;
                        }

                        @Override
                        public int lastIndexOf(Object o) {
                            return 0;
                        }

                        @NonNull
                        @Override
                        public ListIterator<Publication> listIterator() {
                            return null;
                        }

                        @NonNull
                        @Override
                        public ListIterator<Publication> listIterator(int index) {
                            return null;
                        }

                        @NonNull
                        @Override
                        public List<Publication> subList(int fromIndex, int toIndex) {
                            return null;
                        }
                    };
                    if (sharedPreferences.contains(PUBLICATION_USER)) {
                        json = sharedPreferences.getString(PUBLICATION_USER, null);
                        JSONObject objetMessage;
                        try {
                            JSONArray tableau = new JSONArray(json);
                            for (int j=0; j<tableau.length(); j++){
                                objetMessage = tableau.getJSONObject(j);
                                String publicationContenu1 = objetMessage.getString("publicationContenu");
                                String publicationDateCreation1 = objetMessage.getString("publicationDateCreation");
                                publications.add(new Publication(publicationContenu1,publicationDateCreation1));


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //Creer l'objet message
                        JSONObject objetMessage1 = new JSONObject();
                        JSONArray tableau = null;
                        try {
                            tableau = new JSONArray(json);
                            objetMessage1.put("publicationContenu",publicationContenu);
                            objetMessage1.put("publicationDateCreation",publicationDateCreation);

                            tableau.put(objetMessage1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Remplir shared preferences
                        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                        //si aucun utilisateur n'est sauvegardé, on ajouter
                        sharedPreferences
                                .edit()
                                //.putString(COMMENT_USER, gson.toJson(messageArrayList))
                                .putString(PUBLICATION_USER, String.valueOf(tableau))
                                .apply();
                        //Log.i("GSON", gson.toJson(messageArrayList));
                        customAdapterMessage = new CustomAdapterMessage(ItemAccueilActivity.this,nomService,publications);
                        gridView1.setAdapter(customAdapterMessage);
                        customAdapterMessage.notifyDataSetChanged();
                        //Afficher la derniere ligne
                        gridView1.setSelection(gridView1.getCount());










                    }else {
                        //Creer l'objet message
                        JSONObject objetMessage = new JSONObject();
                        JSONArray tableau = null;

                        try {
                            tableau = new JSONArray();
                            objetMessage.put("publicationContenu",publicationContenu);
                            objetMessage.put("publicationDateCreation",publicationDateCreation);

                            tableau.put(objetMessage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Remplir shared preferences
                        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                        //si aucun utilisateur n'est sauvegardé, on ajouter
                        sharedPreferences
                                .edit()
                                .putString(PUBLICATION_USER, String.valueOf(tableau))
                                .apply();

                        if (customAdapterMessage == null){
                            customAdapterMessage = new CustomAdapterMessage(getApplicationContext(),nomService,publicationArrayList);
                            gridView1.setAdapter(customAdapterMessage);
                            customAdapterMessage.notifyDataSetChanged();

                        }else {
                            customAdapterMessage.add(new Publication(publicationContenu,publicationDateCreation));
                            customAdapterMessage.notifyDataSetChanged();

                        }

                    }













                    //final CustomAdapterMessage customAdapterMessage = new CustomAdapterMessage(ItemAccueilActivity.this, intent, tableauId,tableauMessage,tableauDate,tableauNbreVue,nomService,clientIdOnline,tableauImage);
                    //gridView1.setAdapter(customAdapterMessage);
                    //Afficher la derniere ligne
                    //gridView1.setSelection(gridView1.getCount());
                    //TODO : Ameliorer affichage de vote
                    // TODO : Rendre affichage des publications images dynamique

                    //String[] tableauImage = {String.valueOf(R.drawable.imgtest),String.valueOf(R.drawable.blatte), "http://192.168.1.10:80/soutenance/auto.webm",String.valueOf(R.drawable.imgtest2)};
                    /*String[] tableauImage = {String.valueOf(R.drawable.imgtest),"http://192.168.1.9:80/soutenance/auto.webm",null,"http://192.168.1.9:80/soutenance/auto.webm"};
                    //String[] tableauVideo = {"http://192.168.1.14:80/soutenance/auto.webm","http://192.168.1.14:80/soutenance/auto.webm"};
                    String[] tableauAudio = {"http://192.168.1.14:80/soutenance/GodsPlan.MP3","http://192.168.1.14:80/soutenance/GodsPlan.MP3"};

                    final CustomAdapterMessage customAdapterMessage = new CustomAdapterMessage(ItemAccueilActivity.this, intent, tableauId,tableauMessage,tableauDate,tableauNbreVue,nomService,clientIdOnline,tableauImage);
                    gridView1.setAdapter(customAdapterMessage);
                    //Afficher la derniere ligne
                    //gridView1.setSelection(gridView1.getCount());*/
                    ;
                    //Publication publication1 = new Publication();







                }

                /*
                publication1.setPublicationImage(saveImageToExternalStorage(bitmaps));
                publication1.setPublicationID(tableauId);
                publication1.setPublicationContenu(tableauMessage);
                publication1.setPublicationDate(tableauDate);
                publication1.setPublicationNbreVue(tableauNbreVue);
                publication.add(publication1);

                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file,false));
                    objectOutputStream.writeObject(publication);
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Recuperation des objets
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                    List<Publication> objetSer = new List<Publication>() {
                        @Override
                        public int size() {
                            return 0;
                        }

                        @Override
                        public boolean isEmpty() {
                            return false;
                        }

                        @Override
                        public boolean contains(Object o) {
                            return false;
                        }

                        @NonNull
                        @Override
                        public Iterator<Publication> iterator() {
                            return null;
                        }

                        @NonNull
                        @Override
                        public Object[] toArray() {
                            return new Object[0];
                        }

                        @NonNull
                        @Override
                        public <T> T[] toArray(@NonNull T[] a) {
                            return null;
                        }

                        @Override
                        public boolean add(Publication publication) {
                            return false;
                        }

                        @Override
                        public boolean remove(Object o) {
                            return false;
                        }

                        @Override
                        public boolean containsAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public boolean addAll(@NonNull Collection<? extends Publication> c) {
                            return false;
                        }

                        @Override
                        public boolean addAll(int index, @NonNull Collection<? extends Publication> c) {
                            return false;
                        }

                        @Override
                        public boolean removeAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public boolean retainAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public void clear() {

                        }

                        @Override
                        public Publication get(int index) {
                            return null;
                        }

                        @Override
                        public Publication set(int index, Publication element) {
                            return null;
                        }

                        @Override
                        public void add(int index, Publication element) {

                        }

                        @Override
                        public Publication remove(int index) {
                            return null;
                        }

                        @Override
                        public int indexOf(Object o) {
                            return 0;
                        }

                        @Override
                        public int lastIndexOf(Object o) {
                            return 0;
                        }

                        @NonNull
                        @Override
                        public ListIterator<Publication> listIterator() {
                            return null;
                        }

                        @NonNull
                        @Override
                        public ListIterator<Publication> listIterator(int index) {
                            return null;
                        }

                        @NonNull
                        @Override
                        public List<Publication> subList(int fromIndex, int toIndex) {
                            return null;
                        }
                    };
                    objetSer = (List<Publication>) objectInputStream.readObject();

                    int[] tableauPublicationId1 = new int[objetSer.size()] ;
                    String[] tableauPublicationContenu1 = new String[objetSer.size()] ;
                    String[] tableauDate1 = new String[objetSer.size()] ;
                    int[] tableauNbreVue1 = new int[objetSer.size()] ;
                    String[] tableauImage1 = new String[objetSer.size()] ;

                    for (int j = 0; j < objetSer.size(); j++)
                    {
                        tableauPublicationId1  = objetSer.get(j).getPublicationID();
                        tableauPublicationContenu1  = objetSer.get(j).getPublicationContenu();
                        tableauDate1  = objetSer.get(j).getPublicationDate();
                        tableauNbreVue1  = objetSer.get(j).getPublicationNbreVue();
                        tableauImage1  = objetSer.get(j).getPublicationImage();
                        Log.i("Tableau message", tableauPublicationContenu1.toString());
                        Log.i("Tableau Date", String.valueOf(tableauDate1));
                        String[] tableauImage2 = {String.valueOf(R.drawable.imgtest),"http://192.168.43.186/soutenance/auto.webm",null,"http://192.168.43.186/soutenance/auto.webm"};

                        final CustomAdapterMessage customAdapterMessage = new CustomAdapterMessage(ItemAccueilActivity.this, intent, objetSer.get(j).getPublicationID(),objetSer.get(j).getPublicationContenu(),objetSer.get(j).getPublicationDate(),objetSer.get(j).getPublicationNbreVue(),nomService,clientIdOnline,objetSer.get(j).getPublicationImage());
                        gridView1.setAdapter(customAdapterMessage);

                    }


                    objectInputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                */


                gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    Intent intent = new Intent(ItemAccueilActivity.this,CommentaireActivity.class);

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //Recuperer les informations de la ligne selectionnée
                        String message = tableauMessage[position].toString();
                        int idPublication = tableauId[position];

                        Intent intent2 = getIntent();
                        int clientIdOnline = intent2.getIntExtra("clientIdOnline",0);
                        intent.putExtra("clientIdOnline",clientIdOnline);
                        startActivity(intent);
                        Toast.makeText(ItemAccueilActivity.this, "Test du click", Toast.LENGTH_SHORT).show();

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings != null){
                if (strings.length > 0) {
                    Toast.makeText(ItemAccueilActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                    tvInfo.setVisibility(View.INVISIBLE);
                }
                else {
                    Toast.makeText(ItemAccueilActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();
                    tvInfo.setVisibility(View.VISIBLE);

                }

            }
            else {
                Toast.makeText(ItemAccueilActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();
                //tvInfo.setVisibility(View.VISIBLE);
            }

            //progressBar.setVisibility(View.INVISIBLE);


        }


    }

    private class ReceptionPublication extends AsyncTask<Integer, String, String[]>
    {


        //ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar1);


        //fonction Okhttp get
        public String run(String API){

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API)
                    .build();

            String publication = null;
            String reponse = null;

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponseMessage", response.toString());
                //publishProgress(valeur);
                reponse = response.body().string();
                publishProgress(reponse);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return reponse;
        }

        @Override
        protected String[] doInBackground(Integer... params) {

            int alerteid = params[0];
            String API = ipOnline+"/alerterest/listallalerte/"+ serviceId+"/"+alerteid;

            String reponse = run(API);




            JSONArray reponseBody = null;
            String[] message = null;
            try {
                if (reponse != null){
                    reponseBody = new JSONArray(reponse);
                    message = new String[reponseBody.length()];
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




            return message;
            //return new String[0];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            gridView1 = (GridView) findViewById(R.id.gvPublication);
            int publicationid;
            //Appel des informations de la ListView
            tvInfo.setVisibility(View.INVISIBLE);

            String valeur = values[0];

            try {

                JSONArray reponseBody = new JSONArray(valeur);
                final int[] tableauId = new int[reponseBody.length()];
                final String[] tableauMessage = new String[reponseBody.length()];
                final String[] tableauDate = new String[reponseBody.length()];
                final int[] tableauNbreVue = new int[reponseBody.length()];
                final String[] tableauImage = new String[reponseBody.length()];
                Bitmap[] bitmaps = null;
                ArrayList<Publication> publication = new ArrayList<>();

                String nomService = null;
                Intent intent = null;
                //Recuperer le nom du service
                intent = getIntent();
                nomService = intent.getStringExtra("nom");
                //Recuperer l'ID du client
                //Intent intent2 = getIntent();
                //intent.putExtra("clientIdOnline",clientIdOnline);
                int clientIdOnline = intent.getIntExtra("clientIdOnline",0);


                int i;


                //Prendre les 3 dernieres publications
                for(i=0; i < tableauId.length; i++) {
                    JSONObject elementi = reponseBody.getJSONObject(i);

                    publicationid = elementi.getInt("alerteid");
                    String publicationContenu = elementi.get("alertemessage").toString();
                    String publicationDateCreation = elementi.get("alertedatecreation").toString();
                    //int publicationNbreVue = elementi.getInt("publicationnbrevue");
                    //String publicationImage = elementi.get("publicationimage").toString();


                    tableauId[i] = publicationid;


                    tableauMessage[i] = publicationContenu;
                    //tableauDate[i] = dateToday;
                    tableauDate[i] = publicationDateCreation;
                    //tableauNbreVue[i] = publicationNbreVue;
                    //tableauImage[i] = publicationImage;


                    publicationArrayList.add(new Publication(publicationContenu,publicationDateCreation));



                    sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                    String json;
                    List<Publication> publications = new ArrayList<>();
                    if (sharedPreferences.contains(PUBLICATION_USER)) {
                        json = sharedPreferences.getString(PUBLICATION_USER, null);
                        JSONObject objetMessage;
                        try {
                            JSONArray tableau = new JSONArray(json);
                            for (int j=0; j<tableau.length(); j++){
                                objetMessage = tableau.getJSONObject(j);
                                String publicationContenu1 = objetMessage.getString("alertemessage");
                                String publicationDateCreation1 = objetMessage.getString("alertedatecreation");
                                publications.add(new Publication(publicationContenu1,publicationDateCreation1));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //Creer l'objet message
                        JSONObject objetMessage1 = new JSONObject();
                        JSONArray tableau = null;
                        try {
                            tableau = new JSONArray(json);
                            objetMessage1.put("idmessage",publicationid);
                            objetMessage1.put("alertemessage",publicationContenu);
                            objetMessage1.put("alertedatecreation",publicationDateCreation);

                            publications.add(new Publication(publicationContenu,publicationDateCreation));


                            tableau.put(objetMessage1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Remplir shared preferences
                        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                        //si aucun utilisateur n'est sauvegardé, on ajouter
                        sharedPreferences
                                .edit()
                                //.putString(COMMENT_USER, gson.toJson(messageArrayList))
                                .putString(PUBLICATION_USER, String.valueOf(tableau))
                                .apply();
                        //Log.i("GSON", gson.toJson(messageArrayList));

                        Collections.reverse(publications);
                        customAdapterMessage = new CustomAdapterMessage(ItemAccueilActivity.this,nomService,publications);
                        gridView1.setAdapter(customAdapterMessage);
                        customAdapterMessage.notifyDataSetChanged();

                        //Envoyer une notification
                        notification(publicationContenu,nomService);

                        //Afficher la derniere ligne
                        //gridView1.setSelection(gridView1.getCount());










                    }else {
                        //Creer l'objet message
                        JSONObject objetMessage = new JSONObject();
                        JSONArray tableau = null;

                        try {
                            tableau = new JSONArray();
                            objetMessage.put("idmessage",publicationid);
                            objetMessage.put("alertemessage",publicationContenu);
                            objetMessage.put("alertedatecreation",publicationDateCreation);

                            tableau.put(objetMessage);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Remplir shared preferences
                        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                        //si aucun utilisateur n'est sauvegardé, on ajouter
                        sharedPreferences
                                .edit()
                                .putString(PUBLICATION_USER, String.valueOf(tableau))
                                .apply();

                        if (customAdapterMessage == null){
                            Collections.reverse(publications);
                            customAdapterMessage = new CustomAdapterMessage(getApplicationContext(),nomService,publicationArrayList);
                            gridView1.setAdapter(customAdapterMessage);
                            customAdapterMessage.notifyDataSetChanged();

                        }else {
                            customAdapterMessage.add(new Publication(publicationContenu,publicationDateCreation));
                            customAdapterMessage.notifyDataSetChanged();

                        }

                        //Envoi de notification
                        notification(publicationContenu,nomService);

                    }













                    //final CustomAdapterMessage customAdapterMessage = new CustomAdapterMessage(ItemAccueilActivity.this, intent, tableauId,tableauMessage,tableauDate,tableauNbreVue,nomService,clientIdOnline,tableauImage);
                    //gridView1.setAdapter(customAdapterMessage);
                    //Afficher la derniere ligne
                    //gridView1.setSelection(gridView1.getCount());
                    //TODO : Ameliorer affichage de vote
                    // TODO : Rendre affichage des publications images dynamique

                    //String[] tableauImage = {String.valueOf(R.drawable.imgtest),String.valueOf(R.drawable.blatte), "http://192.168.1.10:80/soutenance/auto.webm",String.valueOf(R.drawable.imgtest2)};
                    /*String[] tableauImage = {String.valueOf(R.drawable.imgtest),"http://192.168.1.9:80/soutenance/auto.webm",null,"http://192.168.1.9:80/soutenance/auto.webm"};
                    //String[] tableauVideo = {"http://192.168.1.14:80/soutenance/auto.webm","http://192.168.1.14:80/soutenance/auto.webm"};
                    String[] tableauAudio = {"http://192.168.1.14:80/soutenance/GodsPlan.MP3","http://192.168.1.14:80/soutenance/GodsPlan.MP3"};

                    final CustomAdapterMessage customAdapterMessage = new CustomAdapterMessage(ItemAccueilActivity.this, intent, tableauId,tableauMessage,tableauDate,tableauNbreVue,nomService,clientIdOnline,tableauImage);
                    gridView1.setAdapter(customAdapterMessage);
                    //Afficher la derniere ligne
                    //gridView1.setSelection(gridView1.getCount());*/
                    ;
                    //Publication publication1 = new Publication();







                }

                /*
                publication1.setPublicationImage(saveImageToExternalStorage(bitmaps));
                publication1.setPublicationID(tableauId);
                publication1.setPublicationContenu(tableauMessage);
                publication1.setPublicationDate(tableauDate);
                publication1.setPublicationNbreVue(tableauNbreVue);
                publication.add(publication1);

                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file,false));
                    objectOutputStream.writeObject(publication);
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Recuperation des objets
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                    List<Publication> objetSer = new List<Publication>() {
                        @Override
                        public int size() {
                            return 0;
                        }

                        @Override
                        public boolean isEmpty() {
                            return false;
                        }

                        @Override
                        public boolean contains(Object o) {
                            return false;
                        }

                        @NonNull
                        @Override
                        public Iterator<Publication> iterator() {
                            return null;
                        }

                        @NonNull
                        @Override
                        public Object[] toArray() {
                            return new Object[0];
                        }

                        @NonNull
                        @Override
                        public <T> T[] toArray(@NonNull T[] a) {
                            return null;
                        }

                        @Override
                        public boolean add(Publication publication) {
                            return false;
                        }

                        @Override
                        public boolean remove(Object o) {
                            return false;
                        }

                        @Override
                        public boolean containsAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public boolean addAll(@NonNull Collection<? extends Publication> c) {
                            return false;
                        }

                        @Override
                        public boolean addAll(int index, @NonNull Collection<? extends Publication> c) {
                            return false;
                        }

                        @Override
                        public boolean removeAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public boolean retainAll(@NonNull Collection<?> c) {
                            return false;
                        }

                        @Override
                        public void clear() {

                        }

                        @Override
                        public Publication get(int index) {
                            return null;
                        }

                        @Override
                        public Publication set(int index, Publication element) {
                            return null;
                        }

                        @Override
                        public void add(int index, Publication element) {

                        }

                        @Override
                        public Publication remove(int index) {
                            return null;
                        }

                        @Override
                        public int indexOf(Object o) {
                            return 0;
                        }

                        @Override
                        public int lastIndexOf(Object o) {
                            return 0;
                        }

                        @NonNull
                        @Override
                        public ListIterator<Publication> listIterator() {
                            return null;
                        }

                        @NonNull
                        @Override
                        public ListIterator<Publication> listIterator(int index) {
                            return null;
                        }

                        @NonNull
                        @Override
                        public List<Publication> subList(int fromIndex, int toIndex) {
                            return null;
                        }
                    };
                    objetSer = (List<Publication>) objectInputStream.readObject();

                    int[] tableauPublicationId1 = new int[objetSer.size()] ;
                    String[] tableauPublicationContenu1 = new String[objetSer.size()] ;
                    String[] tableauDate1 = new String[objetSer.size()] ;
                    int[] tableauNbreVue1 = new int[objetSer.size()] ;
                    String[] tableauImage1 = new String[objetSer.size()] ;

                    for (int j = 0; j < objetSer.size(); j++)
                    {
                        tableauPublicationId1  = objetSer.get(j).getPublicationID();
                        tableauPublicationContenu1  = objetSer.get(j).getPublicationContenu();
                        tableauDate1  = objetSer.get(j).getPublicationDate();
                        tableauNbreVue1  = objetSer.get(j).getPublicationNbreVue();
                        tableauImage1  = objetSer.get(j).getPublicationImage();
                        Log.i("Tableau message", tableauPublicationContenu1.toString());
                        Log.i("Tableau Date", String.valueOf(tableauDate1));
                        String[] tableauImage2 = {String.valueOf(R.drawable.imgtest),"http://192.168.43.186/soutenance/auto.webm",null,"http://192.168.43.186/soutenance/auto.webm"};

                        final CustomAdapterMessage customAdapterMessage = new CustomAdapterMessage(ItemAccueilActivity.this, intent, objetSer.get(j).getPublicationID(),objetSer.get(j).getPublicationContenu(),objetSer.get(j).getPublicationDate(),objetSer.get(j).getPublicationNbreVue(),nomService,clientIdOnline,objetSer.get(j).getPublicationImage());
                        gridView1.setAdapter(customAdapterMessage);

                    }


                    objectInputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                */


                gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    Intent intent = new Intent(ItemAccueilActivity.this,CommentaireActivity.class);

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //Recuperer les informations de la ligne selectionnée
                        String message = tableauMessage[position].toString();
                        int idPublication = tableauId[position];

                        Intent intent2 = getIntent();
                        int clientIdOnline = intent2.getIntExtra("clientIdOnline",0);
                        intent.putExtra("clientIdOnline",clientIdOnline);
                        startActivity(intent);
                        Toast.makeText(ItemAccueilActivity.this, "Test du click", Toast.LENGTH_SHORT).show();

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings != null){
                if (strings.length > 0) {
                    Toast.makeText(ItemAccueilActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                    tvInfo.setVisibility(View.INVISIBLE);
                }
                else {
                    Toast.makeText(ItemAccueilActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();
                    tvInfo.setVisibility(View.VISIBLE);

                }

            }
            else {
                Toast.makeText(ItemAccueilActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();
                //tvInfo.setVisibility(View.VISIBLE);
            }

            //progressBar.setVisibility(View.INVISIBLE);


        }


    }


    private class VerificationPublication extends AsyncTask<Void, String, String[]>
    {


        //ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar1);


        //fonction Okhttp get
        public String run(String API){

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API)
                    .build();

            String publication = null;
            String reponse = null;

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponseMessage", response.toString());
                //publishProgress(valeur);
                reponse = response.body().string();
                publishProgress(reponse);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return reponse;
        }

        @Override
        protected String[] doInBackground(Void... voids) {

            String API = ipOnline+"/alerterest/listallalerte/"+ serviceId;

            String reponse = run(API);




            JSONArray reponseBody = null;
            String[] message = null;
            try {
                if (reponse != null){
                    reponseBody = new JSONArray(reponse);
                    message = new String[reponseBody.length()];
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




            return message;
            //return new String[0];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            gridView1 = (GridView) findViewById(R.id.gvPublication);
            int publicationid;
            //Appel des informations de la ListView
            tvInfo.setVisibility(View.INVISIBLE);

            String valeur = values[0];
            try {

                JSONArray reponseBody = new JSONArray(valeur);
                final int[] tableauId = new int[reponseBody.length()];


                    JSONObject element0 = reponseBody.getJSONObject(0);

                    publicationid = element0.getInt("alerteid");
                    String publicationContenu = element0.get("alertemessage").toString();
                    String publicationDateCreation = element0.get("alertedatecreation").toString();


                    tableauId[0] = publicationid;

                    //Verifier si ya de nouveaux messages
                    int lastId = tableauId[0];
                    String idmessage = String.valueOf(lastId);

                //Verification d'existance du ID
                SharedPreferences sharedPreferences2 = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
                if (sharedPreferences2.contains(PREFS_LAST_ID)) {

                    idmessage = sharedPreferences2.getString(PREFS_LAST_ID, null);

                    if (lastId == Integer.valueOf(idmessage)){
                        //Toast.makeText(ItemAccueilActivity.this, "Aucune nouvelle alerte", Toast.LENGTH_SHORT).show();
                    }else if (lastId > Integer.valueOf(idmessage)){
                        Toast.makeText(ItemAccueilActivity.this, "Nouvelle alerte", Toast.LENGTH_SHORT).show();
                        //Afficher la nouvelle Alerte
                        sharedPreferences2 = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
                        sharedPreferences2
                                .edit()
                                .putString(PREFS_LAST_ID, String.valueOf(lastId))
                                .apply();

                        new ReceptionPublication().execute(Integer.valueOf(idmessage));

                    }


                }else{
                    sharedPreferences2 = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
                    sharedPreferences2
                            .edit()
                            .putString(PREFS_LAST_ID, String.valueOf(idmessage))
                            .apply();
                }


                    //Enregistrement du dernier ID
                 /*   sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                String json;
                if (sharedPreferences.contains(PUBLICATION_USER)) {
                    json = sharedPreferences.getString(PUBLICATION_USER, null);
                    JSONObject objetMessage;
                    try {
                        JSONArray tableau = new JSONArray(json);
                            objetMessage = tableau.getJSONObject(0);
                            idmessage = objetMessage.getInt("idmessage");




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }



                        if (lastId == idmessage){
                            Toast.makeText(ItemAccueilActivity.this, "Aucune nouvelle alerte", Toast.LENGTH_SHORT).show();
                        }else if (lastId > idmessage){
                            Toast.makeText(ItemAccueilActivity.this, "Nouvelle alerte", Toast.LENGTH_SHORT).show();
                            //Afficher la nouvelle Alerte
                            new ReceptionPublication().execute();

                        }

                            */





            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings != null){
                if (strings.length > 0) {
                    //Toast.makeText(ItemAccueilActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                    tvInfo.setVisibility(View.INVISIBLE);
                }
                else {
                    //Toast.makeText(ItemAccueilActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();
                    tvInfo.setVisibility(View.VISIBLE);

                }

            }
            else {
                //Toast.makeText(ItemAccueilActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();
                //tvInfo.setVisibility(View.VISIBLE);
            }

            //progressBar.setVisibility(View.INVISIBLE);


        }


    }




    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }


    //TODO : Fonction pour générer des noms pour les images
    public String[] saveImageToExternalStorage(Bitmap[] image) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
        //String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/storage/myvibes/images/";

        String[] pathImage = new String[image.length];
        for (int i = 0; i < image.length; i++) {


        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, "myvibes" + Otp.generateCode(4));
            file.createNewFile();
            fOut = new FileOutputStream(file);

            // 100 means no compression, the lower you go, the stronger the compression
            image[i].compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            pathImage[i] = file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            return pathImage;
        }

    }

        return pathImage;

    }

    private class getBitmapFromURL extends AsyncTask<String,Bitmap,String>{

        @Override
        protected String doInBackground(String... params) {
            String url1 = params[0];

                String API = url1;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(API)
                        .build();

                String reponse = null;
                try {
                    URL url = new URL(url1);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    publishProgress(myBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                try {
                    Response response = client.newCall(request).execute();
                    Log.i("ReponsePublication", response.toString());
                    reponse = response.body().string();


                } catch (IOException e) {
                    e.printStackTrace();
                }





            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            Bitmap bitmap = values[0];
            //saveImageToExternalStorage(bitmap);
            //iv2.setImageBitmap(bitmap);

        }
    }


    //Verifier les permissions
    public void verifyPermissions()
    {
        // This will return the current Status
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(ItemAccueilActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionExternalMemory != PackageManager.PERMISSION_GRANTED)
        {
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(ItemAccueilActivity.this,STORAGE_PERMISSIONS,1);
        }
    }



    public void notification(String contenu,String nomService){

        int notificationId = 1;
        String CHANNEL_ID = "Chaine01";

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, ItemAccueilActivity.class);
        intent.putExtra("nom",nomService);
        tvInfo.setVisibility(View.GONE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "nom";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            String description = "Description";
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                //.setSmallIcon(R.drawable.ic_message_white_24dp)
                .setSmallIcon(R.drawable.kse2)
                .setContentTitle(nomService)
                .setContentText(contenu)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[] {1000,1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)


                //LED
                .setLights(Color.RED, 3000, 3000)
                ;





        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());

    }


    private class RequestServiceSouscrit extends AsyncTask<String, String, String[]>
    {

        @Override
        protected String[] doInBackground(String... params) {

            String num = params[0];
            //URL
            //String API = ipOffline+"/servicerest/listDistinctservice/"+num;
            String API = ipOnline+"/servicerest/listfindate/"+num;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API)
                    .build();

            String publication = null;
            String reponse = null;

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponsePublication", response.toString());
                reponse = response.body().string();
                publishProgress(reponse);




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
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            String result = values[0];
            JSONArray reponseBody = null;

            try {
                if (result != null){

                    reponseBody = new JSONArray(result);

                    if (reponseBody.length() > 0){
                        //Toast.makeText(ItemAccueilActivity.this, "OK OK", Toast.LENGTH_LONG).show();
                        long unixSecondsParse = reponseBody.getLong(0);
                        String unixSecondsString = String.valueOf(unixSecondsParse);
                        String b = unixSecondsString.substring(0,10);
                        long unixSeconds = Integer.parseInt(b);
                        Log.i("long", String.valueOf(unixSeconds));


                        // convert seconds to milliseconds
                        Date date = new java.util.Date(unixSeconds*1000L);
                        // the format of your date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        // give a timezone reference for formatting (see comment at the bottom)
                        //sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
                        String dateFinSous1 = sdf.format(date);

                        //Formater la date du jour
                        Date now = new Date();
                        String dateJour1 = sdf.format(now);
                        try {
                            Date dateFinSous =  sdf.parse(dateFinSous1);
                            Date dateJour =  sdf.parse(dateJour1);

                            if (dateFinSous.before(dateJour)){
                                //Souscription invalide
                                Log.i("Souscription", "Souscription perdu");
                                StopRunnable();

                            }else {
                                //Souscription valide
                                Log.i("Souscription", "Souscription valider");
                                new VerificationPublication().execute();

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }

                    else if (reponseBody.length() == 0){
                        //Toast.makeText(ItemAccueilActivity.this, "NON NON", Toast.LENGTH_LONG).show();
                        StopRunnable();
                    }



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            /*if (strings != null){
                if (strings.length > 0){
                    //Toast.makeText(ItemAccueilActivity.this, "OK OK", Toast.LENGTH_LONG).show();
                    new VerificationPublication().execute();
                }

                else if (strings.length == 0){
                    //Toast.makeText(ItemAccueilActivity.this, "NON NON", Toast.LENGTH_LONG).show();
                    StopRunnable();
                }

            }*/

        }

    }







    //Fonctions pour le menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                break;
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, AccueilActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}