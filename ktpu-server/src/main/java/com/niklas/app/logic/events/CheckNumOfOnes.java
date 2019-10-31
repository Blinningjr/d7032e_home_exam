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
 * CheckNumOfOnes class is a event which handels the logic of giving stars for the number of ones rolled.
 */
public class CheckNumOfOnes extends Event {
    private GameState gameState;
    private int numOnes;
    private int numOnesNeeded;
    private int starsAdded;


    /**
     * Creates a CheckNumOfOnes event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param numOnes is the number of ones rolled.
     */
    public CheckNumOfOnes(GameState gameState, int numOnes) {
        this.gameState = gameState;
        this.numOnes = numOnes;
        numOnesNeeded = 3;
        starsAdded = 1;
    }


    /**
     * Starts the CheckNumOfOnes event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Follows rule 12 and gives stars by starting event AwardStar.
     * 
     * Rule: 12.
     *          Tripple 1’s = 1 StarEach additional 1 equals +1 star
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            Client client = gameState.getCurrentPlayer();
            if (numOnes >= numOnesNeeded) {
                checkCards();
                AwardStar aw = new AwardStar(gameState, client, starsAdded + numOnes - numOnesNeeded);
                aw.execute();
            }
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
			if (effect.getActivation() == Activation.CheckNumOfOnes) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckNumOfOnes");
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
			if (effect.getActivation() == Activation.CheckNumOfOnes) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckNumOfOnes");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
