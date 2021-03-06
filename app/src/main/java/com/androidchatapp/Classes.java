package com.androidchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Iterator;

public class Classes extends AppCompatActivity {

    TextView noClassesText;

    ArrayList<RequestBuilder<Bitmap>> images;

    ArrayList<String> content;

    ArrayList<String> active;

    ListView lView;

    ListAdapter lAdapter;

    int totalUsers = 0;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        noClassesText = (TextView)findViewById(R.id.noUsersText);
        images = new ArrayList<>();
        content = new ArrayList<>();
        active = new ArrayList<>();

        lView = (ListView) findViewById(R.id.usersList);

        pd = new ProgressDialog(Classes.this);
        pd.setMessage("Loading...");
        pd.show();

        String url = "https://androidchatapp2-6b313.firebaseio.com/posts.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(Classes.this);
        rQueue.add(request);


    }

    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";
            final ProgressDialog pd = new ProgressDialog(Classes.this);
            pd.setMessage("Loading...");
            pd.show();
            while (i.hasNext()) {
                key = i.next().toString();

                if (key.split(";")[0].equals(UserDetails.username)) {
                    RequestBuilder<Bitmap> request = Glide.with(getApplicationContext()).asBitmap().load(UserDetails.imagePath);
                    images.add(request);
                    content.add(obj.getJSONObject(key).getString("content"));
                    active.add(UserDetails.username);
                }

            }

            pd.dismiss();
            lAdapter = new ListAdapter(Classes.this, content, active, images, content);

            lView.setAdapter(lAdapter);

            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(Classes.this, PostLayout.class);
                    intent.putExtra("content", content.get(i));
                    intent.putExtra("image", UserDetails.imagePath);
                    intent.putExtra("username", UserDetails.username);
                    intent.putExtra("locationDistance",  "0.0km");

                    startActivity(intent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        pd.dismiss();
    }
}