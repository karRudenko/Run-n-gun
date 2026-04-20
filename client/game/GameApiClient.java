package shared.model;
// GameApiClient.java - komunikacja z backendem
import com.badlogic.gdx.net.HttpRequest;
import com.badlogic.gdx.net.HttpResponse;
import com.badlogic.gdx.net.HttpResponseListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class GameApiClient {
    private static final String API_URL = "http://localhost:8080/api/game/state";
    private JsonReader jsonReader = new JsonReader();
    
    public interface GameStateCallback {
        void onSuccess(GameStateResponse gameState);
        void onError(String error);
    }
    
    public void fetchGameState(GameStateCallback callback) {
        HttpRequest request = new HttpRequest(HttpRequest.HttpMethods.GET);
        request.setUrl(API_URL);
        request.setTimeOut(5000); // 5 sekund timeout
        request.setHeader("Content-Type", "application/json");
        
        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == 200) {
                    String responseData = httpResponse.getResultAsString();
                    GameStateResponse gameState = parseGameState(responseData);
                    callback.onSuccess(gameState);
                } else {
                    callback.onError("HTTP Error: " + statusCode);
                }
            }
            
            @Override
            public void failed(Throwable t) {
                callback.onError("Connection failed: " + t.getMessage());
            }
            
            @Override
            public void cancelled() {
                callback.onError("Request cancelled");
            }
        });
    }
    
    private GameStateResponse parseGameState(String json) {
        JsonValue root = jsonReader.parse(json);
        GameStateResponse state = new GameStateResponse();
        
        // Parsowanie listy graczy
        JsonValue playersArray = root.get("players");
        List<PlayerData> players = new ArrayList<>();
        
        for (JsonValue playerJson : playersArray) {
            PlayerData player = new PlayerData();
            player.setId(playerJson.getString("id"));
            player.setNickname(playerJson.getString("nickname"));
            player.setX(playerJson.getFloat("x"));
            player.setY(playerJson.getFloat("y"));
            player.setRotation(playerJson.getFloat("rotation", 0f));
            player.setHealth(playerJson.getInt("health", 100));
            player.setTeam(playerJson.getString("team", "red"));
            players.add(player);
        }
        
        state.setPlayers(players);
        state.setTimestamp(root.getLong("timestamp", 0));
        state.setCurrentMap(root.getString("currentMap", "default"));
        
        return state;
    }
}