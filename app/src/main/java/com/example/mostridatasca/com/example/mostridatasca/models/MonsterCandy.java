package com.example.mostridatasca.com.example.mostridatasca.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.mostridatasca.R;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class MonsterCandy {
    private String id;
    private String type;
    private String size;
    private String name;
    private double lat;
    private double lon;
    private Bitmap img;

    public MonsterCandy(Context context, String id, String type, String size, String name, double lat, double lon) {
        this.id = id;
        this.type = type;
        this.size = size;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.img = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_marker);
    }

    public MonsterCandy(Context context, JSONObject jsonObjectMoncan){
        try {
            this.id = jsonObjectMoncan.getString("id");
            this.type = jsonObjectMoncan.getString("type");
            this.size = jsonObjectMoncan.getString("size");
            this.name = jsonObjectMoncan.getString("name");
            this.lat = jsonObjectMoncan.getDouble("lat");
            this.lon = jsonObjectMoncan.getDouble("lon");
        }catch(JSONException e) {
            e.printStackTrace();
        }
        this.img = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_marker);
    }

    public LatLng getPosition() {
        LatLng position = new LatLng(this.lat, this.lon);
        return position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        String mcString = "MonsterCandy{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon;
        if (img != null){
            mcString += ", img=S";
        }else{
            mcString += ", img=N";
        }
        mcString += img.toString().substring(0,15);
        return mcString;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public double distanceTo(LatLng latLng){
        return latLng.distanceTo(this.getPosition());
    }

    public boolean isNear(LatLng latLng, double distanza){
        return this.distanceTo(latLng) <= distanza;
    }
}
