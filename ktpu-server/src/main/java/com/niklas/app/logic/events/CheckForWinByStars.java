package com.niklas.app.logic.events;


import java.util.ArrayList;

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
 * CheckForWinByStars class is a event which handels the logic of checking if any client 
 * has won the game by having over 20 stars.
 */
public class CheckForWinByStars extends Event {
    private static final int NUM_STARS_NEEDED_TO_WIN = 20;
    private GameState gameState;


    /**
     * Creates a CheckForWinByStars event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     */
    public CheckForWinByStars(GameState gameState) {
        this.gameState = gameState;
    }


    /**
     * Starts the CheckForWinByStars event and handels the logic for it.
     */
    public void execute() {
        checkCards();
        ArrayList<Client> clients = new ArrayList<Client>();
        clients.add(gameState.getCurrentPlayer());
        clients.addAll(gameState.getPlayers());
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            if (client.getMonster().getStars() >= NUM_STARS_NEEDED_TO_WIN) {
                Client winner = client;
                clients.remove(winner);
                gameState.getComunication().sendStarsWinner(winner, clients);

                gameState.endGame();
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
			if (effect.getActivation() == Activation.CheckForWinByStars) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckForWinByStars");
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
			if (effect.getActivation() == Activation.CheckForWinByStars) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, client, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckForWinByStars");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
