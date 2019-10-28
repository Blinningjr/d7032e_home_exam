package com.niklas.app.controller.events;

import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;

public class PowerUp implements Event {
    private Comunication comunication;
    private Client client;

    private int numHearts;
    private int heartsNeeded;

    public PowerUp(Comunication comunication, Client client, int numHearts) {
        this.comunication = comunication;
        this.client = client;
        this.numHearts = numHearts;
        heartsNeeded = 3;
    }

    public void execute(){
        Monster monster = client.get_monster();
        if (numHearts >= heartsNeeded) {
            EvolutionCard evolutionCard = monster.draw_evolution_card();
            monster.evolutionCards.add(evolutionCard);
        }
    }

    public void addHeartsNeeded(int addedHeartsNeeded) {
        heartsNeeded += addedHeartsNeeded;
    }
}
