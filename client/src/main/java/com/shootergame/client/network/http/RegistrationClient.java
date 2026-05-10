package com.shootergame.client.network.http;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.Gdx;
import com.shootergame.client.model.dto.RegisterRequest;

public class RegistrationClient {
    private static final String REGISTER_URL = "http://localhost:8080/game/register";
    private Json json = new Json();
    
    public interface RegistrationCallback {
        void onSuccess(String response);
        void onError(String error);
    }
    
    public void register(String nickname, RegistrationCallback callback) {
        String jsonBody = "{\"name\":\"" + nickname + "\"}";
        
        HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.POST);
        httpRequest.setUrl(REGISTER_URL);
        httpRequest.setHeader("Content-Type", "application/json");
        httpRequest.setContent(jsonBody);
        httpRequest.setTimeOut(5000);
        
        Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String responseData = httpResponse.getResultAsString();
                
                if (statusCode == 200 || statusCode == 201) {
                    callback.onSuccess(responseData);
                } else {
                    callback.onError("HTTP " + statusCode + ": " + responseData);
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