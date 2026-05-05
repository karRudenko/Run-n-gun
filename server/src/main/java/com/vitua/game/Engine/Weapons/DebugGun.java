package com.vitua.game.Engine.Weapons;

import com.vitua.game.Engine.GameObject;
import com.vitua.game.EventSystem.EventManager;
import com.vitua.game.EventSystem.EventType;
import com.vitua.game.EventSystem.ShotEvent;
import com.vitua.game.math.Vector2D;

public class DebugGun extends Weapon {
    
    public DebugGun(GameObject owner, EventManager eventManager){
        super(owner, eventManager);
    }
    @Override
    public void shoot() {
        Vector2D ownerDirection=owner.forward();
        Vector2D shotPos=owner.getPos();
        eventManager.sendEventDirect(EventType.SHOT_EVENT,new ShotEvent(ownerDirection, shotPos, owner.getId()));
    }
}
