package com.shootergame.client.network.http;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.shootergame.client.model.entities.RectangleBounds;
import java.util.ArrayList;
import java.util.List;

public class GameApiClient {
    private static final String API_URL = "http://localhost:8080/api/game";
    private JsonReader jsonReader = new JsonReader();

    public interface GameStateCallback {
        void onSuccess(JsonValue playersArray);
        void onError(String error);
    }

    public void fetchGameState(GameStateCallback callback) {
        HttpRequest request = new HttpRequest(Net.HttpMethods.GET);
        request.setUrl(API_URL);
        request.setTimeOut(5000);
        request.setHeader("Content-Type", "application/json");

        Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == 200) {
                    String responseData = httpResponse.getResultAsString();
                    JsonValue root = jsonReader.parse(responseData);
                    callback.onSuccess(root.get("players"));
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
}