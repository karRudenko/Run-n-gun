package com.vitua.game.Engine;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.util.Pair;
import javafx.scene.shape.Shape;
public class CollisionManager {
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
    public List<Pair<GameObject,GameObject>>  getCollisions(Collection<GameObject> objectsCol){
        List<Pair<GameObject,GameObject>> res= new ArrayList<>();
        List<GameObject> objects = new ArrayList<>(objectsCol);
        for(int i=0; i < objects.size();i++){
            GameObject first =objects.get(i);
            for(int j=i+1; j<objects.size();j++){
                GameObject second =objects.get(j);
                if(checkCollisionOfObjects(first,second))
                    res.add(new Pair<>(first, second));
            }
        }

        return res;
    }
    
}