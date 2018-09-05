package com.kse.vas.myvibes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.GregorianCalendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServiceActivity extends AppCompatActivity {

    int[] drapeau = {R.drawable.gbich,R.drawable.blatte,R.drawable.kse2,R.drawable.showbizz,R.drawable.showbizz,R.drawable.showbizz};
    int drapeau1 = R.drawable.showbizz;
    GridView gridView;
    ProgressBar pbService;

    String ipOffline = ConfigApp.ipOffline;
    String ipOnline = ConfigApp.ipOnline;


    //sharedPreferences
    SharedPreferences sharedPreferences;
    private static final String PREFS = "PREFS";
    private static final String PREFS_NUM = "PREFS_NUM";
    private static final String PREFS_OTP = "PREFS_OTP";
    private static final String PREFS_PSEUDO = "PREFS_PSEUDO";

    JSONObject jsonObject;


    String num;
    String OTP;
    String pseudo;
    String photo;

    int prixOffre;
    int dureeOffre;
    int idClientOnline;
    int idOffre;
    int idSouscription;
    Date oldFinDate;
    int oldDuree;
    int offreduree;
    Intent intent;

    private MenuItem menuItem;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent = new Intent(ServiceActivity.this,AccueilActivity.class);

        pbService = findViewById(R.id.pbService);
        gridView = (GridView) findViewById(R.id.gvService);
        gridView.setNumColumns(3);

        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        num = sharedPreferences.getString(PREFS_NUM, null);

        //TODO: Enregistrement d'un nouveau client

        if(ContextCompat.checkSelfPermission(ServiceActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ServiceActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

        //Recuperer l'ID de l'utilisateur en ligne
        new RequestClientOnline().execute(num);

        new RequestOneService().execute();





    }


    //Afficher les données
    private class RequestOneService extends AsyncTask<Void, String, String[]>
    {

        //ProgressBar progressBar = new ProgressBar(AccueilActivity.this);

        @Override
        protected String[] doInBackground(Void... voids) {

            //URL
            String API = ipOnline+"/servicerest/getoneservice/48";

            //String API = "http://"+ip+":8082/emprest/listallemp";
            //String API = "http://192.168.43.186:8080/emprest/listallemp";

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
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbService.setVisibility(View.VISIBLE);

        }



        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            gridView = findViewById(R.id.gvService);

            String valeur = values[0];
            try {

                JSONObject reponseBody = new JSONObject(valeur);

                int serviceid = reponseBody.getInt("serviceid");
                String servicelibelle = reponseBody.getString("servicelibelle");

                final int tableauId = serviceid;
                final String name = servicelibelle;
                //chargement
                pbService.setVisibility(View.VISIBLE);

                final CustomAdapterOneService customAdapter = new CustomAdapterOneService(getApplicationContext(),name,drapeau1);
                gridView.setAdapter(customAdapter);




                gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String nom = name.toString();
                        Toast.makeText(ServiceActivity.this, nom, Toast.LENGTH_LONG).show();
                        return false;
                    }
                });


                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    Intent intentSouscription = new Intent(ServiceActivity.this,AccueilActivity.class);

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Recuperer les informations de la ligne selectionnée
                        final int idService = tableauId;
                        String nom = name.toString();
                        //Enregistrement des infos dans Intent
                        intentSouscription.putExtra("nom",nom);

                        //popup du choix de l'offre
                        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);
                        builder.setMessage("Choisissez l'offre pour le service " + nom)
                                .setTitle("Choix de l'offre")
                                .setNegativeButton("SEMAINE", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // SEMAINE

                                        Toast.makeText(ServiceActivity.this, "Choix de l'offre SEMAINE", Toast.LENGTH_SHORT).show();

                                        //popup de confirmation de facturation
                                        prixOffre = 150;
                                        dureeOffre = 7;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ServiceActivity.this);
                                        builder1.setMessage("Vous serez facturer de " + prixOffre + " francs CFA")
                                                .setTitle("Confirmation de souscription")
                                                .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // CANCEL

                                                    }
                                                })
                                                .setPositiveButton("CONFIRMER", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // CONFIRMATION
                                                        new VerifSouscriptionExistante().execute(idService,dureeOffre);
                                                        //new RechercheOffreId().execute(idService,dureeOffre);

                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog dialog1 =  builder1.create();
                                        dialog1.show();

                                    }
                                })
                                .setPositiveButton("JOUR", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // JOUR

                                        Toast.makeText(ServiceActivity.this, "Choix de l'offre JOUR", Toast.LENGTH_SHORT).show();

                                        //popup de confirmation de facturation
                                        prixOffre = 25;
                                        dureeOffre = 1;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ServiceActivity.this);
                                        builder1.setMessage("Vous serez facturer de " + prixOffre + " francs CFA")
                                                .setTitle("Confirmation de souscription")
                                                .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // CANCEL

                                                    }
                                                })
                                                .setPositiveButton("CONFIRMER", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // CONFIRMATION
                                                        new VerifSouscriptionExistante().execute(idService,dureeOffre);
                                                        //new RechercheOffreId().execute(idService,dureeOffre);


                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog dialog1 =  builder1.create();
                                        dialog1.show();
                                    }
                                })
                                .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // CANCEL
                                    }
                                });
                        // Create the AlertDialog object and return it
                        AlertDialog dialog =  builder.create();
                        dialog.show();


                    }
                });





            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (menuItem != null){
                menuItem.collapseActionView();
                menuItem.setActionView(null);
            }
            if (strings != null){
                if (strings.length > 1){
                    //Toast.makeText(ServiceViewActivity.this, "charg_terminer", Toast.LENGTH_LONG).show();
                    pbService.setVisibility(View.INVISIBLE);

                }
                else
                    Toast.makeText(ServiceActivity.this, "charg_vide", Toast.LENGTH_LONG).show();
                pbService.setVisibility(View.INVISIBLE);


            }
            else
                Toast.makeText(ServiceActivity.this, "enr_echoue", Toast.LENGTH_LONG).show();
            pbService.setVisibility(View.INVISIBLE);





        }

    }

    //Afficher les données
    private class RequestAllService extends AsyncTask<Void, String, String[]>
    {

        //ProgressBar progressBar = new ProgressBar(AccueilActivity.this);

        @Override
        protected String[] doInBackground(Void... voids) {

            //URL
            String API = ipOnline+"/servicerest/listallservice";

            //String API = "http://"+ip+":8082/emprest/listallemp";
            //String API = "http://192.168.43.186:8080/emprest/listallemp";

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
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbService.setVisibility(View.VISIBLE);

        }



        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            gridView = findViewById(R.id.gvService);
            //gridView.setNumColumns(2);

            //Appel des informations de la ListView
            String valeur = values[0];
            try {

                JSONArray reponseBody = new JSONArray(valeur);
                final int[] tableauId = new int[reponseBody.length()];
                final String[] name = new String[reponseBody.length()];
                //final String[] description = new String[reponseBody.length()];
                //final int[] imageService = new int[reponseBody.length()];


                int i;
                for(i=0; i < reponseBody.length(); i++) {
                    JSONObject elementi = reponseBody.getJSONObject(i);

                    int serviceid = elementi.getInt("serviceid");
                    String servicelibelle = elementi.getString("servicelibelle");
                    //String servicedescription = elementi.getString("servicedescription");
                    //int servicephoto = elementi.getInt("servicephoto");
                    tableauId[i] = serviceid;
                    name[i] = servicelibelle;
                    //description[i] = servicedescription;
                    //chargement
                    pbService.setVisibility(View.VISIBLE);

                    //final CustomAdapterService customAdapter = new CustomAdapterService(getApplicationContext(),name,description,drapeau);
                    final CustomAdapterService customAdapter = new CustomAdapterService(getApplicationContext(),name,drapeau);
                    gridView.setAdapter(customAdapter);

                }


                gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String nom = name[position].toString();
                        Toast.makeText(ServiceActivity.this, nom, Toast.LENGTH_LONG).show();
                        return false;
                    }
                });


                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    Intent intentSouscription = new Intent(ServiceActivity.this,AccueilActivity.class);

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Recuperer les informations de la ligne selectionnée
                        final int idService = tableauId[position];
                        String nom = name[position].toString();
                        //Enregistrement des infos dans Intent
                        intentSouscription.putExtra("nom",nom);

                        //popup du choix de l'offre
                        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);
                        builder.setMessage("Choisissez l'offre pour le service " + nom)
                                .setTitle("Choix de l'offre")
                                .setNegativeButton("SEMAINE", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // SEMAINE

                                        Toast.makeText(ServiceActivity.this, "Choix de l'offre SEMAINE", Toast.LENGTH_SHORT).show();

                                        //popup de confirmation de facturation
                                        prixOffre = 150;
                                        dureeOffre = 7;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ServiceActivity.this);
                                        builder1.setMessage("Vous serez facturer de " + prixOffre + " francs CFA")
                                                .setTitle("Confirmation de souscription")
                                                .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // CANCEL

                                                    }
                                                })
                                                .setPositiveButton("CONFIRMER", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // CONFIRMATION
                                                        new RechercheOffreId().execute(idService,dureeOffre);
                                                        //startActivity(intentSouscription);
                                                        //finish();

                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog dialog1 =  builder1.create();
                                        dialog1.show();

                                    }
                                })
                                .setPositiveButton("JOUR", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // JOUR

                                        Toast.makeText(ServiceActivity.this, "Choix de l'offre JOUR", Toast.LENGTH_SHORT).show();

                                        //popup de confirmation de facturation
                                        prixOffre = 25;
                                        dureeOffre = 1;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ServiceActivity.this);
                                        builder1.setMessage("Vous serez facturer de " + prixOffre + " francs CFA")
                                                .setTitle("Confirmation de souscription")
                                                .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // CANCEL

                                                    }
                                                })
                                                .setPositiveButton("CONFIRMER", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // CONFIRMATION

                                                        new RechercheOffreId().execute(idService,dureeOffre);
                                                        //startActivity(intentSouscription);
                                                        //finish();

                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog dialog1 =  builder1.create();
                                        dialog1.show();
                                    }
                                })
                                .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // CANCEL
                                    }
                                });
                        // Create the AlertDialog object and return it
                        AlertDialog dialog =  builder.create();
                        dialog.show();


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
                if (strings.length > 1){
                    //Toast.makeText(ServiceViewActivity.this, "charg_terminer", Toast.LENGTH_LONG).show();
                    pbService.setVisibility(View.INVISIBLE);

                }
                else
                    Toast.makeText(ServiceActivity.this, "charg_vide", Toast.LENGTH_LONG).show();
                pbService.setVisibility(View.INVISIBLE);


            }
            else
                Toast.makeText(ServiceActivity.this, "enr_echoue", Toast.LENGTH_LONG).show();
            pbService.setVisibility(View.INVISIBLE);





        }

    }


    private class EnvoiDonnées extends AsyncTask<Integer, String, Message[]> {

        @Override
        protected Message[] doInBackground(Integer... params) {

            int clientOnline = params[0];

            sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
            if (sharedPreferences.contains(PREFS_NUM) && sharedPreferences.contains(PREFS_OTP)) {

                num = sharedPreferences.getString(PREFS_NUM, null);
                OTP = sharedPreferences.getString(PREFS_OTP, null);
                pseudo = sharedPreferences.getString(PREFS_PSEUDO, null);

                Intent intentValeur = getIntent();
                photo = intentValeur.getStringExtra("photo");
                //photo = intentValeur.getByteArrayExtra("photo");
                Log.i("photo",String.valueOf(photo));



                Log.i("infoUtilisateur", "Num: " + num + " OTP: " + OTP + "Pseudo " + pseudo);
            }

            /*try {

                //arr = new JSONArray(new String(photo));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    arr = new JSONArray(photo);
                }
                Log.i("photo1",String.valueOf(arr));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/


                Date now = new Date();

            //DateFormat dateformatter = DateFormat.getDateInstance(DateFormat.SHORT);
            DateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");;
            //String formattedDate = dateformatter.format(now);
            String formattedDate = dateformatter.format(now);
            Log.i("DateNow", formattedDate);

            DateFormat timeformatter = DateFormat.getTimeInstance(DateFormat.SHORT);
            String formattedTime = timeformatter.format(now);
            Log.i("TimeNow", formattedTime);

            jsonObject = new JSONObject();

            if (clientOnline > 0){

                try {
                    //jsonObject.put("clientid",1);
                    //jsonObject.put("clientid",idClientOnline);
                    jsonObject.put("abonneid",clientOnline);
                    jsonObject.put("abonnemsisdn",num);
                    jsonObject.put("abonnepseudo",pseudo);
                    jsonObject.put("abonneotp",OTP);
                    jsonObject.put("abonnephoto",photo) ;
                    jsonObject.put("abonnedatesouscription",formattedDate);
                    jsonObject.put("abonnestatus","Actif");
                    jsonObject.put("abonneenable",true);



                    Log.i("objetJson", String.valueOf(jsonObject));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                String valeur = null;

                //URL
                String API = ipOnline+"/abonnerest/updateabonne";

                String json = String.valueOf(jsonObject);



                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url(API)
                        .put(body)
                        .build();
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

            }else{


                try {
                    //jsonObject.put("clientid",1);
                    /*if (idClientOnline > 0){
                        jsonObject.put("clientid",idClientOnline);
                    }*/
                    jsonObject.put("abonnemsisdn",num);
                    jsonObject.put("abonnepseudo",pseudo);
                    jsonObject.put("abonneotp",OTP);
                    jsonObject.put("abonnephoto",photo) ;
                    jsonObject.put("abonnedatesouscription",formattedDate);
                    jsonObject.put("abonnestatus","Actif");
                    jsonObject.put("abonneenable",true);



                    Log.i("objetJson", String.valueOf(jsonObject));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                String valeur = null;

                //URL
                String API = ipOnline+"/abonnerest/addabonne";

                String json = String.valueOf(jsonObject);



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

            }



            return new Message[0];
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //pbChargement.setVisibility(View.VISIBLE);
            if(values[0] == "erreur"){
                Toast.makeText(ServiceActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();
            }
            else if (values[0] == "reussite"){
                Toast.makeText(ServiceActivity.this, R.string.enr_reussi, Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected void onPostExecute(Message[] messages) {
            super.onPostExecute(messages);

        }



    }


    //Obtenir les informations client(Id client Online)
    private class RequestClientOnline extends AsyncTask<String, String, String[]>
    {

        @Override
        protected String[] doInBackground(String... params) {

            String numTel = params[0];
            String API = ipOnline+"/abonnerest/listmsisdnabonne/"+numTel;
            //String API = "http://192.168.43.186:8080/emprest/listallemp";

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

            String reponse = null;

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponsePublication", response.toString());
                reponse = response.body().string();

                int codeReponse = response.code();
                if(codeReponse == 500){
                    new EnvoiDonnées().execute(idClientOnline);
                }
                else if (codeReponse == 200){
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

                JSONObject reponseBody = new JSONObject(valeur);
                int clientid = reponseBody.getInt("abonneid");
                idClientOnline = clientid;
                new EnvoiDonnées().execute(idClientOnline);


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

        }

    }

    //Obtenir les informations client(Id client Online)
    private class RequestIdClientOnline extends AsyncTask<String, String, String[]>
    {

        @Override
        protected String[] doInBackground(String... params) {

            String numTel = params[0];
            String API = ipOnline+"/clientrest/listmsisdnclient/"+numTel;
            //String API = "http://192.168.43.186:8080/emprest/listallemp";

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

            String reponse = null;

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponsePublication", response.toString());
                reponse = response.body().string();

                int codeReponse = response.code();
                if(codeReponse == 500){
                    new EnvoiDonnées().execute(idClientOnline);
                }
                else if (codeReponse == 200){
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

                JSONObject reponseBody = new JSONObject(valeur);
                int clientid = reponseBody.getInt("clientid");
                idClientOnline = clientid;


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

        }

    }

    //Enregistrer les informations de souscription
    private class EnrSouscription extends AsyncTask<Integer, String, Message[]> {


        @Override
        protected Message[] doInBackground(Integer... params) {

            int offreDuree = params[0];

            JSONObject jsonObject = new JSONObject();
            JSONObject objetClient = new JSONObject();
            JSONObject objetOffre = new JSONObject();

            if (idSouscription > 0){
                //Mise a jour de la souscription
                try {

                    jsonObject.put("souscriptionid",idSouscription);
                    //objetClient.put("clientid",idClientOnline);
                    objetClient.put("abonneid",idClientOnline);
                    objetOffre.put("offreid",idOffre);
                    jsonObject.put("souscriptionlibelle",num);

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


                    //Ajout de la durée de la souscription à la date de souscription
                    GregorianCalendar gregorianCalendar = new GregorianCalendar();
                    gregorianCalendar.setTime(oldFinDate);
                    gregorianCalendar.add(gregorianCalendar.DAY_OF_YEAR,offreDuree);

                    Log.i("Time", String.valueOf(gregorianCalendar.getTime()));

                    DateFormat finDateFormater = new SimpleDateFormat("yyyy-MM-dd");
                    String formatedFinDate = finDateFormater.format(gregorianCalendar.getTime());
                    String finDate = formatedFinDate;


                    jsonObject.put("souscriptiondebutdate",dateJour);
                    //Nouvelle duree
                    int newDuree = offreDuree + oldDuree ;
                    jsonObject.put("souscriptionduree",newDuree);
                    jsonObject.put("souscriptionfindate",finDate);
                    //jsonObject.putOpt("client",objetClient);
                    jsonObject.putOpt("abonne",objetClient);
                    jsonObject.putOpt("offre",objetOffre);


                    Log.i("objetJsonSouscription", String.valueOf(jsonObject));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String valeur = null;
                publishProgress(valeur);


                //URL
                String API = ipOnline+"/souscriptionrest/updatesouscription";


                String json = String.valueOf(jsonObject);



                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url(API)
                        .put(body)
                        .build();

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


            }else {

                //Ajout de la souscription
                try {

                    //objetClient.put("clientid",idClientOnline);
                    objetClient.put("abonneid",idClientOnline);
                    objetOffre.put("offreid",idOffre);
                    jsonObject.put("souscriptionlibelle",num);

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


                    //Ajout de la durée de la souscription à la date de souscription
                    GregorianCalendar gregorianCalendar = new GregorianCalendar();
                    gregorianCalendar.setGregorianChange(now);
                    gregorianCalendar.add(gregorianCalendar.DAY_OF_YEAR,offreDuree);
                    DateFormat finDateFormater = new SimpleDateFormat("yyyy-MM-dd");
                    String formatedFinDate = finDateFormater.format(gregorianCalendar.getTime());
                    String finDate = formatedFinDate;


                    jsonObject.put("souscriptiondebutdate",dateJour);
                    jsonObject.put("souscriptionduree",offreDuree);
                    jsonObject.put("souscriptionfindate",finDate);
                    //jsonObject.putOpt("client",objetClient);
                    jsonObject.putOpt("abonne",objetClient);
                    jsonObject.putOpt("offre",objetOffre);


                    Log.i("objetJsonSouscription", String.valueOf(jsonObject));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String valeur = null;
                publishProgress(valeur);


                //URL
                String API = ipOnline+"/souscriptionrest/addsouscription";


                String json = String.valueOf(jsonObject);



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


            }



            return new Message[0];
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //pbChargement.setVisibility(View.VISIBLE);
            if(values[0] == "erreur"){
                Toast.makeText(ServiceActivity.this, R.string.sous_echoue, Toast.LENGTH_LONG).show();
            }
            else if (values[0] == "reussite"){
                Toast.makeText(ServiceActivity.this, R.string.sous_reussi, Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }

        }

        @Override
        protected void onPostExecute(Message[] messages) {
            super.onPostExecute(messages);

        }


    }

    private class RechercheOffreId extends AsyncTask<Integer, String, String[]>
    {

        //int offreduree;

        @Override
        protected String[] doInBackground(Integer... params) {

            int serviceid = params[0];
            offreduree = params[1];

            String API = ipOnline+"/offrerest/listobjectoffre/"+serviceid+"/"+offreduree;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API)
                    .build();

            String reponse = null;

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponsePublication", response.toString());
                reponse = response.body().string();
                publishProgress(reponse);
                //publishProgress(String.valueOf(offreduree));

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
                JSONArray jsonArray = new JSONArray(valeur);
                JSONObject objetOffre = jsonArray.getJSONObject(0);

                String objet = String.valueOf(objetOffre);

                new EnregistrerSouscription().execute(objet, String.valueOf(offreduree));

            } catch (JSONException e) {
                e.printStackTrace();
            }




        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
        }

    }

    public String ObtainDate(int dureeSouscription){
        //Ajout de la durée de la souscription à la date de souscription
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(gregorianCalendar.DAY_OF_YEAR,dureeSouscription);
        DateFormat finDateFormater = new SimpleDateFormat("dd-MM-yyyy");
        String formatedFinDate = finDateFormater.format(gregorianCalendar.getTime());
        return formatedFinDate;
    }

    //Enregistrer souscription
    private class EnregistrerSouscription extends AsyncTask<String, String, Message[]> {


        int dureeSouscription;
        String offre;
        String dateFinSous;

        @Override
        protected Message[] doInBackground(String... params) {

            String jsonObject = params[0];
            //Verifier la durée de la souscription
            dureeSouscription = Integer.parseInt(params[1]);
            if (dureeSouscription == 1){
                dateFinSous = ObtainDate(dureeSouscription);
                Log.i("dateFinSous", dateFinSous);
                offre = "Jour";
            }else if (dureeSouscription == 7){
                dateFinSous = ObtainDate(dureeSouscription);
                Log.i("dateFinSous", dateFinSous);
                offre = "Semaine";
            }

            String valeur = null;
                //publishProgress(valeur);


                //URL
                String API = ipOnline+"/souscriptionrest/souscrire/"+num;


                String json = String.valueOf(jsonObject);



                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url(API)
                        .post(body)
                        .build();

                String reponse = null;

                try {
                    Response response = client.newCall(request).execute();
                    Log.i("ReponseMessage", response.toString());
                    Log.i("ReponseMessage", String.valueOf(response.body()));

                    if (response.isSuccessful()){
                        reponse = response.body().string();
                        Log.i("reponse", reponse);
                    }

                    int messageReponse = response.code();
                    if(messageReponse == 404){
                        valeur = "erreur";
                        publishProgress(valeur);
                    }
                    else if (messageReponse == 200){
                        publishProgress(reponse);

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
                Toast.makeText(ServiceActivity.this, R.string.sous_echoue, Toast.LENGTH_LONG).show();
            }
            else if (values[0].contains("true")){
                //Toast.makeText(ServiceActivity.this, R.string.sous_reussi, Toast.LENGTH_SHORT).show();
                //Creation du message de reussite
                String messageReussite = getResources().getString(R.string.fact_reussi, offre,dateFinSous);

                Toast.makeText(ServiceActivity.this, messageReussite, Toast.LENGTH_LONG).show();
                startActivity(intent);

            }else if (values[0].contains("false")){
                //Toast.makeText(ServiceActivity.this, R.string.sous_echoue, Toast.LENGTH_SHORT).show();
                Toast.makeText(ServiceActivity.this, R.string.fact_echoue, Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected void onPostExecute(Message[] messages) {
            super.onPostExecute(messages);

        }


    }




    //Verifier si l'utilisateur à deja une offre active
    private class VerifSouscriptionExistante extends AsyncTask<Integer, String, String>
    {

        int idOffre;
        int idService;

        @Override
        protected String doInBackground(Integer... params) {

            idService = params[0];
            idOffre = params[1];

            String API = ipOnline+"/souscriptionrest/countsouscription/"+num+"/"+idOffre;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API)
                    .build();

            String reponse = null;

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponsePublication", response.toString());
                reponse = response.body().string();
                int messageReponse = response.code();
                if(messageReponse == 404){

                }
                else if (messageReponse == 500){

                }else if (messageReponse == 200){
                    publishProgress(reponse);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String reponse = values[0];
            String message = "Vous avez deja une offre" ;
            if (reponse.contains(message)){
                //Offre deja existante
                Snackbar.make(findViewById(R.id.coordService), reponse, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }else {
                //Offre non existante
                Snackbar.make(findViewById(R.id.coordService), reponse, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                new RechercheOffreId().execute(idService,idOffre);

            }



        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(ServiceActivity.this,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(ContextCompat.checkSelfPermission(ServiceActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ServiceActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }





//Fonctions pour le menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service, menu);
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
            case R.id.rafraichir:
                menuItem = item;
                menuItem.setActionView(R.layout.progressbar);
                menuItem.expandActionView();
                //Tache asynchrone pour le rafraichissement
                new RequestOneService().execute();

                break;
            case R.id.quitter:
                startActivity(new Intent(getApplicationContext(),AccueilActivity.class));
                break;
                /* Vider SharedPreferences*/
            /*case R.id.Vider:
                sharedPreferences.edit().clear().commit();
                sharedPreferences.edit().remove("PREFS_LAST_ID").commit();
                break;*/

        }

        return super.onOptionsItemSelected(item);
    }


}
