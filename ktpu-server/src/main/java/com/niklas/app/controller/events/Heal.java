package com.niklas.app.controller.events;

import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.cards.Duration;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreCardType;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;


public class Heal implements Event {
    private GameState gameState;
    private Client client; 

    private int addedMaxHp;
    private int healing;

    public Heal(GameState gameState, Client client, int numHearts) {
        this.gameState = gameState;
        this.client = client;
        healing = numHearts;

        addedMaxHp = 0;
    }

    public void execute() {
    	checkCards();
        Monster monster = client.getMonster();
        int maxHp = monster.get_max_hp() + addedMaxHp;
        int newHp = monster.getHp() + healing;
        if (newHp > maxHp) {
            newHp = maxHp;
        }
        if (!monster.getIsDead()) {
            monster.setHp(newHp);
        }
    }

    public void addHealing(int healing) {
        if (healing >= 0) {
            this.healing += healing;
        }
    }

    public void addMaxHp(int addedMaxHp) {
        if (addedMaxHp >= 0) {
            this.addedMaxHp += addedMaxHp;
        }
    }

    private void checkCards() {
        Monster currentMonster = client.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.Heal) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
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
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
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
