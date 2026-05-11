package com.vitua.game.Engine;


import com.vitua.game.Engine.Collisions.Collision;
import com.vitua.game.Engine.Weapons.ShotRecord;
import com.vitua.game.EventSystem.EventManager;
import com.vitua.game.math.Vector2D;

public class GameObject {
    public GameObject(Collision collision, EventManager eventManager){
        this.collision=collision;
        this.eventManager=eventManager;
    }
    public GameObject(Vector2D pos, Collision collision){
        this.collision=collision;
        setPos(pos);
    }
    public GameObject(Collision collision){
        this.collision=collision;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setPos(Vector2D pos) {
        if (pos == null) {

            return; 
        }
        this.pos = pos.copy();
        collision.setPos(pos);
    }
    public void setRotation(double rotation) {
        this.rotation = rotation;
        collision.setRotations(rotation);
    }
    public double getSize() {
        return size;
    }
    public void translate(Vector2D oth){
        setPos(Vector2D.addVectors(oth, pos));
    }
    public void rotate(double degree){
        rotation+=degree;
        collision.setRotations(rotation);
    }
    public Vector2D forward(){
        Vector2D res =new Vector2D(1,0);
        res.rotate(rotation);
        return res;
    }
    public Vector2D left(){
        Vector2D res =new Vector2D(1,0);
        res.rotate(rotation+90.0);
        return res;
    }
    public Vector2D right(){
        Vector2D res =new Vector2D(1,0);
        res.rotate(rotation-90.0);
        return res;
    }
    public Vector2D back(){
        Vector2D res =new Vector2D(1,0);
        res.rotate(rotation-180);
        return res;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Collision getCollision() {
        return collision;
    }
    public Vector2D getPos() {
        return pos.copy();
    }
    public void update(long nanoDeltaTime){
        if(!active) return;
        double deltaTimeSec=nanoDeltaTime/1e9;
        translate(Vector2D.vecScal(vel, deltaTimeSec));
    }
    public void setVelocity(Vector2D newVel){
        vel=newVel.copy();
    }
    public Vector2D getVelocity(){
        return vel;
    }
    public void enable(){
        active=true;
    }
    public void disable(){
        active=false;
    }
    public boolean isActive() {
        return active;
    }
    public void handleHit(ShotRecord record){
        
    }
    public double getRotation(){
        return rotation;
    }
    protected String name; 
    protected Vector2D pos = new Vector2D(0, 0);
    protected Vector2D vel = new Vector2D(0, 0);
    protected double rotation=0;
    protected Collision collision;
    protected double size;
    protected int id=-1;
    protected boolean active =false;
    protected EventManager eventManager=null;
}
