package com.androidchatapp;

import android.location.Location;

import org.json.JSONException;

import java.util.Comparator;

public class PostComparator implements Comparator<Post> {


    @Override
    public int compare(Post o1, Post o2) {
        float distanceInMeters1 = 0;
        float distanceInMeters2 = 0;
        try {
            String latitude1 = o1.getContent().getString("latitude");
            String longitude1 = o1.getContent().getString("longitude");
            String latitude2 = o1.getContent().getString("latitude");
            String longitude2 = o1.getContent().getString("longitude");
            Location loc1 = new Location("");
            loc1.setLatitude(Double.parseDouble(latitude1));
            loc1.setLongitude(Double.parseDouble(longitude1));

            Location loc2 = new Location("");
            loc2.setLatitude(Double.parseDouble(latitude2));
            loc2.setLongitude(Double.parseDouble(longitude2));

            Location locUser = new Location("");
            locUser.setLatitude(Double.parseDouble(UserDetails.latitude));
            locUser.setLongitude(Double.parseDouble(UserDetails.longitude));
            distanceInMeters1 = loc1.distanceTo(locUser)*1000000;
            distanceInMeters2 = loc2.distanceTo(locUser)*1000000;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (int)distanceInMeters1 - (int)distanceInMeters2;
    }
}
