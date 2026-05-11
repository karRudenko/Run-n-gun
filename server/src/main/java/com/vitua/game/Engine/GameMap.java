package com.vitua.game.Engine;

import java.util.Map;
import java.util.Queue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.stereotype.Component;
import java.util.AbstractMap;

import com.vitua.game.Engine.Collisions.Collision;
import com.vitua.game.Engine.Collisions.CollisionManager;
import com.vitua.game.Engine.Collisions.RaycastResult;
import com.vitua.game.Engine.Weapons.ShotRecord;
import com.vitua.game.EventSystem.Event;
import com.vitua.game.EventSystem.EventManager;
import com.vitua.game.EventSystem.EventType;
import com.vitua.game.EventSystem.KillEvent;
import com.vitua.game.EventSystem.ShotEvent;
import com.vitua.game.math.Vector2D;

import javafx.util.Pair;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameMap {
    protected Map<String, Integer> nameId;
    protected Map<Integer, GameObject> idObject;
    protected Map<String, InputRecord> nameInput;
    protected Map<Integer, Vector2D> posMemo;
    protected Map<Integer, List<Player>> visiblePlayers=new ConcurrentHashMap<>();
    protected Stack<Integer> ids; 
    protected List<Vector2D> spawnPoints;
    protected CollisionManager collisionManager;
    protected long lastUpdate=System.nanoTime();
    protected Queue<Player> spawnQueue = new LinkedList<>();
    public List<ShotRecord> shotRecords= new ArrayList<>();
    protected EventManager eventManager;
    protected double timeToRespawn=4000;
    protected List<Player> deadPlayers=new ArrayList<>();
    
    public GameMap(EventManager eventManager){
        this.eventManager=eventManager;
        nameId= new ConcurrentHashMap<>();
        idObject = new ConcurrentHashMap<>();
        ids=new Stack<>();
        posMemo= new ConcurrentHashMap<>();
        collisionManager= new CollisionManager(); 
        ids.add(0); 
        spawnPoints = new ArrayList<>();
        nameInput = new ConcurrentHashMap<>();
        spawnPoints=getBaseSpawnPoints();
        
        eventManager.subscribe(EventType.KILL_EVENT, e -> handleEvent(e));

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
        Player player = new Player(new Collision(Collision.getRecCollision(0.2,1),true), eventManager);
        player.setId(id);
        player.setName(nickName);
        nameId.put(nickName, id);
        idObject.put(id, player);
        spawnQueue.add(player);
        return true;
    }
    protected boolean spawnPlayer(Player player){
        Vector2D pos = null;
        Collision areaOfSpawn = new Collision(Collision.getRecCollision(1, 1));
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
        posMemo.put(player.getId(), pos);
        player.revive();
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
        

        for(var player : getActivePlayers()){
            player.injectInput(nameInput.getOrDefault(player.getName(), InputRecord.emptyInputRecord()));
        }

        List<GameObject> activeObjects=getActivGameObjects();
        while(!spawnQueue.isEmpty()){
            spawnPlayer(spawnQueue.poll());
        }

        long t = System.nanoTime();

        revivePlayers((t-lastUpdate)/1e6);
        for(GameObject o : activeObjects){
            o.update(t-lastUpdate);
        
        }  

        lastUpdate=System.nanoTime();
        

        for(ShotRecord record : shotRecords){
            if(record.shot().hitObject()!=null)
            record.shot().hitObject().handleHit(record);
        }
        shotRecords.clear();



        collisionManager.resolveColisions(activeObjects, posMemo);
        updateVision();
        writeMemo();
    }
    protected List<Vector2D> getBaseSpawnPoints(){
        List<Vector2D> res = new ArrayList<>(Arrays.asList(new Vector2D(10, 10), new Vector2D(20, 25), new Vector2D(20, 30)));
        return res;
    }
    public Player getPlayer(String nickName){
        return (Player)  idObject.get(nameId.get(nickName));
    }
    public List<Player> getActivePlayers(){
        return  getActivGameObjects().parallelStream().filter( o-> nameId.containsValue(o.getId())).map(o -> (Player)o).collect(Collectors.toList());
    }
    
    public void handleEvent(Event event){
        if(event instanceof KillEvent kill){
            kill.killed.disable();
            kill.killed.reviveIn(5000);
            deadPlayers.add(kill.killed);

        }
    }
    public void revivePlayers(double milisecTime){
        deadPlayers.removeIf(p -> { 
            p.lowerReviveTime(milisecTime);
            if(p.isAlive()){
                
                return spawnPlayer(p);
            }
            return false;
        });
    }

    public void registerShots(RaycastResult res, ShotEvent shot){
                        
        shotRecords.add(new ShotRecord(res,idObject.get(shot.ownerId),shot.weapon));

    }
    public void injectInput(String name, InputRecord input){
        nameInput.put(name, input);
    }

    public void addObject(GameObject obj) {
        int id = getId(); 
        obj.setId(id);    
        
        idObject.put(id, obj); 
        

        if (obj.getPos() != null) {
            posMemo.put(id, obj.getPos().copy());
        }
    }

    public void readObjects(MapData data) {
        for (GameObject obj : data.getObjects()) {
            addObject(obj);
            obj.enable();
        }

        List<Vector2D> fileSpawns = data.getSpawnPointsAsVectors();
        if (!fileSpawns.isEmpty()) {
            spawnPoints = fileSpawns;
        }
    }
    public void updateVision() {
        List<Player> active = getActivePlayers();
        HashSet<Player> set = new HashSet<>();
        double viewAngle = 40;
        double deltaAngle = 1;
        double distance = 40;

        for (Player p : active) {
            List<GameObject> toCheck = getActivGameObjects().stream()
                                        .filter(o -> o.getId() != p.getId())
                                        .toList();

            for (double angle = viewAngle; angle > -viewAngle; angle -= deltaAngle) {
                Vector2D dir = p.forward(); 
                dir.rotate(angle);
                
                Vector2D step = Vector2D.vecScal(dir, 0.1); 

                RaycastResult res = collisionManager.getRayCollision(p.getPos(), step, distance, toCheck);
                
                GameObject hit = res.hitObject();
                if (hit instanceof Player target) {
                    set.add(target);
                }
            }
            
            visiblePlayers.put(p.getId(), new ArrayList<>(set));
            set.clear();
        }
    }
    public  List<Player> getVisiblePlayers(int id) {
        return visiblePlayers.get(id);
    }
}
