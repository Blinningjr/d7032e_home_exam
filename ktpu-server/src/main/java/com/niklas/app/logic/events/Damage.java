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
 * Damage class is a event which handels the logic of a monster being damaged.
 */
public class Damage extends Event {
    private GameState gameState;
    private Player player;
    private int damage;


    /**
     * Creates a Damage event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param player is the player of the monster which is being damaged.
     * @param damage is the amount of damaged being dealt.
     */
    public Damage(GameState gameState, Player player, int damage) {
        this.gameState = gameState;
        this.player = player;
        this.damage = damage;
    }


    /**
     * Starts the Damage event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Deals damage to the players monster and starts event CheckForWinByElimination.
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            checkCards();
            Monster monster = player.getMonster();
            if (damage > 0 && !monster.getIsDead()) {
                monster.setHp(monster.getHp() - damage);
                if (monster.getHp() < 1) {
                    monster.setIsDead(true);
                    monster.setInTokyo(false);
                    ArrayList<Player> players = new ArrayList<Player>();
                    players.add(gameState.getCurrentPlayer());
                    players.addAll(gameState.getPlayers());
                    players.remove(player);
                    gameState.getComunication().sendMonsterDied(player, players);

                    CheckForWinByElimination cfwbe = new CheckForWinByElimination(gameState);
                    cfwbe.execute();
                }
            }
        }
    }


    /**
     * Checks all the clients cards for cards that should activate at this event
     * and executes the cards effect.
     */
    protected void checkCards() {
        Monster currentMonster = player.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.Damage) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event Damage");
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
			if (effect.getActivation() == Activation.Damage) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event Damage");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
