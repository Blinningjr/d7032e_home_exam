package com.niklas.app.controller.events;


import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class CheckNumOfThrees implements Event {
    private Comunication comunication;
    private Client client;

    private int numThrees;
    private int numThreesNeeded;
    private int starsAdded;

    public CheckNumOfThrees(Comunication comunication, Client client, int numThrees) {
        this.comunication = comunication;
        this.client = client;
        this.numThrees = numThrees;
        numThreesNeeded = 3;
        starsAdded = 3;
    }

    public void execute() {
        if (numThrees >= numThreesNeeded) {
    		client.get_monster().set_stars(client.get_monster().get_stars() + starsAdded + numThrees - numThreesNeeded);
    	}
    }
}
