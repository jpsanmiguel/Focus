package com.androidchatapp;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.firebase.client.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class Wall extends AppCompatActivity {

    ArrayList<RequestBuilder<Bitmap>> images;

    ArrayList<String> contents;

    ArrayList<String> usernames;

    ArrayList<String> users;

    ArrayList<UserImage> usersImages;

    FirebaseStorage storage;
    StorageReference storageReference;

    ListView lView;

    ListAdapter lAdapter;

    ImageView perfilPrincipal;

    Button postButton;

    EditText postText;

    private Button chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muro);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //perfilPrincipal.setImageBitmap(getBitmapFromURL(UserDetails.imagePath.toString()));

        images = new ArrayList<>();
        contents = new ArrayList<>();
        usernames = new ArrayList<>();
        users = new ArrayList<>();
        usersImages = new ArrayList<>();
        perfilPrincipal = (ImageView) findViewById(R.id.perfilprincipal);
        Glide.with(getApplicationContext()).load(UserDetails.imagePath).into(perfilPrincipal);
        getAllPosts();

        Firebase.setAndroidContext(this);

        postButton = (Button) findViewById(R.id.post);
        postText = (EditText) findViewById(R.id.postText);

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

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postText.getText().toString().equals("")){
                    postText.setError("can't be blank");
                }
                else
                {
                final ProgressDialog pd = new ProgressDialog(Wall.this);
                pd.setMessage("Loading...");
                pd.show();
                Calendar calendar = Calendar.getInstance();


                String day;
                String month;
                String year = ""+calendar.get(Calendar.YEAR);
                int hourBefore = (calendar.get(Calendar.HOUR_OF_DAY) + 24 -5) % 24;
                String hour;
                String minute;
                if(calendar.get(Calendar.DAY_OF_MONTH) < 10 ){ day = "0" + calendar.get(Calendar.DAY_OF_MONTH);}else{ day = "" + calendar.get(Calendar.DAY_OF_MONTH);}
                if((calendar.get(Calendar.MONTH)+1) < 10 ){ month = "0" + (calendar.get(Calendar.MONTH)+1);}else{ month = "" + (calendar.get(Calendar.MONTH)+1);}
                year = year.substring(year.length()-2);
                if(hourBefore < 10 ){ hour = "0" + hourBefore;}else{ hour = "" + hourBefore;}
                if(calendar.get(Calendar.MINUTE) < 10 ){ minute = "0" + calendar.get(Calendar.MINUTE);}else{ minute = "" + calendar.get(Calendar.MINUTE);}
                final String postName = UserDetails.username + ";" + day + month + year + ";" + hour + ":" + minute;

                String url = "https://androidchatapp2-6b313.firebaseio.com/posts.json";

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Firebase reference = new Firebase("https://androidchatapp2-6b313.firebaseio.com/posts");

                        try {
                            JSONObject obj = new JSONObject(s);

                            if (!obj.has(postName)) {
                                reference.child(postName).child("content").setValue(postText.getText().toString());
                                Toast.makeText(Wall.this, "Succesfully posted!", Toast.LENGTH_LONG).show();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            } else {
                                Toast.makeText(Wall.this, "You can only post once in a while", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pd.dismiss();
                    }



                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError );
                        pd.dismiss();
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(Wall.this);
                rQueue.add(request);
            }}});

    }

    public void getAllPosts() {

        String urlPosts = "https://androidchatapp2-6b313.firebaseio.com/posts.json";
        String urlImages = "https://androidchatapp2-6b313.firebaseio.com/users.json";

        final ProgressDialog pd = new ProgressDialog(Wall.this);
        pd.setMessage("Loading...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, urlPosts, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccessPosts(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(Wall.this);
        rQueue.add(request);


        request = new StringRequest(Request.Method.GET, urlImages, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccessImages(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);

            }
        });

        pd.dismiss();

        rQueue = Volley.newRequestQueue(Wall.this);
        rQueue.add(request);

    }

    public void doOnSuccessImages(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";
            int num = 0;
            while (i.hasNext()) {
                key = i.next().toString();

                if (users.contains(key)) {
                    RequestBuilder<Bitmap> request = Glide.with(getApplicationContext()).asBitmap().load(obj.getJSONObject(key).getString("profilePic"));
                    UserImage userImage = new UserImage(key, num);
                    num++;
                    images.add(request);
                    usersImages.add(userImage);
                }

            }

            lAdapter = new ListAdapter(Wall.this, contents, usernames, images, users, usersImages);

            lView.setAdapter(lAdapter);

            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Toast.makeText(Wall.this, contents.get(i), Toast.LENGTH_SHORT).show();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void doOnSuccessPosts(String s) {
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";
            while (i.hasNext()) {
                key = i.next().toString();

                String[] parts = key.split(";");

                String username = parts[0];
                users.add(username);
                String date = parts[1].substring(0,1) + "/" + parts[1].substring(2,3) + "/" + parts[1].substring(4,5) + " " + parts[2];
                contents.add(obj.getJSONObject(key).getString("content"));
                usernames.add(username + "\t" + date);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}