package com.androidchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UserProfile extends AppCompatActivity {

    ImageView profilePic;
    TextView username;
    TextView degree;
    TextView university;
    TextView teacher;
    String rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = (ImageView) findViewById(R.id.profilePic);
        username = (TextView) findViewById(R.id.username);
        degree = (TextView) findViewById(R.id.degree);
        university = findViewById(R.id.university);
        teacher = findViewById(R.id.teaches);
        rating ="";
        getInfo();


        Intent launchingIntent = getIntent();

    }

    public void getInfo() {
        String url = "https://androidchatapp2-6b313.firebaseio.com/users.json";

        final ProgressDialog pd = new ProgressDialog(UserProfile.this);
        pd.setMessage("Loading...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);

            }
        });

        pd.dismiss();

        RequestQueue rQueue = Volley.newRequestQueue(UserProfile.this);
        rQueue.add(request);

    }

    public void doOnSuccess(String s) {
        JSONObject obj = UserDetails.json;
        Intent launchingIntent = getIntent();
        String user = launchingIntent.getStringExtra("username");

        try {
            if (obj.has(user)) {
                RequestBuilder<Bitmap> request = Glide.with(getApplicationContext()).asBitmap().load(obj.getJSONObject(user).getString("profilePic"));
                request.into(profilePic);
                rating = obj.getJSONObject(user).getString("score");
                username.setText(user);
                degree.setText(obj.getJSONObject(user).getString("degree"));
                university.setText(obj.getJSONObject(user).getString("uni"));
                teacher.setText(obj.getJSONObject(user).getString("teacher"));
                username.setText(launchingIntent.getStringExtra("username") + " Rating: " + round(Double.parseDouble(rating),2) + " ☆)");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
