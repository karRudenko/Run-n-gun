package com.vitua.game.DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.vitua.game.DTO.GameResponceDTO;
import com.vitua.game.DTO.MyPlayerData;
import com.vitua.game.DTO.PlayerData;
import com.vitua.game.Engine.GameMap;
import com.vitua.game.Engine.GameObject;
import com.vitua.game.Engine.InputRecord;
import com.vitua.game.Engine.Player;
import com.vitua.game.Engine.GameMap;

@Component
public class MapDAO {
    private final GameMap map;
    private boolean running = true;
    public MapDAO(GameMap map) {
        this.map = map;
        
    }

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public CompletableFuture<String> startGame() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(16); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return CompletableFuture.failedFuture(e);
            }
            map.update();
        }
        return CompletableFuture.completedFuture("game finished");
    }
    public GameResponceDTO getAllPlayers(String name) {
        var nameId = map.getNameId(); 
        var idObject = map.getIdObject(); 
        
        List<PlayerData> playerList = new ArrayList<>();
        MyPlayerData myPlayerData = null;

        for (Integer id : nameId.values()) {
            GameObject obj = idObject.get(id);
            String myName=obj.getName();
            
            if(!obj.isActive()) continue;
                if(obj instanceof Player){
                if (myName.equals(name)) {
                    myPlayerData = new MyPlayerData(
                        obj.getName(),
                        obj.getPos().getM_x(),
                        obj.getPos().getM_y(),
                        obj.getCollision().getCollision()
                    );
                } else {
                    PlayerData data = new PlayerData(
                        obj.getName(),
                        obj.getPos().getM_x(),
                        obj.getPos().getM_y(),
                        obj.getCollision().getCollision()
                    );
                    playerList.add(data);
                }
            }
        }

        return new GameResponceDTO(myPlayerData, playerList);
    }
    public void injectInput(String nickname, InputRecord data){
        Player  p = map.getPlayer(nickname);
        if(p != null) p.injectInput(data);
    }
    public boolean addPlayer(String name){
        return map.addPlayer(name);
    }
        
}
