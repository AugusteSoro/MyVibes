package com.kse.vas.myvibes;

import android.graphics.drawable.Drawable;

/**
 * Created by dognime on 12/07/18.
 */

public class Parametre2 {

    private int drawable;
    private String titre;
    private String description;

    public Parametre2(int drawable, String titre, String description) {
        this.drawable = drawable;
        this.titre = titre;
        this.description = description;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
