package com.niklas.app.controller.events;


import com.niklas.app.model.GameState;
import com.niklas.app.online.Client;


public class CheckNumOfTwos implements Event {
    private GameState gameState;
    private int numTwos;
    private int numTwosNeeded;
    private int starsAdded;

    public CheckNumOfTwos(GameState gameState, int numTwos) {
        this.gameState = gameState;
        this.numTwos = numTwos;
        numTwosNeeded = 3;
        starsAdded = 2;
    }

    public void execute() {
        Client client = gameState.getCurrentPlayer();
        if (numTwos >= numTwosNeeded) {
    		client.getMonster().set_stars(client.getMonster().getStars() + starsAdded + numTwos - numTwosNeeded);
    	}
    }
}
