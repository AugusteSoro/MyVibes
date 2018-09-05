package com.kse.vas.myvibes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class BienvenueActivity extends AppCompatActivity {

    TextSwitcher textSwitcher;
    ImageSwitcher imageSwitcher;
    Intent intent;
    FloatingActionButton fab;

    private static final String PREFS = "PREFS";
    private static final String PREFS_NUM = "PREFS_NUM";
    private static final String PREFS_OTP = "PREFS_OTP";
    private static final String PREFS_PSEUDO = "PREFS_PSEUDO";
    SharedPreferences sharedPreferences;


    // Array of String to Show In TextSwitcher
    String textes[] = {"Application Android", "Chat et voix", "Rapide et securisé", "Developpé par KSE", "Commencez"};
    int image[] = {0, 0, 0, 0, R.drawable.smile};
    int messageCount = textes.length;
    // to keep current Index of textID array
    int currentIndex = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Afficher l'application en plein ecran
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bienvenue);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        final Intent intentPseudo = new Intent(BienvenueActivity.this, PseudoActivity.class);
        final Intent intentServiceView = new Intent(BienvenueActivity.this, ServiceViewActivity.class);
        //Verifier si l'utilisateur existe dans le contexte de l'application
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        /* Vider SharedPreferences*/
        //sharedPreferences.edit().clear().commit();
        if (sharedPreferences.contains(PREFS_NUM) && sharedPreferences.contains(PREFS_OTP) && sharedPreferences.contains(PREFS_PSEUDO)) {


            String num = sharedPreferences.getString(PREFS_NUM, null);
            String OTP = sharedPreferences.getString(PREFS_OTP, null);
            String pseudo = sharedPreferences.getString(PREFS_PSEUDO, null);
            Toast.makeText(this, "Bienvenue : " + pseudo + ",Votre numero : "+ num + " OTP: " + OTP, Toast.LENGTH_LONG).show();
            startActivity(intentServiceView);
            finish();
        }
        else if (sharedPreferences.contains(PREFS_NUM) && sharedPreferences.contains(PREFS_OTP)) {
            startActivity(intentPseudo);
        }
        else{


            intent = new Intent(BienvenueActivity.this,MainActivity.class);
            //intent = new Intent(BienvenueActivity.this,CommentaireActivity.class);

            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // TODO Auto-generated method stub
                    currentIndex++;
                    // If index reaches maximum then reset it

                    if (currentIndex == messageCount){
                        fab.setImageResource(R.drawable.ic_chevron_right_black_24dp);
                        startActivity(intent);
                        finish();
                        currentIndex = 0;
                    }
                    else if(currentIndex == (messageCount -1) ){
                        fab.setImageResource(R.drawable.ic_done_all_white_24dp);
                    }
                    else {
                    }
                    textSwitcher.setText(textes[currentIndex]); // set Text in TextSwitcher
                    imageSwitcher.setImageResource(image[currentIndex]);
                }
            });


            textSwitcher = (TextSwitcher)findViewById(R.id.tsBienvenue);
            imageSwitcher = (ImageSwitcher)findViewById(R.id.isBienvenue);


            //TextSwitcher
            // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
            textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

                public View makeView() {
                    // TODO Auto-generated method stub
                    // create a TextView
                    TextView message = new TextView(BienvenueActivity.this);
                    // set the gravity of text to top and center horizontal
                    message.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                    // set displayed text size
                    message.setTextSize(36);
                    //set dispayed text color
                    message.setTextColor(getResources().getColor(R.color.colorNoir));
                    return message;
                }
            });


            imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

                public View makeView() {
                    // TODO Auto-generated method stub
                    // create a ImageView
                    ImageView message = new ImageView(BienvenueActivity.this);

                    return message;
                }
            });

            // Declare in and out animations and load them using AnimationUtils class
            Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
            Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);


            // set the animation type to TextSwitcher
            textSwitcher.setInAnimation(in);
            textSwitcher.setOutAnimation(out);

            //text appear on start
            textSwitcher.setCurrentText("Bienvenue sur MyVibes");

        }



    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

}
