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
import com.niklas.app.online.Player;


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
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Follows rule 13.
     * 
     * Rule: 13.Buying Cards (As long as long as you have the Energy, you can take any of the following actions)
     *          Reset store –pay 2 energy
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            Player currentPlayer = gameState.getCurrentPlayer();
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
    }


    /**
     * Adds cost to reseting the store inventory.
     * @param addedCost adds cost to the cost of reseting(Can be negative to reduce cost).
     */
    public void addCost(int addedCost) {
        extraCost += addedCost;
    }


    /**
     * Checks all the current players cards for cards that should activate at this event
     * and executes the cards effect.
     */
    protected void checkCards() {
        Player player = gameState.getCurrentPlayer();
        Monster currentMonster = player.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.ResetStore) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
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
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
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
