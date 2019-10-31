package com.niklas.app.logic.events;


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
import com.niklas.app.online.Client;


/**
 * RollDice class is a event which handels the logic of rolling and rerolling dice.
 */
public class RollDice extends Event{
    private int numDice;
    private int numRerolls;
    private GameState gameState;
    private ArrayList<KTPUDice> dice;


    /**
     * Creates a RollDice event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     */
    public RollDice(GameState gameState) {
        this.gameState = gameState;
        numDice = 6;
        numRerolls = 2;
    }


    /**
     * Starts the RollDice event and handels the logic for it.
     */
    public void execute() {
        checkCards();
        dice = new ArrayList<KTPUDice>();
        for (int i = 0; i < numDice; i++) {
        	dice.add(new KTPUDice());
        }
        for (int i = 0; i < numRerolls; i++) {
        	int[] reroll = gameState.getComunication().sendRerollDice(dice, gameState.getCurrentPlayer());
        	if (reroll.length > 0 && reroll[0] > 0) {
                for (int j : reroll) {
                    if (j > 0 && j < 7) {
                        dice.get(j-1).roll();
                    }
                }
        	} else{
        		return;
        	}
        }
    }


    /**
     * Retrives the dice result of the RollDice event.
     * @return An ArrayList whith the rolled KTPUDice 
     */
    public ArrayList<KTPUDice> getDice() {
        return dice;
    }

    
    /**
     * Adds extra dice that will be rolled.
     * @param numDice is the added extra dice that will be rolled.
     */
    public void addDice(int numDice) {
        this.numDice += numDice;
    }


    /**
     * Addes extra rerolls to the event.
     * @param numRerolls is the number of extra rerolls.
     */
    public void addRerolls(int numRerolls){
        this.numRerolls += numRerolls;
    }


    /**
     * Checks all the current clients cards for cards that should activate at this event
     * and executes the cards effect.
     */
    protected void checkCards() {
        Client client = gameState.getCurrentPlayer();
        Monster currentMonster = client.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.RollDice) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event RollDice");
                }
				if (storeCard.getType() == StoreCardType.discard) {
					currentMonster.storeCards.remove(i);
				    gameState.getCardStore().discardCard(storeCard);
				}
			}
        }
        for (int i = 0; i < currentMonster.evolutionCards.size(); i++) {
            EvolutionCard evolutionCard = currentMonster.evolutionCards.get(i);
            Effect effect = evolutionCard.getEffect();
			if (effect.getActivation() == Activation.RollDice) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event RollDice");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
