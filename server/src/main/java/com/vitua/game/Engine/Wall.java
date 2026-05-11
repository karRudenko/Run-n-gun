package com.vitua.game.Engine;

import com.vitua.game.DTO.WallDTO;
import com.vitua.game.Engine.Collisions.Collision;
import com.vitua.game.EventSystem.EventManager;
import com.vitua.game.math.Vector2D;

public class Wall extends GameObject {
    public Wall(Vector2D pos, double h, double w){
        super(new Collision(Collision.getRecCollision(w, h),true));
        setPos(pos.copy());
    }
    public WallDTO gWallDTO(){
        return new WallDTO(id, collision.getCollision());
    }
}
