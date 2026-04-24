package com.vitua.game;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.vitua.game.Engine.Collider;
import com.vitua.game.Engine.GameMap;
import com.vitua.game.Engine.GameObject;
import com.vitua.game.Engine.Player;
import com.vitua.game.math.Vector2D;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * Unit test for simple App.
 */
public class AppTest {

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
        GameObject test = new GameObject(new Vector2D(0,0), new Collider(Collider.getRecCollider(0.2,1)));
        test.setPos(new Vector2D(0,0));
        
        assertEquals(test.forward().getM_x(),1,0.0001);
        test.rotate(90);
        assertEquals(test.forward().getM_y(), 1,0.0001);
        
    }

@Test
    public void gameObjectRotationTest() {
        double w = 0.2;
        double h = 1.0;
        GameObject test = new GameObject(new Vector2D(0,0), new Collider(Collider.getRecCollider(w, h)));
        
        assertEquals(1.0, test.forward().getM_x(), 0.0001);
        
        double angle = 30;
        test.setRotation(angle);
        
        assertEquals(Math.cos(Math.toRadians(angle)), test.forward().getM_x(), 0.0001);
        assertEquals(Math.sin(Math.toRadians(angle)), test.forward().getM_y(), 0.0001);

        Collider c = test.getCollider();
        var points = c.getCollider(); 

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

}
