package com.vitua.game.Engine.Weapons;
import java.util.Random;

import com.vitua.game.Engine.GameObject;
import com.vitua.game.EventSystem.EventManager;

public abstract class Weapon {
    protected GameObject owner;
    Random r = new Random();

    protected double damage;
    protected double headShotDamage;
    protected int maxAmmo;
    protected int ammo;

    protected double coolDown=0;
    protected double fireRate;
    protected double lastShotTime;

    protected double reloadTime;
    protected double reloadTimer;
    protected boolean isRealoading=false;

    protected double recoilStatic;
    protected double recoilStaticTime;
    protected double recoilDynamic;
    protected double lastRecoil;
    protected double interpolTime;

    protected EventManager eventManager;
    Weapon(GameObject owner, EventManager eventManager){
        this.owner=owner;
        this.eventManager=eventManager;
        ammo=maxAmmo;
    }
    public void update(long nanoSecDelta){
        double miliSec=nanoSecDelta/1e6;

        if (coolDown > 0) {
                coolDown -= miliSec;
        }

        if (isRealoading) {
                reloadTimer -= miliSec;
                
                if (reloadTimer <= 0) {
                    ammo = maxAmmo;
                    isRealoading = false;
                    reloadTimer = 0;
                }
        }
        lastShotTime+=miliSec;
        
    }
    public abstract void shoot();

    public void reload(){
        isRealoading=true;
        reloadTimer=reloadTime;
    }
    public boolean isReadyToShoot(){
        return !isRealoading && coolDown<=0;
    }
    public void enable(){
        isRealoading=false;
    }


    public double calculateRecoile(double time){
        double targetRecoil=0;
        if(time<recoilStaticTime){
            targetRecoil+=recoilStatic;
        }
        if(owner.getVelocity().length()>0){
            targetRecoil+=recoilDynamic;
        }
        
        if(time>interpolTime){
            return targetRecoil;
        }
        
        double res=lastRecoil+time*(targetRecoil-lastRecoil)/interpolTime;
        return res;    
    }
    public double curRecoil(){
        return calculateRecoile(lastShotTime);
    }
    public double getRandomAngle(double angle){
        return  -angle + 2*angle * r.nextDouble();

    }
    public WeaponData gWeaponData(){
        return new WeaponData(getMyType(),ammo, maxAmmo, curRecoil(), isRealoading);
    }
    public abstract String getMyType();
    public double gHeadshotDamage(){
        return headShotDamage + -0.5 + 0.10 * r.nextDouble();
    }
    public double gDamage(){
        return damage + -0.5 + 0.10*r.nextDouble();
    }
}