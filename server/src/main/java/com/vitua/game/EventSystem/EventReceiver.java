package com.vitua.game.EventSystem;
@FunctionalInterface
public interface EventReceiver {
    public void handle(Event event);
}
