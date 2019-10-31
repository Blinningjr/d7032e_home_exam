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
 * AwardStarIfCurrentPlayerInTokyo class is a event which handels the logic of giving a star to a monster if it is in tokyo.
 */
public class AwardStarIfCurrentPlayerInTokyo extends Event {
    private GameState gameState;
    private int stars;


    /**
     * Creates a AwardStarIfCurrentPlayerInTokyo event with the given parameters.
     * @param gameStateis the games state which has all the information about the current game.
     */
    public AwardStarIfCurrentPlayerInTokyo(GameState gameState) {
    	this.gameState = gameState;
        stars = 1;
    }


    /**
     * Starts the AwardStarIfCurrentPlayerInTokyo event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Add stars to the players monsters stars if the monster is in tokyo by starting event AwardStar.
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            Player currentPlayer = gameState.getCurrentPlayer();
            if (currentPlayer.getMonster().getInTokyo()) {
                checkCards();
                AwardStar awardStar = new AwardStar(gameState, currentPlayer, stars);
                awardStar.execute();
            }
            gameState.getComunication().sendAllStats(currentPlayer, gameState.getPlayers());
        }
    }


    /**
     * Adds to the amount of stars given to the monster.
     * @param stars is the amount of stars that will be added to the amount of stars given to the monster.
     */
    public void addStars(int stars) {
        this.stars += stars;
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
			if (effect.getActivation() == Activation.AwardStarIfCurrentPlayerInTokyo) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event AwardStarIfCurrentPlayerInTokyo");
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
			if (effect.getActivation() == Activation.AwardStarIfCurrentPlayerInTokyo) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, player, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, player, effect);
                        break;
                    default:
                        throw new Error("action=" + effect.getAction() 
                            + " is not implemented for event AwardStarIfCurrentPlayerInTokyo");
                }
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
                    currentMonster.discardEvolutionCard(evolutionCard);
				}
			}
		}
    }
}
