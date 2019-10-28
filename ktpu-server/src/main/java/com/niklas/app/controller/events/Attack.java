package com.niklas.app.controller.events;

import java.util.ArrayList;

import com.niklas.app.model.GameState;
import com.niklas.app.online.Client;

public class Attack implements Event {
    private GameState gameState;
    private Client attackingClient;
    private ArrayList<Client> clients;
    private int numClaws;
    private int bonusDamage;

    public Attack(GameState gameState, Client attackingClient, ArrayList<Client> clients, int numClaws) {
        this.gameState = gameState;
        this.attackingClient = attackingClient;
        this.clients = clients;
        this.numClaws = numClaws;

        bonusDamage = 0;
    }

    public void execute() {
        if (numClaws + bonusDamage > 0) {
    		boolean enter_tokyo = true;
    		if (attackingClient.getMonster().getInTokyo()) {
    			for (Client client : clients) {
    				attack(client, numClaws + bonusDamage);
				}
    		} else {
    			for (Client client : clients) {
    				if (client.getMonster().getInTokyo()) {
						attack(client,numClaws);
						
						// 6e. If you were outside, then the monster inside tokyo may decide to leave Tokyo
                        String answer = gameState.getComunication().send_leave_tokyo(client);
                        if(answer.equalsIgnoreCase("YES")) {
                        	client.getMonster().set_in_tokyo(false);
                        	enter_tokyo = true;
                        } else {
                        	enter_tokyo = false;
                        }
					}
				}
    			if (enter_tokyo) {
    				attackingClient.getMonster().set_in_tokyo(true);
    			}
    		}
    	}
    }

    private void attack(Client defendingClient, int damage) {
        Defend defend = new Defend(gameState.getComunication(), attackingClient, defendingClient, damage);
        defend.execute();
    }

    public void addBonusDamage(int bonusDamage) {
        if (bonusDamage > 0) {
            this.bonusDamage += bonusDamage;
        } 
    }
}
