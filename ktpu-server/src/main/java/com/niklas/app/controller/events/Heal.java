package com.niklas.app.controller.events;

import com.niklas.app.model.GameState;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;


public class Heal implements Event {
    private GameState gameState;
    private Client client; 

    private int addedMaxHp;
    private int healing;

    public Heal(GameState gameState, Client client, int numHearts) {
        this.gameState = gameState;
        this.client = client;
        healing = numHearts;

        addedMaxHp = 0;
    }

    public void execute() {
        Monster monster = client.getMonster();
        int maxHp = monster.get_max_hp() + addedMaxHp;
        int newHp = monster.get_hp() + healing;
        if (newHp > maxHp) {
            newHp = maxHp;
        }
        monster.set_hp(newHp);
    }

    public void addHealing(int healing) {
        if (healing >= 0) {
            this.healing += healing;
        }
    }

    public void addMaxHp(int addedMaxHp) {
        if (addedMaxHp >= 0) {
            this.addedMaxHp += addedMaxHp;
        }
    }
}
