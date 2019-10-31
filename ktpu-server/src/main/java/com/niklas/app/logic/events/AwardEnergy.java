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
 *  AwardEnergy class is a event which handels the logic of giving a monster energy.
 */
public class AwardEnergy extends Event {
    private GameState gameState;
    private Player player;
    private int numEnergy;
    

    /**
     * Creates a AwardEnergy event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param player is the player Which mosnter will recive the energy
     * @param numEnergy is the number of energy added to the monster.
     */
    public AwardEnergy(GameState gameState, Player player, int numEnergy) {
        this.gameState = gameState;
        this.player = player;
        this.numEnergy = numEnergy;
    }


    /**
     * Starts the AwardEnergy event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Add numEnergy to the players monsters energy.
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            checkCards();
            player.getMonster().setEnergy(player.getMonster().getEnergy() + numEnergy);
        }
    }


    /**
     * Checks all the players cards for cards that should activate at this event
     * and executes the cards effect.
     */
    protected void checkCards() {
        Monster currentMonster = player.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.AwardEnergy) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event AwardEnergy");
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
			if (effect.getActivation() == Activation.AwardEnergy) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event AwardEnergy");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
