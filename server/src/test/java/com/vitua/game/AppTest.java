package com.vitua.game;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.vitua.game.Engine.GameObject;
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
    public void gameObjectTest() {
        GameObject test = new GameObject();
        test.setPos(new Vector2D(0,0));
        
        assertEquals(test.forward().getM_x(),1,0.0001);
        test.rotate(90);
        assertEquals(test.forward().getM_y(), 1,0.0001);
        
    }
    @Test
    public void testCirclesOverlap() {
        // Создаем два круга, которые пересекаются
        Circle circle1 = new Circle(0, 0, 10);
        Circle circle2 = new Circle(5, 5, 10);

        // Стандартный метод JavaFX для проверки пересечения Node
        Shape intersect = Shape.intersect(circle1, circle2);
        
        // Если область пересечения не пуста — они столкнулись
        assertTrue(intersect.getBoundsInLocal().getWidth() > 0, "Круги должны пересекаться");
    }

    @Test
    public void testCirclesDoNotOverlap() {
        // Создаем два круга далеко друг от друга
        Circle circle1 = new Circle(0, 0, 5);
        Circle circle2 = new Circle(50, 50, 5);

        Shape intersect = Shape.intersect(circle1, circle2);

        // Проверяем, что ширина области пересечения отрицательна или 0
        assertFalse(intersect.getBoundsInLocal().getWidth() > 0, "Круги не должны пересекаться");
    }
}
