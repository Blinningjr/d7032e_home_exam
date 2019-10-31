package com.niklas.app.controller.events;


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
import com.niklas.app.online.Client;


public class CheckDice implements Event {
    private int numOnes;
    private int numTwos;
    private int numThrees;
    private int numHearts;
    private int numClaws;
    private int numEnergy;

    private ArrayList<KTPUDice> dice;
    private GameState gameState;

    public CheckDice(GameState gameState, ArrayList<KTPUDice> dice) {
        this.gameState = gameState;
        this.dice = dice;
        resetNum();
    }

    public void execute() {
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
    }

    private void resetNum() {
        numOnes = 0;
        numTwos = 0;
        numThrees = 0;
        numHearts = 0;
        numClaws = 0;
        numEnergy = 0;
    }

    public int getNumOnes() {
        return numOnes;
    }

    public int getNumTwos() {
        return numTwos;
    }

    public int getNumThrees() {
        return numThrees;
    }

    public int getNumHearts() {
        return numHearts;
    }

    public int getNumClaws() {
        return numClaws;
    }

    public int getNumEnergy() {
        return numEnergy;
    }

    private void checkCards() {
        Client client = gameState.getCurrentPlayer();
        Monster currentMonster = client.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.CheckDice) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
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
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
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
