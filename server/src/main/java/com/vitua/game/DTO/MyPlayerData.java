package com.vitua.game.DTO;
import java.util.List;

import com.vitua.game.Engine.Weapons.WeaponData;
public record MyPlayerData(
    String name,
    int id,
    double x,
    double y,
    double rotation,
    double xSpeed,
    double ySpeed,
    WeaponData weapon,
    List<Double> polygons,
    double health,
    double timeToRevive
){}
