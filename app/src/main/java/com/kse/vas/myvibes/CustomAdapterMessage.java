package com.kse.vas.myvibes;


import android.content.Context;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * Created by dognime on 09/03/18.
 */

public class CustomAdapterMessage extends BaseAdapter {

    String ipOffline = ConfigApp.ipOffline;
    String ipOnline = ConfigApp.ipOnline;


    Context context;
    String nomService;
    int serviceId;
    List<Publication> publicationArrayList = new ArrayList<>();
    LayoutInflater inflate;


    public CustomAdapterMessage(Context context,int serviceId,String nomService, List<Publication> publicationArrayList) {
        this.context = context;
        this.serviceId = serviceId;
        this.nomService = nomService;
        this.publicationArrayList = publicationArrayList;
        inflate = LayoutInflater.from(context);
    }

    public void add(Publication object) {
        publicationArrayList.add(object);
    }


    //@Override
    public int getCount() {
        return publicationArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.publicationArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //Obtenir les infos de l'objet
        final Publication messageObj = (Publication) getItem(position);

        convertView = inflate.inflate(R.layout.item_publication, null);

        //Recuperer les layouts
        CardView cardView = convertView.findViewById(R.id.cvPublication);

        Button btnCommentaire = convertView.findViewById(R.id.btnCommentaire);

        TextView nomService1 = convertView.findViewById(R.id.tvNomService);
        TextView message1 = convertView.findViewById(R.id.tvMessage1);
        TextView dateCourante1 = convertView.findViewById(R.id.timestamp1);
        TextView heureCourante1 = convertView.findViewById(R.id.timestampHeure);

        ImageView iv7 = convertView.findViewById(R.id.imageView7);

        //Obtenir la date depuis l'objet
        String date = messageObj.getPublicationDate().substring(0,19);
        //Conversion de la date

        //Parse date
        // Formater la date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf3 = new SimpleDateFormat("dd");


        Date DateCreation = null;
        try {
            DateCreation = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int month = DateCreation.getMonth();
        int day = DateCreation.getDay() + 1;

        String hour = sdf2.format(DateCreation);
        String year = sdf1.format(DateCreation);
        String dayDate = sdf3.format(DateCreation);
        //Convertir la date
        ConversionDate conversionDate = new ConversionDate();
        String monthparse = conversionDate.getMois(month);
        String dayparse = conversionDate.getJour(day);
        String dateComplete = dayparse + " "+dayDate+ " "+monthparse+" "+year;
        String heureComplete = hour;


        //Afficher les informations reçues
        nomService1.setText(nomService);
        message1.setText(messageObj.getPublicationContenu());
        Picasso.get().load(new File(messageObj.getPublicationImage())).into(iv7);
        //dateCourante1.setText(messageObj.getPublicationDate().substring(0,19));
        dateCourante1.setText("Réçu à "+heureComplete);
        heureCourante1.setText(dateComplete);

        //Evenement au clic du bouton commentaire
        btnCommentaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Bientôt disponible... " + "Position: "+position, Toast.LENGTH_SHORT).show();
                //TODO: Evenement au click du bouton commentaire
                Intent intent = new Intent(context,CommentaireActivity.class);
                //Obtenir les infos de l'objet
                final Publication messageObj1 = (Publication) getItem(position);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Envoi de l'ID de la publication
                intent.putExtra("Idpublication",messageObj.getPublicationID());
                intent.putExtra("serviceId",serviceId);
                context.getApplicationContext().startActivity(intent);
                //context.startActivity(intent);
            }
        });

        return convertView;
    }






}





