package com.kse.vas.myvibes;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TelephonyActivity extends AppCompatActivity {

    EditText mUssdNumberView;
    private Context mContext;
    Button place_ussd_button;
    public static final String LOG_TAG = "TestUssdActivity";

    private TelephonyManager.UssdResponseCallback mReceiveUssdResponseCallback =
            new TelephonyManager.UssdResponseCallback () {
                @Override
                public void onReceiveUssdResponse(final TelephonyManager telephonyManager,
                                                  String request, CharSequence response) {
                    Log.i(LOG_TAG, "USSD Success: " + request + "," + response);
                    Toast.makeText(mContext, "USSD Response Successly received for code:" + request + "," +
                            response, Toast.LENGTH_SHORT).show();
                }

                public void onReceiveUssdResponseFailed(final TelephonyManager telephonyManager,
                                                        String request, int failureCode) {
                    Log.i(LOG_TAG, "USSD Fail: " + request + "," + failureCode);
                    Toast.makeText(mContext, "USSD Response failed for code:" + request + "," + failureCode, Toast.LENGTH_SHORT).show();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephony);
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

        place_ussd_button = findViewById(R.id.place_ussd_button);
        mUssdNumberView = findViewById(R.id.edNumber);


        place_ussd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeUssdRequest();

            }
        });










    }

    private void placeUssdRequest() {
        String mUssdNumber = mUssdNumberView.getText().toString();
        if (mUssdNumber.equals("") || mUssdNumber == null) {
            mUssdNumber = "*100#";
        }
        final TelephonyManager telephonyManager =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Handler h = new Handler(Looper.getMainLooper());
            Log.i(LOG_TAG, "placeUssdRequest: " + mUssdNumber);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.sendUssdRequest(mUssdNumber, mReceiveUssdResponseCallback, h);
            }
        } catch (SecurityException e) {
            Toast.makeText(mContext, "Verifier les permissions", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}
