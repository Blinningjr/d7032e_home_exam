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
 * PowerUp class is a event which handels the logic of a monster powering up.
 */
public class PowerUp extends Event {
    private GameState gameState;
    private int numHearts;
    private int heartsNeeded;


    /**
     * Creates a PowerUp event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param numHearts is the number of hearts rolled.
     */
    public PowerUp(GameState gameState, int numHearts) {
        this.gameState = gameState;
        this.numHearts = numHearts;
        heartsNeeded = 3;
    }


    /**
     * Starts the PowerUp event and handels the logic for it.
     */
    public void execute(){
        Monster monster = gameState.getCurrentPlayer().getMonster();
        if (numHearts >= heartsNeeded) {
            checkCards();
            EvolutionCard evolutionCard = monster.drawEvolutionCard();
            monster.evolutionCards.add(evolutionCard);
            checkNewCard();
        }
    }


    /**
     * Checks if the current clients new evolution card should activate instantly
     * and executes the cards effect.
     */
    private void checkNewCard() {
        Client client = gameState.getCurrentPlayer();
        Monster currentMonster = client.getMonster();
        EvolutionCard evolutionCard = currentMonster.evolutionCards.get(currentMonster.evolutionCards.size() - 1);
        Effect effect = evolutionCard.getEffect();
        if (effect.getActivation() == Activation.Now) {
            switch (effect.getAction()) {
                case giveStarsEnergyAndHp:
                    gameState.action.giveStarsEnergyAndHp(gameState,
                    client, effect);
                    break;
                case damageEveryoneElse:
                    gameState.action.damageEveryoneElse(gameState, client, effect);
                    break;
                default:
                    throw new Error("action=" + effect.getAction() 
                        + " is not implemented for event PowerUp");
            }
            if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
                currentMonster.evolutionCards.remove(currentMonster.evolutionCards.size() - 1);
                currentMonster.discardEvolutionCard(evolutionCard);
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
			if (effect.getActivation() == Activation.PowerUp) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event PowerUp");
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
			if (effect.getActivation() == Activation.PowerUp) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event PowerUp");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
