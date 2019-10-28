package com.niklas.app.controller.events;


import java.util.ArrayList;

import com.niklas.app.model.dice.KTPUDice;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class CheckDice implements Event {
    private int numOnes;
    private int numTwos;
    private int numThrees;
    private int numHearts;
    private int numClaws;
    private int numEnergy;

    private ArrayList<KTPUDice> dice;
    private Comunication comunication;
    private Client client;

    public CheckDice(ArrayList<KTPUDice> dice, Comunication comunication, Client client) {
        this.dice = dice;
        this.comunication = comunication;
        this.client = client;
        resetNum();
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
        comunication.sendRolledDice(dice, client);
    }

    private void resetNum() {
        numOnes = 0;
        numTwos = 0;
        numThrees = 0;
        numHearts = 0;
        numClaws = 0;
        numEnergy = 0;
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
