package com.vitua.game.Engine.Collisions;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import java.util.List;

import com.vitua.game.math.Vector2D;

import java.util.ArrayList;
public class Collision {
    private Polygon collisionOryg;
    private Polygon collision;
    private double rot=0;   
    private Vector2D pos=new Vector2D(0, 0);
    private boolean impenetrable=false;
    public Collision(Polygon oryg){
        this.collisionOryg=oryg;
        updateGlobalCords();
    }
    public Collision(Polygon oryg, boolean impenetrable){
        this.collisionOryg=oryg;
        this.impenetrable=impenetrable;
        updateGlobalCords();
    }
    public boolean isImpenetrable() {
        return impenetrable;
    }
    public List<Double> getCollision(){
        return collision.getPoints();
    }
    public Polygon getCollisionShape(){
        return collision;
    }

    public void setPos(Vector2D pos){
        this.pos=pos.copy();
        updateGlobalCords();
    }
    public void setRotations(double degree){
        rot=degree;
        updateGlobalCords();

    }
    private void updateGlobalCords(){

        double rad=Math.toRadians(rot);
        List<Double> points=collisionOryg.getPoints();
        List<Double> res= new ArrayList<>(points.size());
        double sin=Math.sin(rad);
        double cos=Math.cos(rad);
        for(int i =0 ; i<points.size() ;i=i+2){
            double px=points.get(i);
            double py=points.get(i+1);
            
            double x = cos*px-py*sin;
            double y = cos*py+px*sin;
            x = x+pos.getM_x();
            y = y+pos.getM_y();

            res.add(x);
            res.add(y);
        }
        collision=new Polygon();
        collision.getPoints().addAll(res);

    }

    public static Polygon getRecCollision(double w, double h) {
    Polygon polygon = new Polygon();
    
    double halfW = w / 2.0;
    double halfH = h / 2.0;
    polygon.getPoints().addAll(new Double[]{
        -halfW, -halfH,  
         halfW, -halfH,  
         halfW,  halfH,  
        -halfW,  halfH   
    });

    return polygon;
}
}
