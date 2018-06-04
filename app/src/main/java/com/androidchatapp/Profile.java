package com.androidchatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Profile extends AppCompatActivity {

    ImageView profilePic;
    TextView username;
    TextView degree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = (ImageView) findViewById(R.id.profilePic);
        username = (TextView) findViewById(R.id.username);
        degree = (TextView) findViewById(R.id.degree);

        Glide.with(getApplicationContext()).load(UserDetails.imagePath).into(profilePic);
        username.setText(UserDetails.username);
        degree.setText(UserDetails.imagePath);

    }

}
