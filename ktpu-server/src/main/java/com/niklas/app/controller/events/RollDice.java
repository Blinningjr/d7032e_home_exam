package com.niklas.app.controller.events;


import java.util.ArrayList;

import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.cards.Duration;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreCardType;
import com.niklas.app.model.dice.KTPUDice;
import com.niklas.app.model.monsters.Monster;



public class RollDice implements Event{
    private int numDice;
    private int numRerolls;
    private GameState gameState;
    private ArrayList<KTPUDice> dice;

    public RollDice(GameState gameState) {
        this.gameState = gameState;
        numDice = 6;
        numRerolls = 3;
    }

    public void execute() {
        checkCards();
        dice = new ArrayList<KTPUDice>();
        for (int i = 0; i < numDice; i++) {
        	dice.add(new KTPUDice());
        }
        for (int i = 0; i < numRerolls; i++) {
        	int[] reroll = gameState.getComunication().send_reroll_dice(dice, gameState.getCurrentPlayer());
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

    private void checkCards() {
        Monster currentMonster = gameState.getCurrentPlayer().getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.RollDice) {
				switch (effect.getAction()) {
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event RollDice");
                }
//				if (storeCard.getType() == StoreCardType.discard) {
//					currentMonster.storeCards.remove(i);
//				    gameState.getCardStore().discardCard(storeCard);
//				}
			}
        }
        for (int i = 0; i < currentMonster.evolutionCards.size(); i++) {
            EvolutionCard evolutionCard = currentMonster.evolutionCards.get(i);
            Effect effect = evolutionCard.getEffect();
			if (effect.getActivation() == Activation.RollDice) {
				switch (effect.getAction()) {
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event RollDice");
                }
//				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
//					currentMonster.evolutionCards.remove(i);
//                    currentMonster.discardEvolutionCard(evolutionCard);
//				}
			}
		}
    }
}