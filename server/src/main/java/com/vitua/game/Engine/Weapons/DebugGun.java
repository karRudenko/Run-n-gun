package com.vitua.game.Engine.Weapons;

import com.vitua.game.Engine.GameObject;
import com.vitua.game.EventSystem.EventManager;
import com.vitua.game.EventSystem.EventType;
import com.vitua.game.EventSystem.ShotEvent;
import com.vitua.game.math.Vector2D;

public class DebugGun extends Weapon {



    public DebugGun(GameObject owner, EventManager eventManager){
        super(owner, eventManager);

        recoilStatic=5;
        recoilDynamic=10;
        damage=10;
        headShotDamage=40;
        maxAmmo=30;
        ammo=maxAmmo;

        coolDown=0;
        fireRate=10.0;

        reloadTime=1000;
        reloadTimer=0;

        recoilDynamic=10;
        recoilStatic=5;
        recoilStaticTime=200;
        interpolTime=500;
        

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


        double angle = getRandomAngle(lastRecoil);

        
        Vector2D ownerDirection=owner.forward();
        ownerDirection.rotate(angle);
        Vector2D shotPos=owner.getPos();

        eventManager.sendEventDirect(EventType.SHOT_EVENT,new ShotEvent(ownerDirection, shotPos, owner.getId(),this, 50));
        lastShotTime=0;
    }
    
    @Override
    public String getMyType() {

        return "DebugGun";
    }


}
