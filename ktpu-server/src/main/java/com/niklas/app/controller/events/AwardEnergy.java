package com.niklas.app.controller.events;

import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;

public class AwardEnergy implements Event {
    private Comunication comunication;
    private Client client;
    private int numEnergy;
    
    public AwardEnergy(Comunication comunication, Client client, int numEnergy) {
        this.comunication = comunication;
        this.client = client;
        this.numEnergy = numEnergy;
    }

    public void execute() {
        client.get_monster().set_entergy(client.get_monster().get_energy() + numEnergy);
    }
}
