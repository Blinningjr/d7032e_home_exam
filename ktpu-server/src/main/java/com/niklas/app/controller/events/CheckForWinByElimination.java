package com.niklas.app.controller.events;


import java.util.ArrayList;

import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class CheckForWinByElimination implements Event {
    private ArrayList<Client> clients;
    private Comunication comunication;
    private boolean gameOver;

    public CheckForWinByElimination(ArrayList<Client> clients, Comunication comunication) {
        this.clients = clients;
        this.comunication = comunication;
        gameOver = false;
    }

    public void execute() {
        ArrayList<Client> aliveClients = new ArrayList<Client>();
        for (Client client : clients) {
            if (client.get_monster().getIsDead()) {
                aliveClients.add(client);
            } 
        }
        if (aliveClients.size() == 1) {
            Client winner = aliveClients.get(0);
            clients.remove(winner);
            comunication.sendWinner(winner, clients);
            clients.add(winner);
        }
    }

    public boolean getGameOver() {
        return gameOver;
    }
}