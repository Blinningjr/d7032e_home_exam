package com.niklas.app.controller.events;


import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class CheckNumOfOnes implements Event {
    private Comunication comunication;
    private Client client;

    private int numOnes;
    private int numOnesNeeded;
    private int starsAdded;

    public CheckNumOfOnes(Comunication comunication, Client client, int numOnes) {
        this.comunication = comunication;
        this.client = client;
        this.numOnes = numOnes;
        numOnesNeeded = 3;
        starsAdded = 1;
    }

    public void execute() {
        if (numOnes >= numOnesNeeded) {
    		client.get_monster().set_stars(client.get_monster().get_stars() + starsAdded + numOnes - numOnesNeeded);
    	}
    }
}
