package com.vitua.game.Engine;

import com.vitua.game.math.Vector2D;

public class GameObject {
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
    }
    public double getSize() {
        return size;
    }
    public void translate(Vector2D oth){
        pos.addVector(oth);
    }
    public void rotate(double degree){
        rotation+=degree;
    }
    public Vector2D forward(){
        Vector2D res =new Vector2D(1,0);
        res.rotate(rotation);
        return res;
    }
    private Vector2D pos;
    private double rotation;
    private double size;
    private int id;
}
