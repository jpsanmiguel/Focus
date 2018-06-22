package com.androidchatapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
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
    ArrayList<RequestBuilder<Bitmap>> images;

    ArrayList<JSONObject> contents;

    ArrayList<String> usernames;

    ArrayList<String> users;

    ArrayList<UserImage> usersImages;

    FirebaseStorage storage;
    StorageReference storageReference;

    ListView lView;

    ListAdapterPosts lAdapter;

    ImageView perfilPrincipal, goChat;

    Button postButton, classes;

    Button locationButtonNear;
    Button locationButtonFar;

    EditText postText;
    TextView welcome;

    ArrayList<Post> posts;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muro);

        Intent launchingIntent = getIntent();

        if (!launchingIntent.getBooleanExtra("yaTieneUbicacion", false)) {
            mContext = this;
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

            String locationProvider2 = LocationManager.PASSIVE_PROVIDER;
            Location lastKnownLocation2 = locationManager.getLastKnownLocation(locationProvider2);

            String locationProvider3 = LocationManager.GPS_PROVIDER;
            Location lastKnownLocation3 = locationManager.getLastKnownLocation(locationProvider3);
            if (lastKnownLocation != null) {
                UserDetails.longitude = lastKnownLocation.getLongitude() + "";
                UserDetails.latitude = lastKnownLocation.getLatitude() + "";
            } else if (lastKnownLocation2 != null && UserDetails.longitude == "") {
                UserDetails.longitude = lastKnownLocation2.getLongitude() + "";
                UserDetails.latitude = lastKnownLocation2.getLatitude() + "";
            } else if (lastKnownLocation3 != null && UserDetails.longitude == "") {
                UserDetails.longitude = lastKnownLocation3.getLongitude() + "";
                UserDetails.latitude = lastKnownLocation3.getLatitude() + "";
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerGPS);
                isLocationEnabled();
            }
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
        classes = findViewById(R.id.classes);
        goChat = findViewById(R.id.goChat);
        welcome = findViewById(R.id.welcome);
        welcome.setText("Bienvenido " + UserDetails.username + "!");
        Glide.with(getApplicationContext()).load(UserDetails.imagePath).into(perfilPrincipal);
        getAllPosts();

        Firebase.setAndroidContext(this);

        locationButtonNear = findViewById(R.id.locationNear);
        locationButtonFar = findViewById(R.id.locationFar);
        postButton = (Button) findViewById(R.id.post);
        postText = (EditText) findViewById(R.id.postText);

        locationButtonNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Location loc2 = new Location("");
                loc2.setLatitude(Double.parseDouble(UserDetails.latitude));
                loc2.setLongitude(Double.parseDouble(UserDetails.longitude));
                for (Post post : posts) {
                    String latitude1 = null;
                    String longitude1 = null;
                    try {
                        latitude1 = post.getContent().getString("latitude");
                        longitude1 = post.getContent().getString("longitude");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(Double.parseDouble(latitude1));
                    loc1.setLongitude(Double.parseDouble(longitude1));

                    float distanceInMeters = loc1.distanceTo(loc2);
                    if (distanceInMeters == 0) {
                        post.setDistance(new Float(10));
                    } else {
                        post.setDistance(distanceInMeters);
                    }
                }
                MergeSortPostNear merge = new MergeSortPostNear(posts);
                merge.sort();
                lAdapter = new ListAdapterPosts(Wall.this, contents, usernames, images, users, usersImages, posts);

                lView.setAdapter(lAdapter);

                lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Toast.makeText(Wall.this, posts.get(i).getDistance() / 1000 + "km", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        locationButtonFar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Location loc2 = new Location("");
                loc2.setLatitude(Double.parseDouble(UserDetails.latitude));
                loc2.setLongitude(Double.parseDouble(UserDetails.longitude));
                for (Post post : posts) {
                    String latitude1 = null;
                    String longitude1 = null;
                    try {
                        latitude1 = post.getContent().getString("latitude");
                        longitude1 = post.getContent().getString("longitude");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(Double.parseDouble(latitude1));
                    loc1.setLongitude(Double.parseDouble(longitude1));

                    float distanceInMeters = loc1.distanceTo(loc2);
                    if (distanceInMeters == 0) {
                        post.setDistance(new Float(10));
                    } else {
                        post.setDistance(distanceInMeters);
                    }
                }
                MergeSortPostFar merge = new MergeSortPostFar(posts);
                merge.sort();
                lAdapter = new ListAdapterPosts(Wall.this, contents, usernames, images, users, usersImages, posts);

                lView.setAdapter(lAdapter);

                lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Toast.makeText(Wall.this, posts.get(i).getDistance() / 1000 + "km", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        perfilPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Wall.this, Profile.class);
                startActivity(i);
            }
        });

        goChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Wall.this, Users.class);
                startActivity(i);
            }
        });

        classes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Wall.this, Classes.class);
                startActivity(i);
            }
        });

        lView = (ListView) findViewById(R.id.usersList);




        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postText.getText().toString().equals("")) {
                    postText.setError("No puede ser vacío");
                } else {
                    final ProgressDialog pd = new ProgressDialog(Wall.this);
                    pd.setMessage("Cargando...");
                    pd.show();
                    Calendar calendar = Calendar.getInstance();


                    String day;
                    String month;
                    String year = "" + calendar.get(Calendar.YEAR);
                    String hour;
                    String minute;
                    String second;
                    if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
                        day = "0" + calendar.get(Calendar.DAY_OF_MONTH);
                    } else {
                        day = "" + calendar.get(Calendar.DAY_OF_MONTH);
                    }
                    if ((calendar.get(Calendar.MONTH) + 1) < 10) {
                        month = "0" + (calendar.get(Calendar.MONTH) + 1);
                    } else {
                        month = "" + (calendar.get(Calendar.MONTH) + 1);
                    }
                    year = year.substring(year.length() - 2);
                    if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
                        hour = "0" + calendar.get(Calendar.HOUR_OF_DAY);
                    } else {
                        hour = "" + calendar.get(Calendar.HOUR_OF_DAY);
                    }
                    if (calendar.get(Calendar.MINUTE) < 10) {
                        minute = "0" + calendar.get(Calendar.MINUTE);
                    } else {
                        minute = "" + calendar.get(Calendar.MINUTE);
                    }
                    if (calendar.get(Calendar.SECOND) < 10) {
                        second = "0" + calendar.get(Calendar.SECOND);
                    } else {
                        second = "" + calendar.get(Calendar.SECOND);
                    }
                    final String postName = UserDetails.username + ";" + day + month + year + ";" + hour + ":" + minute + ":" + second;

                    String url = "https://androidchatapp2-6b313.firebaseio.com/posts.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://androidchatapp2-6b313.firebaseio.com/posts");

                            try {
                                JSONObject obj = new JSONObject(s);
                                if (!obj.has(postName) && !UserDetails.latitude.equals("") && !UserDetails.longitude.equals("")) {
                                    reference.child(postName).child("content").setValue(postText.getText().toString());
                                    reference.child(postName).child("latitude").setValue(UserDetails.latitude);
                                    reference.child(postName).child("longitude").setValue(UserDetails.longitude);
                                    reference.child(postName).child("active").setValue("true");
                                    Toast.makeText(Wall.this, "Publicado correctamente!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Wall.this, Wall.class);
                                    long currTime = System.currentTimeMillis();
                                    while(System.currentTimeMillis() - currTime <1000)

                                    startActivity(intent);
                                } else if (obj.has(postName)) {
                                    Toast.makeText(Wall.this, "Tienes que esperar un poco antes de volver a publicar", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            pd.dismiss();
                        }


                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(Wall.this);
                    rQueue.add(request);
                }
            }
        });

    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            UserDetails.latitude = latitude + "";
            UserDetails.longitude = longitude + "";
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

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
        }
    }

    public void getAllPosts() {

        String urlPosts = "https://androidchatapp2-6b313.firebaseio.com/posts.json";
        String urlImages = "https://androidchatapp2-6b313.firebaseio.com/users.json";

        final ProgressDialog pd = new ProgressDialog(Wall.this);
        pd.setMessage("Cargando...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, urlPosts, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccessPosts(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(Wall.this);
        rQueue.add(request);


        request = new StringRequest(Request.Method.GET, urlImages, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                doOnSuccessImages(s);
            }
        }, new Response.ErrorListener() {
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
                    UserImage userImage = new UserImage(key, num, obj.getJSONObject(key).getString("profilePic"));
                    num++;
                    images.add(request);
                    usersImages.add(userImage);
                    for (Post post : posts) {
                        if (post.getUsername().equals(key)) {
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

                    Toast.makeText(Wall.this, posts.get(i).getDistance() + "", Toast.LENGTH_SHORT).show();

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
                String day = parts[1].substring(0, 2);
                String month = parts[1].substring(2, 4);
                String year = parts[1].substring(4, 6);
                String[] hourParts = parts[2].split(":");
                String date = day + "/" + month + "/" + year + " " + hourParts[0] + ":" + hourParts[1];
                contents.add(obj.getJSONObject(key));
                usernames.add(username + "\t" + date);
                Post post = new Post(username, null, username + "\t" + date, obj.getJSONObject(key), null, 0, key);
                posts.add(post);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}