package com.androidchatapp;

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
    private final  ArrayList<RequestBuilder<Bitmap>> images;
    private final ArrayList<Post> posts;

    public ListAdapterPosts(Context context, ArrayList<JSONObject> contents, ArrayList<String> fullUser, ArrayList<RequestBuilder<Bitmap>> images, ArrayList<String> names, ArrayList<UserImage> userImages, ArrayList<Post> posts){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.fullUser = fullUser;
        this.names = names;
        this.contents = contents;
        this.images = images;
        this.userImages = userImages;
        this.posts = posts;
        MergeSort ob = new MergeSort(posts);
        ob.sort();
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

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(posts.get(position).getFullUser());
        try {
            viewHolder.txtVersion.setText(posts.get(position).getContent().getString("content"));
        } catch (JSONException e) {
            e.printStackTrace();
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
        return convertView;
    }

    private void sortPosts()
    {

    }

    private static class ViewHolder {

        TextView txtName;
        TextView txtVersion;
        ImageView icon;

    }

}