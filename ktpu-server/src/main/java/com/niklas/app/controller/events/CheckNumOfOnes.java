package com.niklas.app.controller.events;


import com.niklas.app.model.GameState;
import com.niklas.app.online.Client;


public class CheckNumOfOnes implements Event {
    private GameState gameState;

    private int numOnes;
    private int numOnesNeeded;
    private int starsAdded;

    public CheckNumOfOnes(GameState gameState, int numOnes) {
        this.gameState = gameState;
        this.numOnes = numOnes;
        numOnesNeeded = 3;
        starsAdded = 1;
    }

    public void execute() {
        Client client = gameState.getCurrentPlayer();
        if (numOnes >= numOnesNeeded) {
    		client.getMonster().set_stars(client.getMonster().getStars() + starsAdded + numOnes - numOnesNeeded);
    	}
    }
}
