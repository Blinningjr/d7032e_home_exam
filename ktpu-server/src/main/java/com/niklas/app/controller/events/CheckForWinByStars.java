package com.niklas.app.controller.events;


import java.util.ArrayList;

import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class CheckForWinByStars implements Event {
    private static final int NUM_STARS_NEEDED_TO_WIN = 200;
    private ArrayList<Client> clients;
    private Comunication comunication;
    private boolean gameOver;

    public CheckForWinByStars(ArrayList<Client> clients, Comunication comunication) {
        this.clients = clients;
        this.comunication = comunication;
        gameOver = false;
    }

    public void execute() {
        for (Client client : clients) {
            if (client.get_monster().get_stars() >= NUM_STARS_NEEDED_TO_WIN) {
                Client winner = client;
                clients.remove(winner);
                comunication.sendWinner(winner, clients);
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
