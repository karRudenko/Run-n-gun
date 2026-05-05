package com.vitua.game.Engine.Weapons;
import com.vitua.game.Engine.GameObject;
import com.vitua.game.EventSystem.EventManager;

public abstract class Weapon {
    protected GameObject owner;
    protected double recoil=0;
    protected double damage=0;
    protected EventManager eventManager;
    Weapon(GameObject owner, EventManager eventManager){
        this.owner=owner;
        this.eventManager=eventManager;
    }
    public abstract void shoot();
     
}