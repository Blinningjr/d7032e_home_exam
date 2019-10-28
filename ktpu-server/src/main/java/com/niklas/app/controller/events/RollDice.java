package com.niklas.app.controller.events;

import java.util.ArrayList;

import com.niklas.app.model.dice.KTPUDice;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;

public class RollDice implements Event{
    private int numDice;
    private int numRerolls;
    private Comunication comunication;
    private Client client;
    private ArrayList<KTPUDice> dice;

    public RollDice(Comunication comunication, Client client) {
        this.comunication = comunication;
        this.client = client;
        numDice = 6;
        numRerolls = 3;
    }

    public void execute() {
        dice = new ArrayList<KTPUDice>();
        for (int i = 0; i < numDice; i++) {
        	dice.add(new KTPUDice());
        }
        for (int i = 0; i < numRerolls; i++) {
        	int[] reroll = comunication.send_reroll_dice(dice, client);
        	if (reroll.length > 0 && reroll[0] > 0) {
        		for (int j : reroll) {
					dice.get(j-1).roll();
				}
        	} else{
        		return;
        	}
        }
    }

    public ArrayList<KTPUDice> getDice() {
        return dice;
    }

    public void addDice(int numDice) {
        this.numDice += numDice;
    }

    public void addRerolls(int numRerolls){
        this.numRerolls += numRerolls;
    }
}