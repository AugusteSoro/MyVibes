package com.kse.vas.myvibes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
 * {@link AbonnementFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AbonnementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AbonnementFragment extends Fragment {

    int[] drapeau = {R.drawable.showbizz,R.drawable.blatte,R.drawable.kse2,R.drawable.showbizz,R.drawable.showbizz,R.drawable.showbizz};
    int drapeau1 = R.drawable.showbizz;
    int imageIcon = R.drawable.ic_chevron_right_black_24dp;
    GridView gridView;
    String ipOffline = ConfigApp.ipOffline;
    String ipOnline = ConfigApp.ipOnline;
    TextView tvTabFrag;

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
    Intent intentAccueil;
    Context context;

    ProgressBar progressBar;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AbonnementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AbonnementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AbonnementFragment newInstance(String param1, String param2) {
        AbonnementFragment fragment = new AbonnementFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_abonnement, container, false);

        intent = new Intent(getActivity().getApplicationContext(),AccueilActivity.class);
        intentAccueil = new Intent(getActivity().getApplicationContext(),AccueilNavigationActivity.class);

        progressBar = (ProgressBar)view.findViewById(R.id.pbFrag);

        tvTabFrag = (TextView)view.findViewById(R.id.tvTabFrag);
        gridView = (GridView) view.findViewById(R.id.gvService);

        context = view.getContext();


        sharedPreferences = view.getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        num = sharedPreferences.getString(PREFS_NUM, null);


        gridView.setNumColumns(3);
        new RequestOneService().execute();

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


    //Afficher les services
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
            //gridView = getActivity().findViewById(R.id.gvService);

            String valeur = values[0];
            try {

                JSONObject reponseBody = new JSONObject(valeur);

                int serviceid = reponseBody.getInt("serviceid");
                String servicelibelle = reponseBody.getString("servicelibelle");

                final int tableauId = serviceid;
                final String name = servicelibelle;

                final CustomAdapterOneService customAdapter = new CustomAdapterOneService(context,name,drapeau1);
                gridView.setAdapter(customAdapter);




                gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String nom = name.toString();
                        Toast.makeText(context, nom, Toast.LENGTH_LONG).show();
                        return false;
                    }
                });


                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    Intent intentSouscription = new Intent(context,AccueilActivity.class);

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Recuperer les informations de la ligne selectionnée
                        final int idService = tableauId;
                        String nom = name.toString();
                        //Enregistrement des infos dans Intent
                        intentSouscription.putExtra("nom",nom);

                        //showDialog(nom);
                        AlertDialog.Builder myDialog =new AlertDialog.Builder(context);
                        myDialog.setMessage("Choisissez l'offre pour le service " + nom)
                                .setTitle("Confirmation de souscription")
                                .setPositiveButton("JOUR",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        //JOUR

                                        prixOffre = 25;
                                        dureeOffre = 1;
                                        AlertDialog.Builder myDialog =new AlertDialog.Builder(getActivity());
                                        myDialog.setTitle("Confirmation de souscription")
                                        .setMessage("Vous serez facturer de " + prixOffre + " francs CFA");
                                        myDialog.setPositiveButton("CONFIRMER",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                                        int whichButton) {
                                                        // CONFIRMATION
                                                        new VerifSouscriptionExistante().execute(idService,dureeOffre);

                                                    }
                                                });
                                        myDialog.setNegativeButton("ANNULER",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                                        int whichButton) {
                                                        //ANNULER

                                                    }
                                                });
                                        myDialog.create();
                                        myDialog.show();






                                    }
                                });

                        myDialog.setNegativeButton("SEMAINE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        //SEMAINE
                                        prixOffre = 150;
                                        dureeOffre = 7;
                                        AlertDialog.Builder myDialog =new AlertDialog.Builder(getActivity());
                                        myDialog.setTitle("Confirmation de souscription")
                                                .setMessage("Vous serez facturer de " + prixOffre + " francs CFA");
                                        myDialog.setPositiveButton("CONFIRMER",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                                        int whichButton) {
                                                        // CONFIRMATION
                                                        new VerifSouscriptionExistante().execute(idService,dureeOffre);

                                                    }
                                                });
                                        myDialog.setNegativeButton("ANNULER",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                                        int whichButton) {
                                                        //ANNULER

                                                    }
                                                });
                                        myDialog.create();
                                        myDialog.show();
                                    }
                                });

                        myDialog.setNeutralButton("ANNULER", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //ANNULER
                            }
                        });
                        myDialog.create();
                        myDialog.show();




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
                if (strings.length > 0){
                    Toast.makeText(context, R.string.charg_terminer, Toast.LENGTH_LONG).show();
                    tvTabFrag.setVisibility(View.GONE);
                }
                else
                    Toast.makeText(context, R.string.charg_vide, Toast.LENGTH_LONG).show();

            }
            else
                Toast.makeText(context, R.string.enr_echoue, Toast.LENGTH_LONG).show();


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
                Snackbar snackbar;
                snackbar = Snackbar.make(getView().findViewById(R.id.frameAbonnement), reponse, Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                snackbar.show();

            }else {
                //Offre non existante
                Snackbar.make(getView().findViewById(R.id.frameAbonnement), reponse, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                new RechercheOffreId().execute(idService,idOffre);

            }



        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
        }

    }

    /**
     * Recuperer les infos de l'offre
     */
    private class RechercheOffreId extends AsyncTask<Integer, String, String[]>
    {

        String dureeSouscription;

        @Override
        protected String[] doInBackground(Integer... params) {

            int serviceid = params[0];
            offreduree = params[1];
            dureeSouscription = String.valueOf(offreduree);

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

                new EnregistrerSouscription().execute(objet,dureeSouscription);

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
                int messageReponse = response.code();

                //Reponse du serveur
                if (response.isSuccessful()){
                    reponse = response.body().string();
                    Log.i("reponse", reponse);
                }


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
            Log.i("reponse", values[0]);
            if(values[0] == "erreur"){
                Toast.makeText(context, R.string.sous_echoue, Toast.LENGTH_LONG).show();

            }
            else if (values[0].contains("true") ){
                //Toast.makeText(ServiceViewActivity.this, R.string.sous_reussi, Toast.LENGTH_SHORT).show();

                //Creation du message de reussite
                String messageReussite = getResources().getString(R.string.fact_reussi, offre,dateFinSous);

                Toast.makeText(context, messageReussite, Toast.LENGTH_LONG).show();
                startActivity(intentAccueil);

            }else if (values[0].contains("false") ){
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

        public static MyAlertDialogFragment newInstance(String nomService) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putString("nomService", nomService);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String nomService = getArguments().getString("nomService");

            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.baseline_local_offer_black_24)
                    .setTitle("Choix de l'offre")
                    .setMessage("Choisissez l'offre pour le service " + nomService)
                    .setPositiveButton("JOUR",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    doPositiveClick();

                                    AlertDialog.Builder myDialog =new AlertDialog.Builder(getActivity());
                                    myDialog.setTitle("title");
                                    myDialog.setPositiveButton(R.string.common_signin_button_text,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int whichButton) {
                                                    doPositiveClick();
                                                }
                                            });
                                    myDialog.setNegativeButton(R.string.common_signin_button_text,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int whichButton) {
                                                    doNegativeClick();
                                                }
                                            });
                                    myDialog.create();
                                    myDialog.show();



                                }
                            }
                    )
                    .setNegativeButton("SEMAINE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    doNegativeClick();
                                }
                            }
                    )
                    .setNeutralButton("ANNULER", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
        }

    }

    //Alert Dialog fragment
    public static class DialogConfirmationFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int prix, int duree) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("prix", prix);
            args.putInt("duree", duree);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final int prix = getArguments().getInt("prix");
            final int duree = getArguments().getInt("duree");

            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setTitle("Confirmation de souscription")
                    .setMessage("Vous serez facturer de " + prix + " francs CFA")
                    .setPositiveButton("JOUR",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    doPositiveClick();


                                }
                            }
                    )
                    .setNegativeButton("SEMAINE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    doNegativeClick();
                                }
                            }
                    )
                    .setNeutralButton("ANNULER", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
        }

    }

    //Afficher AlertDialog
    void showDialog(String nomService) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(nomService);
        newFragment.show(getFragmentManager(), "dialog");
    }



    //Action au Boutton "JOUR" de AlertDialog
    public static void doPositiveClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    //Action au Boutton "SEMAINE" de AlertDialog
    public static void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }
    //Action au Boutton "ANNULER" de AlertDialog
    public static void doNeutralClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Neutral click!");
    }

}
