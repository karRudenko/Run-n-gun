package com.vitua.game.Engine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitua.game.Engine.Collisions.Collision;
import com.vitua.game.math.Vector2D;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapData {


    private List<WallEntry>  walls       = new ArrayList<>();
    private List<PointEntry> spawnPoints = new ArrayList<>();


    public List<WallEntry>  getWalls()       { return walls; }
    public List<PointEntry> getSpawnPoints() { return spawnPoints; }
    public void setWalls(List<WallEntry> walls)             { this.walls = walls; }
    public void setSpawnPoints(List<PointEntry> spawnPoints){ this.spawnPoints = spawnPoints; }


    @JsonIgnore
    public List<GameObject> getObjects() {
        List<GameObject> result = new ArrayList<>();
        for (WallEntry e : walls) {
            result.add(new Wall(new Vector2D(e.x, e.y), e.h, e.w));
        }
        return result;
    }

    @JsonIgnore
    public List<Vector2D> getSpawnPointsAsVectors() {
        List<Vector2D> result = new ArrayList<>();
        for (PointEntry p : spawnPoints) {
            result.add(new Vector2D(p.x, p.y));
        }
        return result;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WallEntry {
        public double x, y, w, h;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PointEntry {
        public double x, y;
    }


    public static MapData fromFile(File file) throws IOException {
        return new ObjectMapper().readValue(file, MapData.class);
    }


    public static MapData fromResource(String resourcePath) throws IOException {
        InputStream is = MapData.class.getResourceAsStream(resourcePath);
        if (is == null) throw new IOException("Resource not found: " + resourcePath);
        return new ObjectMapper().readValue(is, MapData.class);
    }
}