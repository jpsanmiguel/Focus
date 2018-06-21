package com.androidchatapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.bumptech.glide.RequestBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class Post {

    private String username;
    private UserImage userImage;
    private String fullUser;
    private JSONObject content;
    private RequestBuilder<Bitmap> image;
    private float distance;


    public Post(String username, UserImage userImage, String fullUser, JSONObject content, RequestBuilder<Bitmap> image, float distance) {
        this.username = username;
        this.userImage = userImage;
        this.fullUser = fullUser;
        this.content = content;
        this.image = image;
        this.distance = distance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserImage getUserImage() {
        return userImage;
    }

    public void setUserImage(UserImage userImage) {
        this.userImage = userImage;
    }

    public String getFullUser() {
        return fullUser;
    }

    public void setFullUser(String fullUser) {
        this.fullUser = fullUser;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public RequestBuilder<Bitmap> getImage() {
        return image;
    }

    public void setImage(RequestBuilder<Bitmap> image) {
        this.image = image;
    }

    public float getDistance()
    {
        return distance;
    }

    public void setDistance(float distance)
    {
        this.distance = distance;
    }

    public int compareToNear(@NonNull Post o) {
        float distanceInMeters1 = 0;
        float distanceInMeters2 = 0;
        try {
            String latitude1 = this.getContent().getString("latitude");
            String longitude1 = this.getContent().getString("longitude");
            String latitude2 = o.getContent().getString("latitude");
            String longitude2 = o.getContent().getString("longitude");
            Location loc1 = new Location("");
            loc1.setLatitude(Double.parseDouble(latitude1));
            loc1.setLongitude(Double.parseDouble(longitude1));

            Location loc2 = new Location("");
            loc2.setLatitude(Double.parseDouble(latitude2));
            loc2.setLongitude(Double.parseDouble(longitude2));

            Location locUser = new Location("");
            locUser.setLatitude(Double.parseDouble(UserDetails.latitude));
            locUser.setLongitude(Double.parseDouble(UserDetails.longitude));
            //distance = (distanceInMeters1/1000) + "km";
            distanceInMeters1 = loc1.distanceTo(locUser)*1000000;
            distanceInMeters2 = loc2.distanceTo(locUser)*1000000;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return (int) this.getDistance() - (int)o.getDistance();
    }

    public int compareToFar(@NonNull Post o) {
        float distanceInMeters1 = 0;
        float distanceInMeters2 = 0;
        try {
            String latitude1 = this.getContent().getString("latitude");
            String longitude1 = this.getContent().getString("longitude");
            String latitude2 = o.getContent().getString("latitude");
            String longitude2 = o.getContent().getString("longitude");
            Location loc1 = new Location("");
            loc1.setLatitude(Double.parseDouble(latitude1));
            loc1.setLongitude(Double.parseDouble(longitude1));

            Location loc2 = new Location("");
            loc2.setLatitude(Double.parseDouble(latitude2));
            loc2.setLongitude(Double.parseDouble(longitude2));

            Location locUser = new Location("");
            locUser.setLatitude(Double.parseDouble(UserDetails.latitude));
            locUser.setLongitude(Double.parseDouble(UserDetails.longitude));
            //distance = (distanceInMeters1/1000) + "km";
            distanceInMeters1 = loc1.distanceTo(locUser)*1000000;
            distanceInMeters2 = loc2.distanceTo(locUser)*1000000;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -((int) this.getDistance() - (int)o.getDistance());
    }
}
