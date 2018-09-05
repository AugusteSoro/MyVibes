package com.kse.vas.myvibes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kse.vas.myvibes.MainActivity.TailleOTP;

public class OTPActivity extends AppCompatActivity {

    EditText textOtp;
    Button bt_cliquer;
    String codeOTP;
    TextView voirOTP;
    String numTel;

    private TextWatcher textWatcher = null;


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

                //Envoi de l'OTP par sms
                new EnvoiOTP().execute(numTel,codeOTP);

                //test
                voirOTP.setText(codeOTP);

            }
        });

        //Evenement à chaque fois que l'utilisateur saisit au clavier
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String codeSaisi = textOtp.getText().toString();
                textOtp.setTextColor(getResources().getColor(R.color.colorNoir));
                Log.i("code", codeSaisi.toString());
                if(codeSaisi.length() == TailleOTP)
                {
                    Toast.makeText(OTPActivity.this, "Verification en cours", Toast.LENGTH_SHORT).show();


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
                        textOtp.setTextColor(getResources().getColor(R.color.colorError));
                    }

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        textOtp.addTextChangedListener(textWatcher);




    }


    //fonction d'envoi de OTP vers un numero
    private class EnvoiOTP extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... strings) {

            String API = "http://5.189.171.167:8080/SmsInfo/moovci/smsc/sendBulksms";

            String msisdn = strings[0];
            String CodeOTP = strings[1];

            String message = "Votre code de validation est : "+CodeOTP+ " . Merci d'utiliser "+ getResources().getString(R.string.app_name);

            String reponse = null;
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            try {
                //tableau de numero
                jsonArray.put(msisdn);

                //Objet JSON
                //jsonObject.put("msisdnList",jsonArray);
                jsonObject.put("msisdnList",jsonArray);
                jsonObject.put("message",message);
                jsonObject.put("sender","MyVibes");
                Log.i("objetJson", String.valueOf(jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String json = String.valueOf(jsonObject);
            //
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(API)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.i("ReponseMessage", response.toString());
                reponse = response.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return reponse;
        }
    }














}
