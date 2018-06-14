package com.androidchatapp;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
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

    LocationManager locationManager;
    Context mContext;
    ProgressDialog pdLocation;
    ArrayList<RequestBuilder<Bitmap>> images;

    ArrayList<JSONObject> contents;

    ArrayList<String> usernames;

    ArrayList<String> users;

    ArrayList<UserImage> usersImages;

    FirebaseStorage storage;
    StorageReference storageReference;

    ListView lView;

    ListAdapterPosts lAdapter;

    ImageView perfilPrincipal;

    Button postButton;

    Button locationButton;

    EditText postText;

    ArrayList<Post> posts;

    private Button chat;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muro);
        Intent launchingIntent = getIntent();
        if(!launchingIntent.getBooleanExtra("yaTieneUbicacion", false))
        {
            mContext=this;
            locationManager=(LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            pdLocation = new ProgressDialog(Wall.this);
            pdLocation.setMessage("Estamos accediendo a tu ubicación :)");
            pdLocation.show();
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);
            isLocationEnabled();
        }

        //Location



        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //perfilPrincipal.setImageBitmap(getBitmapFromURL(UserDetails.imagePath.toString()));

        images = new ArrayList<>();
        contents = new ArrayList<>();
        usernames = new ArrayList<>();
        users = new ArrayList<>();
        usersImages = new ArrayList<>();
        posts = new ArrayList<>();
        perfilPrincipal = (ImageView) findViewById(R.id.perfilprincipal);
        Glide.with(getApplicationContext()).load(UserDetails.imagePath).into(perfilPrincipal);
        getAllPosts();

        Firebase.setAndroidContext(this);

        locationButton = findViewById(R.id.location);
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
                String hour;
                String minute;
                String second;
                if(calendar.get(Calendar.DAY_OF_MONTH) < 10 ){ day = "0" + calendar.get(Calendar.DAY_OF_MONTH);}else{ day = "" + calendar.get(Calendar.DAY_OF_MONTH);}
                if((calendar.get(Calendar.MONTH)+1) < 10 ){ month = "0" + (calendar.get(Calendar.MONTH)+1);}else{ month = "" + (calendar.get(Calendar.MONTH)+1);}
                year = year.substring(year.length()-2);
                if(calendar.get(Calendar.HOUR_OF_DAY)  < 10 ){ hour = "0" + calendar.get(Calendar.HOUR_OF_DAY) ;}else{ hour = "" + calendar.get(Calendar.HOUR_OF_DAY) ;}
                if(calendar.get(Calendar.MINUTE) < 10 ){ minute = "0" + calendar.get(Calendar.MINUTE);}else{ minute = "" + calendar.get(Calendar.MINUTE);}
                if(calendar.get(Calendar.SECOND) < 10 ){ second = "0" + calendar.get(Calendar.SECOND);}else{ second = "" + calendar.get(Calendar.SECOND);}
                final String postName = UserDetails.username + ";" + day + month + year + ";" + hour + ":" + minute + ":" + second;

                String url = "https://androidchatapp2-6b313.firebaseio.com/posts.json";

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Firebase reference = new Firebase("https://androidchatapp2-6b313.firebaseio.com/posts");

                        try {
                            JSONObject obj = new JSONObject(s);

                            if(UserDetails.latitude.equals("") || UserDetails.longitude.equals(""))
                            {
                                pdLocation.setMessage("Estamos accediendo a tu ubicación :)");
                            }
                            else if (!obj.has(postName) && !UserDetails.latitude.equals("") && !UserDetails.longitude.equals("")) {
                                reference.child(postName).child("content").setValue(postText.getText().toString());
                                reference.child(postName).child("latitude").setValue(UserDetails.latitude);
                                reference.child(postName).child("longitude").setValue(UserDetails.longitude);
                                Toast.makeText(Wall.this, "Succesfully posted!", Toast.LENGTH_LONG).show();
                                Intent intent = getIntent();
                                finish();
                                intent.putExtra("yaTieneUbicacion", true);
                                startActivity(intent);
                            } else if(obj.has(postName)){
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

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
                UserDetails.latitude = latitude + "";
                UserDetails.longitude = longitude + "";
            pdLocation.dismiss();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
    }

    public void getAllPosts() {

        String urlPosts = "https://androidchatapp2-6b313.firebaseio.com/posts.json";
        String urlImages = "https://androidchatapp2-6b313.firebaseio.com/users.json";

        final ProgressDialog pd = new ProgressDialog(Wall.this);
        pd.setMessage("Cargando...");
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
                    String fuck = obj.getJSONObject(key).getString("profilePic");
                    RequestBuilder<Bitmap> request = Glide.with(getApplicationContext()).asBitmap().load(obj.getJSONObject(key).getString("profilePic"));
                    UserImage userImage = new UserImage(key, num);
                    num++;
                    images.add(request);
                    usersImages.add(userImage);
                    for(Post post:posts)
                    {
                        if(post.getUsername().equals(key))
                        {
                            post.setUserImage(userImage);
                            post.setImage(request);
                        }
                    }
                }

            }

            lAdapter = new ListAdapterPosts(Wall.this, contents, usernames, images, users, usersImages, posts);

            lView.setAdapter(lAdapter);

            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    try {
                        Toast.makeText(Wall.this, contents.get(i).getString("content"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
                contents.add(obj.getJSONObject(key));
                usernames.add(username + "\t" + date);
                Post post = new Post( username, null, username + "\t" + date, obj.getJSONObject(key), null);
                posts.add(post);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}