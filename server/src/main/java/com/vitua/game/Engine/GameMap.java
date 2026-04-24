package com.vitua.game.Engine;

import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

import org.springframework.stereotype.Component;

import com.vitua.game.math.Vector2D;
@Component
public class GameMap {
    Map<String, Integer> nameId;
    Map<Integer, GameObject> idObject;
    Stack<Integer> ids; 
    public GameMap(){
        nameId= new HashMap<>();
        idObject = new HashMap<>();
        ids=new Stack<>();
        ids.add(0);
    }
    private int getId(){
        int id = ids.peek();
        ids.pop();
        if(ids.isEmpty()) ids.push(id+1);
        return id;
    }
    public boolean addPlayer(String nickName){
        if(nameId.containsKey(nickName)) return false;
        int id =getId();
        Player player = new Player(new Vector2D(id*2, id*2), new Collider(Collider.getRecCollider(0.2,1)));
        player.setId(id);
        player.setName(nickName);
        nameId.put(nickName, id);
        idObject.put(id, player);
        return true;
    }
    public Map<Integer, GameObject> getIdObject() {
        return idObject;
    }
    public Map<String, Integer> getNameId() {
        return nameId;
    }
}
