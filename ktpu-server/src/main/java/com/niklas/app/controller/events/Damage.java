package com.niklas.app.controller.events;


import com.niklas.app.model.GameState;
import com.niklas.app.online.Client;


public class Damage implements Event {
    private GameState gameState;
    private Client client;
    private int damage;

    public Damage(GameState gameState, Client client, int damage) {
        this.gameState = gameState;
        this.client = client;
        this.damage = damage;
    }

    public void execute() {
        if (damage > 0) {
    		client.getMonster().set_hp(client.getMonster().get_hp() - damage);
    	}
    }
}
