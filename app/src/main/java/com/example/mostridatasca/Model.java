package com.example.mostridatasca;

import android.content.Context;
import android.util.Log;

import com.example.mostridatasca.com.example.mostridatasca.models.MonsterCandy;
import com.example.mostridatasca.com.example.mostridatasca.models.Player;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class Model {
    /**
     * Model
     * Singleton
     */

    public static final String TAG = " Debug - Model ";
    private static final Model ourInstance = new Model();

    private ArrayList<Player> players = null;
    private ArrayList<MonsterCandy> moncan = null;


    public static Model getInstance() {
        return ourInstance;
    }

    private Model() {
        players = new ArrayList<Player>();
        moncan = new ArrayList<MonsterCandy>();
    }

    public Player getPlayer(int index){ return players.get(index);}
    public MonsterCandy getMoncan(int index){ return moncan.get(index);}

    public MonsterCandy getMoncanById(String id){   // Fare attenzione: se l'ID non esiste ritorna null

        for(MonsterCandy mc : moncan){
            if(mc.getId().equals(id)){ return mc; }
        }
       return null;
    }

    public int getPlayersSize() { return players.size(); }
    public int getMoncanSize() { return moncan.size(); }

    public void addPlayer(Player player){ players.add(player);}
    public void addMoncan(MonsterCandy monsterCandy) { moncan.add(monsterCandy);}
    public void addPlayersFromJSONArray(JSONArray jsonArrayPlayers){
        players.clear();
        Log.d(TAG,"playersArraylist pulito.");
        for(int i = 0; i < jsonArrayPlayers.length(); i++) {
            try {
                Player player = new Player(jsonArrayPlayers.getJSONObject(i));
                players.add(player);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void addMoncanFromJSONArray(Context context, JSONArray jsonArrayMoncan){
        moncan.clear();
        Log.d(TAG,"moncanArraylist pulito.");
        for (int i = 0; i<jsonArrayMoncan.length(); i++){
            try {
                MonsterCandy monsterCandy = new MonsterCandy(context, jsonArrayMoncan.getJSONObject(i));
                moncan.add(monsterCandy);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String playersToString(){
        String playersString = "";
        for(Player player : players){
            playersString += player.toString() + " ";
        }
        return playersString;
    }

    public String moncanToString(){
        String moncanString = "";
        for(MonsterCandy monsterCandy : moncan){
            moncanString += monsterCandy.toString() + " ";
        }
        return moncanString;
    }

    public void clearAll(){
        Log.d(TAG,"model pulito.");
        players.clear();
        moncan.clear();
    }

    public void clearMoncan(){
        moncan.clear();
        Log.d(TAG,"moncanArraylist pulito.");
    }
    public void clearPlayers(){
        players.clear();
        Log.d(TAG,"playersArraylist pulito.");
    }

}