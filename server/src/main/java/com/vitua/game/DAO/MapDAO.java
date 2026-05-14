package com.vitua.game.DAO;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.vitua.game.DTO.GameResponceDTO;
import com.vitua.game.DTO.MyPlayerData;
import com.vitua.game.DTO.PlayerData;
import com.vitua.game.DTO.ShotDTO;
import com.vitua.game.DTO.WallDTO;
import com.vitua.game.Engine.GameMap;
import com.vitua.game.Engine.GameObject;
import com.vitua.game.Engine.InputRecord;
import com.vitua.game.Engine.MapData;
import com.vitua.game.Engine.Player;
import com.vitua.game.Engine.Wall;
import com.vitua.game.Engine.Collisions.CollisionManager;
import com.vitua.game.Engine.Collisions.RaycastResult;
import com.vitua.game.Engine.Weapons.ShotRecord;
import com.vitua.game.EventSystem.EventType;
import com.vitua.game.EventSystem.ShotEvent;
import com.vitua.game.EventSystem.Event;
import com.vitua.game.Engine.GameMap;
import java.util.stream.Collectors;
import com.vitua.game.math.Vector2D;
import com.vitua.game.EventSystem.EventManager;
import java.util.HashSet;

@Component
public class MapDAO {
    private final GameMap map;
    private CollisionManager collisionManager = new CollisionManager();
    private boolean running = true;
    private final EventManager eventHandler;
    private HashSet<String> nicks=new HashSet<>();

    private HashMap<String, ArrayList<ShotDTO>> shots = new HashMap<>();

    public MapDAO(GameMap map, EventManager eventHandler) {
        this.map = map;
        this.eventHandler=eventHandler;
        
    }

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public CompletableFuture<String> startGame() {

        eventHandler.subscribe(EventType.SHOT_EVENT, (e)->handleEvents(e));
        try{
            map.readObjects(MapData.fromResource("/mirage.json"));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        long curtimeUpdate=Math.round(System.nanoTime()/1e6);
        long lastUpdate=Math.round(System.nanoTime()/1e6);

        while (running && !Thread.currentThread().isInterrupted()) {

            long timeToWait=Math.max(15-lastUpdate+curtimeUpdate,0);
            

            
            try {
                Thread.sleep(timeToWait); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return CompletableFuture.failedFuture(e);

            }
            curtimeUpdate=Math.round(System.nanoTime()/1e6);
            map.update();
            lastUpdate=Math.round(System.nanoTime()/1e6);
        }
        return CompletableFuture.completedFuture("game finished");
    }
    public GameResponceDTO getAllPlayers(String name) {
        
        if(!nicks.contains(name)) return null;


        
        Player myPlayer=map.getPlayer(name);
        List<PlayerData> playerList = new ArrayList<>();
        MyPlayerData myPlayerData = myPlayer.gMyPlayerData();
        var visibleOthers = map.getVisiblePlayers(myPlayer.getId());
        if(visibleOthers != null){
            for ( Player obj: visibleOthers) {
                if(!obj.getName().equals(name))
                playerList.add(obj.gPlayerData());
            }
        }
        List<WallDTO> walls = new ArrayList<>();
        for(GameObject o : map.getIdObject().values()){
            if(o instanceof Wall wall){
                walls.addLast(wall.gWallDTO());
            }
        }
        ArrayList<ShotDTO> playerShots =
            shots.computeIfAbsent(name, k -> new ArrayList<>());

        GameResponceDTO res = new GameResponceDTO(
            myPlayerData,
            playerList,
            new ArrayList<>(playerShots),
            System.nanoTime() / 1_000_000,
            walls
        );

        playerShots.clear();

        return res;
    }
    public void injectInput(String nickname, InputRecord data){
        map.injectInput(nickname, data);
    }
    public boolean addPlayer(String name){  
        nicks.add(name);
        shots.put(name, new ArrayList<>());
        return map.addPlayer(name);
    }
    public void handleEvents(Event e){
        if(e instanceof ShotEvent shot){
            List<GameObject> objectsToCheck=map.getActivGameObjects().parallelStream().filter(o -> o.getId()!=shot.ownerId).collect(Collectors.toList());
            RaycastResult res= collisionManager.getRayCollision(shot.startPos, Vector2D.vecScal(shot.direction, 0.01), 
                                                               shot.length, objectsToCheck);
            for(var list :  shots.values()){
                if(res.hitObject() !=null && res.hitObject().isActive()){



                    list.add(new ShotDTO(shot.ownerId,
                            res.startPoint().getM_x(),res.startPoint().getM_y(),
                            res.hitObject().getId(),
                            res.hitPoint().getM_x(),
                            res.hitPoint().getM_y(),
                            res.hitPoint().getM_x()-res.hitObject().getPos().getM_x(),
                            res.hitPoint().getM_y()-res.hitObject().getPos().getM_y()
                            ));
                }
                else{
                    list.add(new ShotDTO(shot.ownerId,
                            res.startPoint().getM_x(),res.startPoint().getM_y(),
                            -1,
                            res.hitPoint().getM_x(),
                            res.hitPoint().getM_y(),
                            0,
                            0
                            ));                  
                }
            }
            map.registerShots(res,shot);

        }
    }
    public List<String> getAllNicks(){
        return new ArrayList<>(nicks);
    }
        
}
