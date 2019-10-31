package com.niklas.app.logic.events;


/**
 * Event class defines the funtions all events need to have to work in the game.
 */
public abstract class Event {


    /**
     * Starts the events and handels all it logic.
     */
    abstract public void execute();


    /**
     * Looks for cards that should activate when this event starts.
     */
    abstract protected void checkCards();
}
