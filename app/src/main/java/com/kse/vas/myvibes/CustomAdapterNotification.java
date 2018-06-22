package com.kse.vas.myvibes;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by dognime on 07/05/18.
 */

public class CustomAdapterNotification extends BaseAdapter {

    Intent intent;
    int notificationid;
    int publicationid;
    boolean notificationLike;
    boolean notificationVoter;
    LayoutInflater inflate;
    Context context;

    public CustomAdapterNotification(Context context, Intent intent,int notificationid, int publicationid, boolean notificationLike, boolean notificationVoter) {
        this.context = context;
        this.intent = intent;
        this.notificationid = notificationid;
        this.publicationid = publicationid;
        this.notificationLike = notificationLike;
        this.notificationVoter = notificationVoter;
        inflate = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflate.inflate(R.layout.item_publication, null);

        CardView cardView = convertView.findViewById(R.id.cvPublication);


        final ImageButton ibLike = convertView.findViewById(R.id.ibLike);
        final ImageButton ibVote = convertView.findViewById(R.id.ibVote);


        final RatingBar rbVote1 = convertView.findViewById(R.id.rbVote);

        //Affichage de l'icone de like en fonctiondes infos de la BD


        if (notificationLike == true){
            ibLike.setImageResource(R.drawable.ic_favorite_black_36dp);
        }else {
            ibLike.setImageResource(R.drawable.ic_favorite_border_black_36dp);
        }

        //Evenement au clic du bouton Like
        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationLike == true){
                    ibLike.setImageResource(R.drawable.ic_favorite_border_black_36dp);
                    new notifLike().execute();
                }else {
                    ibLike.setImageResource(R.drawable.ic_favorite_black_36dp);
                    new notifLike().execute();
                }

            }
        });


        //ibLike.setImageResource(R.drawable.ic_favorite_border_black_36dp);
        //ibVote.setImageResource(R.drawable.ic_star_border_black_36dp);

        //Evenement au clic du bouton de vote
        ibVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rbVote1.setVisibility(View.VISIBLE);


            }
        });

        return convertView;
    }
}

    class notifLike extends AsyncTask<CustomAdapterNotification, String, Message[]> {

    String ip = "192.168.1.11";

    @Override
    protected Message[] doInBackground(CustomAdapterNotification... params) {

        int notificationid = params[0].notificationid;
        int publicationid = params[0].publicationid;
        boolean notificationlike = params[0].notificationLike;
        //boolean notificationVoter = params[0].notificationVoter;

        Date now = new Date();

        //DateFormat dateformatter = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");;
        //String formattedDate = dateformatter.format(now);
        String formattedDate = dateformatter.format(now);
        Log.i("DateNow", formattedDate);

        DateFormat timeformatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        String formattedTime = timeformatter.format(now);
        Log.i("TimeNow", formattedTime);

        JSONObject jsonObject = new JSONObject();
        boolean like;


        try {
            jsonObject.put("notificationid",notificationid);
            if (notificationlike == true){
                like = false;
                jsonObject.put("notificationlike",like);
            }else {
                like = true;
                jsonObject.put("notificationlike",like);
            }
            jsonObject.put("clientdatecreation",formattedDate);



            Log.i("objetJson", String.valueOf(jsonObject));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        String valeur = null;

        //URL
        String API = "http://"+ip+":9092/notificationrest/updatenotif";

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

        return new Message[0];
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        //pbChargement.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Message[] messages) {
        super.onPostExecute(messages);

    }



}
