package com.vitua.game.Engine.Collisions;

import com.vitua.game.Engine.GameObject;
import com.vitua.game.math.Vector2D;

public record RaycastResult(
    GameObject hitObject,
    Vector2D hitPoint,
    Vector2D startPoint

) {
} 

