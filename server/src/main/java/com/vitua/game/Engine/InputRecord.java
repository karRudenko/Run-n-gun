package com.vitua.game.Engine;

public record InputRecord(
    boolean forwardHolded,
    boolean rightHolded,
    boolean leftHolded,
    boolean backHolded,
    boolean leftMouseHolded,
    boolean rightMouseHolded,
    double mousePosX,
    double mousePosY


) {
    public static InputRecord emptyInputRecord(){
        return new InputRecord(false, false, false, false, false, false, 0.0, 0.0);
    }
} 