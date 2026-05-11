package com.vitua.game.Engine;

public record InputRecord(
    boolean forwardHolded,
    boolean rightHolded,
    boolean leftHolded,
    boolean backHolded,
    boolean leftMouseHolded,
    boolean rightMouseHolded,
    boolean reloadPressed,
    boolean[] numPressed,
    double mousePosX,
    double mousePosY
) {
    public static InputRecord emptyInputRecord() {
        return new InputRecord(
            false, false, false, false,    
            false, false,                  
            false,
            new boolean[0],

            0.0, 0.0                       
        );
    }

    public int getPressedDigit() {
        if(numPressed == null) return -1;
        for (int i = 0; i < numPressed.length; i++) {
            if (numPressed[i]) return i;
        }
        return -1; 
    }
}