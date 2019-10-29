package com.niklas.app.controller.events;


import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.cards.CardStore;
import com.niklas.app.model.cards.Duration;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreCardType;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;


public class Shopping implements Event {
    private GameState gameState;
    private int extraCost;

    public Shopping(GameState gameState) {
        this.gameState = gameState;
        extraCost = 0;
    }

    public void execute() {
        checkCards();
        Client currentPlayer = gameState.getCurrentPlayer();
        CardStore cardStore = gameState.getCardStore();
        StoreCard[] storeCards = cardStore.get_inventory();

        String answer = gameState.getComunication().send_shopping(currentPlayer, cardStore, extraCost);
        int buy = Integer.parseInt(answer);

        if(buy>0 && (currentPlayer.getMonster().get_energy() >= storeCards[buy -1].get_cost() + extraCost)) { 
        	try {
        		currentPlayer.getMonster().set_entergy(currentPlayer.getMonster().get_energy() -  storeCards[buy - 1].get_cost() + extraCost);
                currentPlayer.getMonster().storeCards.add(cardStore.buy(buy-1));
                checkBoughtCard();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public void add_cost(int addedCost) {
        extraCost += addedCost;
    }

    private void checkBoughtCard() {
        Monster currentMonster = gameState.getCurrentPlayer().getMonster();
        StoreCard storeCard = currentMonster.storeCards.get(currentMonster.storeCards.size() - 1);
        Effect effect = storeCard.getEffect();
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
                        + " is not implemented for event Shopping");
            }
            if (storeCard.getType() == StoreCardType.discard) {
                currentMonster.storeCards.remove(currentMonster.storeCards.size() - 1);
                gameState.getCardStore().discardCard(storeCard);
            }
        }
    }

    private void checkCards() {
        Monster currentMonster = gameState.getCurrentPlayer().getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.Shopping) {
				switch (effect.getAction()) {
                    case addCost:
                        gameState.action.addCost(this, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event Shopping");
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
			if (effect.getActivation() == Activation.Shopping) {
				switch (effect.getAction()) {
                    case addCost:
                        gameState.action.addCost(this, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event Shopping");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
