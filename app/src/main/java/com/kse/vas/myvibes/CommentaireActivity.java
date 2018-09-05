package com.kse.vas.myvibes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentaireActivity extends AppCompatActivity {

    EditText message;
    JSONObject jsonObject;
    ImageButton btn_send;
    ListView listView1;
    FloatingActionButton fbDescendre;
    String ipOffline = ConfigApp.ipOffline;
    String ipOnline = ConfigApp.ipOnline;
    int temp = 0;
    CustomAdapterCommentaire customAdapterCommentaire;
    List<Message> messageArrayList = null;
    Message message1 = null;
    ImageView ivValidation;


    String messageSaisi;
    int Idpublication;
    int serviceId;

    String DATA;
    File file;
    File directory;

    //SharedPreferences
    String PREFS = "COMMENT";
    Gson gson = new Gson();
    //String COMMENT_USER = "COMMENT_USER";
    //String COMMENT_USER = "COMMENT_USER"+Idpublication;
    String COMMENT_USER;
    SharedPreferences sharedPreferences;



    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 30000; // milliseconds
    Handler myHandler = new Handler();  // android.os.Handler
    Runnable mRefreshCommentaireRunnable = new Runnable() {
        @Override
        public void run() {
            //new ReceptionMessage().execute();

            String num = ObtainNum();
            new RequestServiceSouscrit().execute(num);

            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };


    public void StopRunnable(){
        myHandler.removeCallbacks(mRefreshCommentaireRunnable);
        Log.i("Stop alerte", "Arrêt des recherches de nouveaux messages");
        Toast.makeText(this, getResources().getString(R.string.sous_expiree), Toast.LENGTH_LONG).show();
        ToastPerso();
    }

    public void ToastPerso(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.tvCustomToast);
        text.setText(R.string.sous_expiree);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
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
        setContentView(R.layout.activity_commentaire);

        //Enregistrer des objets
        DATA = "listCommentaire.txt";
        directory = getApplicationContext().getFilesDir();
        file = new File(directory, DATA);

        message = (EditText) findViewById(R.id.message);
        btn_send = (ImageButton)findViewById(R.id.btn_send);
        fbDescendre = (FloatingActionButton)findViewById(R.id.fbDescendre);
        ivValidation = (ImageView) findViewById(R.id.ivValidation);

        messageArrayList = new ArrayList<>();
        message1 = new Message("Hello","Test");








        myHandler.postDelayed(mRefreshCommentaireRunnable, POLL_INTERVAL);

        //new ReceptionMessage().execute();

        Intent intent = getIntent();
        Idpublication = intent.getIntExtra("Idpublication",0);

        serviceId = intent.getIntExtra("serviceId", 0);


        COMMENT_USER = "COMMENT_USER"+Idpublication;



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageSaisi = message.getText().toString().trim();
                if (TextUtils.isEmpty(messageSaisi)){
                    Toast.makeText(CommentaireActivity.this, "Aucun message saisi", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(messageSaisi);
                    //new EnvoiMessage().execute();
                    //myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
                    //new ReceptionMessage().execute();
                }

            }
        });


        fbDescendre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Afficher la derniere ligne
                listView1.setSelection(listView1.getCount());
                //Back to top
                // listView1.smoothScrollToPosition(0);
            }
        });

        implementScrollListener();


    }

    @Override
    protected void onResume() {
        super.onResume();

        /*sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        if (sharedPreferences.contains(COMMENT_USER)) {
            String json = sharedPreferences.getString("COMMENT_USER",null);
            List<Message> messages = null;
            if (json != null){
                messages = gson.fromJson(json,new TypeToken<List<Message>>() {}.getType());
                for (int i=0; i<messages.size(); i++){
                    //Log.i("Liste"+i, messages.get(i).getMessage());
                    customAdapterCommentaire = new CustomAdapterCommentaire(getApplicationContext(),messages);
                    listView1.setAdapter(customAdapterCommentaire);
                    customAdapterCommentaire.notifyDataSetChanged();

                }
            }else {
                Toast.makeText(this, "Conversation vide", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Conversation vide", Toast.LENGTH_SHORT).show();

        }*/

        //2eme methode
        Intent intent = getIntent();
        Idpublication = intent.getIntExtra("Idpublication",0);
        String COMMENT_USER = "COMMENT_USER"+Idpublication;

        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        if (sharedPreferences.contains(COMMENT_USER)) {
            String json = sharedPreferences.getString(COMMENT_USER,null);
            List<Message> messages = new ArrayList<>();
            JSONObject objetMessage;
            try {
                JSONArray tableau = new JSONArray(json);
                for (int i=0; i<tableau.length(); i++){
                    objetMessage = tableau.getJSONObject(i);
                    String message = objetMessage.getString("message");
                    String dateCourante = objetMessage.getString("dateCourante");
                    messages.add(new Message(message,dateCourante));


                }
                customAdapterCommentaire = new CustomAdapterCommentaire(getApplicationContext(),messages);
                listView1.setAdapter(customAdapterCommentaire);
                customAdapterCommentaire.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            Toast.makeText(this, "Conversation vide", Toast.LENGTH_SHORT).show();

        }




    }



    // Implement scroll listener
    private void implementScrollListener() {

        listView1 = findViewById(R.id.lvCommentaire);
        listView1.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                /*if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    fbDescendre.setVisibility(View.INVISIBLE);

                }*/
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //Verifier si on est en bas de la liste
                if (listView1.getLastVisiblePosition() == listView1.getCount() - 1
                        && listView1.getChildAt(listView1.getChildCount() - 1) != null
                        && listView1.getChildAt(listView1.getChildCount() - 1).getBottom() <= listView1.getHeight()) {
                    //Toast.makeText(getApplicationContext(), "Bottom!", Toast.LENGTH_SHORT).show();
                    fbDescendre.setVisibility(View.INVISIBLE);
                }else {
                    fbDescendre.setVisibility(View.VISIBLE);
                }


            }
        });

    }


    //Tache Asynchrone d'enregistrement de message
    //Insertion de donnees
    //TODO : Afficher l'heure dans l'affichage des messages
    private class EnvoiMessage extends AsyncTask<Void, String, Message[]> {

        @Override
        protected Message[] doInBackground(Void... voids) {

            String messageSaisi = message.getText().toString();


            Log.i("Message saisi", "message: "+messageSaisi);

            jsonObject = new JSONObject();
            JSONObject objetAbonne = new JSONObject();
            JSONObject objetAlerte = new JSONObject();

            //Intent intent = getIntent();
            //int myclientid = intent.getIntExtra("clientIdOnline",0);
            //Recuperer idclientonline dans sharedpreferences
            sharedPreferences = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
            int myclientid = sharedPreferences.getInt("PREFS_ID_ONLINE", 0);


            try {

                objetAbonne.put("abonneid",myclientid);
                objetAlerte.put("alerteid",Idpublication);
                jsonObject.put("commentairecontenu",messageSaisi);

                //Formater la date du jour
                Date now = new Date();
                DateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");;
                String formattedDate = dateformatter.format(now);
                Log.i("DateNow", formattedDate);
                //Formater l'heure
                DateFormat timeformatter = DateFormat.getTimeInstance(DateFormat.SHORT);
                String formattedTime = timeformatter.format(now);
                Log.i("TimeNow", formattedTime);

                String dateJour = formattedDate;


                jsonObject.put("commentairedatecreation",dateJour);
                jsonObject.put("commentairevue",false);
                jsonObject.put("commentaireenable",true);
                jsonObject.putOpt("abonne_",objetAbonne);
                jsonObject.putOpt("alerte",objetAlerte);


                Log.i("objetJson", String.valueOf(jsonObject));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String valeur = null;
            publishProgress(valeur);


            //URL
            String API = ipOffline+"/commentairerest/addcommentaire";


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
                Log.i("ReponseMessage", response.toString());
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


            return new Message[0];
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //pbChargement.setVisibility(View.VISIBLE);
            if(values[0] == "erreur"){
                Toast.makeText(CommentaireActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();

            }
            else if (values[0] == "reussite"){
                Toast.makeText(CommentaireActivity.this, R.string.enr_reussi, Toast.LENGTH_SHORT).show();
                reset();
            }

        }

        @Override
        protected void onPostExecute(Message[] messages) {
            super.onPostExecute(messages);
            //pbChargement.setVisibility(View.INVISIBLE);
            //relativeLayout1.setBackgroundResource(R.drawable.bulle_discution_sortante);
            //messageRecu.setText(message.getText().toString());

        }

        private void reset() {
            message.setText("");
        }


    }


    //reception des messages
    //TODO : Afficher les messages les plus recents en bas de la liste des commentaires

    private class ReceptionMessage extends AsyncTask<Integer, String, Boolean>
    {

        @Override
        protected Boolean doInBackground(Integer... params) {

            int commentaireId = params[0];

            //URL
            //String API = ipOffline+"/commentairerest/listallcomment/"+Idpublication;
            String API = ipOffline+"/commentairerest/listnewcomment/"+Idpublication+"/"+commentaireId;

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
            JSONArray reponse1 = null;
            int clientidresultat = 0;
            boolean messageNotification = false;


            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponseMessage", response.toString());
                //publishProgress(valeur);
                reponse = response.body().string();


                int tailleReponse = reponse.length();

                /*if (tailleReponse > temp){
                    //Toast.makeText(CommentaireActivity.this, "Notification", Toast.LENGTH_SHORT).show();
                    publishProgress("Notification");
                    temp = tailleReponse;
                }*/

                publishProgress(reponse);



                //publishProgress(publication.toString());

                //TextView tvTest = (TextView)findViewById(R.id.tvTest);

                //tvTest.setText(publication.getEmpname());

            } catch (IOException e) {
                e.printStackTrace();
            }

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






            /////////PARSE NOTIFICATION
            String[] tailleTableau = null;
            String commentairecontenu = null;


            try {

                if (reponse != null){
                    JSONArray reponseBodyNotif = new JSONArray(reponse);
                    int[] tableauid = new int[reponseBodyNotif.length()];
                    String[] tableaucommentaire = new String[reponseBodyNotif.length()];
                    boolean commentairevue = true;

                    int i;
                    int tailleReponse = reponseBodyNotif.length() - 1;
                    for (i = 0; i < reponseBodyNotif.length(); i++) {
                        JSONObject elementi = reponseBody.getJSONObject(i);
                        JSONObject clientObjet = elementi.getJSONObject("client");
                        clientidresultat = clientObjet.getInt("clientid");
                        commentairecontenu = elementi.getString("commentairecontenu");
                        tableauid[i] = clientidresultat;
                        tableaucommentaire[i] = commentairecontenu;
                        commentairevue = elementi.getBoolean("commentairevue");


                    }
                    //Envoi de notification
                    Intent intent = getIntent();
                    int myclientid = intent.getIntExtra("clientIdOnline", 0);
                    tailleTableau = new String[reponseBody.length()];
                    if (tailleTableau.length > temp) {
                        int b = tableauid[tailleReponse];
                        //if (myclientid == tableauid[tailleReponse] && !commentairevue){
                        if (myclientid != tableauid[tailleReponse]) {
                            messageNotification = true;
                            notification2(tableaucommentaire[tailleReponse]);
                        }
                        temp = tailleTableau.length;
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }





            return messageNotification;
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

            listView1 = (ListView)findViewById(R.id.lvCommentaire);

            //Appel des informations de la ListView
            String valeur = values[0];
            /*if (valeur.toString().contains("Notification")) {
                Toast.makeText(CommentaireActivity.this, valeur, Toast.LENGTH_SHORT).show();

            }*/
            try {

                JSONArray reponseBody = new JSONArray(valeur);
                final String[] tableauMessage = new String[reponseBody.length()];
                final String[] tableauDate = new String[reponseBody.length()];
                //final int[] clientid = new int[reponseBody.length()];



                int i;
                for(i=0; i < reponseBody.length(); i++) {
                    JSONObject elementi = reponseBody.getJSONObject(i);


                    String commentaireContenu = elementi.get("commentairecontenu").toString();
                    String commentaireDateCreation = elementi.get("commentairedatecreation").toString();
                    //int clientid1 = elementi.getInt("clientid");
                    boolean commentairevue = elementi.getBoolean("commentairevue");


                    /*
                    //Conversion de Unix time
                    Calendar calendrier = Calendar.getInstance();
                    long dateLong = Long.parseLong(commentaireDateCreation.substring(0,10));
                    //int dateInt = Integer.parseInt(commentaireDateCreation) ;
                    calendrier.setTimeInMillis((long)dateLong *1000);
                    Log.i("calendrier", String.valueOf(calendrier.getTime()));


                    //Conversion des dates
                    ConversionDate conversionDate = new ConversionDate();

                    //resultats
                    String dateToday = ( conversionDate.getJour(calendrier.get(Calendar.DAY_OF_WEEK)) +" "+ calendrier.get(Calendar.DAY_OF_MONTH)+" "+conversionDate.getMois(calendrier.get(Calendar.MONTH))+" "+calendrier.get(Calendar.YEAR) ).toString();

                    //Log
                    Log.i("Json,DateDuJour", dateToday);
                    */

                    tableauMessage[i] = commentaireContenu;
                    //tableauDate[i] = dateToday;
                    tableauDate[i] = commentaireDateCreation;

                    //Message message1 =new Message(commentaireContenu,commentaireDateCreation);
                    message1.setMessage(commentaireContenu);
                    message1.setDateCourante(commentaireDateCreation);
                    messageArrayList.add(message1);

                }










                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file,false));
                    objectOutputStream.writeObject(messageArrayList);
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Recuperation des objets
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                    List<Message> objetSer = new List<Message>() {
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
                        public Iterator<Message> iterator() {
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
                        public boolean add(Message message) {
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
                        public boolean addAll(@NonNull Collection<? extends Message> c) {
                            return false;
                        }

                        @Override
                        public boolean addAll(int index, @NonNull Collection<? extends Message> c) {
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
                        public Message get(int index) {
                            return null;
                        }

                        @Override
                        public Message set(int index, Message element) {
                            return null;
                        }

                        @Override
                        public void add(int index, Message element) {

                        }

                        @Override
                        public Message remove(int index) {
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
                        public ListIterator<Message> listIterator() {
                            return null;
                        }

                        @NonNull
                        @Override
                        public ListIterator<Message> listIterator(int index) {
                            return null;
                        }

                        @NonNull
                        @Override
                        public List<Message> subList(int fromIndex, int toIndex) {
                            return null;
                        }
                    };
                    objetSer = (List<Message>) objectInputStream.readObject();

                    String[] tableauMessage1 = new String[objetSer.size()] ;
                    String[] tableauDate1 = new String[objetSer.size()] ;

                    for (int j = 0; j < objetSer.size(); j++)
                    {
                        //tableauMessage1  = objetSer.get(j).getMessage();
                        //tableauDate1  = objetSer.get(j).getDateCourante();

                        final CustomAdapterCommentaire customAdapterCommentaire = new CustomAdapterCommentaire(getApplicationContext(),objetSer);
                        //final CustomAdapterCommentaire customAdapterCommentaire = new CustomAdapterCommentaire(getApplicationContext(),objetSer.get(j).getMessage(),objetSer.get(j).getDateCourante());
                        // Save the ListView state (= includes scroll position) as a Parceble
                        Parcelable state = listView1.onSaveInstanceState();

                        listView1.setAdapter(customAdapterCommentaire);

                        // Restore previous state (including selected item index and scroll position)
                        listView1.onRestoreInstanceState(state);

                    }


                    objectInputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


                /*final CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),name,description,drapeau,imageIcon);
                listView1.setAdapter(customAdapter);*/

                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Recuperer les informations de la ligne selectionnée
                        String message = tableauMessage[position].toString();



                    }
                });




            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(Boolean strings) {
            super.onPostExecute(strings);
            if (strings == true){
                //Toast.makeText(CommentaireActivity.this, "Notification parfaite", Toast.LENGTH_SHORT).show();
                //notification2();
            }
            /*if (strings != null){
                if (strings.length > temp){
                    Toast.makeText(CommentaireActivity.this, "Notification old", Toast.LENGTH_SHORT).show();
                    temp = strings.length;
                }
                if (strings.length > 1) {
                    //Toast.makeText(CommentaireActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(CommentaireActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(CommentaireActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();*/


            //progressBar.setVisibility(View.INVISIBLE);


        }

    }

    public void notification(){

        int notificationId = 1;
        String CHANNEL_ID = "Chaine01";
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, CommentaireActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_message_white_24dp)
                .setContentTitle("Titre")
                .setContentText("Contenu")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[] {1000,1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                //LED
                .setLights(Color.RED, 3000, 3000);



        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());

    }

    //Fonction d'envoi de notification
    public void notification2(String contenu){

        int notificationId = 1;
        String CHANNEL_ID = "Chaine01";

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, CommentaireActivity.class);
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
                .setSmallIcon(R.drawable.ic_message_white_24dp)
                .setContentTitle("Nouveau message")
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

    public void sendMessage(String commentaire){
        Date now = new Date();
        DateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateformatter.format(now);
        Log.i("DateNow", formattedDate);
        //Formater l'heure
        DateFormat timeformatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        String formattedTime = timeformatter.format(now);
        Log.i("TimeNow", formattedTime);

        //Enregistrement de l'info
        /*try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file,true));
            objectOutputStream.writeObject(messageArrayList);
            //objectOutputStream.writeObject(messageArrayList);
            objectOutputStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }*/
        //message1.setMessage(commentaire);
        //message1.setDateCourante(formattedDate);
        messageArrayList.add(new Message(commentaire,formattedDate));


        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        List<Message> messages = new List<Message>() {
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
            public Iterator<Message> iterator() {
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
            public boolean add(Message message) {
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
            public boolean addAll(@NonNull Collection<? extends Message> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, @NonNull Collection<? extends Message> c) {
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
            public Message get(int index) {
                return null;
            }

            @Override
            public Message set(int index, Message element) {
                return null;
            }

            @Override
            public void add(int index, Message element) {

            }

            @Override
            public Message remove(int index) {
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
            public ListIterator<Message> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<Message> listIterator(int index) {
                return null;
            }

            @NonNull
            @Override
            public List<Message> subList(int fromIndex, int toIndex) {
                return null;
            }
        };
        String json = null;

        if (sharedPreferences.contains(COMMENT_USER)) {
            json = sharedPreferences.getString(COMMENT_USER,null);
            JSONObject objetMessage;
            try {
                JSONArray tableau = new JSONArray(json);
                for (int i=0; i<tableau.length(); i++){
                    objetMessage = tableau.getJSONObject(i);
                    String message = objetMessage.getString("message");
                    String dateCourante = objetMessage.getString("dateCourante");
                    messages.add(new Message(message,dateCourante));


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*if (json != null){
                messages = gson.fromJson(json,new TypeToken<List<Message>>() {}.getType());
                for (int i=0; i<messages.size(); i++){
                    //Log.i("Liste"+i, messages.get(i).getMessage());
                    customAdapterCommentaire = new CustomAdapterCommentaire(getApplicationContext(),messages);
                    listView1.setAdapter(customAdapterCommentaire);
                    customAdapterCommentaire.notifyDataSetChanged();

                }
            }else {
                Toast.makeText(this, "Conversation vide", Toast.LENGTH_SHORT).show();
            }*/



            //Creer l'objet message
            JSONObject objetMessage1 = new JSONObject();
            JSONArray tableau = null;
            try {
                tableau = new JSONArray(json);
                objetMessage1.put("message",commentaire);
                objetMessage1.put("dateCourante",formattedDate);

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
                    .putString(COMMENT_USER, String.valueOf(tableau))
                    .apply();
            //Log.i("GSON", gson.toJson(messageArrayList));
            customAdapterCommentaire.add(new Message(message.getText().toString().trim(), formattedDate));
            customAdapterCommentaire.notifyDataSetChanged();
            //Enregistrement du message en bd
            new EnvoiMessage().execute();


        }else {
            Toast.makeText(this, "Conversation vide", Toast.LENGTH_SHORT).show();


            //Creer l'objet message
            JSONObject objetMessage = new JSONObject();
            JSONArray tableau = null;
            try {
                tableau = new JSONArray();
                objetMessage.put("message",commentaire);
                objetMessage.put("dateCourante",formattedDate);

                tableau.put(objetMessage);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Remplir shared preferences
            sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
            //si aucun utilisateur n'est sauvegardé, on ajouter
            sharedPreferences
                    .edit()
                    //.putString(COMMENT_USER, gson.toJson(messageArrayList))
                    .putString(COMMENT_USER, String.valueOf(tableau))
                    .apply();
            //Log.i("GSON", gson.toJson(messageArrayList));


            if (customAdapterCommentaire == null){
                customAdapterCommentaire = new CustomAdapterCommentaire(getApplicationContext(),messageArrayList);
                listView1.setAdapter(customAdapterCommentaire);
                customAdapterCommentaire.notifyDataSetChanged();
                //Enregistrement du message en bd
                new EnvoiMessage().execute();

            }else {
                customAdapterCommentaire.add(new Message(message.getText().toString().trim(), formattedDate));
                customAdapterCommentaire.notifyDataSetChanged();
                //Enregistrement du message en bd
                new EnvoiMessage().execute();

            }



        }

        //Creer l'objet message
        /*JSONObject objetMessage = new JSONObject();
        JSONArray tableau = null;
        try {
            tableau = new JSONArray(json);
            objetMessage.put("message",commentaire);
            objetMessage.put("dateCourante",formattedDate);
            
            tableau.put(objetMessage);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Remplir shared preferences
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
            //si aucun utilisateur n'est sauvegardé, on ajouter
            sharedPreferences
                    .edit()
                    //.putString(COMMENT_USER, gson.toJson(messageArrayList))
                    .putString(COMMENT_USER, String.valueOf(tableau))
                    .apply();
        //Log.i("GSON", gson.toJson(messageArrayList));


            if (customAdapterCommentaire == null){
                customAdapterCommentaire = new CustomAdapterCommentaire(getApplicationContext(),messageArrayList);
                listView1.setAdapter(customAdapterCommentaire);
                customAdapterCommentaire.notifyDataSetChanged();

            }else {
                customAdapterCommentaire.add(new Message(message.getText().toString().trim(), formattedDate));
                customAdapterCommentaire.notifyDataSetChanged();

            }*/
            //customAdapterCommentaire = new CustomAdapterCommentaire(getApplicationContext(),messageArrayList);
            //customAdapterCommentaire.add(new Message(message.getText().toString().trim(), formattedDate));
            //listView1.setAdapter(customAdapterCommentaire);
            //customAdapterCommentaire.notifyDataSetChanged();





        //message.setText("");
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
                                Log.i("Commentaire", "Souscription perdu");
                                //Arreter la recherche des messages
                                StopRunnable();

                            }else {
                                //Souscription valide
                                Log.i("Commentaire", "Souscription valider");
                                new VerificationCommentaire().execute();

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


    private class VerificationCommentaire extends AsyncTask<Void, String, String[]>
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

            String API = ipOffline+"/commentairerest/idlastcomment/"+Idpublication;

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
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            int lastMessageId;

            String valeur = values[0];
            try {

                JSONArray idLastMessage = new JSONArray(valeur);

                lastMessageId = idLastMessage.getInt(0);
                Log.i("Commentaireid", String.valueOf(lastMessageId));
                //tableauId[0] = idLastMessage;

                //Verifier si ya de nouveaux messages
                int lastId = lastMessageId;
                String idmessage = String.valueOf(lastId);

                //Verification d'existance du ID
                SharedPreferences sharedPreferences2 = getBaseContext().getSharedPreferences("IDLASTCOMMENT", MODE_PRIVATE);
                if (sharedPreferences2.contains("LAST_COMMENT_ID"+Idpublication)) {

                    idmessage = sharedPreferences2.getString("LAST_COMMENT_ID"+Idpublication, null);

                    if (lastId == Integer.valueOf(idmessage)){
                        //Toast.makeText(ItemAccueilActivity.this, "Aucune nouvelle alerte", Toast.LENGTH_SHORT).show();
                    }else if (lastId > Integer.valueOf(idmessage)){
                        Toast.makeText(CommentaireActivity.this, "Nouveau message", Toast.LENGTH_SHORT).show();
                        //Afficher la nouvelle Alerte
                        sharedPreferences2 = getBaseContext().getSharedPreferences("IDLASTCOMMENT", MODE_PRIVATE);
                        sharedPreferences2
                                .edit()
                                .putString("LAST_COMMENT_ID"+Idpublication, String.valueOf(lastId))
                                .apply();

                        new ReceptionMessage().execute(Integer.valueOf(idmessage));

                    }


                }else{
                    sharedPreferences2 = getBaseContext().getSharedPreferences("IDLASTCOMMENT", MODE_PRIVATE);
                    sharedPreferences2
                            .edit()
                            .putString("LAST_COMMENT_ID"+Idpublication, String.valueOf(idmessage))
                            .apply();
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings != null){
                if (strings.length > 0) {
                }
                else {

                }

            }
            else {

            }



        }


    }


}
