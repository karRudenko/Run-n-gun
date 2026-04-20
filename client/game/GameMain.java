package client.game;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class GameMain extends Game {
    private String playerId;
    
    public GameMain(String playerId) {
        this.playerId = playerId;
    }
    
    @Override
    public void create() {
        setScreen(new GameScreen(playerId));
    }
    
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Multiplayer Shooter");
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);
        
        String playerId = args.length > 0 ? args[0] : "player-123";
        new Lwjgl3Application(new GameMain(playerId), config);
    }
}