package com.shootergame.client.model.entities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;

public class Record {
    private Map<StringBuilder, StringBuilder[]> players = new ConcurrentHashMap<>();

    public void addPlayer(StringBuilder portServer, StringBuilder nickname) {
        if(players.containsKey(portServer)) {
            StringBuilder[] current = players.get(portServer);
            StringBuilder[] updated = Arrays.copyOf(current, current.length + 1);
            updated[updated.length - 1] = nickname;
            players.put(portServer, updated);
        } else {
            players.put(portServer, new StringBuilder[]{nickname});
        }
    }

    public boolean containsValue(StringBuilder key, StringBuilder value) {
        if (!players.containsKey(key) || value == null) {
            return false;
        }
        for (StringBuilder sb : players.get(key)) {
            if (sb.toString().equals(value.toString())) {
                return true;
            }
        }
        return false;
    }

    public Map<StringBuilder, StringBuilder[]> getPlayers() { return players; }
    public StringBuilder[] getPlayer(StringBuilder key) { return (players.containsKey(key) ? players.get(key) : null); }

    // public static void main(String[] args) {
    //     Record map = new Record();
    //     StringBuilder one = new StringBuilder("hello");
    //     StringBuilder two = new StringBuilder("2");
    //     StringBuilder three = new StringBuilder("3");
    //     map.addPlayer(one, two);
    //     map.addPlayer(one, three);
    //     map.addPlayer(two, three);
    //     map.addPlayer(two, three);
    //     System.out.println(Arrays.toString(map.getPlayer(one)));
    //     System.out.println(Arrays.toString(map.getPlayer(two)));
    //     System.out.println(map.containsValue(two, three));
    // }

}