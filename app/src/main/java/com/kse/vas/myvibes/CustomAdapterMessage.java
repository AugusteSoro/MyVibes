package com.kse.vas.myvibes;


import android.content.Context;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by dognime on 09/03/18.
 */

public class CustomAdapterMessage extends BaseAdapter {

    String ipOffline = ConfigApp.ipOffline;
    String ipOnline = ConfigApp.ipOnline;


    Context context;
    String nomService;
    List<Publication> publicationArrayList = new ArrayList<>();
    LayoutInflater inflate;


    public CustomAdapterMessage(Context context,String nomService, List<Publication> publicationArrayList) {
        this.context = context;
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

        Publication messageObj = (Publication) getItem(position);


        convertView = inflate.inflate(R.layout.item_publication, null);

        CardView cardView = convertView.findViewById(R.id.cvPublication);

        TextView nomService1 = convertView.findViewById(R.id.tvNomService);
        TextView message1 = convertView.findViewById(R.id.tvMessage1);
        TextView dateCourante1 = convertView.findViewById(R.id.timestamp1);

        nomService1.setText(nomService);
        message1.setText(messageObj.getPublicationContenu());
        dateCourante1.setText(messageObj.getPublicationDate().substring(0,19));

        return convertView;
    }






}





