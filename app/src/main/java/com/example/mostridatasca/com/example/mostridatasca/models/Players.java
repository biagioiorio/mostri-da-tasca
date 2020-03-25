package com.example.mostridatasca.com.example.mostridatasca.models;

public class Players {
    private String name;
    private String img;

    // costruttore
    public Players(String name, String img) {
        this.name = name;
        this.img = img;
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
