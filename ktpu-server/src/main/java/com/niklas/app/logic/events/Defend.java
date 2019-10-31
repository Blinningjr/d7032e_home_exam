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
 * Defend class is a event which handels the logic of defending an attack.
 */
public class Defend extends Event {
    private GameState gameState;
    private Player attackingPlayer;
    private Player defendingPlayer;
    private int damage;
    private int armor;


    /**
     * Creates a Defend event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param attackingPlayer is the player of the monster attacking.
     * @param defendingPlayer is the player of the monster deffending.
     * @param damage is the damage of the attack.
     */
    public Defend(GameState gameState, Player attackingPlayer, Player defendingPlayer, int damage) {
        this.gameState = gameState;
        this.attackingPlayer = attackingPlayer;
        this.defendingPlayer = defendingPlayer;
        this.damage = damage;

        armor = 0;
    }


    /**
     * Starts the Defend event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Starts damage event with damage - armor.
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            checkCards();
            Damage d = new Damage(gameState, defendingPlayer, damage - armor);
            d.execute();
        }
    }


    /**
     * Adds armor to the deffending monster in this attack.
     * @param addedArmor is added to the armor.
     */
    public void addArmor(int addedArmor) {
        if (addedArmor > 0) {
            armor += addedArmor;
        } 
    }


    /**
     * Checks all the defending players cards for cards that should activate at this event
     * and executes the cards effect.
     */
    protected void checkCards() {
        Monster currentMonster = defendingPlayer.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.Defend) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, defendingPlayer, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, defendingPlayer, effect);
                        break;
                    case addArmor:
                        gameState.action.addarmor(this, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event Defend");
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
			if (effect.getActivation() == Activation.Defend) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, defendingPlayer, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, defendingPlayer, effect);
                        break;
                    case addArmor:
                        gameState.action.addarmor(this, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event Defend");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
