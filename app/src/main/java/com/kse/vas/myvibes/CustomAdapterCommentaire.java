package com.kse.vas.myvibes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dognime on 23/04/18.
 */

public class CustomAdapterCommentaire extends BaseAdapter {

    Context context;
    //String[] message;
    List<Message> messageArrayList = new ArrayList<>();
    //String[] dateCourante;
    LayoutInflater inflate;

    //String tableauMessage[][];

    public CustomAdapterCommentaire(Context context, List<Message> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
        inflate = LayoutInflater.from(context);
    }

    public void add(Message object) {
        messageArrayList.add(object);
    }

    @Override
    public int getCount() {
        return messageArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.messageArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message messageObj = (Message) getItem(position);

        convertView = inflate.inflate(R.layout.item_message, null);

        LinearLayout linearLayout = convertView.findViewById(R.id.LinearLayoutMessage);


        linearLayout.setBackgroundResource(R.drawable.bulle_discution_entrante);
        TextView message1 = convertView.findViewById(R.id.tvMessage);
        TextView dateCourante1 = convertView.findViewById(R.id.timestamp);


        //message1.setText(message[position]);
        //dateCourante1.setText(dateCourante[position]);

        message1.setText(messageObj.message);
        dateCourante1.setText(messageObj.dateCourante);



        return convertView;
    }


}
