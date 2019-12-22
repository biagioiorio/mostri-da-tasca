package com.example.mostridatasca;

import java.util.ArrayList;

public class Model {
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

    public void initWithFakeData() {
        contacts.add("Andrea");
        contacts.add("Bruna");
        contacts.add("Carlo");
        contacts.add("Daniela");
        contacts.add("Ettore");
        contacts.add("Filippa");
        contacts.add("Guido");
        contacts.add("Ilario");
        contacts.add("Laura");
        contacts.add("Mario");
        contacts.add("Bruna");
        contacts.add("Carlo");
        contacts.add("Daniela");
        contacts.add("Ettore");
        contacts.add("Filippa");
        contacts.add("Guido");
        contacts.add("Ilario");
        contacts.add("Laura");
        contacts.add("Mario");
        contacts.add("Nicoletta");
    }

}