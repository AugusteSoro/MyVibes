package com.kse.vas.myvibes;

import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dognime on 09/04/18.
 */

public class Service {

    String nom;
    String description;


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }





    public Service(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }


}
