package com.niklas.app.controller.events;


import com.niklas.app.model.GameState;
import com.niklas.app.online.Client;


public class CheckNumOfThrees implements Event {
    private GameState gameState;
    private int numThrees;
    private int numThreesNeeded;
    private int starsAdded;

    public CheckNumOfThrees(GameState gameState, int numThrees) {
        this.gameState = gameState;
        this.numThrees = numThrees;
        numThreesNeeded = 3;
        starsAdded = 3;
    }

    public void execute() {
        Client client = gameState.getCurrentPlayer();
        if (numThrees >= numThreesNeeded) {
    		client.getMonster().set_stars(client.getMonster().getStars() + starsAdded + numThrees - numThreesNeeded);
    	}
    }
}
