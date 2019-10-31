package com.niklas.app.logic.events;


import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.cards.Duration;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreCardType;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Player;


/**
 * CheckNumOfThrees class is a event which handels the logic of giving a stars for the number of threes rolled.
 */
public class CheckNumOfThrees extends Event {
    private GameState gameState;
    private int numThrees;
    private int numThreesNeeded;
    private int starsAdded;


    /**
     * Creates a CheckNumOfThrees event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param numThrees is the number of threes rolled.
     */
    public CheckNumOfThrees(GameState gameState, int numThrees) {
        this.gameState = gameState;
        this.numThrees = numThrees;
        numThreesNeeded = 3;
        starsAdded = 3;
    }


    /**
     * Starts the CheckNumOfThrees event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Follows rule 12 and gives stars by starting event AwardStar.
     * 
     * Rule: 12.
     *          Tripple 3’s = 3 StarsEach additional 3 equals +1 star
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            Player player = gameState.getCurrentPlayer();
            if (numThrees >= numThreesNeeded) {
                checkCards();
                AwardStar aw = new AwardStar(gameState, player, starsAdded + numThrees - numThreesNeeded);
                aw.execute();
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
			if (effect.getActivation() == Activation.CheckNumOfThrees) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckNumOfThrees");
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
			if (effect.getActivation() == Activation.CheckNumOfThrees) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event CheckNumOfThrees");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
