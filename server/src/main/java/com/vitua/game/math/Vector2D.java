package com.vitua.game.math;


public class Vector2D{
    private double m_x;
    private double m_y;

    public Vector2D(double x, double y){
        m_x=x;
        m_y=y;
    }
    public void addVector(Vector2D oth){
        m_x+=oth.m_x;
        m_y+=oth.m_y;
    }
    public double dotProduct(Vector2D oth){
        return m_x*oth.m_x + m_y*oth.m_y;
    }

    public void rotate(double degree){
        degree=Math.toRadians(degree);
        double sin = Math.sin(degree);
        double cos = Math.cos(degree);
        double x=m_x;
        m_x=x*cos-m_y*sin;
        m_y=x*sin+m_y*cos;
    }

    public double length(){
        return Math.sqrt(m_x*m_x+m_y*m_y);
    }
    public void normalize(){
        double l=length();
        m_x/=l;
        m_y/=l;
    }
    public Vector2D copy(){
        return new Vector2D(m_x, m_y);
    }
    public double getM_x() {
        return m_x;
    }
    public double getM_y() {
        return m_y;
    }
    public static Vector2D addVectors(Vector2D a, Vector2D b){
        return new Vector2D(a.m_x+b.m_x, a.m_y+b.m_y);
    }
    public static Vector2D negativeVector2d(Vector2D a){
        return new Vector2D(-a.m_x, -a.m_y);
    }
    public static Vector2D vecScal(Vector2D a, double b){
        return new Vector2D(a.m_x*b, a.m_y*b);
    }
    public static Vector2D normalaze(Vector2D a){
        return new Vector2D(a.m_x/a.length(), a.m_y/a.length());
    }    

}