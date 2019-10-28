package com.niklas.app.controller.events;


import com.niklas.app.model.GameState;
import com.niklas.app.online.Client;


public class AwardEnergy implements Event {
    private GameState gameState;
    private Client client;
    private int numEnergy;
    
    public AwardEnergy(GameState gameState, Client client, int numEnergy) {
        this.gameState = gameState;
        this.client = client;
        this.numEnergy = numEnergy;
    }

    public void execute() {
        client.getMonster().set_entergy(client.getMonster().get_energy() + numEnergy);
    }
}
