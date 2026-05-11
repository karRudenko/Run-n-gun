package com.vitua.game.DTO;

import java.util.List;

public record WallDTO(
    int id,
    List<Double> polygons
) {
    
}
