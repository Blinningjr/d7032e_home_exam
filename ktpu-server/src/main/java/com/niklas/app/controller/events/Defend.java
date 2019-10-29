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


public class Defend implements Event {
    private GameState gameState;
    private Client attackingClient;
    private Client defendingClient;
    private int damage;
    private int armor;

    public Defend(GameState gameState, Client attackingClient, Client defendingClient, int damage) {
        this.gameState = gameState;
        this.attackingClient = attackingClient;
        this.defendingClient = defendingClient;
        this.damage = damage;

        armor = 0;
    }

    public void execute() {
        checkCards();
        Damage d = new Damage(gameState, defendingClient, damage - armor);
        d.execute();
    }

    public void addArmor(int addedArmor) {
        if (addedArmor > 0) {
            armor += addedArmor;
        } 
    }

    private void checkCards() {
        Monster currentMonster = defendingClient.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.Defend) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, defendingClient, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
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
                        gameState.action.giveStarsEnergyAndHp(gameState, defendingClient, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
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
