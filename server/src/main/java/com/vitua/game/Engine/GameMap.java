package com.vitua.game.Engine;

import java.util.Map;
import java.util.Queue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javafx.util.Pair;
import org.springframework.stereotype.Component;

import com.vitua.game.math.Vector2D;

@Component
public class GameMap {
    Map<String, Integer> nameId;
    Map<Integer, GameObject> idObject;
    Map<Integer, Vector2D> posMemo;
    Stack<Integer> ids; 
    List<Vector2D> spawnPoints;
    CollisionManager collisionManager;
    long lastUpdate=System.nanoTime();
    Queue<Player> spawnQueue = new LinkedList<>();
    public GameMap(){
        nameId= new HashMap<>();
        idObject = new HashMap<>();
        ids=new Stack<>();
        posMemo= new HashMap<>();
        collisionManager= new CollisionManager(); 
        ids.add(0); 
        spawnPoints = new ArrayList<>();
        spawnPoints=getBaseSpawnPoints();

    }
    
    protected int getId(){
        int id = ids.peek();
        ids.pop();
        if(ids.isEmpty()) ids.push(id+1);
        return id;
    }
    public boolean addPlayer(String nickName){
        
        if(nameId.containsKey(nickName)) return false;
        int id =getId();
        Player player = new Player(new Collision(Collision.getRecCollision(0.2,1)));
        player.setId(id);
        player.setName(nickName);
        nameId.put(nickName, id);
        idObject.put(id, player);
        spawnQueue.add(player);
        return true;
    }
    protected boolean spawnPlayer(Player player){
        Vector2D pos = null;
        Collision areaOfSpawn = new Collision(Collision.getRecCollision(2, 2));
        Collection<GameObject> objects=idObject.values();
        for(Vector2D v : spawnPoints){
            pos=v;
            areaOfSpawn.setPos(pos);
            for(GameObject o : objects){
                if(collisionManager.checkCollisionOfObjects(areaOfSpawn,o)){
                    pos=null;
                    break;
                }

            }
            if(pos!=null){
                break;
            }
        }
        if(pos == null){ 
            spawnQueue.add(player);
            return false;}
        player.setPos(pos.copy());
        player.enable();
        return true;
    }
    public Map<Integer, GameObject> getIdObject() {
        return idObject;
    }
    public Map<String, Integer> getNameId() {
        return nameId;
    }
    protected void writeMemo(){
        posMemo.clear();
        for(Map.Entry<Integer, GameObject> entry : idObject.entrySet()){
            posMemo.put(entry.getKey(), entry.getValue().getPos().copy());
        }
    }
    public void update(){
        if(!spawnQueue.isEmpty()){
            spawnPlayer(spawnQueue.poll());
        }
        long t = System.nanoTime();
        for(GameObject o : idObject.values()){
            o.update(t-lastUpdate);
        }
        lastUpdate=System.nanoTime();
        collisionManager.resolveColisions(idObject.values());
        writeMemo();
    }
    protected List<Vector2D> getBaseSpawnPoints(){
        List<Vector2D> res = new ArrayList<>(Arrays.asList(new Vector2D(10, 10), new Vector2D(20, 25), new Vector2D(20, 30)));
        return res;
    }
    public Player getPlayer(String nickName){
        return (Player)  idObject.get(nameId.get(nickName));
    }

}
