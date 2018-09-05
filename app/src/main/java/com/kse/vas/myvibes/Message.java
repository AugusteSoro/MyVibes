package com.kse.vas.myvibes;

import java.io.Serializable;

/**
 * Created by dognime on 05/04/18.
 */

public class Message implements Serializable{


    String message;
    String dateCourante;

    public Message(String message,String dateCourante) {
        this.message = message;
        this.dateCourante = dateCourante;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateCourante() {
        return dateCourante;
    }

    public void setDateCourante(String dateCourante) {
        this.dateCourante = dateCourante;
    }

}
