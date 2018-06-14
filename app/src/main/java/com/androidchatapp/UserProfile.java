package com.androidchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Iterator;

public class UserProfile extends AppCompatActivity {

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


        Intent launchingIntent = getIntent();
        username.setText(launchingIntent.getStringExtra("username"));
        getInfo();

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
                username.setText(user);
                degree.setText(obj.getJSONObject(user).getString("profilePic"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
