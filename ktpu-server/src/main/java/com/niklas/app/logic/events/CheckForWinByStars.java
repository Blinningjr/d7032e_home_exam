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
import com.niklas.app.online.Player;


/**
 * CheckForWinByStars class is a event which handels the logic of checking if any player
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
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          First monster that is finds with 20 or more stars wins and the game is over.
     * 
     * Rule: 16.First monster to get 20 stars win the game
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            checkCards();
            ArrayList<Player> players = new ArrayList<Player>();
            players.add(gameState.getCurrentPlayer());
            players.addAll(gameState.getPlayers());
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                if (player.getMonster().getStars() >= NUM_STARS_NEEDED_TO_WIN) {
                    Player winner = player;
                    players.remove(winner);
                    gameState.getComunication().sendStarsWinner(winner, players);

                    gameState.endGame();
                } 
            }
        }
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
			if (effect.getActivation() == Activation.CheckForWinByStars) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
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
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
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
