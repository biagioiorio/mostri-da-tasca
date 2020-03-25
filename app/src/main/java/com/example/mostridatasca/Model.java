package com.example.mostridatasca;

import com.example.mostridatasca.com.example.mostridatasca.models.MonsterCandy;

import java.util.ArrayList;

public class Model {
    /**
     * @author Betto
     * Model
     * Singleton
     */
    private static final Model ourInstance = new Model();

    private ArrayList<String> players = null;
    private ArrayList<String> imgs = null;
    private ArrayList<MonsterCandy> moncan = null;

    public static Model getInstance() {
        return ourInstance;
    }

    private Model() {
        players = new ArrayList<String>();
        imgs = new ArrayList<String>();
        moncan = new ArrayList<MonsterCandy>();
    }

    public String getPlayerName(int index) {
        return players.get(index);
    }
    public String getImg(int index) { return imgs.get(index); }
    public MonsterCandy getMoncan(int index){ return moncan.get(index);}

    // Fare attanzione
    public MonsterCandy getMoncanById(String id){
        MonsterCandy monsterCandy = null;
        for(MonsterCandy mc : moncan){
            if(mc.getId() == id) monsterCandy = mc;
        }
        return monsterCandy;
    }

    public int getPlayersSize() {
        return players.size();
    }
    public int getMoncanSize() { return moncan.size(); }

    public void add(String nome){
        players.add(nome);
    }
    public void addImg(String img){
        imgs.add(img);
    }
    public void addMoncan(MonsterCandy monsterCandy) { moncan.add(monsterCandy);}

    public String moncanToString(){
        String moncanString = "";
        for(MonsterCandy mc : moncan){
            moncanString += mc.toString() + " ";
        }
        return moncanString;
    }

    public void clear(){
        players.clear();
        imgs.clear();
        moncan.clear();
    }

    public void clearMoncan(){
        moncan.clear();
    }

}