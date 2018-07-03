package com.kse.vas.myvibes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity{

    FloatingActionButton btValider;
    EditText textNumDeTel;
    TextView tvCnx;
    ProgressBar pbMain;

    private static final String PREFS = "PREFS";
    private static final String PREFS_NUM = "PREFS_NUM";
    private static final String PREFS_OTP = "PREFS_OTP";
    private static final String PREFS_PSEUDO = "PREFS_PSEUDO";
    SharedPreferences sharedPreferences;

    public static final int TailleOTP = 4;
    int indicatifCI = 225;
    String ipOffline = ConfigApp.ipOffline;
    String ipOnline = ConfigApp.ipOnline;
    String numTel;
    int idClientOnline;



    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 1000; // milliseconds
    Handler myHandler = new Handler();  // android.os.Handler
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            if (haveInternetConnection())
            {
                // Faire quelque chose si le périphérique est connecté
                //Toast.makeText(this, "Connecté à Internet", Toast.LENGTH_SHORT).show();
                tvCnx.setVisibility(View.INVISIBLE);
                btValider.setClickable(true);
                btValider.setEnabled(true);
                btValider.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark)));
            }
            else
            {
                // Faire quelque chose s'il n'est pas connecté
                tvCnx.setVisibility(View.VISIBLE);
                tvCnx.setText(R.string.error_cnx);
                btValider.setClickable(false);
                btValider.setEnabled(false);
                //btValider.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorGrise)));
                btValider.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorGrise)));



            }

            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCnx = findViewById(R.id.tvCnx);
        pbMain = findViewById(R.id.pbMain);

        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);



        final Intent intent = new Intent(MainActivity.this, OTPActivity.class);
        final Intent intentPseudo = new Intent(MainActivity.this, PseudoActivity.class);
        final Intent intentService = new Intent(MainActivity.this, ServiceActivity.class);
        final Intent intentServiceView = new Intent(MainActivity.this, ServiceViewActivity.class);


        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        /* Vider SharedPreferences*/
        //sharedPreferences.edit().clear().commit();
        if (sharedPreferences.contains(PREFS_NUM) && sharedPreferences.contains(PREFS_OTP) && sharedPreferences.contains(PREFS_PSEUDO)) {


            String num = sharedPreferences.getString(PREFS_NUM, null);
            String OTP = sharedPreferences.getString(PREFS_OTP, null);
            String pseudo = sharedPreferences.getString(PREFS_PSEUDO, null);
            Toast.makeText(this, "Bienvenue : " + pseudo + ",Votre numero : "+ num + " OTP: " + OTP, Toast.LENGTH_LONG).show();
            startActivity(intentServiceView);
            finish();
        }
        else if (sharedPreferences.contains(PREFS_NUM) && sharedPreferences.contains(PREFS_OTP)) {
            startActivity(intentPseudo);
        }
        else{
            Log.i("Nouvel utilisateur", "Nouvel utilisateur");
            Snackbar.make(findViewById(R.id.constraintLayoutBienvenue), "Bienvenue sur MyVibes", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


        textNumDeTel = (EditText) findViewById(R.id.textNumDeTel);
        //Animation de l'Edittext
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.slide_in_left);
        animation.setDuration(3000);
        textNumDeTel.startAnimation(animation);



        //Bouton pour aller a la prochaine activité
        btValider = (FloatingActionButton) findViewById(R.id.btValider);
        //Animation du bouton
        Animation animationbtn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade);
        btValider.startAnimation(animationbtn);


        //Evenement au clique du bouton valider
        btValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //recuperation du numero de tel
                String valueTel = textNumDeTel.getText().toString().trim();
                numTel = indicatifCI + valueTel;

                Log.i("Num tel", numTel);
                Log.i("Num tel", valueTel);

                //Verification de champ vide
                boolean cancel = false;
                View focusView = null;
                if (TextUtils.isEmpty(valueTel.trim())) {
                    textNumDeTel.setError(getString(R.string.error_field_required));
                    focusView = textNumDeTel;
                    cancel = true;
                }else {
                    if (valueTel.trim().length() < 8){
                        textNumDeTel.setError(getString(R.string.error_field_short));
                        focusView = textNumDeTel;
                        cancel = true;
                    }
                    else{

                        new RequestClient().execute(numTel);
                        //Generation du code OTP
                        /*Otp otp = new Otp();

                        String codeGenere = otp.generateCode(TailleOTP);;
                        Log.i("Code generé", codeGenere);
                        //Envoi des infos vers l'activité suivante
                        intent.putExtra("codeOTP", codeGenere);
                        intent.putExtra("numTel", numTel);

                        startActivity(intent);*/
                        //finish();
                    }

                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                }



            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        
    }

    //Fonction pour verifier la connectivité a internet
    private boolean haveInternetConnection(){
        // Fonction haveInternetConnection : return true si connecté, return false dans le cas contraire
        NetworkInfo network = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (network==null || !network.isConnected())
        {
            // Le périphérique n'est pas connecté à Internet
            return false;
        }

        // Le périphérique est connecté à Internet
        return true;
    }



    //Afficher les données
    private class RequestClient1 extends AsyncTask<String, String, String[]>
    {


        //ProgressBar progressBar = new ProgressBar(AccueilActivity.this);

        @Override
        protected String[] doInBackground(String... strings) {

            //URL
            String API = ipOnline+"/clientrest/listallclient";
            //String API = ipOffline+"/clientrest/listallclient";

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
            pbMain.setVisibility(View.VISIBLE);

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            String valeur = values[0];
            try {

                JSONArray reponseBody = new JSONArray(valeur);
                //Verifier si la bd est vide
                if (reponseBody.length() > 0){

                    int tailleReponse = reponseBody.length() -1 ;
                    int i;
                    for(i=0; i < reponseBody.length(); i++) {
                        JSONObject elementi = reponseBody.getJSONObject(i);
                        String clientmsisdn = elementi.getString("clientmsisdn");
                        //chargement
                        pbMain.setVisibility(View.VISIBLE);

                        if (numTel.equals(clientmsisdn)){
                            pbMain.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Numero existant", Toast.LENGTH_SHORT).show();
                            Log.i("Numero inexistant", "Numero existant");
                            //Popup
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Vous utilisez déjà le service\r\nVoulez vous vous connecter sur ce telephone? -->"+ Build.BRAND + " " + Build.MODEL +"\r\nCliquer sur OK vous deconnectera de votre ancien telephone")
                                    .setTitle("Numéro existant")
                                    .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // CANCEL

                                        }
                                    })
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //OK
                                            Toast.makeText(MainActivity.this, "Les données sont en cours de traitement...", Toast.LENGTH_SHORT).show();
                                            Otp otp = new Otp();

                                            String codeGenere = otp.generateCode(TailleOTP);
                                            Log.i("Code generé", codeGenere);
                                            //Envoi des infos vers l'activité suivante
                                            Intent intent = new Intent(MainActivity.this, OTPActivity.class);
                                            intent.putExtra("codeOTP", codeGenere);
                                            intent.putExtra("numTel", numTel);

                                            //Envoi de l'OTP par sms
                                            new EnvoiOTP().execute(numTel,codeGenere);

                                            startActivity(intent);
                                        }
                                    });
                            // Create the AlertDialog object and return it
                            AlertDialog dialog1 =  builder.create();
                            dialog1.setIcon(R.drawable.ic_warning_black_24dp);
                            dialog1.show();

                            break;
                        }
                        else{
                            if (tailleReponse == i){
                                pbMain.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Numero inexistant", Toast.LENGTH_SHORT).show();
                                Log.i("Numero inexistant", "Numero inexistant");
                                Otp otp = new Otp();

                                String codeGenere = otp.generateCode(TailleOTP);;
                                Log.i("Code generé", codeGenere);
                                //Envoi des infos vers l'activité suivante
                                Intent intent = new Intent(MainActivity.this, OTPActivity.class);
                                intent.putExtra("codeOTP", codeGenere);
                                intent.putExtra("numTel", numTel);

                                //Envoi de l'OTP par sms
                                new EnvoiOTP().execute(numTel,codeGenere);


                                startActivity(intent);
                            }
                        }

                    }


                }else{

                    Toast.makeText(MainActivity.this, "Numero inexistant", Toast.LENGTH_SHORT).show();
                    Log.i("Numero inexistant", "Base de données vide et numero inexistant");
                    Otp otp = new Otp();

                    String codeGenere = otp.generateCode(TailleOTP);;
                    Log.i("Code generé", codeGenere);
                    //Envoi des infos vers l'activité suivante
                    Intent intent = new Intent(MainActivity.this, OTPActivity.class);
                    intent.putExtra("codeOTP", codeGenere);
                    intent.putExtra("numTel", numTel);

                    //Envoi de l'OTP par sms
                    new EnvoiOTP().execute(numTel,codeGenere);

                    startActivity(intent);

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
                    //Toast.makeText(MainActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                    pbMain.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(MainActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(MainActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();

            pbMain.setVisibility(View.INVISIBLE);


        }

    }


    //Afficher les données
    private class RequestClient extends AsyncTask<String, String, String[]>
    {


        //ProgressBar progressBar = new ProgressBar(AccueilActivity.this);

        @Override
        protected String[] doInBackground(String... strings) {

            String num = strings[0];
            //URL
            //String API = ipOffline+"/clientrest/listallclient";
            String API = ipOnline+"/abonnerest/listmsisdnabonne/"+num;
            //String API = ipOffline+"/clientrest/listallclient";

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
                int messageReponse = response.code();
                if(messageReponse == 500){
                    publishProgress("erreur");
                }
                else if (messageReponse == 200){
                    reponse = response.body().string();
                    publishProgress(reponse);

                }




            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject reponseBody = null;
            String[] name = null;
            try {
                if (reponse != null){
                    reponseBody = new JSONObject(reponse);
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
            pbMain.setVisibility(View.VISIBLE);

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            String valeur = values[0];

            if (valeur.equals("erreur")){
                //Si numero inexistant dans la base
                pbMain.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Numero inexistant", Toast.LENGTH_SHORT).show();
                Log.i("Numero inexistant", "Numero inexistant");
                Otp otp = new Otp();

                String codeGenere = otp.generateCode(TailleOTP);;
                Log.i("Code generé", codeGenere);
                //Envoi des infos vers l'activité suivante
                Intent intent = new Intent(MainActivity.this, OTPActivity.class);
                intent.putExtra("codeOTP", codeGenere);
                intent.putExtra("numTel", numTel);

                //Envoi de l'OTP par sms
                new EnvoiOTP().execute(numTel,codeGenere);


                startActivity(intent);




            }else {
                //Si numero existant dans la base
                pbMain.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Numero trouvé", Toast.LENGTH_SHORT).show();
                Log.i("Numero existant", "Numero existant");
                //Popup
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Numero de télephone deja existant\r\nVoulez vous vous connecter sur ce telephone? -->"+ Build.BRAND + " " + Build.MODEL +"\r\nCliquer sur OK vous deconnectera de votre ancien telephone")
                        .setTitle("Numéro existant")
                        .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // CANCEL

                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //OK
                                Toast.makeText(MainActivity.this, "Les données sont en cours de traitement...", Toast.LENGTH_SHORT).show();
                                Otp otp = new Otp();

                                String codeGenere = otp.generateCode(TailleOTP);;
                                Log.i("Code generé", codeGenere);
                                //Envoi des infos vers l'activité suivante
                                Intent intent = new Intent(MainActivity.this, OTPActivity.class);
                                intent.putExtra("codeOTP", codeGenere);
                                intent.putExtra("numTel", numTel);

                                //Envoi de l'OTP par sms
                                new EnvoiOTP().execute(numTel,codeGenere);

                                startActivity(intent);
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog1 =  builder.create();
                dialog1.setIcon(R.drawable.ic_warning_black_24dp);
                dialog1.show();



            }


        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings != null){
                if (strings.length > 0) {
                    //Toast.makeText(MainActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                    pbMain.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(MainActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(MainActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();

            }

            pbMain.setVisibility(View.INVISIBLE);


        }

    }


    //fonction d'envoi de OTP vers un numero
    private class EnvoiOTP extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... strings) {

            String API = "http://5.189.171.167:8080/SmsInfo/moovci/smsc/sendBulksms";

        String msisdn = strings[0];
        String CodeOTP = strings[1];

        String message = "Votre code de validation est : "+CodeOTP+ " . Merci d'utiliser "+ getResources().getString(R.string.app_name);

            String reponse = null;
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            try {
                //tableau de numero
                jsonArray.put(msisdn);

                //Objet JSON
                //jsonObject.put("msisdnList",jsonArray);
                jsonObject.put("msisdnList",jsonArray);
                jsonObject.put("message",message);
                jsonObject.put("sender","MyVibes");
                Log.i("objetJson", String.valueOf(jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String json = String.valueOf(jsonObject);
            //
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(API)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponseMessage", response.toString());
                reponse = response.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return reponse;
        }
    }




    //Fonctions pour le menu

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    } */
}
