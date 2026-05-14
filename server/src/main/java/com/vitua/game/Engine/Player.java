package com.vitua.game.Engine;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vitua.game.DTO.MyPlayerData;
import com.vitua.game.DTO.PlayerData;
import com.vitua.game.Engine.Collisions.Collision;
import com.vitua.game.Engine.Weapons.Weapon;
import com.vitua.game.Engine.Weapons.WeaponDispenser;
import com.vitua.game.EventSystem.EventManager;
import com.vitua.game.EventSystem.EventType;
import com.vitua.game.EventSystem.KillEvent;
import com.vitua.game.Engine.Weapons.DebugGun;
import com.vitua.game.Engine.Weapons.ShotGun;
import com.vitua.game.Engine.Weapons.ShotRecord;
import com.vitua.game.math.Vector2D;

import javafx.scene.shape.Polygon;

public class Player extends GameObject{
    InputRecord playerInput=InputRecord.emptyInputRecord();
    Weapon weapon=null;
    double headShotDistance=0.3;
    double health;
    double maxHealth=100;
    double timeToRevive=0;
    protected WeaponDispenser weaponDispenser=null;

    public void injectInput(InputRecord input){
        playerInput=input;
    }
    public Player(Collision collision, EventManager eventManager){
        super(collision,eventManager);
        weaponDispenser= new WeaponDispenser(new ArrayList<>(Arrays.asList(new DebugGun(this, eventManager),
                                                            new ShotGun(this, eventManager))));
       this.weapon=weaponDispenser.getWeapon(1);
        
    }


    public Player(Vector2D pos, Collision col){
        super(pos, col);
    }
    public Player(Collision col){
        super(col);
    }
    public PlayerData gPlayerData(){
        return new PlayerData(name, id, pos.getM_x(), pos.getM_y(), rotation ,vel.getM_x(),vel.getM_y(), collision.getOrygRotated());
    }
    public MyPlayerData gMyPlayerData(){
        return  new MyPlayerData(name, id, pos.getM_x(), pos.getM_y(), rotation, vel.getM_x(),vel.getM_y(),weapon.gWeaponData(),
         collision.getOrygRotated(),health, timeToRevive);
    }

    @Override
    public void update(long nanoDeltaTime){
        if(!active) return;
        weaponDispenser.update(nanoDeltaTime);
        double deltaTimeSec=nanoDeltaTime/1e9;
        handleInput(deltaTimeSec);
        translate(Vector2D.vecScal(vel, deltaTimeSec));
        
    }
    public void reviveIn(double timeMiliSec){
        timeToRevive=timeMiliSec;
    }
    public void lowerReviveTime(double timeMiliSec){
        timeToRevive-=timeMiliSec;
    }
    public boolean isAlive(){
        return timeToRevive<0;
    }
    
    private void handleInput(double deltaTimeSec){
        handleMovement(deltaTimeSec);
    }
    
    private void handleMovement(double deltaTimeSec){
        vel=new Vector2D(0, 0);
        setRotation(Math.toDegrees(Math.atan2(playerInput.mousePosY(), playerInput.mousePosX())));
        if(playerInput.forwardHolded()){
            velAddition(new Vector2D(0,1), deltaTimeSec);
        }
        if(playerInput.rightHolded()){
            velAddition(new Vector2D(1,0), deltaTimeSec);
        }
        if(playerInput.leftHolded()){
            velAddition(new Vector2D(-1,0), deltaTimeSec);
        }
        if(playerInput.backHolded()){
            velAddition(new Vector2D(0,-1), deltaTimeSec);
        }
        if(playerInput.leftMouseHolded()){
            shoot();
        }

        if(playerInput.reloadPressed()){
            weapon.reload();
        }

        int numPressed = playerInput.getPressedDigit();

        if(numPressed>=0){
            this.weapon=weaponDispenser.getWeapon(numPressed);
        }

    }
    private void velAddition(Vector2D dir, double deltaTimeSec){
        double maxSpeed=6;
        double acceleration=100000000;
        vel.addVector(Vector2D.vecScal(dir, maxSpeed));
        if(vel.length()<0.1){
            vel=new Vector2D(0, 0);
        }
        else{
            vel=Vector2D.vecScal(Vector2D.normalaze(vel), maxSpeed);
        }

    }
    public void shoot(){
        weapon.shoot();
    }
    @Override
    public void handleHit(ShotRecord record){
        Vector2D hitPoint = record.shot().hitPoint();
        Vector2D relative = Vector2D.addVectors(Vector2D.negativeVector2d(hitPoint), pos);
        if(relative.length()>headShotDistance){
            takeDamage(record, record.weapon().gDamage());
        }
        else{
            takeDamage(record ,record.weapon().gHeadshotDamage());
        }
    }
    public void revive(){
        health = maxHealth;
        weaponDispenser.refillAmmo();
    }
    public void takeDamage(ShotRecord record, double damage){
        health -= damage;
        if(health<=0){
            die(record);
        }
    }
    public void die(ShotRecord record){
        if(record.owner() instanceof Player killer){
            eventManager.sendEventDirect(EventType.KILL_EVENT, new KillEvent(weapon,   killer, this));
        }
    }

}