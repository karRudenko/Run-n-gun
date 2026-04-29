package com.shootergame.client.model.entities;

public class Player {
    private String id;
    private String nickName;
    private RectangleBounds bounds;
    private int health;
    private boolean isActive;
    
    public Player() {}
    
    public Player(String id, String nickName, RectangleBounds bounds) {
        this.id = id;
        this.nickName = nickName;
        this.bounds = bounds;
        this.health = 100;
        this.isActive = true;
    }
    
    // Getters i setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    
    public RectangleBounds getBounds() { return bounds; }
    public void setBounds(RectangleBounds bounds) { this.bounds = bounds; }
    
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public float getX() { return bounds != null ? bounds.getCenterX() : 0; }
    public float getY() { return bounds != null ? bounds.getCenterY() : 0; }
    public float getRotation() { return bounds != null ? bounds.getRotation() : 0; }
}