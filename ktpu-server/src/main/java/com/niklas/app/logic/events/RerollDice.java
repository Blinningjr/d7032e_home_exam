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
 * RerollDice class is a event which handels the logic of rerolling dice.
 */
public class RerollDice extends Event{
    private GameState gameState;
    private ArrayList<KTPUDice> dice;
    private int numRerolls;


    /**
     * Creates a RerollDice event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param dice is the dice that can be rerolled.
     */
    public RerollDice(GameState gameState, ArrayList<KTPUDice> dice) {
        this.gameState = gameState;
        this.dice = dice;
        numRerolls = 2;
    }


    /**
     * Starts the RerollDice event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Folows rule 9, 10, and 11 exept if there is a card that affects the amount of rerolls.
     * 
     * Rule:    9.Select which of your 6 dice to reroll
     *          10.Reroll theselected dice
     *          11.Repeat step 9 and 10once
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            checkCards();
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
    }


    /**
     * Retrives the dice from the RerollDice event.
     * @return An ArrayList whith the KTPUDice 
     */
    public ArrayList<KTPUDice> getDice() {
        return dice;
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
			if (effect.getActivation() == Activation.RerollDice) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event RerollDice");
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
			if (effect.getActivation() == Activation.RerollDice) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event RerollDice");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
