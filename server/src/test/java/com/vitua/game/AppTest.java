package com.vitua.game;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;

import com.vitua.game.Engine.Collision;
import com.vitua.game.Engine.CollisionManager;
import com.vitua.game.Engine.GameMap;
import com.vitua.game.Engine.GameObject;
import com.vitua.game.Engine.Player;
import com.vitua.game.Engine.RaycastResult;
import com.vitua.game.EventSystem.EventManager;
import com.vitua.game.EventSystem.EventType;
import com.vitua.game.EventSystem.MassageEvent;
import com.vitua.game.EventSystem.Event;
import com.vitua.game.math.Vector2D;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
/**
 * Unit test for simple App.
 */

public class AppTest {
    String eventTest;
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }


    @Test
    public void vectorTest() {
        Vector2D v= new Vector2D(10, 10);
        Vector2D rotated=v.copy();
        rotated.rotate(90);
        rotated.normalize();
        v.normalize();
        assertAll(
           () -> assertEquals(v.length(),1.0,0.0001),
           () -> assertEquals(rotated.dotProduct(v),0.0,0.0001)
        );
    }
    @Test
    public void gameObjectTest() {
        GameObject test = new GameObject(new Vector2D(0,0), new Collision(Collision.getRecCollision(0.2,1)));
        test.setPos(new Vector2D(0,0));
        
        assertEquals(test.forward().getM_x(),1,0.0001);
        test.rotate(90);
        assertEquals(test.forward().getM_y(), 1,0.0001);
        
    }

@Test
    public void gameObjectRotationTest() {
        double w = 0.2;
        double h = 1.0;
        GameObject test = new GameObject(new Vector2D(0,0), new Collision(Collision.getRecCollision(w, h)));
        
        assertEquals(1.0, test.forward().getM_x(), 0.0001);
        
        double angle = 30;
        test.setRotation(angle);
        
        assertEquals(Math.cos(Math.toRadians(angle)), test.forward().getM_x(), 0.0001);
        assertEquals(Math.sin(Math.toRadians(angle)), test.forward().getM_y(), 0.0001);

        Collision c = test.getCollision();
        var points = c.getCollision(); 

        double rad = Math.toRadians(angle);
        double cosA = Math.cos(rad);
        double sinA = Math.sin(rad);

        double[] originalPoints = {
            -w/2, -h/2,
             w/2, -h/2,
             w/2,  h/2,
            -w/2,  h/2
        };

        for (int i = 0; i < originalPoints.length; i += 2) {
            double oldX = originalPoints[i];
            double oldY = originalPoints[i+1];

            double expectedX = oldX * cosA - oldY * sinA;
            double expectedY = oldX * sinA + oldY * cosA;

            assertEquals(expectedX, points.get(i), 0.0001, "Point " + (i/2) + " X mismatch");
            assertEquals(expectedY, points.get(i+1), 0.0001, "Point " + (i/2) + " Y mismatch");
        }
    }
    @Test
    public void mapTest(){
        GameMap m = new GameMap();
        m.addPlayer("biba");
        assertFalse(m.addPlayer("biba"));
    }

    
    @Test
    public void collisionTest() {
        
        
        Player a = new Player(new Vector2D(0, 0), new Collision(Collision.getRecCollision(2, 2)));
        Player b = new Player(new Vector2D(0.5, 0.5), new Collision(Collision.getRecCollision(2, 2)));
        Player c = new Player(new Vector2D(10, 0), new Collision(Collision.getRecCollision(2, 1000)));
        
        CollisionManager colMan = new CollisionManager();
        

        assertTrue(colMan.checkCollisionOfObjects(a, b));
        assertFalse(colMan.checkCollisionOfObjects(a, c));
        
        c.setRotation(90);
        System.out.println();
        assertTrue(colMan.checkCollisionOfObjects(a, c));
        

        List<GameObject> l = new ArrayList<>(Arrays.asList(a, b, c));
        colMan.resolveColisions(l);

        assertEquals(3, colMan.getSoftCollSize());
        
        c.setRotation(0);
        colMan.resolveColisions(l);
        assertFalse(colMan.checkCollisionOfObjects(a, c));
        
        assertEquals(1, colMan.getSoftCollSize());
    }


    @Test
    public void rayTest() {
        
        
        Player a = new Player(new Vector2D(0, 2), new Collision(Collision.getRecCollision(1, 1)));
        a.setName("a");
        Player b = new Player(new Vector2D(2, 0), new Collision(Collision.getRecCollision(1, 1)));
        b.setName("b");
        Player c = new Player(new Vector2D(10, 0), new Collision(Collision.getRecCollision(1, 1)));
        c.setName("c");

        Collection<GameObject> objs= new ArrayList<>();
        objs.addAll(Arrays.asList(a,b,c));

        CollisionManager colMan = new CollisionManager();

        RaycastResult res =  colMan.getRayCollision(new Vector2D(0, 0), new Vector2D(0.1, 0), 10, objs);
        assertTrue(res.hitObject == b);
        assertEquals(res.hitPoint.getM_x(), 1.5,0.1);
        
        res =  colMan.getRayCollision(new Vector2D(0, 0), new Vector2D(0 ,0.1), 10, objs);
        assertTrue(res.hitObject == a);
        assertEquals(res.hitPoint.getM_y(), 1.5,0.1);
        


        res =  colMan.getRayCollision(new Vector2D(5, 0), new Vector2D(0.1 ,0), 10, objs);
        assertTrue(res.hitObject == c);
        assertEquals(res.hitPoint.getM_x(), 9.5,0.1);
        res =  colMan.getRayCollision(new Vector2D(5, 0), new Vector2D(-0.1,0), 10, objs);
        assertTrue(res.hitObject == b);
        assertEquals(res.hitPoint.getM_x(), 2.5,0.1);
        
    }

    @Test
    public void eventTest(){
       EventManager manager = new EventManager();
       manager.subscribe(EventType.MESSAGE_EVENT, (event)->{this.eventReciver(event);});
       Event e = new MassageEvent("test");
       manager.sendEventDirect(EventType.MESSAGE_EVENT, e);
       assertTrue(eventTest.equals("test"));
    
    }
    void eventReciver(Event event){
        if (event instanceof MassageEvent massageEvent) {
            eventTest=massageEvent.data;
        }
    }
}
