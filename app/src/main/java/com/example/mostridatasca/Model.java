package com.example.mostridatasca;

import java.util.ArrayList;

public class Model {
    /**
     * @author Betto
     * Model
     * Singleton
     * Arraylist con i nomi dei top players
     */
    private static final Model ourInstance = new Model();

    private ArrayList<String> contacts = null;
    private ArrayList<String> imgs = null;

    public static Model getInstance() {
        return ourInstance;
    }

    private Model() {
        contacts = new ArrayList<String>();
        imgs = new ArrayList<String>();
    }

    public String get(int index) {
        return contacts.get(index);
    }
    public String getImg(int index){return imgs.get(index); }

    public int getSize() {
        return contacts.size();
    }

    public void add(String nome){
        contacts.add(nome);
    }
    public void addImg(String img){
        imgs.add(img);
    }

    public void clear(){
        contacts.clear();
        imgs.clear();
    }

}