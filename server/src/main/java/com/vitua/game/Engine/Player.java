package com.vitua.game.Engine;

import com.vitua.game.DTO.MyPlayerData;
import com.vitua.game.DTO.PlayerData;
import com.vitua.game.math.Vector2D;

public class Player extends GameObject{
    public Player(Vector2D pos, Collision col){
        super(pos, col);
    }
     public Player(Collision col){
        super(col);
    }
    public PlayerData gPlayerData(){
        return new PlayerData(name, pos.getM_x(), pos.getM_y(), collision.getCollision());
    }
    public MyPlayerData gMyPlayerData(){
        return  new MyPlayerData(name, pos.getM_x(), pos.getM_y(), collision.getCollision());
    }

}