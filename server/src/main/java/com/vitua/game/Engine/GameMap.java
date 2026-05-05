package com.vitua.game.Engine;

import java.util.Map;
import java.util.Queue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.List;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.stereotype.Component;

import com.vitua.game.Engine.Collisions.Collision;
import com.vitua.game.Engine.Collisions.CollisionManager;
import com.vitua.game.Engine.Collisions.RaycastResult;
import com.vitua.game.Engine.Weapons.ShotRecord;
import com.vitua.game.EventSystem.Event;
import com.vitua.game.EventSystem.EventManager;
import com.vitua.game.EventSystem.EventType;
import com.vitua.game.EventSystem.ShotEvent;
import com.vitua.game.math.Vector2D;

@Component
public class GameMap {
    protected Map<String, Integer> nameId;
    protected Map<Integer, GameObject> idObject;
    protected Map<Integer, Vector2D> posMemo;
    protected Stack<Integer> ids; 
    protected List<Vector2D> spawnPoints;
    protected CollisionManager collisionManager;
    protected long lastUpdate=System.nanoTime();
    protected Queue<Player> spawnQueue = new LinkedList<>();
    public List<ShotRecord> shotRecords= new ArrayList<>();
    protected EventManager eventManager;
    
    public GameMap(EventManager eventManager){
        this.eventManager=eventManager;
        nameId= new HashMap<>();
        idObject = new HashMap<>();
        ids=new Stack<>();
        posMemo= new HashMap<>();
        collisionManager= new CollisionManager(); 
        ids.add(0); 
        spawnPoints = new ArrayList<>();
        spawnPoints=getBaseSpawnPoints();
        eventManager.subscribe(EventType.SHOT_EVENT, (e) -> registerShots(e));
        

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
        Player player = new Player(new Collision(Collision.getRecCollision(0.2,1)), eventManager);
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
        Collection<GameObject> objects=getActivGameObjects();
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
    public List<GameObject> getActivGameObjects(){
        ArrayList<GameObject> activeObjects=new ArrayList<>();
        for(GameObject o : idObject.values()){
            if(o.isActive()){
                activeObjects.add(o);
            }
        }
        return activeObjects;
    }


    
    protected void writeMemo(){
        posMemo.clear();
        for(Map.Entry<Integer, GameObject> entry : idObject.entrySet()){
            posMemo.put(entry.getKey(), entry.getValue().getPos().copy());
        }
    }
    public void update(){
        List<GameObject> activeObjects=getActivGameObjects();

        while(!spawnQueue.isEmpty()){
            spawnPlayer(spawnQueue.poll());
        }
        long t = System.nanoTime();
        for(GameObject o : activeObjects){
            o.update(t-lastUpdate);
        }
        lastUpdate=System.nanoTime();
        collisionManager.resolveColisions(activeObjects);
        writeMemo();
    }
    protected List<Vector2D> getBaseSpawnPoints(){
        List<Vector2D> res = new ArrayList<>(Arrays.asList(new Vector2D(10, 10), new Vector2D(20, 25), new Vector2D(20, 30)));
        return res;
    }
    public Player getPlayer(String nickName){
        return (Player)  idObject.get(nameId.get(nickName));
    }
    
    public void registerShots(Event event){
        if(event instanceof ShotEvent shot){
            
            List<GameObject> objectsToCheck=getActivGameObjects().parallelStream().filter(o -> o.getId()!=shot.ownerId).collect(Collectors.toList());
            
            RaycastResult res= collisionManager.getRayCollision(shot.startPos, Vector2D.vecScal(shot.direction, 0.01), 
                                                                10, objectsToCheck);
            

            
            shotRecords.add(new ShotRecord(res,idObject.get(shot.ownerId)));
        }
    }

}
