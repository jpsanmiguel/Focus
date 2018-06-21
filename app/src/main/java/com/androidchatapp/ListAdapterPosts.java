package com.androidchatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.RequestBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ListAdapterPosts extends BaseAdapter {

    Context context;
    private final ArrayList<String> names;
    private final ArrayList<UserImage> userImages;
    private final ArrayList<String> fullUser;
    private final ArrayList<JSONObject> contents;
    private final ArrayList<RequestBuilder<Bitmap>> images;
    private final ArrayList<Post> posts;
    String score = "";

    public ListAdapterPosts(Context context, ArrayList<JSONObject> contents, ArrayList<String> fullUser, ArrayList<RequestBuilder<Bitmap>> images, ArrayList<String> names, ArrayList<UserImage> userImages, ArrayList<Post> posts) {
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.fullUser = fullUser;
        this.names = names;
        this.contents = contents;
        this.images = images;
        this.userImages = userImages;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return fullUser.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_muro_one_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxt);
            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.aVersiontxt);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.appIconIV);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
            getUser(posts.get(position).getUsername());
        try {
            viewHolder.txtName.setText(posts.get(position).getContent().getString("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (posts.get(position).getDistance() != 0) {


                String string =posts.get(position).getFullUser() + " " + posts.get(position).getDistance() / 1000 + "km";
                viewHolder.txtVersion.setText(string);

        } else {
                viewHolder.txtVersion.setText(posts.get(position).getFullUser());
        }
        posts.get(position).getImage().into(viewHolder.icon);

        viewHolder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("username", posts.get(position).getUsername());
                context.startActivity(intent);
            }
        });

        viewHolder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(context, PostLayout.class);
                    intent.putExtra("content", posts.get(position).getContent().getString("content"));
                    intent.putExtra("image", posts.get(position).getUserImage().image);
                    intent.putExtra("username", posts.get(position).getUsername());
                    intent.putExtra("locationDistance", posts.get(position).getDistance() / 1000 + "km");
                    context.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.txtVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(context, PostLayout.class);
                    intent.putExtra("content", posts.get(position).getContent().getString("content"));
                    intent.putExtra("image", posts.get(position).getUserImage().image);
                    intent.putExtra("username", posts.get(position).getUsername());
                    intent.putExtra("locationDistance", posts.get(position).getDistance() / 1000 + "km");
                    context.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        return convertView;
    }

    public void getUser(final String user) {

        String url = "https://androidchatapp2-6b313.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject obj = new JSONObject(s);

                    JSONObject userJson = obj.getJSONObject(user);
                    score = Double.parseDouble(userJson.getString("score")) + "";

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);

    }


    private static class ViewHolder {

        TextView txtName;
        TextView txtVersion;
        ImageView icon;

    }

}