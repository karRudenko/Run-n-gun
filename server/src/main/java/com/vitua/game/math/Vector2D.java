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

}