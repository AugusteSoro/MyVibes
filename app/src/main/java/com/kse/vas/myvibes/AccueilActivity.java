package com.kse.vas.myvibes;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
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


public class AccueilActivity extends AppCompatActivity {


    //String[] tableauDeNom = {"Auguste","Louis","Roger","Florentin","TNT","Cedrick"};
    //String[] description = {"Brieve description","Brieve description","Brieve description","Brieve description","Brieve description","Brieve description"};
    int[] drapeau = {R.drawable.showbizz,R.drawable.blatte,R.drawable.kse2,R.drawable.showbizz,R.drawable.showbizz,R.drawable.showbizz};
    int drapeau1 = R.drawable.showbizz;
    int imageIcon = R.drawable.ic_chevron_right_black_24dp;
    GridView gridView;
    String ipOffline = ConfigApp.ipOffline;
    String ipOnline = ConfigApp.ipOnline;

    TextView tvTab;

    //sharedPreferences
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesSouscription;
    private static final String PREFS = "PREFS";
    private static final String PREFS_SOUS = "SOUSCRIPTION";
    private static final String SOUSCRIPTION_USER = "SOUSCRIPTION_USER";
    private static final String PREFS_OTP = "PREFS_OTP";
    private static final String PREFS_NUM = "PREFS_NUM";



    String num;
    String OTP;

    int idClientOnline;
    String otpClientOnline;
    int idOffre;
    int prixOffre;
    int dureeOffre;
    int idSouscription;
    Date oldFinDate;
    int oldDuree;
    int offreduree;
    Intent intent;

    ListView listView1;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Changer le menu toolbar
        //toolbar.inflateMenu(R.menu.menu_service);

        intent = new Intent(AccueilActivity.this,AccueilActivity.class);

        listView1 = (ListView)findViewById(R.id.lvSouscription);


        tvTab = (TextView)findViewById(R.id.tvTab);
        gridView = (GridView) findViewById(R.id.gvService);

        //Traitement pour les onglets
        TabHost mTabHost = (TabHost)findViewById(R.id.tabHost);
        mTabHost.setup();
        //Lets add the first Tab
        TabHost.TabSpec mSpec = mTabHost.newTabSpec("Mes souscriptions");
        mSpec.setContent(R.id.first_Tab);
        mSpec.setIndicator("Mes souscriptions");
        mTabHost.addTab(mSpec);
        //Lets add the second Tab
        mSpec = mTabHost.newTabSpec("Abonnements");
        mSpec.setContent(R.id.second_Tab);
        mSpec.setIndicator("Abonnements");
        mTabHost.addTab(mSpec);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId){
                    case "Mes souscriptions":
                        break;
                    case "Abonnements":
                        gridView.setNumColumns(3);
                        new RequestOneService().execute();
                        //tvTab.setText("Bientôt disponible");
                        break;

                }

            }
        });

        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        num = sharedPreferences.getString(PREFS_NUM, null);


        new RequestClientOnline().execute(num);


        new RequestServiceSouscrit().execute();
        new RequestClient().execute(num);

    }

    @Override
    protected void onResume() {
        super.onResume();


        sharedPreferencesSouscription = getBaseContext().getSharedPreferences(PREFS_SOUS, MODE_PRIVATE);
        String array;
        //Verification d'existence de souscription
        if (sharedPreferencesSouscription.contains(SOUSCRIPTION_USER)) {
            array = sharedPreferencesSouscription.getString(SOUSCRIPTION_USER, null);
            try {
                JSONArray jsonArray = new JSONArray(array);

            //
            final String[] name = new String[jsonArray.length()];
            final String[] compagnie = new String[jsonArray.length()];
            final int[] serviceTableau = new int[jsonArray.length()];

            int i;
            for(i=0; i < jsonArray.length(); i++) {
                JSONArray elementi = jsonArray.getJSONArray(i);
                int serviceid = elementi.getInt(0);
                String servicelibelle = elementi.getString(1);
                String servicecompagnie = elementi.getString(2);
                //recuperer les données dans des tableaux
                serviceTableau[i] = serviceid;
                name[i] = servicelibelle;
                compagnie[i] = servicecompagnie;



                final CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),name,compagnie,drapeau,imageIcon);
                //final CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),name,description,drapeau,imageIcon);
                listView1.setAdapter(customAdapter);
                customAdapter.notifyDataSetChanged();

            }




        //Permettre à l'utilisateur de naviguer sans internet
        listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int idService = serviceTableau[position];

                AlertDialog.Builder builder1 = new AlertDialog.Builder(AccueilActivity.this);
                builder1.setMessage("Voulez vous vraiment vous desinscrire?")

                        .setTitle("Confirmation de desouscription")
                        .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // CANCEL

                            }
                        })
                        .setPositiveButton("CONFIRMER", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // CONFIRMATION
                                Toast.makeText(AccueilActivity.this, "Desouscription en cours...", Toast.LENGTH_SHORT).show();
                                //TODO: faire une asynktask pour supprimer la souscription(desouscription)
                                new SuppprimerSouscription().execute();





                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog1 =  builder1.create();
                dialog1.show();
                return false;
            }
        });

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(AccueilActivity.this,ItemAccueilActivity.class);

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Recuperer les informations de la ligne selectionnée
                String nom = name[position].toString();
                int idService = serviceTableau[position];
                //Enregistrement des infos dans Intent
                intent.putExtra("nom",nom);
                intent.putExtra("idService",idService);
                intent.putExtra("clientIdOnline",idClientOnline);
                Log.i("Idclient", String.valueOf(idClientOnline));


                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);


            }
        });



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

    //Tab1 Mes souscriptions
    //Afficher les données
    private class RequestServiceSouscrit extends AsyncTask<Void, String, String[]>
    {


        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        //ProgressBar progressBar = new ProgressBar(AccueilActivity.this);

        @Override
        protected String[] doInBackground(Void... voids) {

            //URL
            String API = ipOnline+"/servicerest/listDistinctservice/"+num;

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
            progressBar.setVisibility(View.VISIBLE);

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //final ListView listView1 = (ListView)findViewById(R.id.lvSouscription);
            //Appel des informations de la ListView
            String valeur = values[0];
            try {

                JSONArray reponseBody = new JSONArray(valeur);
                final String[] name = new String[reponseBody.length()];
                final String[] compagnie = new String[reponseBody.length()];
                final int[] serviceTableau = new int[reponseBody.length()];

                int i;
                for(i=0; i < reponseBody.length(); i++) {
                    JSONArray elementi = reponseBody.getJSONArray(i);
                    int serviceid = elementi.getInt(0);
                    String servicelibelle = elementi.getString(1);
                    String servicecompagnie = elementi.getString(2);
                    //recuperer les données dans des tableaux
                    serviceTableau[i] = serviceid;
                    name[i] = servicelibelle;
                    compagnie[i] = servicecompagnie;
                    progressBar.setVisibility(View.VISIBLE);

                    //Remplir shared preferences
                    sharedPreferencesSouscription = getBaseContext().getSharedPreferences(PREFS_SOUS, MODE_PRIVATE);
                    JSONArray tableau = null;
                        tableau = new JSONArray();
                        tableau.put(elementi);


                    sharedPreferencesSouscription
                            .edit()
                            .putString(SOUSCRIPTION_USER, String.valueOf(tableau))
                            .apply();


                    final CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),name,compagnie,drapeau,imageIcon);
                    //final CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),name,description,drapeau,imageIcon);
                    listView1.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();

                }

                listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        final int idService = serviceTableau[position];

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AccueilActivity.this);
                        builder1.setMessage("Voulez vous vraiment vous desinscrire?")

                                .setTitle("Confirmation de desouscription")
                                .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // CANCEL

                                    }
                                })
                                .setPositiveButton("CONFIRMER", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // CONFIRMATION
                                        Toast.makeText(AccueilActivity.this, "Desouscription en cours...", Toast.LENGTH_SHORT).show();
                                        //TODO: faire une asynktask pour supprimer la souscription(desouscription)
                                        new SuppprimerSouscription().execute();




                                    }
                                });
                        // Create the AlertDialog object and return it
                        AlertDialog dialog1 =  builder1.create();
                        dialog1.show();
                        return false;
                    }
                });

                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    Intent intent = new Intent(AccueilActivity.this,ItemAccueilActivity.class);

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Recuperer les informations de la ligne selectionnée
                        String nom = name[position].toString();
                        int idService = serviceTableau[position];
                        //Enregistrement des infos dans Intent
                        intent.putExtra("nom",nom);
                        intent.putExtra("idService",idService);
                        intent.putExtra("clientIdOnline",idClientOnline);
                        Log.i("Idclient", String.valueOf(idClientOnline));


                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);


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
                if (strings.length > 0)
                    Toast.makeText(AccueilActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(AccueilActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(AccueilActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();


            progressBar.setVisibility(View.INVISIBLE);


        }

    }


    private class RequestServicetest extends AsyncTask<Void, String, String[]>
    {


        //ProgressBar progressBar = new ProgressBar(AccueilActivity.this);

        @Override
        protected String[] doInBackground(Void... voids) {

            //URL
            String API = ipOnline+"/servicerest/listallservice";

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
            //ListView listView1 = (ListView)findViewById(R.id.lvSouscription);
            gridView = (GridView) findViewById(R.id.gvService);
            //Appel des informations de la ListView
            String valeur = values[0];
            try {

                JSONArray reponseBody = new JSONArray(valeur);
                final int[] tableauId = new int[reponseBody.length()];
                final String[] name = new String[reponseBody.length()];
                final String[] compagnie = new String[reponseBody.length()];

                int i;
                for(i=0; i < reponseBody.length(); i++) {
                    JSONObject elementi = reponseBody.getJSONObject(i);
                    int serviceid = elementi.getInt("serviceid");
                    String servicelibelle = elementi.getString("servicelibelle");
                    String servicecompagnie = elementi.getString("servicecompagnie");
                    tableauId[i] = serviceid;
                    name[i] = servicelibelle;
                    compagnie[i] = servicecompagnie;
                    //Toast.makeText(AccueilActivity.this, "Chargement en cours", Toast.LENGTH_SHORT).show();

                    final CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),name,compagnie,drapeau,imageIcon);
                    gridView.setAdapter(customAdapter);

                }

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    Intent intent = new Intent(AccueilActivity.this,AccueilActivity.class);

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Recuperer les informations de la ligne selectionnée
                        final int idService = tableauId[position];
                        String nom = name[position].toString();
                        //Enregistrement des infos dans Intent
                        intent.putExtra("nom",nom);


                        //popup de confirmation
                        AlertDialog.Builder builder = new AlertDialog.Builder(AccueilActivity.this);
                        builder.setMessage("Choisissez l'offre pour le service " + nom)
                                .setTitle("Confirmation de souscription")

                                .setNegativeButton("SEMAINE", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Semaine
                                        prixOffre = 100;
                                        dureeOffre = 7;
                                        // SEMAINE
                                        new RechercheOffreId().execute(idService,dureeOffre);
                                        //startActivity(intent);

                                    }
                                })

                                .setPositiveButton("JOUR", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Jour
                                        prixOffre = 25;
                                        dureeOffre = 1;
                                        // JOUR
                                        new RechercheOffreId().execute(idService,dureeOffre);
                                        //startActivity(intent);
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
                    Toast.makeText(AccueilActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                    tvTab.setVisibility(View.GONE);
                }
                else
                    Toast.makeText(AccueilActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(AccueilActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();

        }

    }

    //Tab2 Abonnements
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

                final CustomAdapterOneService customAdapter = new CustomAdapterOneService(getApplicationContext(),name,drapeau1);
                gridView.setAdapter(customAdapter);




                gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String nom = name.toString();
                        Toast.makeText(AccueilActivity.this, nom, Toast.LENGTH_LONG).show();
                        return false;
                    }
                });


                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    Intent intentSouscription = new Intent(AccueilActivity.this,AccueilActivity.class);

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Recuperer les informations de la ligne selectionnée
                        final int idService = tableauId;
                        String nom = name.toString();
                        //Enregistrement des infos dans Intent
                        intentSouscription.putExtra("nom",nom);

                        //popup du choix de l'offre
                        AlertDialog.Builder builder = new AlertDialog.Builder(AccueilActivity.this);
                        builder.setMessage("Choisissez l'offre pour le service " + nom)
                                .setTitle("Choix de l'offre")
                                .setNegativeButton("SEMAINE", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // SEMAINE

                                        Toast.makeText(AccueilActivity.this, "Choix de l'offre SEMAINE", Toast.LENGTH_SHORT).show();

                                        //popup de confirmation de facturation
                                        prixOffre = 150;
                                        dureeOffre = 7;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AccueilActivity.this);
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

                                        Toast.makeText(AccueilActivity.this, "Choix de l'offre JOUR", Toast.LENGTH_SHORT).show();

                                        //popup de confirmation de facturation
                                        prixOffre = 25;
                                        dureeOffre = 1;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AccueilActivity.this);
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
                    Toast.makeText(AccueilActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                    tvTab.setVisibility(View.GONE);
                }
                else
                    Toast.makeText(AccueilActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(AccueilActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();


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

                    //final CustomAdapterService customAdapter = new CustomAdapterService(getApplicationContext(),name,description,drapeau);
                    final CustomAdapterService customAdapter = new CustomAdapterService(getApplicationContext(),name,drapeau);
                    gridView.setAdapter(customAdapter);

                }


                gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String nom = name[position].toString();
                        Toast.makeText(AccueilActivity.this, nom, Toast.LENGTH_LONG).show();
                        return false;
                    }
                });


                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    Intent intentSouscription = new Intent(AccueilActivity.this,AccueilActivity.class);

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Recuperer les informations de la ligne selectionnée
                        final int idService = tableauId[position];
                        String nom = name[position].toString();
                        //Enregistrement des infos dans Intent
                        intentSouscription.putExtra("nom",nom);

                        //popup du choix de l'offre
                        AlertDialog.Builder builder = new AlertDialog.Builder(AccueilActivity.this);
                        builder.setMessage("Choisissez l'offre pour le service " + nom)
                                .setTitle("Choix de l'offre")
                                .setNegativeButton("SEMAINE", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // SEMAINE

                                        Toast.makeText(AccueilActivity.this, "Choix de l'offre SEMAINE", Toast.LENGTH_SHORT).show();

                                        //popup de confirmation de facturation
                                        prixOffre = 150;
                                        dureeOffre = 7;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AccueilActivity.this);
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

                                        Toast.makeText(AccueilActivity.this, "Choix de l'offre JOUR", Toast.LENGTH_SHORT).show();

                                        //popup de confirmation de facturation
                                        prixOffre = 25;
                                        dureeOffre = 1;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AccueilActivity.this);
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
                    Toast.makeText(AccueilActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                    tvTab.setVisibility(View.GONE);
                }
                else
                    Toast.makeText(AccueilActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(AccueilActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();





        }

    }

    //Obtenir les informations clients
    private class RequestClient1 extends AsyncTask<Void, String, String[]>
    {

        @Override
        protected String[] doInBackground(Void... voids) {

            //URL
            String API = ipOnline+"/abonnerest/listallabonne";
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

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String valeur = values[0];
            try {

                JSONArray reponseBody = new JSONArray(valeur);
                final String[] clientnum = new String[reponseBody.length()];
                final String[] clientotp = new String[reponseBody.length()];

                int i;
                for(i=0; i < reponseBody.length(); i++) {
                    JSONObject elementi = reponseBody.getJSONObject(i);
                    String clientmsisdn = elementi.getString("abonnemsisdn");
                    String clientotp1 = elementi.getString("abonneotp");
                    clientnum[i] = clientmsisdn;
                    clientotp[i] = clientotp1;
                    //Toast.makeText(AccueilActivity.this, "Chargement en cours", Toast.LENGTH_SHORT).show();

                    //sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                    //String OTP = sharedPreferences.getString(PREFS_OTP, null);
                    //String num = sharedPreferences.getString(PREFS_NUM, null);

                    sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                    OTP = sharedPreferences.getString(PREFS_OTP, null);
                    if (clientmsisdn.contains(num)){
                        if (clientotp1.contains(OTP)){
                            Toast.makeText(AccueilActivity.this, "Authentification reussi", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            //Popup
                            AlertDialog.Builder builder = new AlertDialog.Builder(AccueilActivity.this);
                            builder.setMessage("Vous serez deconnecter pour votre nouvel equipement...")
                                    .setTitle("Deconnexion")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //OK
                                            /* Vider SharedPreferences*/
                                            sharedPreferences.edit().clear().commit();
                                            sharedPreferences.edit().remove("PREFS_LAST_ID").commit();

                                            //returner à l'accueil
                                            Intent intent = new Intent(AccueilActivity.this, SplashScreenActivity.class);
                                            startActivity(intent);
                                            finish(); //fermer l'activité en cours

                                        }
                                    });
                            // Create the AlertDialog object and return it
                            AlertDialog dialog1 =  builder.create();
                            dialog1.show();
                            //Toast.makeText(AccueilActivity.this, "Authentification echoué... vous serez deconnecté", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }else {
                        //Toast.makeText(AccueilActivity.this, "Une erreur est survenue... MSISDN Introuvable, vous serez deconnecté", Toast.LENGTH_SHORT).show();
                    }


                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings != null){
                if (strings.length > 0)
                    Toast.makeText(AccueilActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(AccueilActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(AccueilActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();
        }

    }

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
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            String valeur = values[0];

            if (valeur.equals("erreur")){
                //Si numero inexistant dans la base
                Toast.makeText(AccueilActivity.this, "Erreur", Toast.LENGTH_SHORT).show();

            }else {
                //Si numero existant dans la base

                sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                OTP = sharedPreferences.getString(PREFS_OTP, null);
                if (otpClientOnline.equals(OTP) ){
                    Toast.makeText(AccueilActivity.this, "Authentification reussi", Toast.LENGTH_SHORT).show();
                }else {

                    //Popup
                    AlertDialog.Builder builder = new AlertDialog.Builder(AccueilActivity.this);
                    builder.setMessage("Vous serez deconnecter pour votre nouvel equipement...")
                            .setTitle("Deconnexion")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //OK
                                            /* Vider SharedPreferences*/
                                    sharedPreferences.edit().clear().commit();
                                    //returner à l'accueil
                                    Intent intent = new Intent(AccueilActivity.this, SplashScreenActivity.class);
                                    startActivity(intent);
                                    finish(); //fermer l'activité en cours

                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog dialog1 =  builder.create();
                    dialog1.show();

                }



            }


        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (strings != null){
                if (strings.length > 0) {
                    //Toast.makeText(MainActivity.this, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(AccueilActivity.this, R.string.charg_vide, Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(AccueilActivity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();

            }



        }

    }



    //Obtenir les informations client connecté
    private class RequestClientOnline extends AsyncTask<String, String, String[]>
    {

        @Override
        protected String[] doInBackground(String... params) {

            String numTel = params[0];
            String API = ipOnline+"/abonnerest/listmsisdnabonne/"+numTel+"";

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

                JSONObject reponseBody = new JSONObject(valeur);
                int clientid = reponseBody.getInt("abonneid");
                String clientOTP = reponseBody.getString("abonneotp");
                idClientOnline = clientid;
                otpClientOnline = clientOTP;

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

        }

    }

    /**
     * Recuperer les infos de l'offre
     */
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

                new EnregistrerSouscription().execute(objet);

            } catch (JSONException e) {
                e.printStackTrace();
            }




        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
        }

    }


    //Enregistrer souscription
    private class EnregistrerSouscription extends AsyncTask<String, String, Message[]> {


        @Override
        protected Message[] doInBackground(String... params) {

            String jsonObject = params[0];

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
                Toast.makeText(AccueilActivity.this, R.string.sous_echoue, Toast.LENGTH_LONG).show();
            }
            else if (values[0].contains("true")){
                Toast.makeText(AccueilActivity.this, R.string.sous_reussi, Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }else if (values[0].contains("false")){
                Toast.makeText(AccueilActivity.this, R.string.sous_echoue, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPostExecute(Message[] messages) {
            super.onPostExecute(messages);

        }


    }



    private class SuppprimerSouscription extends AsyncTask<String, String, Message[]> {


        @Override
        protected Message[] doInBackground(String... params) {


            String valeur = null;
            //publishProgress(valeur);

            //URL
            String API = ipOnline+"/souscriptionrest/deleteSous/"+num;


            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API)
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
                Toast.makeText(AccueilActivity.this, R.string.sous_echoue, Toast.LENGTH_LONG).show();
            }
            else if (values[0].contains("true")){
                Toast.makeText(AccueilActivity.this, R.string.sous_suppr, Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().remove("PREFS_LAST_ID").commit();
                sharedPreferencesSouscription.edit().clear().commit();
                listView1.setAdapter(null);

                //


                new RequestServiceSouscrit().execute();
                new RequestOneService().execute();
            }

        }

        @Override
        protected void onPostExecute(Message[] messages) {
            super.onPostExecute(messages);

        }


    }



    //Fonctions pour le menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accueil, menu);
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
                new RequestServiceSouscrit().execute();
                new RequestOneService().execute();
                break;
            case R.id.codeQR:
                startActivity(new Intent(AccueilActivity.this,QrcodeActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }



}
