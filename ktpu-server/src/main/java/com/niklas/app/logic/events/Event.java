package com.niklas.app.logic.events;


public abstract class Event {
    abstract public void execute();
    abstract protected void checkCards();
}
