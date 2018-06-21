package com.androidchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Class extends AppCompatActivity  {

    TextView username;
    TextView content;
    TextView locationDistance;
    EditText qualification;
    double finalScore, score;
    int finalNumQual, numQual;
    Button submit, updateClassInfo;
    String user, className;
    boolean classActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        username = (TextView) findViewById(R.id.username);
        content = (TextView) findViewById(R.id.content);

        final Intent launchingIntent = getIntent();
        locationDistance = findViewById(R.id.locationDistance);
        submit = findViewById(R.id.submit);
        updateClassInfo = findViewById(R.id.updateClassInfo);
        qualification = findViewById(R.id.qualification);

        user = launchingIntent.getStringExtra("username");
        username.setText("Clase ofrecida por: " + user);
        content.setText("Contenido: " + launchingIntent.getStringExtra("content"));
        className = launchingIntent.getStringExtra("className");
        locationDistance.setText("Esta clase se encuentra a " + launchingIntent.getStringExtra("locationDistance") + " de ti. ");
        submit.setEnabled(false);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUser();


            }
        });

        updateClassInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("content", launchingIntent.getStringExtra("content"));
                intent.putExtra("image", launchingIntent.getStringExtra("image"));
                intent.putExtra("username", launchingIntent.getStringExtra("username"));
                intent.putExtra("locationDistance", launchingIntent.getStringExtra("locationDistance"));
                intent.putExtra("className", launchingIntent.getStringExtra("className"));
                finish();
                startActivity(intent);
            }
        });
        classActive();

    }

    public void classActive() {

        String url = "https://androidchatapp2-6b313.firebaseio.com/classes.json";


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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

        RequestQueue rQueue = Volley.newRequestQueue(Class.this);
        rQueue.add(request);

    }

    public void doOnSuccessClass(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            JSONObject classJson = obj.getJSONObject(className);
            String active = classJson.getString("active");

            if(active.equals("true"))
            {
                classActive = true;
            }
            else
            {
                classActive = false;
            }
            submit.setEnabled(!classActive);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUser() {

        String url = "https://androidchatapp2-6b313.firebaseio.com/users.json";


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccessRetrieve(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(Class.this);
        rQueue.add(request);

    }

    public void doOnSuccessRetrieve(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            JSONObject userJson = obj.getJSONObject(user);
            score = Double.parseDouble(userJson.getString("score"));
            numQual = Integer.parseInt(userJson.getString("numQual"));
            updateUser();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateUser() {

        String url = "https://androidchatapp2-6b313.firebaseio.com/users.json";

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

        RequestQueue rQueue = Volley.newRequestQueue(Class.this);
        rQueue.add(request);

    }

    public void doOnSuccessUpdate(String s) {

        Firebase reference = new Firebase("https://androidchatapp2-6b313.firebaseio.com/users");

            finalNumQual = numQual+1;
            finalScore =(score*numQual + Double.parseDouble(qualification.getText().toString()))/finalNumQual;
            reference.child(user).child("score").setValue(finalScore+"");
            reference.child(user).child("numQual").setValue(finalNumQual+"");
            Toast.makeText(Class.this, "Update successful", Toast.LENGTH_LONG).show();

            Intent i = new Intent(Class.this, Wall.class);
            finish();
            i.putExtra("yaTieneUbicacion", false);
            startActivity(i);
    }


}
