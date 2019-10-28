package com.niklas.app.controller.events;


import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class CheckNumOfTwos implements Event {
    private Comunication comunication;
    private Client client;

    private int numTwos;
    private int numTwosNeeded;
    private int starsAdded;

    public CheckNumOfTwos(Comunication comunication, Client client, int numTwos) {
        this.comunication = comunication;
        this.client = client;
        this.numTwos = numTwos;
        numTwosNeeded = 3;
        starsAdded = 2;
    }

    public void execute() {
        if (numTwos >= numTwosNeeded) {
    		client.get_monster().set_stars(client.get_monster().get_stars() + starsAdded + numTwos - numTwosNeeded);
    	}
    }
}
