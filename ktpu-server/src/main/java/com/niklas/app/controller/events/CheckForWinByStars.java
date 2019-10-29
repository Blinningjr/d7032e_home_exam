package com.niklas.app.controller.events;


import java.util.ArrayList;

import com.niklas.app.model.GameState;
import com.niklas.app.online.Client;


public class CheckForWinByStars implements Event {
    private static final int NUM_STARS_NEEDED_TO_WIN = 20;
    private GameState gameState;
    private boolean gameOver;

    public CheckForWinByStars(GameState gameState) {
        this.gameState = gameState;
        gameOver = false;
    }

    public void execute() {
        ArrayList<Client> clients = new ArrayList<Client>();
        clients.add(gameState.getCurrentPlayer());
        clients.addAll(gameState.getPlayers());
        for (Client client : clients) {
            if (client.getMonster().getStars() >= NUM_STARS_NEEDED_TO_WIN) {
                Client winner = client;
                clients.remove(winner);
                gameState.getComunication().sendStarsWinner(winner, clients);
                clients.add(winner);
                gameOver = true;
                return;
            } 
        }
    }

    public boolean getGameOver() {
        return gameOver;
    }
}
