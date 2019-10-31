package com.niklas.app.logic.events;


import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.cards.CardStore;
import com.niklas.app.model.cards.Duration;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreCardType;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;


/**
 * ResetStore class is a event which handels the logic of reseting the store inventory.
 */
public class ResetStore extends Event {
    private GameState gameState;
    private int cost;
    private int extraCost;


    /**
     * Creates a ResetStore event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     */
    public ResetStore(GameState gameState) {
        this.gameState = gameState;
        cost = 2;
        extraCost = 0;
    }


    /**
     * Starts the ResetStore event and handels the logic for it.
     */
    public void execute() {
        Client currentPlayer = gameState.getCurrentPlayer();
        int totalCost = cost + extraCost;
        if (currentPlayer.getMonster().getEnergy() >= totalCost) {
            checkCards();
            currentPlayer.getMonster().setEnergy(currentPlayer.getMonster().getEnergy() - totalCost);

            CardStore cardStore = gameState.getCardStore();
            for (int i = 0; i < cardStore.getInventory().length; i++) {
                cardStore.discardCard(cardStore.buy(i));
            }
        }
    }


    /**
     * Adds cost to reseting the store inventory.
     * @param addedCost adds cost to the cost of reseting(Can be negative to reduce cost).
     */
    public void addCost(int addedCost) {
        extraCost += addedCost;
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
			if (effect.getActivation() == Activation.ResetStore) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event ResetStore");
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
			if (effect.getActivation() == Activation.ResetStore) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event ResetStore");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
