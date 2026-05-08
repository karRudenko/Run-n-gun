package com.shootergame.client.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import com.shootergame.client.core.GameApplication;
import com.shootergame.client.model.dto.MovementData;
import com.shootergame.client.model.entities.Player;
import com.shootergame.client.model.entities.RectangleBounds;
import com.shootergame.client.network.socket.GameSocketClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameScreen implements Screen {
    private GameApplication game;
    private GameSocketClient socketClient;
    
    // Renders
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    
    // Textures
    private Texture playerTexture;
    private Pixmap pixmap;
    
    // State of game
    private Map<String, Player> players = new ConcurrentHashMap<>();
    private String myNickname;
    private String myPlayerId;
    private Player myPlayer;
    
    // Entry data
    private MovementData movementData;
    private float lastSendTime = 0;
    private static final float SEND_INTERVAL = 0.05f;
    
    private float cameraX, cameraY;

    public GameScreen(GameApplication game, String serverIp, int serverPort, String nickname) {
        this.game = game;
        this.myNickname = nickname;
        this.movementData = new MovementData(nickname);
    }
    
    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        

        pixmap = new Pixmap(48, 48, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        pixmap.setColor(Color.BLACK);
        pixmap.drawRectangle(0, 0, 47, 47);
        playerTexture = new Texture(pixmap);
        

        socketClient = new GameSocketClient(game.getServerIp(), game.getServerPort(), myNickname, new GameSocketClient.SocketListener() {
            @Override
            public void onGameStateReceived(String currentPlayerNickname, JsonValue playersData) {
                parsePlayersFromServer(playersData);
            }
            
            @Override
            public void onRegistrationConfirmed(String playerId) {
                myPlayerId = playerId;
                Gdx.app.log("GameScreen", "Registration confirmed, ID: " + playerId);
            }
            
            @Override
            public void onConnectionError(String error) {
                Gdx.app.log("GameScreen", "Error: " + error);
            }
            
            @Override
            public void onDisconnected() {
                Gdx.app.log("GameScreen", "Disconnect");
            }
        });
        
        socketClient.connect();
    }
    
    private void parsePlayersFromServer(JsonValue playersArray) {
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
                
                newPlayers.put(id, player);
                
                if (nickName.equals(myNickname)) {
                    myPlayer = player;
                }
            }
        }
        
        players.clear();
        players.putAll(newPlayers);
    }
    
    private RectangleBounds parseRectangleBounds(JsonValue boundsJson) {
        try {
            float topLeftX = boundsJson.get("topLeft").getFloat("x");
            float topLeftY = boundsJson.get("topLeft").getFloat("y");
            float topRightX = boundsJson.get("topRight").getFloat("x");
            float topRightY = boundsJson.get("topRight").getFloat("y");
            float bottomRightX = boundsJson.get("bottomRight").getFloat("x");
            float bottomRightY = boundsJson.get("bottomRight").getFloat("y");
            float bottomLeftX = boundsJson.get("bottomLeft").getFloat("x");
            float bottomLeftY = boundsJson.get("bottomLeft").getFloat("y");
            
            return new RectangleBounds(topLeftX, topLeftY, topRightX, topRightY,bottomRightX, bottomRightY, bottomLeftX, bottomLeftY);
        } catch (Exception e) {
            return new RectangleBounds();
        }
    }
    
    private void handleInput(float delta) {
        MovementData.PlayerInput input = movementData.getData();
        
        input.setForwardHolded(false);
        input.setRightHolded(false);
        input.setLeftHolded(false);
        input.setBackHolded(false);
        
        // Move
        if (Gdx.input.isKeyPressed(Input.Keys.W)) input.setForwardHolded(true);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) input.setBackHolded(true);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) input.setRightHolded(true);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) input.setLeftHolded(true);
        
        // Mouse position
        input.setMouseposX(Gdx.input.getX());
        input.setMousePosY(Gdx.graphics.getHeight() - Gdx.input.getY());
        
        // Shot
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);
            socketClient.sendShoot(mousePos.x, mousePos.y);
        }
        
        // Sending moves to the server
        lastSendTime += delta;
        if (lastSendTime >= SEND_INTERVAL && socketClient.isConnected()) {
            lastSendTime = 0;
            socketClient.sendMovement(movementData);
        }
    }
    
    @Override
    public void render(float delta) {
        handleInput(delta);
        
        // Camera follow the player
        if (myPlayer != null) {
            camera.position.set(myPlayer.getX(), myPlayer.getY(), 0);
            camera.update();
        }
        
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Draw players
        for (Player player : players.values()) {
            if (player.getBounds() != null) {
                RectangleBounds bounds = player.getBounds();
                
                batch.draw(new com.badlogic.gdx.graphics.g2d.TextureRegion(playerTexture),
           bounds.getCenterX() - 24,
           bounds.getCenterY() - 24,
           24, 24,
           48, 48,
           1, 1,
           bounds.getRotation(),
           false);
                
                // Nickname
                font.draw(batch, player.getNickName(), bounds.getCenterX() - 30, bounds.getCenterY() + 35);
                
                // Health
                drawHealthBar(bounds.getCenterX(), bounds.getCenterY() + 30, 50, 8, player.getHealth() / 100f);
            }
        }
        
        batch.end();
        
        renderHUD();
    }
    
    private void drawHealthBar(float x, float y, float width, float height, float percent) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(x - width/2, y, width, height);
        shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1f);
        shapeRenderer.rect(x - width/2, y, width * percent, height);
        shapeRenderer.end();
    }
    
    private void renderHUD() {
        OrthographicCamera uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiCamera.combined);
        
        batch.begin();
        font.draw(batch, "Players online: " + players.size(), 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "State: " + (socketClient.isConnected() ? "ONLINE" : "OFFLINE"), 20, Gdx.graphics.getHeight() - 50);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 40);
        batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
    
    @Override
    public void dispose() {
        socketClient.disconnect();
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        playerTexture.dispose();
        pixmap.dispose();
    }
    
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}