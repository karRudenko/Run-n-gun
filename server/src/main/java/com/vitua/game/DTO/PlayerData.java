
package com.vitua.game.DTO;
import java.util.List;
public record PlayerData(
    String name,
    int id,
    double x,
    double y,
    double rotation,
    double xSpeed,
    double ySpeed,
    List<Double> polygons 

){
    
}