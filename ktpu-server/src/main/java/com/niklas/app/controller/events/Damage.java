package com.niklas.app.controller.events;


import java.lang.reflect.Array;
import java.util.ArrayList;

import com.niklas.app.model.GameState;
import com.niklas.app.model.monsters.Monster;
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
        Monster monster = client.getMonster();
        if (damage > 0 && !monster.getIsDead()) {
            monster.setHp(monster.getHp() - damage);
            if (monster.getHp() < 1) {
                monster.setIsDead(true);
                monster.setInTokyo(false);
                ArrayList<Client> clients = new ArrayList<Client>();
                clients.add(gameState.getCurrentPlayer());
                clients.addAll(gameState.getPlayers());
                clients.remove(client);
                gameState.getComunication().sendMonsterDied(client, clients);
            }
    	}
    }
}
