package com.kse.vas.myvibes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Parametre2Activity extends AppCompatActivity {

    private ListView lvParametre;
    CustomAdapterParametre2 customAdapterParametre2;
    SharedPreferences sharedPreferences;
    TextView tvNomUser;
    EditText etChangeName;
    Button btnEdit;

    String ipOffline = ConfigApp.ipOffline;
    String ipOnline = ConfigApp.ipOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametre2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lvParametre = findViewById(R.id.lvParametre);
        tvNomUser = findViewById(R.id.tvNomUser);
        etChangeName = findViewById(R.id.etChangePseudo);
        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = String.valueOf(btnEdit.getText());
                if (nom.equals("EDIT")){
                    tvNomUser.setVisibility(View.GONE);
                    etChangeName.setVisibility(View.VISIBLE);
                    btnEdit.setText("OK");

                }else if (nom.equals("OK")){
                    String newName = String.valueOf(etChangeName.getText());

                    //Verification de champ vide
                    boolean cancel = false;
                    View focusView = null;
                    if (TextUtils.isEmpty(newName.trim())) {
                        etChangeName.setError(getString(R.string.error_field_required));
                        focusView = etChangeName;
                        cancel = true;
                    }else {
                        tvNomUser.setVisibility(View.VISIBLE);
                        etChangeName.setVisibility(View.GONE);
                        Toast.makeText(Parametre2Activity.this, "Modification en cours merci de patienter...", Toast.LENGTH_SHORT).show();
                        //Tache pour modifier le nom de l'utilisateur
                        new RequestClientOnline().execute(newName);

                        //Enregistrer dans sharedPreferences
                        sharedPreferences = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
                        sharedPreferences
                                    .edit()
                                    .putString("PREFS_PSEUDO", newName)
                                    .apply();

                        Toast.makeText(Parametre2Activity.this, "Pseudo modifié", Toast.LENGTH_SHORT).show();


                        tvNomUser.setText(newName);
                        etChangeName.setText("");
                        btnEdit.setText("EDIT");
                    }
                    if (cancel) {
                        // There was an error; don't attempt login and focus the first
                        // form field with an error.
                        focusView.requestFocus();
                    }




                }

            }
        });

        /*savedInstanceState.putInt("tvNomUser",tvNomUser.getVisibility());
        savedInstanceState.putInt("etChangeName",etChangeName.getVisibility());
        savedInstanceState.putString("btnEdit",String.valueOf(btnEdit.getText()));*/

        //Recuperer le Pseudo de l'utilisateur
        sharedPreferences = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        String nomUser = sharedPreferences.getString("PREFS_PSEUDO", null);
        //Afficher le pseudo  de l'utilisateur
        tvNomUser.setText(nomUser);



        //Obtenir liste des parametres
        final List<Parametre2> parametres = genererParametre();

        customAdapterParametre2 = new CustomAdapterParametre2(Parametre2Activity.this,parametres);
        lvParametre.setAdapter(customAdapterParametre2);


        lvParametre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String titre = parametres.get(position).getTitre();
                String description = parametres.get(position).getDescription();

                switch (position){
                    case 0:
                        popup(titre,description);

                        break;
                    case 1:
                        popup(titre,description);

                        break;
                    case 2:
                        popup(titre,description);

                        break;
                }
            }
        });



    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Toast.makeText(this, "Restore", Toast.LENGTH_SHORT).show();

        /*int tvNomUser1 = savedInstanceState.getInt("tvNomUser");
        int etChangeName1 = savedInstanceState.getInt("etChangeName");
        String btnEdit1 = savedInstanceState.getString("btnEdit");

        tvNomUser.setVisibility(tvNomUser1);
        etChangeName.setVisibility(etChangeName1);
        btnEdit.setText(btnEdit1);*/




    }

    private List<Parametre2> genererParametre(){
        List<Parametre2> parametres = new ArrayList<Parametre2>();
        parametres.add(new Parametre2(R.drawable.ic_notifications_black_24dp,"Informations",getResources().getString(R.string.param_info)));
        parametres.add(new Parametre2(R.drawable.ic_info_black_24dp,"A propos de nous",getResources().getString(R.string.param_apropos)));
        parametres.add(new Parametre2(R.drawable.baseline_help_black_24,"Aide",getResources().getString(R.string.param_aide)));
        return parametres;
    }


    public void popup(String titre,String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Parametre2Activity.this);
        builder1.setMessage(message)
                .setTitle(titre)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // OK

                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog1 =  builder1.create();
        dialog1.show();
    }

    private class EnvoiDonnées extends AsyncTask<String, String, Message[]> {

        @Override
        protected Message[] doInBackground(String... params) {

            String valeur = params[0];
            String newPseudo = params[1];
            JSONObject jsonObject = new JSONObject();

            try {
                //Parser l'objet existant
                JSONObject reponseBody = new JSONObject(valeur);
                int abonneid = reponseBody.getInt("abonneid");
                String abonnemsisdn = reponseBody.getString("abonnemsisdn");
                //String abonnepseudo = reponseBody.getString("abonnepseudo");
                String abonneotp = reponseBody.getString("abonneotp");
                //String abonnephoto = reponseBody.getString("abonnephoto");
                String abonnedatesouscription = reponseBody.getString("abonnedatesouscription");
                //String abonnestatus = reponseBody.getString("abonnestatus");
                //String abonneenable = reponseBody.getString("abonneenable");

                //Creation de l'objet pour la modification
                jsonObject.put("abonneid",abonneid);
                jsonObject.put("abonnemsisdn",abonnemsisdn);
                jsonObject.put("abonnepseudo",newPseudo);
                jsonObject.put("abonneotp",abonneotp);
                //jsonObject.put("abonnephoto",abonnephoto) ;
                jsonObject.put("abonnedatesouscription",abonnedatesouscription);
                jsonObject.put("abonnestatus","Actif");
                jsonObject.put("abonneenable",true);



                Log.i("objetJson", String.valueOf(jsonObject));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                String valeur1 = null;

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
                        valeur1 = "erreur";
                        publishProgress(valeur1);
                    }
                    else if (messageReponse == 200){
                        valeur1 = "reussite";
                        publishProgress(valeur1);

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
                Toast.makeText(Parametre2Activity.this, R.string.enr_echoue, Toast.LENGTH_LONG).show();
            }
            else if (values[0] == "reussite"){
                Toast.makeText(Parametre2Activity.this, R.string.enr_reussi, Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected void onPostExecute(Message[] messages) {
            super.onPostExecute(messages);

        }



    }


    //Obtenir les informations client connecté
    private class RequestClientOnline extends AsyncTask<String, String, String[]>
    {

        String newPseudo;

        @Override
        protected String[] doInBackground(String... params) {

            newPseudo = params[0];
            //Recuperer le Pseudo de l'utilisateur
            sharedPreferences = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
            String num = sharedPreferences.getString("PREFS_NUM", null);
            String API = ipOffline+"/abonnerest/listmsisdnabonne/"+num+"";

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
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String valeur = values[0];
            new EnvoiDonnées().execute(valeur,newPseudo);


        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

        }

    }

}
