package com.vitua.game.Engine;

import com.vitua.game.math.Vector2D;

public class GameObject {
    public GameObject(Vector2D pos, Collider collider){
        this.pos=pos;
        this.collider=collider;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setPos(Vector2D pos) {
        this.pos = pos.copy();
    }
    public void setRotation(double rotation) {
        this.rotation = rotation;
        collider.setRotations(rotation);
    }
    public double getSize() {
        return size;
    }
    public void translate(Vector2D oth){
        pos.addVector(oth);
    }
    public void rotate(double degree){
        rotation+=degree;
        collider.setRotations(rotation);
    }
    public Vector2D forward(){
        Vector2D res =new Vector2D(1,0);
        res.rotate(rotation);
        return res;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Collider getCollider() {
        return collider;
    }
    public Vector2D getPos() {
        return pos;
    }
    protected String name; 
    protected Vector2D pos;
    protected double rotation=0;
    protected Collider collider;
    protected double size;
    protected int id=0;
}
