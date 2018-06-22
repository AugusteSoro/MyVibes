package com.kse.vas.myvibes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class OffreActivity extends AppCompatActivity {

    CardView cvSemaine;
    CardView cvJour ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offre);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        cvSemaine = (CardView)findViewById(R.id.cvSemaine);
        cvJour = (CardView)findViewById(R.id.cvJour);

        final TextView textView = (TextView)findViewById(R.id.textView9);

        cvJour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OffreActivity.this, AccueilActivity.class);
                Toast.makeText(OffreActivity.this, "Facturation en cours", Toast.LENGTH_LONG).show();
                startActivity(intent);
                finish();

            }
        });

    }

}
