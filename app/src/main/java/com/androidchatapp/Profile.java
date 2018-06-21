package com.androidchatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Profile extends AppCompatActivity {

    ImageView profilePic;
    TextView username;
    TextView degree;
    TextView university;
    TextView teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = (ImageView) findViewById(R.id.profilePic);
        username = (TextView) findViewById(R.id.username);
        degree = (TextView) findViewById(R.id.degree);
        university = findViewById(R.id.university);
        teacher = findViewById(R.id.teaches);


        Glide.with(getApplicationContext()).load(UserDetails.imagePath).into(profilePic);
        username.setText(UserDetails.username + " Rating: " + round(UserDetails.qualification,2) + " ☆)");
        degree.setText(UserDetails.degree);
        teacher.setText(UserDetails.teachs);
        university.setText(UserDetails.u);
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
