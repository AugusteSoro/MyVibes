package com.kse.vas.myvibes;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dognime on 21/03/18.
 */

public class Otp {

    /**
     *
     * @param length
     * @return this function generate a random string
     */
    public static String generateCode(int length) {
        //String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String chars = "1234567890";
        String pass = "";
        for (int x = 0; x < length; x++) {
            int i = (int) Math.floor(Math.random() * 10);
            pass += chars.charAt(i);
        }
        return pass;
    }


    //// custom shedule to deletion otp not used after 5min
    public class MySchedule {


        Timer t;
        Timer t2;
        private String codeOTP;
        private String numTel;

        public void RepetAction() {
            t = new Timer();
            t2 = new Timer();
            // this.SavenumberOtp("22540544127", getValuetodelete());
            t.schedule(new MonAction(), 500000);

            t2.schedule(new getvalue(), 600000);

        }

        class MonAction extends TimerTask {
            // int nbrRepetitions = 3;

            public void run() {
                // if (nbrRepetitions > 0) {
                Log.i("Valeur supprimée", getValuetodelete());

                if (getNumbermap().containsKey(getMsisdnvalue())) {
                    getNumbermap().remove(getMsisdnvalue());
                    Log.d("otp expired... deletion", "otp expired... deletion");
                } else {

                    Log.d("otp used ... deleted ", "otp used ... deleted ");
                }

                t.cancel();
            }

        }

        // another task to verify
        class getvalue extends TimerTask {

            public void run() {
                Log.d("valeur supprimée", getValuetodelete());
                if (getNumbermap().containsKey(getMsisdnvalue())) {

                    Log.d("valeur supprimée", "");
                    getNumbermap().remove(getMsisdnvalue());
                    Log.d("otp found", "otp found .... deleted ");

                } else {
                    Log.d("otp deleted", "otp deleted");
                }
                t2.cancel();
            }

        }

        public String getValuetodelete() {
            return codeOTP;
        }

        public void setValuetodelete(String valuetodelete) {
            this.codeOTP = valuetodelete;
        }

        public String getMsisdnvalue() {
            return numTel;
        }

        public void setMsisdnvalue(String msisdnvalue) {
            this.numTel = msisdnvalue;
        }
    }

    private Map<String, String> numbermap = new HashMap<>();


    public Map<String, String> getNumbermap() {
        return numbermap;
    }

    public void setNumbermap(Map<String, String> numbermap) {
        this.numbermap = numbermap;
    }




}
