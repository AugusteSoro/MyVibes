package com.kse.vas.myvibes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    private static final String PREFS = "PREFS";
    private static final String PREFS_NUM = "PREFS_NUM";
    private static final String PREFS_OTP = "PREFS_OTP";
    private static final String PREFS_PSEUDO = "PREFS_PSEUDO";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        final Intent intentPseudo = new Intent(SplashScreenActivity.this, PseudoActivity.class);
        final Intent intentServiceView = new Intent(SplashScreenActivity.this, ServiceViewActivity.class);
        //Verifier si l'utilisateur existe dans le contexte de l'application
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        /* Vider SharedPreferences*/
        //sharedPreferences.edit().clear().commit();
        if (sharedPreferences.contains(PREFS_NUM) && sharedPreferences.contains(PREFS_OTP) && sharedPreferences.contains(PREFS_PSEUDO)) {


            String num = sharedPreferences.getString(PREFS_NUM, null);
            String OTP = sharedPreferences.getString(PREFS_OTP, null);
            String pseudo = sharedPreferences.getString(PREFS_PSEUDO, null);
            //Toast.makeText(this, "Bienvenue : " + pseudo + ",Votre numero : "+ num + " OTP: " + OTP, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Bienvenue : " + pseudo + ",Votre numero : "+ num , Toast.LENGTH_LONG).show();
            startActivity(intentServiceView);
            finish();
        }
        else if (sharedPreferences.contains(PREFS_NUM) && sharedPreferences.contains(PREFS_OTP)) {
            startActivity(intentPseudo);
        }
        else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            },SPLASH_TIME_OUT);


        }





    }

}
