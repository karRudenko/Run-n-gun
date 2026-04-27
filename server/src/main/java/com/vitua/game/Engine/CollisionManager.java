package com.vitua.game.Engine;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.util.Pair;
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
    
}