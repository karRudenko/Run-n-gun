package com.vitua.game.Engine;

import com.vitua.game.math.Vector2D;

public class RaycastResult {
    public final GameObject hitObject;
    public final Vector2D hitPoint;

    public RaycastResult(GameObject hitObject, Vector2D hitPoint) {
        this.hitObject = hitObject;
        this.hitPoint = hitPoint;
    }
} 