package com.niklas.app.controller.events;


import java.util.ArrayList;

import com.niklas.app.model.GameState;
import com.niklas.app.online.Client;


public class CheckForWinByElimination implements Event {
    private GameState gameState;
    private boolean gameOver;

    public CheckForWinByElimination(GameState gameState) {
        this.gameState = gameState;
        gameOver = false;
    }

    public void execute() {
        ArrayList<Client> clients = new ArrayList<Client>();
        clients.add(gameState.getCurrentPlayer());
        clients.addAll(gameState.getPlayers());
        ArrayList<Client> aliveClients = new ArrayList<Client>();
        for (Client client : clients) {
            if (client.getMonster().getIsDead()) {
                aliveClients.add(client);
            } 
        }
        if (aliveClients.size() == 1) {
            Client winner = aliveClients.get(0);
            clients.remove(winner);
            gameState.getComunication().sendWinner(winner, clients);
            clients.add(winner);
        }
    }

    public boolean getGameOver() {
        return gameOver;
    }
}