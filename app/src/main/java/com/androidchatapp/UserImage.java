package com.androidchatapp;

import android.graphics.Bitmap;

import com.bumptech.glide.RequestBuilder;

public class UserImage {

    String username;
    int numberImage;

    public UserImage(String username, int numberImage)
    {
        this.username = username;
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
