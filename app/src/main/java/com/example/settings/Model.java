package com.example.settings;

import android.graphics.Bitmap;

/**
 * Created by mukesh on 18/5/15.
 */
public class Model {
    String name;
    Bitmap imaBitmap;
    String packages;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    String Date;
    String Time;

    public Bitmap getImaBitmap() {
        return imaBitmap;
    }

    public void setImaBitmap(Bitmap imaBitmap) {
        this.imaBitmap = imaBitmap;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return imaBitmap;
    }

    public void setImage(Bitmap imaBitmap) {
        this.imaBitmap = imaBitmap;
    }
}
