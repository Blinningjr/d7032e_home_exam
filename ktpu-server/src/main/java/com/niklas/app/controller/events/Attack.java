package com.niklas.app.controller.events;

import java.util.ArrayList;

import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;

public class Attack implements Event {
    private Comunication comunication;
    private Client attackingClient;
    private ArrayList<Client> clients;
    private int numClaws;
    private int bonusDamage;

    public Attack(Comunication comunication, Client attackingClient, ArrayList<Client> clients, int numClaws) {
        this.comunication = comunication;
        this.attackingClient = attackingClient;
        this.clients = clients;
        this.numClaws = numClaws;

        bonusDamage = 0;
    }

    public void execute() {
        if (numClaws + bonusDamage > 0) {
    		boolean enter_tokyo = true;
    		if (attackingClient.get_monster().get_in_tokyo()) {
    			for (Client client : clients) {
    				attack(client, numClaws + bonusDamage);
				}
    		} else {
    			for (Client client : clients) {
    				if (client.get_monster().get_in_tokyo()) {
						attack(client,numClaws);
						
						// 6e. If you were outside, then the monster inside tokyo may decide to leave Tokyo
                        String answer = comunication.send_leave_tokyo(client);
                        if(answer.equalsIgnoreCase("YES")) {
                        	client.get_monster().set_in_tokyo(false);
                        	enter_tokyo = true;
                        } else {
                        	enter_tokyo = false;
                        }
					}
				}
    			if (enter_tokyo) {
    				attackingClient.get_monster().set_in_tokyo(true);
    			}
    		}
    	}
    }

    private void attack(Client defendingClient, int damage) {
        Defend defend = new Defend(comunication, attackingClient, defendingClient, damage);
        defend.execute();
    }

    public void addBonusDamage(int bonusDamage) {
        if (bonusDamage > 0) {
            this.bonusDamage += bonusDamage;
        } 
    }
}
