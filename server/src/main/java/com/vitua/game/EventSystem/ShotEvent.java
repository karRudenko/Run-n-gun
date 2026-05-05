package com.vitua.game.EventSystem;

import com.vitua.game.math.Vector2D;

public class ShotEvent implements Event{

    public final int ownerId;
    public final Vector2D startPos;
    public final Vector2D direction;
    public ShotEvent(Vector2D direction, Vector2D startPos, int ownerId){
        this.ownerId=ownerId;
        this.direction=direction;
        this.startPos=startPos;
    }
    
}