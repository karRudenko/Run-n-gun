package com.vitua.game.Engine;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vitua.game.math.Vector2D;

import javafx.util.Pair;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
public class CollisionManager {
    List<Pair<GameObject,GameObject>> softColl;
    List<GameObject> hardColl;
    public void resolveColisions(Collection<GameObject> objects){
        getCollisions(objects);
        for(Pair<GameObject,GameObject> pair : softColl){
            handleCollision(pair);
            
        }
        
        for(GameObject obj : hardColl){
            resolveCollision(obj);
        }
    }


    private void handleCollision(Pair<GameObject,GameObject> collisions){

    }
    private void resolveCollision(GameObject object){

    }


    public boolean checkCollisionOfObjects(GameObject a, GameObject b){
        Shape colA = a.getCollision().getCollisionShape();
        Shape colB =b.getCollision().getCollisionShape();
        return Shape.intersect(colA, colB).getBoundsInLocal().getWidth() > 0;
    }

    public boolean checkCollisionOfObjects(GameObject a, Point2D b){

        Shape colA = a.getCollision().getCollisionShape();
       return colA.contains(b);

    }

     public boolean checkCollisionOfObjects(Collision a, Collision b){
        Shape colA = a.getCollisionShape();
        Shape colB =b.getCollisionShape();
        return Shape.intersect(colA, colB).getBoundsInLocal().getWidth() != -1;
    }
    public boolean checkCollisionOfObjects(Collision  a, GameObject b){
        Shape colA = a.getCollisionShape();
        Shape colB =b.getCollision().getCollisionShape();
        return Shape.intersect(colA, colB).getBoundsInLocal().getWidth() != -1;
    }

    public void  getCollisions(Collection<GameObject> objs){
        softColl= new ArrayList<>();
        hardColl= new ArrayList<>();

        List<GameObject> objects = new ArrayList<>(objs);
        for(int i=0; i < objects.size();i++){
            GameObject first =objects.get(i);
            for(int j=i+1; j<objects.size();j++){
                GameObject second =objects.get(j);
                if(checkCollisionOfObjects(first,second)){
                    softColl.add(new Pair<>(first, second));

                    if(first.getCollision().isImpenetrable()){
                        hardColl.add(second);
                    }
                    if(second.getCollision().isImpenetrable()){
                        hardColl.add(first);
                    }


                }


            }
        }

    }


    public int getSoftCollSize() {
        return softColl.size();
    }

    public RaycastResult getRayCollision(Vector2D startPoint, Vector2D dir, double distance, Collection<GameObject> objects){
        Collection<GameObject> objectsToCheck = new  ArrayList<>();
        for(GameObject o : objects){
            if(Vector2D.addVectors(Vector2D.negativeVector2d(startPoint), o.getPos()).dotProduct(dir)>0){
                objectsToCheck.add(o);
            }
        }

        double checkDistance=dir.length();

       Point2D point = new Point2D(startPoint.getM_x()-dir.getM_x(), startPoint.getM_y()-dir.getM_y());

        for(double checked=0.0; checked <= distance; checked+=checkDistance){
            point = point.add(dir.getM_x(),dir.getM_y());

            for(GameObject o : objectsToCheck){

                // List<Double> points =o.getCollision().getCollision();
                // double halfH=points.get(4);
                // double halfW=points.get(5);
                // Vector2D objPos=o.getPos();

                // boolean isX=point.getM_x()>objPos.getM_x()-halfW && point.getM_x()<objPos.getM_x()+halfW ;
                // boolean isY=point.getM_y()>objPos.getM_y()-halfH && point.getM_y()<objPos.getM_y()+halfH ;
                
                if(checkCollisionOfObjects(o,point)){

                    return new RaycastResult(o, new Vector2D(point.getX(), point.getY()));
                }
            }

        }
        return new RaycastResult(null,null);
    }
    
}