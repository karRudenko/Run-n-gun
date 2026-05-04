package com.vitua.game.EventSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    Map<EventType,List<EventReceiver>> subs;
    public EventManager(){
        subs = new HashMap<>();
    }
    public void subscribe(EventType type, EventReceiver handler){
        List<EventReceiver> list = subs.getOrDefault(type,new ArrayList<>());
        list.add(handler);
        subs.put(type,list);
    }
    public void sendEventDirect(EventType type, Event event){
        List<EventReceiver> listeners = subs.get(type);
        for(EventReceiver l : listeners){
            l.handle(event);
        }
    }
}
