package com.vitua.game.Engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.vitua.game.DAO.MapDAO;
import com.vitua.game.DTO.MapDTO;
import com.vitua.game.EventSystem.Event;
import com.vitua.game.EventSystem.EventManager;
import com.vitua.game.EventSystem.EventType;
import com.vitua.game.EventSystem.KillEvent;

import javafx.scene.control.Tab;

public class MapRecords {
    protected Set<Integer> activeIds;
    protected Map<Integer, Integer> score;
    protected EventManager eventManager;
    public MapRecords(EventManager eventManager){
        activeIds=new HashSet<>();
        score = new ConcurrentHashMap<>();
        this.eventManager=eventManager;
        eventManager.subscribe(EventType.KILL_EVENT, e -> handleEvent(e));
    }
    public void addPlayer(int id){
        activeIds.add(id);
        score.put(id, 0);
    }
    public void deletePlayer(int id){
        activeIds.remove(id);
        score.remove(id);
    }
    public void handleEvent(Event e){
        if(e instanceof KillEvent killEvent){
            int killerId=killEvent.killer.getId();
            score.put(killerId, score.get(killerId) +1);
        }
    }

    public MapDTO gMapDTO(List<Player> objecst){
        List<String> names = new ArrayList<>();
        List<Integer> scores = new ArrayList<>();
        for(GameObject o : objecst){
            names.add(o.getName());
            scores.add(score.get(o.getId()));
        }
        return new MapDTO(names, scores);
    }

}
