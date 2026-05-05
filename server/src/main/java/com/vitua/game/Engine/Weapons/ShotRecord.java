package com.vitua.game.Engine.Weapons;

import com.vitua.game.Engine.GameObject;
import com.vitua.game.Engine.Collisions.RaycastResult;

public record ShotRecord(
    RaycastResult shot,
    GameObject owner
) {
    
}
