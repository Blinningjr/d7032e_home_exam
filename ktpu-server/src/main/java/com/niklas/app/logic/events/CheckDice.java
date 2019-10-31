package com.niklas.app.logic.events;


import java.util.ArrayList;

import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.cards.Duration;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreCardType;
import com.niklas.app.model.dice.KTPUDice;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Player;


/**
 * CheckDice class is a event which handels the logic of checking the result of the dice roll.
 */
public class CheckDice extends Event {
    private ArrayList<KTPUDice> dice;
    private GameState gameState;
    private int numOnes;
    private int numTwos;
    private int numThrees;
    private int numHearts;
    private int numClaws;
    private int numEnergy;


    /**
     * Creates a CheckDice event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param dice is the rolled dice that will be check for result.
     */
    public CheckDice(GameState gameState, ArrayList<KTPUDice> dice) {
        this.gameState = gameState;
        this.dice = dice;
        resetNum();
    }


    /**
     * Starts the CheckDice event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Checks the result of the dice and starts events:
     *              HealingNotInTokyo
     *              PowerUp
	 *	            CheckNumOfOnes
     *              CheckNumOfTwos
     *              CheckNumOfThrees
     *              Attack
     *              AwardEnergy
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            checkCards();
            for (KTPUDice ktpuDice : dice) {
                switch (ktpuDice.getValue()) {
                case KTPUDice.ONE:
                    numOnes += 1;
                    break;
                case KTPUDice.TWO:
                    numTwos += 1;
                    break;
                case KTPUDice.THREE:
                    numThrees += 1;
                    break;
                case KTPUDice.HEART:
                    numHearts += 1;
                    break;
                case KTPUDice.CLAWS:
                    numClaws += 1;
                    break;
                case KTPUDice.ENERGY:
                    numEnergy += 1;
                    break;
                default:
                    throw new Error("Dice value:" + ktpuDice.getValue() + " is not implemented for KTPUDice");
                }
            }
            gameState.getComunication().sendRolledDice(dice, gameState.getCurrentPlayer());

            HealingNotInTokyo cnh = new HealingNotInTokyo(gameState, numHearts);
            cnh.execute();
        
            PowerUp powerUp = new PowerUp(gameState, numHearts);
            powerUp.execute();
            
            // 6c. 3 of a number = victory points
            CheckNumOfOnes cnoo = new CheckNumOfOnes(gameState, numOnes);
            cnoo.execute();
            
            CheckNumOfTwos cnoTwos = new CheckNumOfTwos(gameState, numTwos);
            cnoTwos.execute();
            
            CheckNumOfThrees cnoThrees= new CheckNumOfThrees(gameState, numThrees);
            cnoThrees.execute();
            
            // 6d. claws = attack (if in Tokyo attack everyone, else attack monster in Tokyo)
            Attack attack = new Attack(gameState, gameState.getCurrentPlayer(), numClaws);
            attack.execute();
            
            // 6f. energy = energy tokens
            AwardEnergy awardEnergy = new AwardEnergy(gameState, gameState.getCurrentPlayer(), numEnergy);
            awardEnergy.execute();
        }
    }


    /**
     * Resetes the result to 0.
     */
    private void resetNum() {
        numOnes = 0;
        numTwos = 0;
        numThrees = 0;
        numHearts = 0;
        numClaws = 0;
        numEnergy = 0;
    }


    /**
     * Returns the number of ones rolled.
     * @return the number of ones rolled.
     */
    public int getNumOnes() {
        return numOnes;
    }


    /**
     * Returns the number of twos rolled.
     * @return the number of twos rolled.
     */
    public int getNumTwos() {
        return numTwos;
    }


    /**
     * Returns the number of threes rolled.
     * @return the number of threes rolled.
     */
    public int getNumThrees() {
        return numThrees;
    }


    /**
     * Returns the number of hearts rolled.
     * @return the number of hearts rolled.
     */
    public int getNumHearts() {
        return numHearts;
    }


    /**
     * Returns the number of claws rolled.
     * @return the number of claws rolled.
     */
    public int getNumClaws() {
        return numClaws;
    }


    /**
     * Returns the number of energy rolled.
     * @return the number of energy rolled.
     */
    public int getNumEnergy() {
        return numEnergy;
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
			if (effect.getActivation() == Activation.CheckDice) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckDice");
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
			if (effect.getActivation() == Activation.CheckDice) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckDice");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
