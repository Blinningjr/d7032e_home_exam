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


public class CheckForWinByElimination implements Event {
    private GameState gameState;
    private boolean gameOver;

    public CheckForWinByElimination(GameState gameState) {
        this.gameState = gameState;
        gameOver = false;
    }

    public void execute() {
        checkCards();
        ArrayList<Client> clients = new ArrayList<Client>();
        clients.add(gameState.getCurrentPlayer());
        clients.addAll(gameState.getPlayers());
        ArrayList<Client> aliveClients = new ArrayList<Client>();
        for (Client client : clients) {
            if (!client.getMonster().getIsDead()) {
                aliveClients.add(client);
            } 
        }
        if (aliveClients.size() == 1) {
            gameOver = true;
            Client winner = aliveClients.get(0);
            clients.remove(winner);
            gameState.getComunication().sendEliminationWinner(winner, clients);
            clients.add(winner);
            gameState.getComunication().closeSocet();
            System.exit(0);
        }
    }

    public boolean getGameOver() {
        return gameOver;
    }

    private void checkCards() {
        Client client = gameState.getCurrentPlayer();
        Monster currentMonster = client.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.CheckForWinByElimination) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckForWinByElimination");
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
			if (effect.getActivation() == Activation.CheckForWinByElimination) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckForWinByElimination");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}