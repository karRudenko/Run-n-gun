package com.shootergame.client.model.dto;

public class RegisterRequest {
    private String name;
    
    public RegisterRequest(String name) {
        this.name = name;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}