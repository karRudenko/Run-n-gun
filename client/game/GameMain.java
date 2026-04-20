package client.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class GameMain {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Multiplayer Shooter");
        config.setWindowedMode(800, 600);
        config.setForegroundFPS(60);
        config.setResizable(true);

        System.out.println("Starting game window...");
        new Lwjgl3Application(new GameScreen(), config);
    }
}