package com.kse.vas.myvibes;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dognime on 20/06/18.
 */

public class CustomAdapterOneService extends BaseAdapter {

    Context context;
    String nom;
    int image;

    LayoutInflater inflate;



    public CustomAdapterOneService(Context context, String nom, int image) {
        this.context = context;
        this.nom = nom;
        this.image = image;
        inflate = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return 1;
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

        convertView = inflate.inflate(R.layout.item_card_view, null);

        ImageView drapeau = convertView.findViewById(R.id.ivAvatarService);
        TextView textViewNom = convertView.findViewById(R.id.nom);
        CardView cvService = convertView.findViewById(R.id.cvService);
        //Animer la CardView
        Animation animationCv = AnimationUtils.loadAnimation(context, R.anim.blink);
        cvService.setAnimation(animationCv);
        //TextView textViewDescription = convertView.findViewById(R.id.description);


        textViewNom.setText(nom);
        drapeau.setImageResource(image);
        //textViewDescription.setText(description[position]);



        return convertView;
    }
}
