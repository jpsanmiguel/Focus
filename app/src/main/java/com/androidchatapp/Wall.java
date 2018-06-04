package com.androidchatapp;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Wall extends AppCompatActivity {

    ArrayList<RequestBuilder<Bitmap>> images;

    ArrayList<String> names;

    ArrayList<String> versionNumber;

    FirebaseStorage storage;
    StorageReference storageReference;

    ListView lView;

    ListAdapter lAdapter;

    ImageView perfilPrincipal;

    Bitmap bitmap;

    private Button chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muro);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //perfilPrincipal.setImageBitmap(getBitmapFromURL(UserDetails.imagePath.toString()));

        images = new ArrayList<>();
        names = new ArrayList<>();
        versionNumber = new ArrayList<>();
        perfilPrincipal = (ImageView) findViewById(R.id.perfilprincipal);
        Glide.with(getApplicationContext()).load(UserDetails.imagePath).into(perfilPrincipal);
        getAllImages();


        perfilPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Wall.this, Profile.class);
                startActivity(i);
            }});

        lView = (ListView) findViewById(R.id.usersList);




        chat = (Button) findViewById(R.id.chatButton);


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Wall.this, Users.class);
                startActivity(i);
            }});

    }

    public void getAllImages() {

        String url = "https://androidchatapp2-6b313.firebaseio.com/users.json";



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

        RequestQueue rQueue = Volley.newRequestQueue(Wall.this);
        rQueue.add(request);

    }

    public void doOnSuccess(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";
            final ProgressDialog pd = new ProgressDialog(Wall.this);
            pd.setMessage("Loading...");
            pd.show();
            while (i.hasNext()) {
                key = i.next().toString();

                if (!key.equals(UserDetails.username)) {
                    RequestBuilder<Bitmap> request = Glide.with(getApplicationContext()).asBitmap().load(obj.getJSONObject(key).getString("profilePic"));
                    images.add(request);
                    names.add(key);
                    versionNumber.add(obj.getJSONObject(key).getString("profilePic"));
                }

            }

            pd.dismiss();
            lAdapter = new ListAdapter(Wall.this, names, versionNumber, images);

            lView.setAdapter(lAdapter);

            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Toast.makeText(Wall.this, names.get(i), Toast.LENGTH_SHORT).show();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}