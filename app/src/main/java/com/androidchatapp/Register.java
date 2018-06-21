package com.androidchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class Register extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    private Uri filePath;
    EditText username, password, degree;
    Switch teachs;
    Button registerButton;
    String user, pass, deg, u, teacher;
    Boolean teacherBool;
    TextView login;
    ImageView profilePicture;
    Uri downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        degree = findViewById(R.id.degree);
        teachs = findViewById(R.id.teachs);
        registerButton = (Button) findViewById(R.id.registerButton);
        login = (TextView) findViewById(R.id.login);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);

        Firebase.setAndroidContext(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();
                deg = degree.getText().toString();
                teacherBool = teachs.isChecked();
                if(teacherBool){
                    teacher = "Dicta monitorias :)";
                }else{
                    teacher = "No dicta monitorias :(";
                }

                String[] userParts = user.split("@");
                if (user.equals("")) {
                    username.setError("No puede ser vacío");
                } else if (pass.equals("")) {
                    password.setError("No puede ser vacío");
                } else if (userParts[0].contains(".")) {
                    username.setError("No puede contener punto");
                }else if (pass.length() < 5) {
                    password.setError("Debe ser de al menos 5");
                } else if (deg.equals("")) {
                    degree.setError("No puede ser vacío");
                } else if(!(user.contains("uniandes.edu.co") || user.contains("javeriana.edu.co") || user.contains("urosario.edu.co"))){
                    username.setError("Debe ser un correo institucional");
                }else{
                    final ProgressDialog pd = new ProgressDialog(Register.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    if(user.contains("uniandes.edu.co"))
                    {
                        u = "Universidad de los Andes";
                    }
                    else if(user.contains("javeriana.edu.co"))
                    {
                        u = "Universidad Javeriana";
                    }
                    else if(user.contains("urosario.edu.co"))
                    {
                        u = "Universidad del Rosario";
                    }
                    final String finalUser = userParts[0];
                    String url = "https://androidchatapp2-6b313.firebaseio.com/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://androidchatapp2-6b313.firebaseio.com/users");

                            if (s.equals("null")) {
                                reference.child(finalUser).child("password").setValue(pass);
                                reference.child(finalUser).child("degree").setValue(deg);
                                reference.child(finalUser).child("uni").setValue(u);
                                reference.child(finalUser).child("teacher").setValue(teacher);
                                reference.child(finalUser).child("score").setValue("5");
                                reference.child(finalUser).child("numQual").setValue("1");
                                reference.child(finalUser).child("profilePic").setValue(downloadUrl.toString());
                                Toast.makeText(Register.this, "registration successful", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(finalUser)) {
                                        uploadImage();
                                        reference.child(finalUser).child("password").setValue(pass);
                                        reference.child(finalUser).child("degree").setValue(deg);
                                        reference.child(finalUser).child("uni").setValue(u);
                                        reference.child(finalUser).child("teacher").setValue(teacher);
                                        reference.child(finalUser).child("score").setValue("5");
                                        reference.child(finalUser).child("numQual").setValue("1");
                                        reference.child(finalUser).child("profilePic").setValue(downloadUrl.toString());
                                        Toast.makeText(Register.this, "registration successful", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(Register.this, Login.class));
                                    } else {
                                        Toast.makeText(Register.this, "username already exists", Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

                    RequestQueue rQueue = Volley.newRequestQueue(Register.this);
                    rQueue.add(request);
                }
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();

            try {
                uploadImage();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // Log.d(TAG, String.valueOf(bitmap));

                profilePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        final ProgressDialog pd = new ProgressDialog(Register.this);
        pd.setMessage("Cargando...");
        pd.show();
        if (filePath != null) {
            StorageReference ref = storageReference.child("images/" + user);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(Register.this, "Cargado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Fallo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        pd.dismiss();
    }
}