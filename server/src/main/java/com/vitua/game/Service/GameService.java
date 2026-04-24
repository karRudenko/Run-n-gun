package com.vitua.game.Service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;

import java.util.List;
import com.vitua.game.DTO.GameResponceDTO;
import com.vitua.game.DAO.MapDAO;
@Service
public class GameService {
    private final MapDAO mapDAO;
    GameService(MapDAO mapDAO){
        this.mapDAO=mapDAO;
    }
    public GameResponceDTO getAllPlayers(int id){
        return mapDAO.getAllPlayers(id);
    }


}