package client.game;
import client.model.Player;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GameScreen implements ApplicationListener {
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    
    private List<Player> players = new ArrayList<>();
    private String localPlayerId = "player-123";
    
    private float updateTimer = 0;
    private static final float UPDATE_INTERVAL = 0.05f;

    private static final String API_URL = "http://localhost:8080";

    private Gson gson = new Gson();

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        shapeRenderer = new ShapeRenderer();

        fetchPlayers();
        System.out.println("Fetching players from: " + API_URL);
    }

    @Override
    public void render() {
        updateTimer += Gdx.graphics.getDeltaTime();
        if(updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0;
            fetchPlayers();
        }

        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        drawGrid();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(Player player : players) {
            drawPlayer(player);
        }

        shapeRenderer.end();
        drawHUD();
    }

    private void drawPlayer(Player player) {
        shapeRenderer.setColor(0.2f, 0.2f, 1f, 1f);
        shapeRenderer.rect(player.x, player.y,20, 20);

        shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(player.x - 20, player.y + 22, 40, 5);

        // shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1f);
        // float healthPercent = player.health / 100f;
        // shapeRenderer.rect(player.x + 20, player.y + 22, healthPercent * 40, 5);
    }

    private void drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 1f);

        for(int x = 0; x < 800; x += 50) {
            shapeRenderer.line(x, 0, x, 600);
        }
        for(int y = 0; y < 600; y += 50) {
            shapeRenderer.line(0, y, 800, y);
        }
        shapeRenderer.end();        
    }

    private void drawHUD() {
        System.out.println("\r[INFO] Players on map: " + players.size());
        // for(Player p : players) {
        //     if(p.id.equals(localPlayerId)) {
        //         System.out.println(" | YOUR HP: " + p.health);
        //     }
        // }
    }

    private void fetchPlayers() {
        new Thread(() -> {
            try {
                String json = httpGet(API_URL);
                if(json != null && !json.isEmpty()) {
                    parseAndUpdatePlayers(json);
                }
            } catch (Exception e) {
                System.err.println("Error fetching: " + e.getMessage());
            }
        }).start();
    }

    private String httpGet(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(2000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            response.append(line);
        }        
        reader.close();
        return response.toString();
    }

    private void parseAndUpdatePlayers(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray playersArray = root.getAsJsonArray("players");

            List<Player> newPlayers = new ArrayList<>();

            for(int i = 0; i < playersArray.size(); i++) {
                JsonObject p = playersArray.get(i).getAsJsonObject();

                String id = p.get("id").getAsString();
                String nickname = p.has("nickname") ? p.get("nickname").getAsString() : "Player";
                float x = p.get("x").getAsFloat();
                float y = p.get("y").getAsFloat();
                float rotation = p.has("rotation") ? p.get("rotation").getAsFloat() : 0f;
                int health = p.has("health") ? p.get("health").getAsInt() : 100;
                String team = p.has("team") ? p.get("team").getAsString() : "neutral";

                newPlayers.add(new Player(id, nickname, x, y, rotation, health, team));
            }

            synchronized(players) {
                players.clear();
                players.addAll(newPlayers);
            }

            System.out.println("\n[UPDATE] Received " + playersArray.size() + " players");

        } catch(Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}

}
