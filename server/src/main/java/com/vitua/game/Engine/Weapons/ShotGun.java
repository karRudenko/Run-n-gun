package com.vitua.game.Engine.Weapons;

import com.vitua.game.Engine.GameObject;
import com.vitua.game.EventSystem.EventManager;
import com.vitua.game.EventSystem.EventType;
import com.vitua.game.EventSystem.ShotEvent;
import com.vitua.game.math.Vector2D;

public class ShotGun extends Weapon {
    double recoilStandart=10;
    double ammoInShot=5;


    public ShotGun(GameObject owner, EventManager eventManager){
        
        super(owner, eventManager);

        recoilStatic=5;
        recoilDynamic=10;
        damage=20;
        headShotDamage=30;
        maxAmmo=7;
        ammo=maxAmmo;

        fireRate=1.5;

        reloadTime=1000;


        recoilDynamic=0;
        recoilStatic=0;
        recoilStaticTime=200;
        interpolTime=100;
        

    }
    @Override
    public void shoot() {
        
        if(ammo<=0 && !isRealoading){
            reload();
            return;
        }
        if(!isReadyToShoot()){
            return;
        }
        
        ammo--;
        
        coolDown= 1/fireRate*1000;


        

        
        
        Vector2D shotPos=owner.getPos();

        for(int i=0;i<ammoInShot;i++){
            Vector2D ownerDirection=owner.forward();
            double angle = getRandomAngle(lastRecoil+recoilStandart);
            ownerDirection.rotate(angle);
            eventManager.sendEventDirect(EventType.SHOT_EVENT,new ShotEvent(ownerDirection, shotPos, owner.getId(),this, 50));
        }
        lastShotTime=0;
    }
    
    @Override
    public String getMyType() {

        return "Shotgun";
    }


}