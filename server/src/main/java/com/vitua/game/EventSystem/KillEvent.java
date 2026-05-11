package com.vitua.game.EventSystem;

import com.vitua.game.Engine.Player;
import com.vitua.game.Engine.Weapons.Weapon;

public class KillEvent implements Event{
    public final Weapon weapon;
    public final Player killer;
    public final Player killed;
    public KillEvent(Weapon weapon, Player killer, Player killed){
        this.weapon=weapon;
        this.killer=killer;
        this.killed = killed;
    }
}
