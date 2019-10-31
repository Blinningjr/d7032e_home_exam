package com.niklas.app.model;


import java.util.ArrayList;

import com.niklas.app.logic.actions.Actions;
import com.niklas.app.model.cards.CardStore;
import com.niklas.app.online.Player;
import com.niklas.app.online.Communication;


/**
 * Holdes all the information about the state of a game.
 */
public class GameState {
    private ArrayList<Player> players;
    private Player currentPlayer;
    private CardStore cardStore;
    private Communication communication;
    private boolean isGameOn;
    public Actions action;


    /**
     * Creates a game state with the given parameters.
     * @param players is a list of all the players in this game.
     * @param cardStore is the card store for this game.
     * @param communication is the communication object for this game.
     */
    public GameState(ArrayList<Player> players, CardStore cardStore, Communication communication) {
        this.players = players;
        this.currentPlayer = players.remove(0);
        this.communication = communication;
        this.cardStore = cardStore;
        action = new Actions();
        isGameOn = true;
    }

    /**
     * Switch current player for the next players.
     */
    public void nextTurn() {
        players.add(currentPlayer);
        currentPlayer = players.remove(0);
    }


    /**
     * Sets the game status to ended.
     */
    public void endGame() {
        isGameOn = false;
    }


    /**
     * Gets all the players except for the current players turn.
     * @return the rest of the players.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    
    /**
     * Gets the current plyer. 
     * @return the curent players.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }


    /**
     * Gets the cards store.
     * @return the card store.
     */
    public CardStore getCardStore() {
        return cardStore;
    }


    /**
     * Gets the communication object.
     * @return the communication object.
     */
    public Communication getCommunication() {
        return communication;
    }


    /**
     * Get if the game is still on.
     * @return boolean which is true if the game is on.
     */
    public boolean getIsGameOn() {
        return isGameOn;
    }
}
