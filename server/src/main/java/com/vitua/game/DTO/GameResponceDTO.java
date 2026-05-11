package com.vitua.game.DTO;
import java.util.List;

import com.vitua.game.Engine.Collisions.RaycastResult;
public record GameResponceDTO(
    
    MyPlayerData myPlayer,
    List<PlayerData> players,
    List<ShotDTO> shot,
    long serverTime,
    List<WallDTO> walls



){
    
}
