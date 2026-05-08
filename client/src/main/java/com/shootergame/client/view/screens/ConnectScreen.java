package com.shootergame.client.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.shootergame.client.core.GameApplication;
import com.shootergame.client.model.entities.Record;

public class ConnectScreen implements Screen {
    private GameApplication game;
    private SpriteBatch batch;
    private BitmapFont font;
    private String errorMessage = "";
    
    private StringBuilder nicknameInput = new StringBuilder("Player");
    private StringBuilder ipInput = new StringBuilder("localhost");
    private StringBuilder portInput = new StringBuilder("12345"); 
    private Record players = new Record();
    
    private boolean typingNickname = true;
    private boolean typingIp = false;
    private boolean typingPort = false;
    
    public ConnectScreen(GameApplication game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        handleInput();
        
        batch.begin();
        font.draw(batch, "MULTIPLAYER SHOOTER", 350, 650);
        font.draw(batch, "Nickname: " + nicknameInput.toString() + (typingNickname ? "_" : ""), 300, 500);
        font.draw(batch, "Server IP: " + ipInput.toString() + (typingIp ? "_" : ""), 300, 430);
        font.draw(batch, "Socket Port: " + portInput.toString() + (typingPort ? "_" : ""), 300, 360);
        font.draw(batch, "Press ENTER in order to connect", 300, 250);
        font.draw(batch, "TAB - switch areas | ESC - exit", 300, 150);
        if (!errorMessage.isEmpty()) {
            font.draw(batch, errorMessage, 300, 550);
        }
        batch.end();
    }
    
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            if (typingNickname) {
                typingNickname = false;
                typingIp = true;
                typingPort = false;
            } else if (typingIp) {
                typingNickname = false;
                typingIp = false;
                typingPort = true;
            } else {
                typingNickname = true;
                typingIp = false;
                typingPort = false;
            }
        }
        
        // Leters
        for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = (char) ('a' + (i - Input.Keys.A));
                addCharacter(c);
            }
        }
        
        // Digits
        for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = (char) ('0' + (i - Input.Keys.NUM_0));
                addCharacter(c);
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
            addCharacter('.');
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (typingNickname && nicknameInput.length() > 0) {
                nicknameInput.deleteCharAt(nicknameInput.length() - 1);
            } else if (typingIp && ipInput.length() > 0) {
                ipInput.deleteCharAt(ipInput.length() - 1);
            } else if (typingPort && portInput.length() > 0) {
                portInput.deleteCharAt(portInput.length() - 1);
            }
        }
        // ================================================================================
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)  && !isConnecting) {
            errorMessage = "";
            String nickname = nicknameInput.toString().trim();
            String ip = ipInput.toString().trim();
            int port = Integer.parseInt(portInput.toString().trim());
            
            if (!nickname.isEmpty() && !players.containsValue(new StringBuilder(ip+port), new StringBuilder(nickname))) {
                players.addPlayer(new StringBuilder(ip+port), new StringBuilder(nickname));
                game.setPlayerInfo(nickname, ip, port);
                game.setScreen(new GameScreen(game, ip, port, nickname));
            } else {
                errorMessage = "The nickname is empty or taken";
            }
            connectToServer();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
    
    private void addCharacter(char c) {
        if (typingNickname && nicknameInput.length() < 20) {
            nicknameInput.append(c);
        } else if (typingIp && ipInput.length() < 30) {
            ipInput.append(c);
        } else if (typingPort && portInput.length() < 6 && Character.isDigit(c)) {
            portInput.append(c);
        }
    }

    private void connectToServer() {
        String nickname = nicknameInput.toString().trim();
        String ip = ipInput.toString().trim();
        int port;
        
        try {
            port = Integer.parseInt(portInput.toString().trim());
        } catch (NumberFormatException e) {
            statusMessage = "Incorrect port number";
            return;
        }
        
        if (nickname.isEmpty()) {
            statusMessage = "Nickname cannot be empty";
            return;
        }
        isConnecting = true;
        statusMessage = "Regestration on the serser...";
        
        registrationClient.register(nickname, new RegistrationClient.RegistrationCallback() {
            @Override
            public void onSuccess(String response) {
                Gdx.app.log("ConnectScreen", "Registration successful: " + response);
                statusMessage = "Registration successful. Conecting with the socket...";
                
                game.setPlayerInfo(nickname, ip, port);
                
                Gdx.app.postRunnable(() -> {
                    game.setScreen(new GameScreen(game, ip, port, nickname));
                });
            }
            @Override
            public void onError(String error) {
                Gdx.app.log("ConnectScreen", "Error with regestration: " + error);
                statusMessage = "Error with regestration: " + error;
                isConnecting = false;
            }
        });
    }
    
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
        font.dispose();
    }
}