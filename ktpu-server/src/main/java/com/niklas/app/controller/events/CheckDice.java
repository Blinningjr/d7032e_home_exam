package com.niklas.app.controller.events;


import java.util.ArrayList;

import com.niklas.app.model.GameState;
import com.niklas.app.model.dice.KTPUDice;


public class CheckDice implements Event {
    private int numOnes;
    private int numTwos;
    private int numThrees;
    private int numHearts;
    private int numClaws;
    private int numEnergy;

    private ArrayList<KTPUDice> dice;
    private GameState gameState;

    public CheckDice(GameState gameState, ArrayList<KTPUDice> dice) {
        this.gameState = gameState;
        this.dice = dice;
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
        gameState.getComunication().sendRolledDice(dice, gameState.getCurrentPlayer());
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
