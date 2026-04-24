package com.vitua.game.DTO;
import java.util.List;
public record MyPlayerData(
    String name,
    double x,
    double y,
    List<Double> polygons 
){}
