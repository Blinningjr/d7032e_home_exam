package com.niklas.app.controller.events;


import java.util.ArrayList;

import com.niklas.app.model.dice.KTPUDice;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class CheckDice implements Event {
    private int numOnes = 0;
    private int numTwos = 0;
    private int numThrees = 0;
    private int numHearts = 0;
    private int numClaws = 0;
    private int numEnergy = 0;

    private ArrayList<KTPUDice> dice;
    private Comunication comunication;
    private Client client;

    public CheckDice(ArrayList<KTPUDice> dice, Comunication comunication, Client client) {
        this.dice = dice;
        this.comunication = comunication;
        this.client = client;
    }

    public void execute() {
        for (KTPUDice ktpuDice : dice) {
			switch (ktpuDice.get_value()) {
			case KTPUDice.ONE:
                numOnes += 1;
				break;
			case KTPUDice.TWO:
                numTwos += 1;
				break;
			case KTPUDice.THREE:
                numThrees += 1;
				break;
			case KTPUDice.HEART:
				numHearts += 1;
				break;
			case KTPUDice.CLAWS:
				numClaws += 1;
				break;
			case KTPUDice.ENERGY:
				numEnergy += 1;
				break;
			default:
				throw new Error("Dice value:" + ktpuDice.get_value() + " is not implemented for KTPUDice");
			}
		}
    }

    public int getNumOnes() {
        return numOnes;
    }

    public int getNumTwos() {
        return numTwos;
    }

    public int getNumThrees() {
        return numThrees;
    }

    public int getNumHearts() {
        return numHearts;
    }

    public int getNumClaws() {
        return numClaws;
    }

    public int getNumEnergy() {
        return numEnergy;
    }
}
