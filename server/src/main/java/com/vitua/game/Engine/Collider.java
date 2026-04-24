package com.vitua.game.Engine;
import javafx.scene.shape.Polygon;
import java.util.List;
import java.util.ArrayList;
public class Collider {
    private Polygon colliderOryg;
    private Polygon collider;
    private double rot=0;
    public Collider(Polygon oryg){
        this.colliderOryg=oryg;
        this.collider=oryg;
    }
    public List<Double> getCollider(){
        return collider.getPoints();
    }

    public void setRotations(double degree){
        double rad=Math.toRadians(degree);
        rot=degree;
        List<Double> points=colliderOryg.getPoints();
        List<Double> res= new ArrayList<>(points.size());
        for(int i =0 ; i<points.size() ;i=i+2){
            double x = Math.cos(rad)*points.get(i)-points.get(i+1)*Math.sin(rad);
            double y = Math.cos(rad)*points.get(i+1)+points.get(i)*Math.sin(rad);

            res.add(x);
            res.add(y);
        }
        collider=new Polygon();
        collider.getPoints().addAll(res);
    }

    public static Polygon getRecCollider(double w, double h) {
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
