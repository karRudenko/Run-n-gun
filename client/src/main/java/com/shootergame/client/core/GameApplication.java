package com.shootergame.client.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.shootergame.client.view.screens.ConnectScreen;

public class GameApplication extends Game {
    private String playerNickname;
    private String serverIp;
    private int serverPort;
    
    @Override
    public void create() {
        setScreen(new ConnectScreen(this));
    }
    
    public void setPlayerInfo(String nickname, String ip, int port) {
        this.playerNickname = nickname;
        this.serverIp = ip;
        this.serverPort = port;
    }
    
    public String getPlayerNickname() { return playerNickname; }
    public String getServerIp() { return serverIp; }
    public int getServerPort() { return serverPort; }
    
    @Override
    public void dispose() {
        super.dispose();
    }
}