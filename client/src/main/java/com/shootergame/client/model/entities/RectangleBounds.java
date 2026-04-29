package com.shootergame.client.model.entities;

public class RectangleBounds {
    private float topLeftX, topLeftY;
    private float topRightX, topRightY;
    private float bottomRightX, bottomRightY;
    private float bottomLeftX, bottomLeftY;
    
    private float centerX, centerY;
    private float width, height;
    private float rotation;
    
    public RectangleBounds() {}
    
    public RectangleBounds(float topLeftX, float topLeftY, float topRightX, float topRightY, float bottomRightX, float bottomRightY, float bottomLeftX, float bottomLeftY) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.topRightX = topRightX;
        this.topRightY = topRightY;
        this.bottomRightX = bottomRightX;
        this.bottomRightY = bottomRightY;
        this.bottomLeftX = bottomLeftX;
        this.bottomLeftY = bottomLeftY;
        
        calculateDerivedValues();
    }
    
    private void calculateDerivedValues() {
        // Center
        centerX = (topLeftX + topRightX + bottomRightX + bottomLeftX) / 4;
        centerY = (topLeftY + topRightY + bottomRightY + bottomLeftY) / 4;
        
        // Width
        width = (float) Math.sqrt(Math.pow(topRightX - topLeftX, 2) + Math.pow(topRightY - topLeftY, 2));
        
        // Height
        height = (float) Math.sqrt(Math.pow(bottomLeftX - topLeftX, 2) + Math.pow(bottomLeftY - topLeftY, 2));
        
        // Rotation
        rotation = (float) Math.toDegrees(Math.atan2(topRightY - topLeftY, topRightX - topLeftX));
    }
    
    // Getters
    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getRotation() { return rotation; }
    
    public float getTopLeftX() { return topLeftX; }
    public float getTopLeftY() { return topLeftY; }
    public float getTopRightX() { return topRightX; }
    public float getTopRightY() { return topRightY; }
    public float getBottomRightX() { return bottomRightX; }
    public float getBottomRightY() { return bottomRightY; }
    public float getBottomLeftX() { return bottomLeftX; }
    public float getBottomLeftY() { return bottomLeftY; }
    
    // Setters
    public void setTopLeftX(float x) { this.topLeftX = x; calculateDerivedValues(); }
    public void setTopLeftY(float y) { this.topLeftY = y; calculateDerivedValues(); }
    public void setTopRightX(float x) { this.topRightX = x; calculateDerivedValues(); }
    public void setTopRightY(float y) { this.topRightY = y; calculateDerivedValues(); }
    public void setBottomRightX(float x) { this.bottomRightX = x; calculateDerivedValues(); }
    public void setBottomRightY(float y) { this.bottomRightY = y; calculateDerivedValues(); }
    public void setBottomLeftX(float x) { this.bottomLeftX = x; calculateDerivedValues(); }
    public void setBottomLeftY(float y) { this.bottomLeftY = y; calculateDerivedValues(); }
}