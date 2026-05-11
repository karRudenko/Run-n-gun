package com.vitua.game.EventSystem;

import com.vitua.game.Engine.Weapons.Weapon;
import com.vitua.game.math.Vector2D;

public class ShotEvent implements Event{

    public final int ownerId;
    public final Vector2D startPos;
    public final Vector2D direction;
    public final Weapon weapon;
    public final double length;
    public ShotEvent(Vector2D direction, Vector2D startPos, int ownerId, Weapon weapon, double length){
        this.ownerId=ownerId;
        this.direction=direction;
        this.startPos=startPos;
        this.weapon=weapon;
        this.length=length;
    }
    
}