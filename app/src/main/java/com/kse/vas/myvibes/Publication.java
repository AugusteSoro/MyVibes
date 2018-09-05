package com.kse.vas.myvibes;

import java.io.Serializable;

/**
 * Created by dognime on 23/03/18.
 */

public class Publication implements Serializable {

    int publicationID;
    String publicationContenu;
    String publicationImage;
    String publicationVideo;
    String publicationAudio;
    String publicationDate;
    int publicationNbreVue;

    public Publication(int publicationID, String publicationContenu, String publicationDate, String publicationImage) {
        this.publicationContenu = publicationContenu;
        this.publicationDate = publicationDate;
        this.publicationImage = publicationImage;
        this.publicationID = publicationID;
    }

    public Publication(String publicationContenu, String publicationDate, String publicationImage) {
        this.publicationContenu = publicationContenu;
        this.publicationDate = publicationDate;
        this.publicationImage = publicationImage;
    }

    /*public Publication(String publicationContenu, String publicationDate) {
        this.publicationContenu = publicationContenu;
        this.publicationDate = publicationDate;
    }*/

    public int getPublicationID() {
        return publicationID;
    }

    public void setPublicationID(int publicationID) {
        this.publicationID = publicationID;
    }

    public int getPublicationNbreVue() {
        return publicationNbreVue;
    }

    public void setPublicationNbreVue(int publicationNbreVue) {
        this.publicationNbreVue = publicationNbreVue;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getPublicationContenu() {
        return publicationContenu;
    }

    public void setPublicationContenu(String publicationContenu) {
        this.publicationContenu = publicationContenu;
    }

    public String getPublicationImage() {
        return publicationImage;
    }

    public void setPublicationImage(String publicationImage) {
        this.publicationImage = publicationImage;
    }

    public String getPublicationVideo() {
        return publicationVideo;
    }

    public void setPublicationVideo(String publicationVideo) {
        this.publicationVideo = publicationVideo;
    }

    public String getPublicationAudio() {
        return publicationAudio;
    }

    public void setPublicationAudio(String publicationAudio) {
        this.publicationAudio = publicationAudio;
    }

}
