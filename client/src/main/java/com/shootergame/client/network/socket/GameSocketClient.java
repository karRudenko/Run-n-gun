package com.shootergame.client.network.socket;

import java.io.*;
import java.net.Socket;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.shootergame.client.model.dto.MovementData;
import com.shootergame.client.network.handlers.GameStateHandler;

public class GameSocketClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String serverIp;
    private int serverPort;
    private boolean connected = false;
    private GameStateHandler gameStateHandler;
    private String playerNickname;
    
    public interface SocketListener {
        void onGameStateReceived(String nickName, JsonValue playersData);
        void onConnectionError(String error);
        void onDisconnected();
    }
    
    private SocketListener listener;
    
    public GameSocketClient(String serverIp, int serverPort, String playerNickname, SocketListener listener) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.playerNickname = playerNickname;
        this.listener = listener;
        this.gameStateHandler = new GameStateHandler();
    }
    
    public void connect() {
        new Thread(() -> {
            try {
                Gdx.app.log("GameSocketClient", "Conecting with " + serverIp + ":" + serverPort);
                socket = new Socket(serverIp, serverPort);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
                connected = true;
                
                Gdx.app.log("GameSocketClient", "Conected with socket server");
                
                sendMessage("{\"type\":\"join\",\"nickName\":\"" + playerNickname + "\"}");
                
                listenForMessages();
                
            } catch (IOException e) {
                Gdx.app.log("GameSocketClient", "Error with conection: " + e.getMessage());
                if (listener != null) {
                    listener.onConnectionError("Cannot conect with the server: " + e.getMessage());
                }
            }
        }).start();
    }
    
    private void listenForMessages() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                Gdx.app.log("GameSocketClient", "Recieved: " + line);
                parseServerMessage(line);
            }
        } catch (IOException e) {
            Gdx.app.log("GameSocketClient", "Disconnected: " + e.getMessage());
            connected = false;
            if (listener != null) {
                listener.onDisconnected();
            }
        }
    }
    
    private void parseServerMessage(String json) {
        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue root = jsonReader.parse(json);
            
            String type = root.getString("type", "game_state");
            
            switch (type) {
                case "game_state":
                    JsonValue playersArray = root.get("players");
                    if (playersArray != null && listener != null) {
                        listener.onGameStateReceived(playerNickname, playersArray);
                    }
                    break;
                    
                case "player_joined":
                    Gdx.app.log("GameSocketClient", "Player joined: " + root.getString("nickName", "unknown"));
                    break;
                    
                case "player_left":
                    Gdx.app.log("GameSocketClient", "Player left: " + root.getString("nickName", "unknown"));
                    break;
                    
                default:
                    Gdx.app.log("GameSocketClient", "Unknown massage: " + type);
            }
            
        } catch (Exception e) {
            Gdx.app.log("GameSocketClient", "Error with parsing: " + e.getMessage());
        }
    }
    
    public void sendMovement(MovementData movementData) {
        if (writer != null && connected) {
            String json = new com.badlogic.gdx.utils.Json().toJson(movementData);
            writer.println(json);
            writer.flush();
        }
    }
    
    private void sendMessage(String message) {
        if (writer != null && connected) {
            writer.println(message);
            writer.flush();
        }
    }
    
    public void disconnect() {
        try {
            connected = false;
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isConnected() { return connected; }
}