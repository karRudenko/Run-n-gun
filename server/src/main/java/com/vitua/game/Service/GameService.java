package com.vitua.game.Service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;

import java.util.List;
import com.vitua.game.DTO.GameResponceDTO;
@Service
public class GameService {
    
    public GameResponceDTO test(){
        return new GameResponceDTO(1,3);
    }
    public List<GameResponceDTO> testAll(){
        ArrayList<GameResponceDTO> list = new ArrayList<>();
        for(int i =0; i<10 ; i++){
            list.add(new GameResponceDTO(i,i));
        }
        return list;
    }

}