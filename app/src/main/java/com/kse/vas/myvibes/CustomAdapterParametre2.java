package com.kse.vas.myvibes;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dognime on 12/07/18.
 */

public class CustomAdapterParametre2 extends BaseAdapter{

    Context context;
    List<Parametre2> genererParametre = new ArrayList<>();
    LayoutInflater inflate;

    public CustomAdapterParametre2(Context context, List<Parametre2> genererParametre) {
        this.context = context;
        this.genererParametre = genererParametre;
        inflate = LayoutInflater.from(context);
    }

    public void add(Parametre2 object) {
        genererParametre.add(object);
    }

    @Override
    public int getCount() {
        return genererParametre.size();
    }

    @Override
    public Object getItem(int position) {
        return this.genererParametre.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Parametre2 messageObj = (Parametre2) getItem(position);

        convertView = inflate.inflate(R.layout.item_parametre2, null);

        TextView tvTitre = convertView.findViewById(R.id.tvTitreParam);
        ImageView ivIcon = convertView.findViewById(R.id.ivIconParam);

        tvTitre.setText(messageObj.getTitre());
        ivIcon.setImageResource(messageObj.getDrawable());



        return convertView;
    }


}
