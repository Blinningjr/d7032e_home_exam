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


public class PowerUp implements Event {
    private GameState gameState;
    private int numHearts;
    private int heartsNeeded;

    public PowerUp(GameState gameState, int numHearts) {
        this.gameState = gameState;
        this.numHearts = numHearts;
        heartsNeeded = 3;
    }

    public void execute(){
        Monster monster = gameState.getCurrentPlayer().getMonster();
        if (numHearts >= heartsNeeded) {
            checkCards();
            EvolutionCard evolutionCard = monster.draw_evolution_card();
            monster.evolutionCards.add(evolutionCard);
            checkNewCard();
        }
    }

    public void addHeartsNeeded(int addedHeartsNeeded) {
        heartsNeeded += addedHeartsNeeded;
    }

    private void checkNewCard() {
        Monster currentMonster = gameState.getCurrentPlayer().getMonster();
        EvolutionCard evolutionCard = currentMonster.evolutionCards.get(currentMonster.evolutionCards.size() - 1);
        Effect effect = evolutionCard.getEffect();
        if (effect.getActivation() == Activation.Now) {
            switch (effect.getAction()) {
                case giveStarsEnergyAndHp:
                    gameState.action.giveStarsEnergyAndHp(gameState,
                        gameState.getCurrentPlayer(), effect);
                    break;
                case damageEveryoneElse:
                    gameState.action.damageEveryoneElse(gameState, effect);
                    break;
                default:
                    throw new Error("action=" + effect.getAction() 
                        + " is not implemented for event PowerUp");
            }
            if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
                currentMonster.evolutionCards.remove(currentMonster.evolutionCards.size() - 1);
                currentMonster.discardEvolutionCard(evolutionCard);
            }
        }
    }

    private void checkCards() {
        Client client = gameState.getCurrentPlayer();
        Monster currentMonster = client.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.PowerUp) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event PowerUp");
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
			if (effect.getActivation() == Activation.PowerUp) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, client, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event PowerUp");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
