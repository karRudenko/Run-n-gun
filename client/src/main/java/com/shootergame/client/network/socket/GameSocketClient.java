package com.shootergame.client.network.socket;
import com.shootergame.client.model.dto.MovementData;

import java.io.*;
import java.net.Socket;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;

public class GameSocketClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String serverIp;
    private int serverPort;
    private boolean connected = false;
    private String playerNickname;
    
    private SocketListener listener;
    private Json json = new Json();
    private JsonReader jsonReader = new JsonReader();
    
    private Thread listeningThread;
    private volatile boolean running = true;
    
    public interface SocketListener {
        void onGameStateReceived(JsonValue playersData);
        void onConnectionError(String error);
        void onDisconnected();
        void onRegistrationConfirmed(String playerId);
    }
    
    public GameSocketClient(String serverIp, int serverPort, String playerNickname, SocketListener listener) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.playerNickname = playerNickname;
        this.listener = listener;
    }
    
    public void connect() {
        new Thread(() -> {
            try {
                Gdx.app.log("GameSocketClient", "Connect with " + serverIp + ":" + serverPort);
                socket = new Socket(serverIp, serverPort);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
                connected = true;
                
                Gdx.app.log("GameSocketClient", "Connected");
                
                sendRegistration();
                
                startListening();
                
            } catch (IOException e) {
                Gdx.app.log("GameSocketClient", "Error: " + e.getMessage());
                if (listener != null) {
                    listener.onConnectionError("Can not connect: " + e.getMessage());
                }
            }
        }).start();
    }
    
    private void sendRegistration() {
        String registerJson = String.format(
            "{\"type\":\"register\",\"nickName\":\"%s\"}",
            playerNickname
        );
        sendMessage(registerJson);
        Gdx.app.log("GameSocketClient", "Sent registration: " + registerJson);
    }
    
    private void startListening() {
        listeningThread = new Thread(() -> {
            try {
                String line;
                while (running && (line = reader.readLine()) != null) {
                    Gdx.app.log("GameSocketClient", "Recieved: " + line);
                    parseServerMessage(line);
                }
            } catch (IOException e) {
                if (running) {
                    Gdx.app.log("GameSocketClient", "Disconnected: " + e.getMessage());
                    if (listener != null) {
                        listener.onDisconnected();
                    }
                }
            }
        });
        listeningThread.start();
    }
    
    private void parseServerMessage(String jsonString) {
        try {
            JsonValue root = jsonReader.parse(jsonString);
            String type = root.getString("type", "");
            
            switch (type) {
                case "Registration_success":
                    String playerId = root.getString("playerId", "");
                    Gdx.app.log("GameSocketClient", "Registration successful ID: " + playerId);
                    if (listener != null) {
                        listener.onRegistrationConfirmed(playerId);
                    }
                    break;
                    
                case "game_state":
                    JsonValue playersArray = root.get("players");
                    if (playersArray != null && listener != null) {
                        listener.onGameStateReceived(playersArray);
                    }
                    break;
                    
                case "player_joined":
                    Gdx.app.log("GameSocketClient", "Player joined: " + root.getString("nickName", ""));
                    break;
                    
                case "player_left":
                    Gdx.app.log("GameSocketClient", "Player left: " + root.getString("nickName", ""));
                    break;
                    
                case "error":
                    String errorMsg = root.getString("message", "Unknown error");
                    Gdx.app.log("GameSocketClient", "Server error: " + errorMsg);
                    break;
                    
                default:
                    Gdx.app.log("GameSocketClient", "Unknown type: " + type);
            }
            
        } catch (Exception e) {
            Gdx.app.log("GameSocketClient", "Parsing error: " + e.getMessage());
        }
    }
    
    
    public void sendMovement(MovementData movementData) {
        if (writer != null && connected) {
            String jsonString = json.toJson(movementData);
            writer.println(jsonString);
            writer.flush();
        }
    }
    
    public void sendShoot(float targetX, float targetY) {
        String shootJson = String.format(
            "{\"type\":\"shoot\",\"nickName\":\"%s\",\"targetX\":%.2f,\"targetY\":%.2f}",
            playerNickname, targetX, targetY
        );
        sendMessage(shootJson);
    }
    
    private void sendMessage(String message) {
        if (writer != null && connected) {
            writer.println(message);
            writer.flush();
            Gdx.app.log("GameSocketClient", "Sent: " + message);
        }
    }
    
    public void disconnect() {
        running = false;
        connected = false;
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
            if (listeningThread != null) listeningThread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isConnected() { return connected; }
}