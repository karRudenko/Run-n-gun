package com.shootergame.client.network.handlers;

import com.badlogic.gdx.utils.JsonValue;
import com.shootergame.client.model.entities.Player;
import com.shootergame.client.model.entities.RectangleBounds;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameStateHandler {
    private Map<String, Player> players = new ConcurrentHashMap<>();
    
    public Map<String, Player> parseGameState(String currentPlayerNickname, JsonValue playersArray) {
        Map<String, Player> newPlayers = new ConcurrentHashMap<>();
        
        for (JsonValue playerJson : playersArray) {
            String nickName = playerJson.getString("nickName", "unknown");
            String id = playerJson.getString("id", nickName);
            
            JsonValue boundsJson = playerJson.get("bounds");
            if (boundsJson != null) {
                RectangleBounds bounds = parseRectangleBounds(boundsJson);
                
                Player player = new Player();
                player.setId(id);
                player.setNickName(nickName);
                player.setBounds(bounds);
                player.setHealth(playerJson.getInt("health", 100));
                player.setActive(playerJson.getBoolean("active", true));
                
                newPlayers.put(id, player);
            }
        }
        
        players.clear();
        players.putAll(newPlayers);
        
        return newPlayers;
    }
    
    private RectangleBounds parseRectangleBounds(JsonValue boundsJson) {
        
        RectangleBounds bounds = new RectangleBounds();
        
        try {
            // if (boundsJson.has("topLeft")) {
            //     float topLeftX = boundsJson.get("topLeft").getFloat("x");
            //     float topLeftY = boundsJson.get("topLeft").getFloat("y");
            //     float topRightX = boundsJson.get("topRight").getFloat("x");
            //     float topRightY = boundsJson.get("topRight").getFloat("y");
            //     float bottomRightX = boundsJson.get("bottomRight").getFloat("x");
            //     float bottomRightY = boundsJson.get("bottomRight").getFloat("y");
            //     float bottomLeftX = boundsJson.get("bottomLeft").getFloat("x");
            //     float bottomLeftY = boundsJson.get("bottomLeft").getFloat("y");
                
            //     return new RectangleBounds(topLeftX, topLeftY, topRightX, topRightY,bottomRightX, bottomRightY, bottomLeftX, bottomLeftY);
            // }
            
            if (boundsJson.isArray() && boundsJson.size >= 8) {
                float[] points = new float[8];
                for (int i = 0; i < 8; i++) {
                    points[i] = boundsJson.get(i).asFloat();
                }
                return new RectangleBounds(points[0], points[1], points[2], points[3],points[4], points[5], points[6], points[7]);
            }
        } catch (Exception e) {
            com.badlogic.gdx.Gdx.app.log("GameStateHandler", "Error with parsing bounds: " + e.getMessage());
        }
        
        return bounds;
    }
    
    public Map<String, Player> getPlayers() { return players; }
    public Player getPlayer(String id) { return players.get(id); }
    public void clear() { players.clear(); }
}