package com.vitua.game.Service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

import java.util.List;
import com.vitua.game.DTO.GameResponceDTO;
import com.vitua.game.Engine.InputRecord;
import com.vitua.game.DAO.MapDAO;
@Service
public class GameService {
    private final MapDAO mapDAO;
    private final SimpMessagingTemplate template;
    GameService(SimpMessagingTemplate template,  MapDAO mapDAO){
        this.mapDAO=mapDAO;
        this.template =template;
    }
    public GameResponceDTO getAllPlayers(String name){
        return mapDAO.getAllPlayers(name);
    }
    public void injectInput(String nickname, InputRecord data){
        mapDAO.injectInput(nickname, data);
    }
    public boolean register(String nickName){
        return mapDAO.addPlayer(nickName);
    }

    @Scheduled(fixedRate = 16   ) 
    public void broadcast() {
        var nicks = mapDAO.getAllNicks();
        if(nicks==null) return;
        for (String nick : nicks) {
            GameResponceDTO dto = mapDAO.getAllPlayers(nick);




            template.convertAndSend("/return/data/" + nick, dto);
        }
    }

}