package client.screen;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    
    private GameApiClient apiClient;
    private GameStateResponse currentGameState;
    private float updateTimer = 0f;
    private static final float UPDATE_INTERVAL = 0.05f; 

    private Texture playerTextureBlue;
    private Texture backgroundTexture;

    private String localPlayerId = "player-123";
    private PlayerData localPlayer;
    
    public GameScreen(String playerId) {
        this.localPlayerId = playerId;
        this.apiClient = new GameApiClient();
        this.currentGameState = new GameStateResponse();
        this.currentGameState.setPlayers(new ArrayList<>());
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(100, 100, camera);
        camera.position.set(640, 360, 0);
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        
        playerTextureBlue = new Texture("player_blue.png");
        backgroundTexture = new Texture("background.png");
        
        fetchGameState();
    }
    
    private void fetchGameState() {
        apiClient.fetchGameState(new GameApiClient.GameStateCallback() {
            @Override
            public void onSuccess(GameStateResponse gameState) {
                currentGameState = gameState;
                
                // Znajdź lokalnego gracza
                for (PlayerData player : currentGameState.getPlayers()) {
                    if (player.getId().equals(localPlayerId)) {
                        localPlayer = player;
                        break;
                    }
                }
            }
            
            @Override
            public void onError(String error) {
                Gdx.app.log("GameScreen", "Błąd API: " + error);
            }
        });
    }

    @Override
    public void render(float delta) {
        updateTimer += delta;
        if (updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0;
            fetchGameState();
        }

        if (localPlayer != null) {
            camera.position.set(localPlayer.getX(), localPlayer.getY(), 0);
            camera.update();
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(backgroundTexture, camera.position.x - 640, camera.position.y - 360, 100, 100);

        if (currentGameState.getPlayers() != null) {
            for(PlayerData player : currentGameState.getPlayers()) {
                renderPlayer(player);
            }
        }

        batch.end();

        renderHUD();
    }

    private void renderPlayer(PlayerData player) {
        Texture texture = playerTextureBlue;
        
        float playerWidth = 48f;
        float playerHeight = 48f;
        
        batch.draw(texture,
                   player.getX() - playerWidth/2,
                   player.getY() - playerHeight/2,
                   playerWidth/2, playerHeight/2,  
                   playerWidth, playerHeight,
                   1f, 1f,
                   player.getRotation());
        
        drawHealthBar(player);

        font.draw(batch, 
                  player.getNickname(),
                  player.getX() - 30, 
                  player.getY() + 35);
    }

    private void drawHealthBar(PlayerData player) {
        float barWidth = 50f;
        float barHeight = 8f;
        float x = player.getX() - barWidth/2;
        float y = player.getY() + 25;

        // float healthPercent = player.getHealth() / 100f;

        // shapeRenderer.setProjectionMatrix(camera.combined);
        // shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1f);
        // shapeRenderer.rect(x, y, barWidth, barHeight);
        
        // shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1f);
        // shapeRenderer.rect(x, y, barWidth * healthPercent, barHeight);
        // shapeRenderer.end();
    }

    private void renderHUD() {
        OrthographicCamera uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiCamera.combined);
        
        batch.begin();
        
        if (localPlayer != null) {
            // font.draw(batch, "Twoje zdrowie: " + localPlayer.getHealth(), 20, Gdx.graphics.getHeight() - 20);
            font.draw(batch, "Pozostali gracze: " + (currentGameState.getPlayers().size() - 1), 20, Gdx.graphics.getHeight() - 50);
        }
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 40);
        
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        playerTextureRed.dispose();
        playerTextureBlue.dispose();
        backgroundTexture.dispose();
    }
    
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
