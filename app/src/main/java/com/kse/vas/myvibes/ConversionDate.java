package com.kse.vas.myvibes;

/**
 * Created by dognime on 15/03/18.
 */

public class ConversionDate {

    public String getMois(int mois){

        String valeur = null;
        switch (mois){
            case 0:
                valeur = "Janvier";
                break;
            case 1:
                valeur = "Fevrier";
                break;
            case 2:
                valeur = "Mars";
                break;
            case 3:
                valeur = "Avril";
                break;
            case 4:
                valeur = "Mai";
                break;
            case 5:
                valeur = "Juin";
                break;
            case 6:
                valeur = "Juillet";
                break;
            case 7:
                valeur = "Août";
                break;
            case 8:
                valeur = "Septembre";
                break;
            case 9:
                valeur = "Octobre";
                break;
            case 10:
                valeur = "Novembre";
            case 11:
                valeur = "Decembre";
                break;
        }
        return valeur;
    }

    public String getJour(int jour){

        String valeur = null;
        switch (jour){
            case 1:
                valeur = "Dimanche";
                break;
            case 2:
                valeur = "Lundi";
                break;
            case 3:
                valeur = "Mardi";
                break;
            case 4:
                valeur = "Mercredi";
                break;
            case 5:
                valeur = "Jeudi";
                break;
            case 6:
                valeur = "Vendredi";
                break;
            case 7:
                valeur = "Samedi";
                break;
        }
        return valeur;
    }
}
