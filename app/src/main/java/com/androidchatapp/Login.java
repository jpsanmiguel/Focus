package com.androidchatapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Login extends AppCompatActivity implements PermissionUtils.PermissionResultCallback {


    //
    TextView registerUser;
    EditText username, password;
    Button loginButton;
    String user, pass;

    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;

    boolean isPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        registerUser = (TextView)findViewById(R.id.register);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.loginButton);

        permissionUtils=new PermissionUtils(Login.this);

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user = username.getText().toString();
                pass = password.getText().toString();

                if(user.equals("")){
                    username.setError("No puede ser vacío");
                }
                else if(pass.equals("")){
                    password.setError("No puede ser vacío");
                }
                else{
                    String url = "https://androidchatapp2-6b313.firebaseio.com/users.json";
                    final ProgressDialog pd = new ProgressDialog(Login.this);
                    pd.setMessage("Cargando...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            if(s.equals("null")){
                                Toast.makeText(Login.this, "Usuario no encontrado", Toast.LENGTH_LONG).show();
                            }
                            else{
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if(!obj.has(user)){
                                        Toast.makeText(Login.this, "Usuario no encontrado", Toast.LENGTH_LONG).show();
                                    }
                                    else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                        UserDetails.username = user;
                                        UserDetails.password = pass;
                                        UserDetails.degree = obj.getJSONObject(user).getString("degree");
                                        UserDetails.u = obj.getJSONObject(user).getString("uni");
                                        UserDetails.teachs = obj.getJSONObject(user).getString("teacher");
                                        UserDetails.qualification = Double.parseDouble(obj.getJSONObject(user).getString("score"));
                                        UserDetails.numQualifications = Integer.parseInt(obj.getJSONObject(user).getString("numQual"));
                                        boolean teachs = false;
                                        if(obj.getJSONObject(user).getString("teacher").equals("Dicta monitoria :)"))
                                        {
                                            teachs = true;
                                        }
                                        UserDetails.teaches = teachs;
                                        UserDetails.imagePath = obj.getJSONObject(user).getString("profilePic");
                                        UserDetails.json = obj;
                                        Intent i = new Intent(Login.this, Wall.class);
                                        i.putExtra("yaTieneUbicacion", false);
                                        startActivity(i);

                                    }
                                    else {
                                        Toast.makeText(Login.this, "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }
                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                    rQueue.add(request);
                }

            }
        });
    }

    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
        isPermissionGranted=true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }

    public void showToast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
