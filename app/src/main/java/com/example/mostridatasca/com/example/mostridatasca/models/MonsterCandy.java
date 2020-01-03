package com.example.mostridatasca.com.example.mostridatasca.models;

public class MonsterCandy {
    private String id;
    private String type;
    private String size;
    private String name;
    private double lat;
    private double lon;

    public MonsterCandy(String id, String type, String size, String name, double lat, double lon) {
        this.id = id;
        this.type = type;
        this.size = size;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
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
        return "MonsterCandy{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
