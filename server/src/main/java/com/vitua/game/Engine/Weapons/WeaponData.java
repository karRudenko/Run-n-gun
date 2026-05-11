package com.vitua.game.Engine.Weapons;

public record WeaponData(
    String name,
    int ammo,
    int maxAmmo,
    double recoil,
    boolean isRealoading
) {
    
}
