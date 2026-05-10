package com.shootergame.client.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.shootergame.client.core.GameApplication;
import com.shootergame.client.model.dto.MovementData;
import com.shootergame.client.model.entities.Player;
import com.shootergame.client.model.entities.RectangleBounds;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameScreen implements Screen {
    private GameApplication game;
    private String serverIp;
    private int serverPort;
    private String myNickname;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private Texture playerTexture;
    private Pixmap pixmap;
    private TextureRegion playerRegion;

    private Map<String, Player> players = new ConcurrentHashMap<>();
    private Player myPlayer;

    private MovementData movementData;
    private float lastSendTime = 0;
    private static final float SEND_INTERVAL = 0.05f;

    private JsonReader jsonReader = new JsonReader();
    private Json json = new Json();
    private String baseUrl;

    public GameScreen(GameApplication game, String serverIp, int serverPort, String nickname) {
        this.game = game;
        this.serverIp = serverIp;
        this.serverPort = 8080; // serwer HTTP zawsze na 8080
        this.myNickname = nickname;
        this.movementData = new MovementData(nickname);
        this.baseUrl = "http://" + serverIp + ":8080";
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
        playerRegion = new TextureRegion(playerTexture);
    }

    private void sendMovementAndFetch(float delta) {
        lastSendTime += delta;
        if (lastSendTime < SEND_INTERVAL) return;
        lastSendTime = 0;

        MovementData.PlayerInput input = movementData.getData();
        String jsonBody = String.format(
            java.util.Locale.US,
            "{\"nickName\":\"%s\",\"data\":{\"forwardHolded\":%b,\"backHolded\":%b,\"leftHolded\":%b,\"rightHolded\":%b,\"mouseposX\":%.2f,\"mousePosY\":%.2f}}",
            myNickname,
            input.isForwardHolded(),
            input.isBackHolded(),
            input.isLeftHolded(),
            input.isRightHolded(),
            input.getMouseposX(),
            input.getMousePosY()
        );
        Gdx.app.log("GameScreen", "Sending: " + jsonBody);

        HttpRequest request = new HttpRequest(Net.HttpMethods.POST);
        request.setUrl(baseUrl + "/game/all");
        request.setHeader("Content-Type", "application/json");
        request.setContent(jsonBody);
        request.setTimeOut(3000);

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                String responseData = httpResponse.getResultAsString();
                try {
                    JsonValue root = jsonReader.parse(responseData);
                    parsePlayersFromServer(root);
                } catch (Exception e) {
                    Gdx.app.log("GameScreen", "Parse error: " + e.getMessage());
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("GameScreen", "Request failed: " + t.getMessage());
            }

            @Override
            public void cancelled() {}
        });
    }

    private void parsePlayersFromServer(JsonValue root) {
        Map<String, Player> newPlayers = new ConcurrentHashMap<>();

        JsonValue myPlayerJson = root.get("myPlayer");
        if (myPlayerJson != null) {
            Player me = buildPlayerFromJson(myPlayerJson);
            newPlayers.put(me.getId(), me);
            myPlayer = me;
        }

        JsonValue playersArray = root.get("players");
        if (playersArray != null) {
            for (JsonValue playerJson : playersArray) {
                Player player = buildPlayerFromJson(playerJson);
                newPlayers.put(player.getId(), player);
            }
        }

        players.clear();
        players.putAll(newPlayers);
    }

    private Player buildPlayerFromJson(JsonValue playerJson) {
        Player player = new Player();
        String name = playerJson.getString("name", "unknown");
        player.setId(name);
        player.setNickName(name);
        player.setHealth(100);

        JsonValue polygons = playerJson.get("polygons");
        if (polygons != null && polygons.size >= 8) {
            float x0 = polygons.get(0).asFloat();
            float y0 = polygons.get(1).asFloat();
            float x1 = polygons.get(2).asFloat();
            float y1 = polygons.get(3).asFloat();
            float x2 = polygons.get(4).asFloat();
            float y2 = polygons.get(5).asFloat();
            float x3 = polygons.get(6).asFloat();
            float y3 = polygons.get(7).asFloat();
            player.setBounds(new RectangleBounds(x0, y0, x1, y1, x2, y2, x3, y3));
        }

        return player;
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
            return new RectangleBounds(topLeftX, topLeftY, topRightX, topRightY,
                                       bottomRightX, bottomRightY, bottomLeftX, bottomLeftY);
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

        if (Gdx.input.isKeyPressed(Input.Keys.W)) input.setForwardHolded(true);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) input.setBackHolded(true);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) input.setRightHolded(true);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) input.setLeftHolded(true);

        input.setMouseposX(Gdx.input.getX());
        input.setMousePosY(Gdx.graphics.getHeight() - Gdx.input.getY());
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        sendMovementAndFetch(delta);

        if (myPlayer != null) {
            Gdx.app.log("GameScreen", "MyPlayer pos: " + myPlayer.getBounds().getCenterX() + "," + myPlayer.getBounds().getCenterY());
            camera.position.set(myPlayer.getBounds().getCenterX(), myPlayer.getBounds().getCenterY(), 0);
            camera.update();
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Player player : players.values()) {
            if (player.getBounds() != null) {
                RectangleBounds bounds = player.getBounds();
                batch.draw(playerRegion,
                           bounds.getCenterX() - 24,
                           bounds.getCenterY() - 24,
                           24, 24, 48, 48, 1, 1,
                           bounds.getRotation(), false);
                font.draw(batch, player.getNickName(),
                          bounds.getCenterX() - 30,
                          bounds.getCenterY() + 35);
            }
        }

        batch.end();
        renderHUD();
    }

    private void renderHUD() {
        OrthographicCamera uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        font.draw(batch, "Players online: " + players.size(), 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 40);
        batch.end();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        playerTexture.dispose();
        pixmap.dispose();
    }
}