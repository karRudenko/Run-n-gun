
package com.vitua.game.DTO;
import java.util.List;
public record PlayerData(
    String name,
    double x,
    double y,
    List<Double> polygons 

){
    
}