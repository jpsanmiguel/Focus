package com.androidchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class PostLayout extends AppCompatActivity {

    ImageView profilePic;
    TextView username;
    TextView content;
    TextView locationDistance;
    Button participate;
    Button chatPost, finishClass;
    String user, className, contentStr, postName;

    boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        profilePic = (ImageView) findViewById(R.id.profilePic);
        username = (TextView) findViewById(R.id.username);
        content = (TextView) findViewById(R.id.content);

        final Intent launchingIntent = getIntent();
        locationDistance = findViewById(R.id.locationDistance);
        participate = findViewById(R.id.participate);
        chatPost = findViewById(R.id.chatPost);
        finishClass = findViewById(R.id.finishClass);


        Glide.with(getApplicationContext()).load(launchingIntent.getStringExtra("image")).into(profilePic);
        postName = launchingIntent.getStringExtra("postName");
        user = launchingIntent.getStringExtra("username");
        if (UserDetails.username.equals(user)) {
            user = "ti";
            participate.setVisibility(View.INVISIBLE);
            chatPost.setVisibility(View.INVISIBLE);
            finishClass.setVisibility(View.VISIBLE);
        }
        else
        {
            finishClass.setVisibility(View.INVISIBLE);
        }
        participateClass();
        contentStr = launchingIntent.getStringExtra("content");
        username.setText("Clase ofrecida por: " + user);
        content.setText("Contenido: " + contentStr);
        locationDistance.setText("Esta clase se encuentra a " + launchingIntent.getStringExtra("locationDistance") + " de ti. ");
        className = user + ";" + contentStr + ";" + UserDetails.username;

        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostLayout.this, Class.class);
                intent.putExtra("postName", launchingIntent.getStringExtra("postName"));
                intent.putExtra("content", launchingIntent.getStringExtra("content"));
                intent.putExtra("image", launchingIntent.getStringExtra("image"));
                intent.putExtra("username", launchingIntent.getStringExtra("username"));
                intent.putExtra("locationDistance", launchingIntent.getStringExtra("locationDistance"));
                intent.putExtra("className", className);

                if (UserDetails.username.equals(user)) {
                    participate.setError("");
                    Toast.makeText(PostLayout.this, "Este post es tuyo. \nNo puedes contactarte contigo.", Toast.LENGTH_LONG).show();

                } else {
                    startClass();
                }


                startActivity(intent);
            }
        });
        chatPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetails.chatWith = user;
                startActivity(new Intent(PostLayout.this, Chat.class));

            }
        });

        finishClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishClass();

            }
        });
    }

    public void startClass() {

        String url = "https://androidchatapp2-6b313.firebaseio.com/classes.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccessUpdate(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(PostLayout.this);
        rQueue.add(request);

    }

    public void doOnSuccessUpdate(String s) {

        Firebase reference = new Firebase("https://androidchatapp2-6b313.firebaseio.com/classes");

        reference.child(className).child("active").setValue("true");
        Toast.makeText(PostLayout.this, "Estás registrado para clase :)", Toast.LENGTH_LONG).show();
    }

    public void participateClass() {

        String url = "https://androidchatapp2-6b313.firebaseio.com/posts.json";
        String url2 = "https://androidchatapp2-6b313.firebaseio.com/classes.json";


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccessPost(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(PostLayout.this);
        rQueue.add(request);
        if(active) {
            request = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    doOnSuccessClass(s);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError);

                }
            });

            rQueue = Volley.newRequestQueue(PostLayout.this);
            rQueue.add(request);
        }

    }

    public void doOnSuccessPost(String s) {
        try {

            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";
            while (i.hasNext()) {
                key = i.next().toString();

                String activeStr = obj.getJSONObject(key).getString("active");
                if(activeStr.equals("true")){
                    active = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void doOnSuccessClass(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            JSONObject classJson = obj.getJSONObject(className);
            String active = classJson.getString("active");

            if (active.equals("false")) {
                finishClass.setEnabled(false);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void finishClass() {

        String url = "https://androidchatapp2-6b313.firebaseio.com/classes.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccessFinish(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(PostLayout.this);
        rQueue.add(request);

    }

    public void doOnSuccessFinish(String s) {

        Firebase reference = new Firebase("https://androidchatapp2-6b313.firebaseio.com/posts");

        reference.child(postName).child("active").setValue("false");
        Toast.makeText(PostLayout.this, "Has terminado la clase", Toast.LENGTH_LONG).show();
    }
}
