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
import com.vitua.game.Engine.GameMap;

@Component
public class MapDAO {
    private final GameMap map;
    private boolean running = true;
    public MapDAO(GameMap map) {
        this.map = map;
        map.addPlayer("dasda");
        map.addPlayer("das32a");
        map.addPlayer("dasdaaaa");
        map.addPlayer("dasdadsdasddadda");
        
    }

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public CompletableFuture<String> startGame() {
        while (running && !Thread.currentThread().isInterrupted()) {
            map.update();
            try {
                Thread.sleep(16); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return CompletableFuture.failedFuture(e);
            }
        }
        return CompletableFuture.completedFuture("game finished");
    }
    public GameResponceDTO getAllPlayers(int mySessionId) {
        var nameId = map.getNameId(); 
        var idObject = map.getIdObject(); 
        
        List<PlayerData> playerList = new ArrayList<>();
        MyPlayerData myPlayerData = null;

        for (Integer id : nameId.values()) {
            GameObject obj = idObject.get(id);
            if (obj == null) continue;



            if (id == mySessionId) {
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

        return new GameResponceDTO(myPlayerData, playerList);
    }
        
}
