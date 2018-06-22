package com.kse.vas.myvibes;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by dognime on 11/04/18.
 */

public class FonctionOTPUnitTest {

    @Test
    public void generateCode() throws Exception {
        int taille = 6;
        String testOtp = Otp.generateCode(taille);
        Assert.assertEquals("Test String Otp.generateCode(int)",taille,testOtp.length());
    }


}
