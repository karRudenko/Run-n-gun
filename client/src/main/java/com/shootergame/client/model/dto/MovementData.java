package com.shootergame.client.model.dto;

public class MovementData {
    private String nickName;
    private PlayerInput data;
    
    public MovementData(String nickName) {
        this.nickName = nickName;
        this.data = new PlayerInput();
    }
    
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    
    public PlayerInput getData() { return data; }
    public void setData(PlayerInput data) { this.data = data; }
    
    public static class PlayerInput {
        private boolean forwardHolded = false;
        private boolean rightHolded = false;
        private boolean leftHolded = false;
        private boolean backHolded = false;
        private float mouseposX = 0;
        private float mousePosY = 0;
        
        // Getters i setters
        public boolean isForwardHolded() { return forwardHolded; }
        public void setForwardHolded(boolean forwardHolded) { this.forwardHolded = forwardHolded; }
        
        public boolean isRightHolded() { return rightHolded; }
        public void setRightHolded(boolean rightHolded) { this.rightHolded = rightHolded; }
        
        public boolean isLeftHolded() { return leftHolded; }
        public void setLeftHolded(boolean leftHolded) { this.leftHolded = leftHolded; }
        
        public boolean isBackHolded() { return backHolded; }
        public void setBackHolded(boolean backHolded) { this.backHolded = backHolded; }
        
        public float getMouseposX() { return mouseposX; }
        public void setMouseposX(float mouseposX) { this.mouseposX = mouseposX; }
        
        public float getMousePosY() { return mousePosY; }
        public void setMousePosY(float mousePosY) { this.mousePosY = mousePosY; }
    }
}