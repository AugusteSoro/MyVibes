package com.kse.vas.myvibes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import static com.kse.vas.myvibes.MainActivity.TailleOTP;

public class OTPActivity extends AppCompatActivity {

    EditText textOtp;
    Button bt_cliquer;
    String codeOTP;
    TextView voirOTP;
    String numTel;


    //SharedPreferences
    private static final String PREFS = "PREFS";
    private static final String PREFS_NUM = "PREFS_NUM";
    private static final String PREFS_OTP = "PREFS_OTP";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textOtp = (EditText) findViewById(R.id.textOtp);
        //Animation sur Editext OTP
        Animation animationOTP = AnimationUtils.loadAnimation(OTPActivity.this, R.anim.myanimation);
        textOtp.startAnimation(animationOTP);

        bt_cliquer= (Button)findViewById(R.id.bt_cliquer);
        voirOTP = (TextView)findViewById(R.id.voirOTP);


        //Intent pour la prochaine activité

        //recuperation des données de l'activité precedente (MainActivity)
        Intent intentValeur = getIntent();
        codeOTP = intentValeur.getStringExtra("codeOTP");
        numTel = intentValeur.getStringExtra("numTel");

        //test
        voirOTP.setText(codeOTP);

        final Intent intent = new Intent(OTPActivity.this,PseudoActivity.class);



        bt_cliquer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Relancer OTP
                Otp otp = new Otp();
                codeOTP = otp.generateCode(4);
                Log.i("codeRegenerer", codeOTP);

                //test
                voirOTP.setText(codeOTP);

            }
        });


        //Evenement à chaque fois que l'utilisateur saisit au clavier
        textOtp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                String codeSaisi = textOtp.getText().toString();
                Log.i("code", codeSaisi.toString());

                if(codeSaisi.length() == TailleOTP)
                {

                    if (codeSaisi.equals(codeOTP)){

                        //SharedPreferences
                        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                        if (sharedPreferences.contains(PREFS_NUM) && sharedPreferences.contains(PREFS_OTP)) {

                            String num = sharedPreferences.getString(PREFS_NUM, null);
                            String OTP = sharedPreferences.getString(PREFS_OTP, null);

                            Toast.makeText(OTPActivity.this, "Num: " + num + " OTP: " + OTP, Toast.LENGTH_SHORT).show();

                        } else {
                            //si aucun utilisateur n'est sauvegardé, on ajouter
                            sharedPreferences
                                    .edit()
                                    .putString(PREFS_NUM, numTel)
                                    .putString(PREFS_OTP, codeOTP)
                                    .apply();

                            Toast.makeText(OTPActivity.this, "SharedPreferences rempli", Toast.LENGTH_SHORT).show();
                        }

                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(OTPActivity.this, R.string.erreur_otp, Toast.LENGTH_LONG).show();
                    }

                }


                return true;
            }
        });

    }















}
