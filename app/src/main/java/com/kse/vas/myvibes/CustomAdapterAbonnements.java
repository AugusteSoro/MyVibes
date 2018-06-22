package com.kse.vas.myvibes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dognime on 09/03/18.
 */

public class CustomAdapterAbonnements extends BaseAdapter {

    Context context;
    String[] nom;
    String[] description;
    int[] image;
    LayoutInflater inflate;



    public CustomAdapterAbonnements(Context context, String[] nom, String[] description, int[] image) {
        this.context = context;
        this.nom = nom;
        this.description = description;
        this.image = image;
        inflate = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return nom.length;
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

        convertView = inflate.inflate(R.layout.activity_list_view, null);

        ImageView drapeau = convertView.findViewById(R.id.imageView);
        TextView textViewNom = convertView.findViewById(R.id.textView1);
        TextView textViewDescription = convertView.findViewById(R.id.textViewDescription);

        drapeau.setImageResource(image[position]);
        textViewNom.setText(nom[position]);
        textViewDescription.setText(description[position]);



        return convertView;
    }
}
