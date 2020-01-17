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
    public static Model getInstance() {
        return ourInstance;
    }

    private Model() {
        contacts = new ArrayList<String>();
    }

    public String get(int index) {
        return contacts.get(index);
    }

    public int getSize() {
        return contacts.size();
    }

    public void add(String nome){
        contacts.add(nome);
    }

    public void clear(){
        contacts.clear();
    }

}