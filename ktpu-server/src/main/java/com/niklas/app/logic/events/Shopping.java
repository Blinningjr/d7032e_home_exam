package com.niklas.app.logic.events;


import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.cards.CardStore;
import com.niklas.app.model.cards.Duration;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreCardType;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Player;


/**
 * Shopping class is a event which handels the logic of shopping.
 */
public class Shopping extends Event {
    private GameState gameState;
    private int extraCost;


    /**
     * Creates a Shopping event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     */
    public Shopping(GameState gameState) {
        this.gameState = gameState;
        extraCost = 0;
    }


    /**
     * Starts the Shopping event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Follows rule 13 and activates and discards the card if it is a discard card or adds it to the player if it is a keep.
     * 
     * Rule: 13.Buying Cards (As long as long as you have the Energy, you can take any of the following actions)
     *          Purchase a card = Pay energy equal to the card cost(replace purchased cards with new from the deck)
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            checkCards();
            Player currentPlayer = gameState.getCurrentPlayer();
            Monster currentMonster = currentPlayer.getMonster();
            CardStore cardStore = gameState.getCardStore();
            StoreCard[] storeCards = cardStore.getInventory();

            String answer = gameState.getComunication().sendShopping(currentPlayer, cardStore, extraCost);
            int buy = Integer.parseInt(answer);
            if (buy > 0){
                int cost = storeCards[buy -1].getCost() + extraCost;
                if(currentMonster.getEnergy() >= cost) { 
                    try {
                        currentMonster.setEnergy(currentPlayer.getMonster().getEnergy() - cost);
                        currentMonster.storeCards.add(cardStore.buy(buy-1));
                        checkBoughtCard();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else if (buy == 0) {
                ResetStore resetStore = new ResetStore(gameState);
                resetStore.execute();
            }
        }
    }


    /**
     * Addes cost to the cards in the store.
     * @param addedCost is the amount of extra cost(Can be negative for reduced cost).
     */
    public void addCost(int addedCost) {
        extraCost += addedCost;
    }


    /**
     * Checks if the current players new card should activate when they are bought and be discarded.
     */
    private void checkBoughtCard() {
        Player currentPlayer = gameState.getCurrentPlayer();
        Monster currentMonster = currentPlayer.getMonster();
        StoreCard storeCard = currentMonster.storeCards.get(currentMonster.storeCards.size() - 1);
        Effect effect = storeCard.getEffect();
        if (effect.getActivation() == Activation.Now) {
            switch (effect.getAction()) {
                case giveStarsEnergyAndHp:
                    gameState.action.giveStarsEnergyAndHp(gameState,
                        currentPlayer, effect);
                    break;
                case damageEveryoneElse:
                    gameState.action.damageEveryoneElse(gameState, currentPlayer, effect);
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
			if (effect.getActivation() == Activation.Shopping) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
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
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
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
