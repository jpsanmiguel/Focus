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

import java.util.ArrayList;


public class ListAdapter extends BaseAdapter {

    Context context;
    private final ArrayList<String> names;
    private final ArrayList<String> fullUser;
    private final ArrayList<String> contents;
    private final  ArrayList<RequestBuilder<Bitmap>> images;

    public ListAdapter(Context context, ArrayList<String> fullUser, ArrayList<String> contents,  ArrayList<RequestBuilder<Bitmap>> images, ArrayList<String> names){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.fullUser = fullUser;
        this.names = names;
        this.contents = contents;
        this.images = images;
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
            convertView = inflater.inflate(R.layout.activity_muro_one_list_item_chat, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxt);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.appIconIV);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(fullUser.get(position));
        images.get(position).into(viewHolder.icon);


        viewHolder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("username", names.get(position));
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private static class ViewHolder {

        TextView txtName;
        ImageView icon;

    }

}
