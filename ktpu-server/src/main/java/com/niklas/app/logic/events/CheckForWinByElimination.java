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
 * CheckForWinByElimination class is a event which handels the logic of checking if any player have won by being the last monster alvie.
 */
public class CheckForWinByElimination extends Event {
    private GameState gameState;


    /**
     * Creates a CheckForWinByElimination event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     */
    public CheckForWinByElimination(GameState gameState) {
        this.gameState = gameState;
    }


    /**
     * Starts the CheckForWinByElimination event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          If there is only one monster alive that monster wins by elimination and game is over.
     * 
     * Rule: 17.The sole surviving monster wins the game (other monsters at 0 or less health)
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            checkCards();
            ArrayList<Player> players = new ArrayList<Player>();
            players.add(gameState.getCurrentPlayer());
            players.addAll(gameState.getPlayers());
            ArrayList<Player> alivePlayers = new ArrayList<Player>();
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                if (!player.getMonster().getIsDead()) {
                    alivePlayers.add(player);
                } 
            }
            if (alivePlayers.size() == 1) {
                Player winner = alivePlayers.get(0);
                players.remove(winner);
                gameState.getCommunication().sendEliminationWinner(winner, players);
                gameState.endGame();
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
			if (effect.getActivation() == Activation.CheckForWinByElimination) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckForWinByElimination");
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
			if (effect.getActivation() == Activation.CheckForWinByElimination) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckForWinByElimination");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
