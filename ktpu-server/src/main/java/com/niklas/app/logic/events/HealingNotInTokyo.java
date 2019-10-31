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
 * HealingNotInTokyo class is a event which handels the logic of healing if not in tokyo.
 */
public class HealingNotInTokyo extends Event {
    private GameState gameState;
    private int numHearts;
    private int extraHealing;


    /**
     * Creates a HealingNotInTokyo event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param numHearts is the number of hearts rolled
     */
    public HealingNotInTokyo(GameState gameState, int numHearts) {
        this.gameState = gameState;
        this.numHearts = numHearts;
        extraHealing = 0;
    }

    /**
     * Starts the HealingNotInTokyo event and handels the logic for it.
     */
    public void execute() {
        Client currentClient = gameState.getCurrentPlayer();
        if (!currentClient.getMonster().getInTokyo()) {
            checkCards();
            Heal heal = new Heal(gameState, currentClient, numHearts + extraHealing);
            heal.execute();
        }
    }


    /**
     * Adds healing to the extra healing.
     * @param healing is number of hp added to the extra healing. 
     */
    public void addHealing(int healing) {
        if (healing >= 0) {
            this.extraHealing += healing;
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
			if (effect.getActivation() == Activation.HealingNotInTokyo) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event HealingNotInTokyo");
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
			if (effect.getActivation() == Activation.HealingNotInTokyo) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event HealingNotInTokyo");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
