package com.niklas.app.logic.events;


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
 * Heal class is a event which handels the logic of giving a monster hp.
 */
public class Heal extends Event {
    private GameState gameState;
    private Player player; 
    private int addedMaxHp;
    private int healing;


    /**
     * Creates a Heal event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param player is the player which monster will be healed.
     * @param numHearts is the number of hearts rolled(hearts = number of hp healed).
     */
    public Heal(GameState gameState, Player player, int numHearts) {
        this.gameState = gameState;
        this.player = player;
        healing = numHearts;

        addedMaxHp = 0;
    }


    /**
     * Starts the Heal event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Adds healing to players monsters hp upto monsters maxHp + addedMaxHp if it is alive.
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            checkCards();
            Monster monster = player.getMonster();
            int maxHp = monster.getMaxHp() + addedMaxHp;
            int newHp = monster.getHp() + healing;
            if (newHp > maxHp) {
                newHp = maxHp;
            }
            if (!monster.getIsDead()) {
                monster.setHp(newHp);
            }
        }
    }


    /**
     * Addes to the amount being healed.
     * @param healing adds to the amount of hp healed.
     */
    public void addHealing(int healing) {
        if (healing >= 0) {
            this.healing += healing;
        }
    }


    /**
     * Addes to the monsters maximum hp when being healed.
     * @param addedMaxHp adds to the monsters maximum hp when being healed.
     */
    public void addMaxHp(int addedMaxHp) {
        if (addedMaxHp >= 0) {
            this.addedMaxHp += addedMaxHp;
        }
    }


    /**
     * Checks all the current players cards for cards that should activate at this event
     * and executes the cards effect.
     */
    protected void checkCards() {
        Monster currentMonster = player.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.Heal) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event Heal");
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
			if (effect.getActivation() == Activation.Heal) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event Heal");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
