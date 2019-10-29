package com.niklas.app.controller.events;

import java.util.ArrayList;

import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.cards.Duration;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreCardType;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;

public class Attack implements Event {
    private GameState gameState;
    private Client attackingClient;
    private ArrayList<Client> clients;
    private int numClaws;
    private int bonusDamage;

    public Attack(GameState gameState, Client attackingClient, ArrayList<Client> clients, int numClaws) {
        this.gameState = gameState;
        this.attackingClient = attackingClient;
        this.clients = clients;
        this.numClaws = numClaws;

        bonusDamage = 0;
    }

    public void execute() {
        checkCards();
        if (numClaws + bonusDamage > 0) {
    		boolean enterTokyo = true;
    		if (attackingClient.getMonster().getInTokyo()) {
    			for (Client client : clients) {
    				attack(client, numClaws + bonusDamage);
				}
    		} else {
    			for (Client client : clients) {
    				if (client.getMonster().getInTokyo()) {
						attack(client,numClaws);
						if (client.getMonster().getInTokyo()) {
                            // 6e. If you were outside, then the monster inside tokyo may decide to leave Tokyo
                            String answer = gameState.getComunication().sendLeaveTokyo(client);
                            if(answer.equalsIgnoreCase("YES")) {
                                client.getMonster().setInTokyo(false);
                            } else {
                                enterTokyo = false;
                            }
                        }
					}
				}
    			if (enterTokyo) {
    				attackingClient.getMonster().setInTokyo(true);
    			}
    		}
    	}
    }

    private void attack(Client defendingClient, int damage) {
        Defend defend = new Defend(gameState, attackingClient, defendingClient, damage);
        defend.execute();
    }

    public void addBonusDamage(int bonusDamage) {
        if (bonusDamage > 0) {
            this.bonusDamage += bonusDamage;
        } 
    }

    private void checkCards() {
        Monster currentMonster = attackingClient.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.Attack) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, attackingClient, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
                        break;
                    case addDamage:
                        gameState.action.addDamage(this, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event Attack");
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
			if (effect.getActivation() == Activation.Attack) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, attackingClient, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
                        break;
                    case addDamage:
                        gameState.action.addDamage(this, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event Attack");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
