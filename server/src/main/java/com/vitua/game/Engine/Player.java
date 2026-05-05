package com.vitua.game.Engine;


import com.vitua.game.DTO.MyPlayerData;
import com.vitua.game.DTO.PlayerData;
import com.vitua.game.Engine.Collisions.Collision;
import com.vitua.game.Engine.Weapons.Weapon;
import com.vitua.game.EventSystem.EventManager;
import com.vitua.game.Engine.Weapons.DebugGun;
import com.vitua.game.math.Vector2D;

public class Player extends GameObject{
    InputRecord playerInput=InputRecord.emptyInputRecord();
    Weapon weapon=null;

    public void injectInput(InputRecord input){
        playerInput=input;
    }
    public Player(Collision collision, EventManager eventManager){
        super(collision,eventManager);
        weapon=new DebugGun(this,eventManager);
    }

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
    @Override
    public void update(long nanoDeltaTime){
        if(!active) return;
        double deltaTimeSec=nanoDeltaTime/1e9;

        handleInput(deltaTimeSec);
        translate(Vector2D.vecScal(vel, deltaTimeSec));
        
    }
    private void handleInput(double deltaTimeSec){
        handleMovement(deltaTimeSec);
    }
    
    private void handleMovement(double deltaTimeSec){
        double friction=0.99;
        vel=Vector2D.addVectors(vel, Vector2D.vecScal(Vector2D.negativeVector2d(vel),friction*deltaTimeSec));

        if(playerInput.forwardHolded()){
            velAddition(forward(), deltaTimeSec);
        }
        if(playerInput.rightHolded()){
            velAddition(right(), deltaTimeSec);
        }
        if(playerInput.leftHolded()){
            velAddition(left(), deltaTimeSec);
        }
        if(playerInput.backHolded()){
            velAddition(back(), deltaTimeSec);
        }

    }
    private void velAddition(Vector2D dir, double deltaTimeSec){
        double maxSpeed=10;
        double acceleration=0.8;
        vel.addVector(Vector2D.vecScal(dir, acceleration));
        if(vel.length()>maxSpeed){
            vel=Vector2D.vecScal(Vector2D.normalaze(vel), maxSpeed);
        }
    }
    public void shoot(){
        weapon.shoot();
    }

}