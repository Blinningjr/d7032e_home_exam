package com.niklas.app.model;


import java.util.ArrayList;

import com.niklas.app.logic.actions.Actions;
import com.niklas.app.model.cards.CardStore;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class GameState {
    private ArrayList<Client> players;
    private Client currentPlayer;
    private CardStore cardStore;
    private Comunication comunication;
    private boolean isGameOn;
    public Actions action;

    public GameState(ArrayList<Client> players, CardStore cardStore, Comunication comunication) {
        this.players = players;
        this.currentPlayer = players.remove(0);
        this.comunication = comunication;
        this.cardStore = cardStore;
        action = new Actions();
        isGameOn = true;
    }

    /**
     * Switch current player for the next player.
     */
    public void nextTurn() {
        players.add(currentPlayer);
        currentPlayer = players.remove(0);
    }

    public void endGame() {
        isGameOn = false;
    }

    public ArrayList<Client> getPlayers() {
        return players;
    }

    /**
     * Gets the current plyer. 
     * @return the curent players.
     */
    public Client getCurrentPlayer() {
        return currentPlayer;
    }

    public CardStore getCardStore() {
        return cardStore;
    }

    public Comunication getComunication() {
        return comunication;
    }

    public boolean getIsGameOn() {
        return isGameOn;
    }
}
