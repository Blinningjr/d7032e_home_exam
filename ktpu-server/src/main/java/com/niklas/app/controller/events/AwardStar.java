package com.niklas.app.controller.events;


import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class AwardStar implements Event {
    private Comunication comunication;
    private Client client;
    private int stars;

    public AwardStar(Comunication comunication, Client client, int stars) {
        this.comunication = comunication;
        this.client = client;
        this.stars = stars;
    }

    public void execute() {
        client.get_monster().set_stars(client.get_monster().get_stars() + stars);
    }

    public void add_stars(int stars) {
        this.stars += stars;
    }
}
