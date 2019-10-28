package com.niklas.app.controller.events;


import com.niklas.app.model.GameState;
import com.niklas.app.online.Client;


public class AwardStar implements Event {
    private GameState gameState;
    private Client client;
    private int stars;

    public AwardStar(GameState gameState, Client client, int stars) {
        this.gameState = gameState;
        this.client = client;
        this.stars = stars;
    }

    public void execute() {
        client.getMonster().set_stars(client.getMonster().getStars() + stars);
    }

    public void add_stars(int stars) {
        this.stars += stars;
    }
}
