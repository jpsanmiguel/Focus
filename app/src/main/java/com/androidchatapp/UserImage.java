package com.androidchatapp;

import android.graphics.Bitmap;

import com.bumptech.glide.RequestBuilder;

public class UserImage {

    String username;
    int numberImage;
    final String image;

    public UserImage(String username, int numberImage, String image)
    {
        this.username = username;
        this.image = image;
        this.numberImage = numberImage;
    }

    public String getUsername()
    {
        return username;
    }

    public int getNumberImage() {
        return numberImage;
    }
}
