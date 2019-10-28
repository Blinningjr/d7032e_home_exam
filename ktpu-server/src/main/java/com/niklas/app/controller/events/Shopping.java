package com.niklas.app.controller.events;


import com.niklas.app.model.cards.CardStore;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class Shopping implements Event {
    private CardStore cardStore;
    private Comunication comunication;
    private Client client;
    private int extraCost;

    public Shopping(CardStore cardStore, Comunication comunication, Client client) {
        this.cardStore = cardStore;
        this.comunication = comunication;
        this.client = client;
        extraCost = 0;
    }

    public void execute() {
        StoreCard[] storeCards = cardStore.get_inventory();

        String answer = comunication.send_shopping(client, cardStore);
        int buy = Integer.parseInt(answer);

        if(buy>0 && (client.get_monster().get_energy() >= storeCards[buy -1].get_cost() + extraCost)) { 
        	try {
        		client.get_monster().set_entergy(client.get_monster().get_energy() -  storeCards[buy - 1].get_cost() + extraCost);
				client.get_monster().store_cards.add(cardStore.buy(buy-1));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public void add_cost(int addedCost) {
        extraCost += addedCost;
    }
}
