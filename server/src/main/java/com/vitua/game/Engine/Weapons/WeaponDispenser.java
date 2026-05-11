package com.vitua.game.Engine.Weapons;

import java.util.ArrayList;
import java.util.List;

public class WeaponDispenser {

    private List<Weapon> weapons= new ArrayList<>();
    private Weapon cur = null;
    public WeaponDispenser(List<Weapon> weapons){
        this.weapons=weapons;
    }
    public Weapon getWeapon(int id){
        if(id == 0) id= 10;
        id=id-1;
        if(id>= weapons.size()) return cur;
        System.out.println();
        if(cur != null) cur.disable();
        Weapon res =weapons.get(id);
        res.enable();
        cur =res;
        return res;
    }
    public void update(long nanoDeltaTime){
        for(Weapon weapon : weapons){
            weapon.update(nanoDeltaTime);
        }
    }
    public void refillAmmo(){
        for(Weapon weapon : weapons){
            weapon.refillAmmo();
        }
    }
}