package com.example.mostridatasca.com.example.mostridatasca.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Player {
    private String name;
    private String img;

    // costruttore
    public Player(String name, String img) {
        this.name = name;
        this.img = img;
    }

    public Player(JSONObject jsonObjectPlayer){
        try {
            this.name = jsonObjectPlayer.getString("username");
            this.img = jsonObjectPlayer.getString("img");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // getter
    public String getName() {
        return name;
    }
    public String getImg() {
        return img;
    }

    //setter
    public void setName(String name) {
        this.name = name;
    }
    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return name+", "+img;
    }
}
