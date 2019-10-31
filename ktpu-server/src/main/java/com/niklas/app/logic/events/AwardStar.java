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
 * AwardStar class is a event which handels the logic of giving a monster stars.
 */
public class AwardStar extends Event {
    private GameState gameState;
    private Client client;
    private int stars;


    /**
     * Creates a AwardStar event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param client is the client Which mosnter will recive the starts
     * @param stars is the number of star added to the monster.
     */
    public AwardStar(GameState gameState, Client client, int stars) {
        this.gameState = gameState;
        this.client = client;
        this.stars = stars;
    }


    /**
     * Starts the AwardStar event and handels the logic for it.
     */
    public void execute() {
        checkCards();
        client.getMonster().setStars(client.getMonster().getStars() + stars);
        
        CheckForWinByStars cfwbs = new CheckForWinByStars(gameState);
        cfwbs.execute();
    }


    /**
     * Adds stars to the number of stars given to the monster.
     * @param stars is the stars that will be added to the number of stars given to the monster.
     */
    public void addStars(int stars) {
        this.stars += stars;
    }


    /**
     * Checks all the clients cards for cards that should activate at this event
     * and executes the cards effect.
     */
    protected void checkCards() {
        Monster currentMonster = client.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.AwardStar) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event AwardStar");
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
			if (effect.getActivation() == Activation.AwardStar) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event AwardStar");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
