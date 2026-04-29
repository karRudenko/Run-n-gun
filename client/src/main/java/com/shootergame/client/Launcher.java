package com.shootergame.client;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.shootergame.client.core.GameApplication;

public class Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Multiplayer Shooter");
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);
        config.setIdleFPS(60);
        
        new Lwjgl3Application(new GameApplication(), config);
    }
}