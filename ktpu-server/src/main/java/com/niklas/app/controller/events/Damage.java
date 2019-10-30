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


public class Damage implements Event {
    private GameState gameState;
    private Client client;
    private int damage;

    public Damage(GameState gameState, Client client, int damage) {
        this.gameState = gameState;
        this.client = client;
        this.damage = damage;
    }

    public void execute() {
        checkCards();
        Monster monster = client.getMonster();
        if (damage > 0 && !monster.getIsDead()) {
            monster.setHp(monster.getHp() - damage);
            if (monster.getHp() < 1) {
                monster.setIsDead(true);
                monster.setInTokyo(false);
                ArrayList<Client> clients = new ArrayList<Client>();
                clients.add(gameState.getCurrentPlayer());
                clients.addAll(gameState.getPlayers());
                clients.remove(client);
                gameState.getComunication().sendMonsterDied(client, clients);

                CheckForWinByElimination cfwbe = new CheckForWinByElimination(gameState);
                cfwbe.execute();
            }
    	}
    }

    private void checkCards() {
        Monster currentMonster = client.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.Damage) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
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
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
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
