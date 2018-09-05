package com.kse.vas.myvibes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SouscriptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SouscriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SouscriptionFragment extends Fragment  {




    //////////////////
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
    Context context;

    ProgressBar progressBar;

    ListView listView1;

    ///////////////////






    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SouscriptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SouscriptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SouscriptionFragment newInstance(String param1, String param2) {
        SouscriptionFragment fragment = new SouscriptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }




    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferencesSouscription = context.getSharedPreferences(PREFS_SOUS, Context.MODE_PRIVATE);
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



                    final CustomAdapter customAdapter = new CustomAdapter(context,name,compagnie,drapeau,imageIcon);
                    //final CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),name,description,drapeau,imageIcon);
                    listView1.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();

                }




                //Permettre à l'utilisateur de naviguer sans internet
                listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        final int idService = serviceTableau[position];

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
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
                                        Toast.makeText(context, "Desouscription en cours...", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(context,ItemAccueilActivity.class);

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
                        //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);


                    }
                });



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_souscription, container, false);
        //
        ///////////
        intent = new Intent(view.getContext(),AccueilActivity.class);

        progressBar = (ProgressBar)view.findViewById(R.id.pbFrag);


        listView1 = (ListView)view.findViewById(R.id.lvSouscription);


        tvTab = (TextView)view.findViewById(R.id.tvTab);
        gridView = (GridView) view.findViewById(R.id.gvService);

        context = view.getContext();



        sharedPreferences = view.getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        //sharedPreferencesSouscription = getActivity().getBaseContext().getSharedPreferences(PREFS_SOUS, Context.MODE_PRIVATE);
        sharedPreferencesSouscription = view.getContext().getSharedPreferences(PREFS_SOUS, Context.MODE_PRIVATE);

        num = sharedPreferences.getString(PREFS_NUM, null);

        new RequestClientOnline().execute(num);


        new RequestServiceSouscrit().execute();
        new RequestClient().execute(num);




        //Afficher alertDialog
        //showDialog("LE TITRE");

        return view;


    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
        if (context instanceof com.kse.vas.myvibes.OnFragmentInteractionListener) {
            Activity mListener = (Activity) getActivity();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    /**Obtenir les informations client connecté
     *
     */
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

    //Afficher les données
    private class RequestServiceSouscrit extends AsyncTask<Void, String, String[]>
    {


        //ProgressBar progressBar = (ProgressBar)getActivity().findViewById(R.id.pbFrag);
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
                    //sharedPreferencesSouscription = getActivity().getSharedPreferences(PREFS_SOUS, Context.MODE_PRIVATE);
                    JSONArray tableau = null;
                    tableau = new JSONArray();
                    tableau.put(elementi);


                    sharedPreferencesSouscription
                            .edit()
                            .putString(SOUSCRIPTION_USER, String.valueOf(tableau))
                            .apply();


                    final CustomAdapter customAdapter = new CustomAdapter(context,name,compagnie,drapeau,imageIcon);
                    //final CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),name,description,drapeau,imageIcon);
                    listView1.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();

                }

                listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        final int idService = serviceTableau[position];
                        final String nom = name[position];
                        //Afficher alertDialog
                        String titre = nom;
                        String tag = "suppression";
                        showDialog(titre, tag);
                        return false;
                    }
                });

                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    Intent intent = new Intent(context,ItemAccueilActivity.class);

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
                        getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);


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
                    Toast.makeText(context, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, R.string.charg_vide, Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(context, R.string.enr_echoue, Toast.LENGTH_LONG).show();


            progressBar.setVisibility(View.INVISIBLE);


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
                Toast.makeText(context, "Erreur", Toast.LENGTH_SHORT).show();

            }else {
                //Si numero existant dans la base

                sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                OTP = sharedPreferences.getString(PREFS_OTP, null);
                if (otpClientOnline.equals(OTP) ){
                    Toast.makeText(context, "Authentification reussi", Toast.LENGTH_SHORT).show();
                }else {

                    //Popup
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Vous serez deconnecter pour votre nouvel equipement...")
                            .setTitle("Deconnexion")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //OK
                                            /* Vider SharedPreferences*/
                                    sharedPreferences.edit().clear().commit();
                                    //returner à l'accueil
                                    Intent intent = new Intent(context, SplashScreenActivity.class);
                                    startActivity(intent);
                                    getActivity().finish(); //fermer l'activité en cours

                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog dialog1 =  builder.create();
                    dialog1.show();

                    //showDialog("");

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
                    Toast.makeText(context, R.string.charg_vide, Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(context, R.string.enr_echoue, Toast.LENGTH_LONG).show();

            }



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
                Toast.makeText(context, R.string.sous_echoue, Toast.LENGTH_LONG).show();
            }
            else if (values[0].contains("true")){
                Toast.makeText(context, "Souscription annulée", Toast.LENGTH_SHORT).show();
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
            gridView = getActivity().findViewById(R.id.gvService);

            String valeur = values[0];
            try {

                JSONObject reponseBody = new JSONObject(valeur);

                int serviceid = reponseBody.getInt("serviceid");
                String servicelibelle = reponseBody.getString("servicelibelle");

                final int tableauId = serviceid;
                final String name = servicelibelle;

                final CustomAdapterOneService customAdapter = new CustomAdapterOneService(getActivity().getApplicationContext(),name,drapeau1);
                gridView.setAdapter(customAdapter);




                gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String nom = name.toString();
                        Toast.makeText(getActivity().getApplicationContext(), nom, Toast.LENGTH_LONG).show();
                        return false;
                    }
                });


                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    Intent intentSouscription = new Intent(getActivity().getApplicationContext(),AccueilActivity.class);

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Recuperer les informations de la ligne selectionnée
                        final int idService = tableauId;
                        String nom = name.toString();
                        //Enregistrement des infos dans Intent
                        intentSouscription.putExtra("nom",nom);

                        //popup du choix de l'offre
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Choisissez l'offre pour le service " + nom)
                                .setTitle("Choix de l'offre")
                                .setNegativeButton("SEMAINE", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // SEMAINE

                                        Toast.makeText(context, "Choix de l'offre SEMAINE", Toast.LENGTH_SHORT).show();

                                        //popup de confirmation de facturation
                                        prixOffre = 150;
                                        dureeOffre = 7;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
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

                                        Toast.makeText(context, "Choix de l'offre JOUR", Toast.LENGTH_SHORT).show();

                                        //popup de confirmation de facturation
                                        prixOffre = 25;
                                        dureeOffre = 1;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
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
                    Toast.makeText(context, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                    tvTab.setVisibility(View.GONE);
                }
                else
                    Toast.makeText(context, R.string.charg_vide, Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(context, R.string.enr_echoue, Toast.LENGTH_LONG).show();


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
                Toast.makeText(context, R.string.sous_echoue, Toast.LENGTH_LONG).show();
            }
            else if (values[0].contains("true")){
                //Toast.makeText(AccueilActivity.this, R.string.sous_reussi, Toast.LENGTH_SHORT).show();

                //Creation du message de reussite
                String messageReussite = getResources().getString(R.string.fact_reussi, offre,dateFinSous);

                Toast.makeText(context, messageReussite, Toast.LENGTH_LONG).show();
                startActivity(intent);

            }else if (values[0].contains("false")){
                Toast.makeText(context, R.string.fact_echoue, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPostExecute(Message[] messages) {
            super.onPostExecute(messages);

        }


    }


    //Alert Dialog fragment
    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(String title, String tag) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("tag", tag);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String title = getArguments().getString("title");
            final String tag = getArguments().getString("tag");
            Log.i("Tag et titre", "Titre :"+title +" et tag :"+tag );

            String message = "Voulez vous vraiment vous desinscrire?";

            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("SUPPRIMER",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    if (tag.equals("suppression")){
                                        // CONFIRMATION
                                        Toast.makeText(getActivity().getApplicationContext(), "Desouscription en cours...", Toast.LENGTH_SHORT).show();
                                        //Supprimer la souscription(desouscription)
                                        supprimeSouscription();
                                    }else if (tag.equals("deconnexion")){

                                    }

                                    //doPositiveClick(title);
                                }
                            }
                    )
                    .setNegativeButton("ANNULER",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //ANNULER
                                    //doNegativeClick(title);
                                }
                            }
                    )
                    .create();
        }

    }

    //Afficher AlertDialog
    void showDialog(String titre, String tag) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(titre,tag);
        newFragment.show(getFragmentManager(), "dialog");
    }

    //Action au Boutton "OK" de AlertDialog
    public static void doPositiveClick(String titre) {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    //Action au Boutton "CANCEL" de AlertDialog
    public static void doNegativeClick(String titre) {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    public static void supprimeSouscription(){
        //Supprimer la souscription(desouscription)
        SouscriptionFragment souscriptionFragment = new SouscriptionFragment(); //Outer class
        SuppprimerSouscription task = souscriptionFragment.new SuppprimerSouscription();
        Log.i("supprimeSouscription", "Souscription en cours de suppression");
        task.execute();
    }








}
