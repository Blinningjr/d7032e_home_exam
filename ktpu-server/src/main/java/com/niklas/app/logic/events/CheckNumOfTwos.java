package com.niklas.app.logic.events;


import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.cards.Duration;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreCardType;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;


/**
 * CheckNumOfTwos class is a event which handels the logic of giving stars for the number of twos rolled.
 */
public class CheckNumOfTwos extends Event {
    private GameState gameState;
    private int numTwos;
    private int numTwosNeeded;
    private int starsAdded;


    /**
     * Creates a CheckNumOfTwos event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param numTwos is the number of twos rolled.
     */
    public CheckNumOfTwos(GameState gameState, int numTwos) {
        this.gameState = gameState;
        this.numTwos = numTwos;
        numTwosNeeded = 3;
        starsAdded = 2;
    }


    /**
     * Starts the CheckNumOfTwos event and handels the logic for it.
     */
    public void execute() {
        Client client = gameState.getCurrentPlayer();
        if (numTwos >= numTwosNeeded) {
            checkCards();
    		client.getMonster().setStars(client.getMonster().getStars() + starsAdded + numTwos - numTwosNeeded);
    	}
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
			if (effect.getActivation() == Activation.CheckNumOfTwos) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckNumOfTwos");
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
			if (effect.getActivation() == Activation.CheckNumOfTwos) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckNumOfTwos");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
