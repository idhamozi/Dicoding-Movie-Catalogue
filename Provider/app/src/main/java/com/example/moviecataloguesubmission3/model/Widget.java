package com.example.moviecataloguesubmission3.model;

import android.graphics.Bitmap;

public class Widget {

    private String name;
    private Bitmap imgPoster;

    public Widget(String name, Bitmap imgPoster) {
        this.name = name;
        this.imgPoster = imgPoster;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImgPoster() {
        return imgPoster;
    }

}
