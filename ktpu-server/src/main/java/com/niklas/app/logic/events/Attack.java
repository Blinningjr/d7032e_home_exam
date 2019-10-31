package com.niklas.app.logic.events;


import java.util.ArrayList;

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
 * Attack class is a event which handels the logic of attacking.
 */
public class Attack extends Event {
    private GameState gameState;
    private Player attackingPlayer;
    private ArrayList<Player> players;
    private int numClaws;
    private int bonusDamage;


    /**
     * Creates a Attack event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param attackingPlayer is the player for the monster that is attacking.
     * @param numClaws is the number of claws the monster has rolled(numClaws = damage).
     */
    public Attack(GameState gameState, Player attackingPlayer, int numClaws) {
        this.gameState = gameState;
        this.attackingPlayer = attackingPlayer;
        this.numClaws = numClaws;

        ArrayList<Player> theRestOfThePlayers = new ArrayList<Player>();
        theRestOfThePlayers.add(gameState.getCurrentPlayer());
        theRestOfThePlayers.addAll(gameState.getPlayers());
        theRestOfThePlayers.remove(attackingPlayer);

        players = theRestOfThePlayers;

        bonusDamage = 0;
    }


    /**
     * Starts the Attack event and handels the logic for it.
     * 
     * Implementation: Checks cards for activation and activates it, if it should.
     *          Folows rule 12 by starting Defend event if monster attacks and starting AwardStar event if monster enters tokyo.
     * 
     * Rule 12.
     *          Each claw
     *              i.Inside Tokyo –1 damage dealt to each monster outside of Tokyo
     *              ii.Outside Tokyo
     *                  1.Tokyo Unoccupied = Move into Tokyo and Gain 1 star
     *                  2.Tokyo Occupied
     *                      a.1 damage dealt to the monster inside Tokyo
     *                      b.Monsters damaged may choose to leave Tokyo
     *                      c.If there is an open spot in Tokyo –Move into Tokyo and Gain 1 star
     */
    public void execute() {
        if (gameState.getIsGameOn()) {
            checkCards();
            if (numClaws + bonusDamage > 0) {
                boolean enterTokyo = true;
                if (attackingPlayer.getMonster().getInTokyo()) {
                    for (Player player : players) {
                        attack(player, numClaws + bonusDamage);
                    }
                } else {
                    for (Player player : players) {
                        if (player.getMonster().getInTokyo()) {
                            attack(player,numClaws);
                            if (player.getMonster().getInTokyo()) {
                                // 6e. If you were outside, then the monster inside tokyo may decide to leave Tokyo
                                String answer = gameState.getCommunication().sendLeaveTokyo(player);
                                if(answer.equalsIgnoreCase("YES")) {
                                    player.getMonster().setInTokyo(false);
                                } else {
                                    enterTokyo = false;
                                }
                            }
                        }
                    }
                    if (enterTokyo) {
                        AwardStar aw = new AwardStar(gameState, attackingPlayer, 1);
                        aw.execute();
                        attackingPlayer.getMonster().setInTokyo(true);
                    }
                }
            }
        }
    }


    /**
     * Attacks a specific players monster.
     * @param defendingplayer the player which monster will be attackt.
     * @param damage the damage of the attack.
     */
    private void attack(Player defendingplayer, int damage) {
        Defend defend = new Defend(gameState, attackingPlayer, defendingplayer, damage);
        defend.execute();
    }


    /**
     * Addes more damage to the event.
     * @param bonusDamage is damage that will be added to the attack(Most be positiv). 
     */
    public void addBonusDamage(int bonusDamage) {
        if (bonusDamage > 0) {
            this.bonusDamage += bonusDamage;
        } 
    }


    /**
     * Checks all the attacking players cards for cards that should activate at this event
     * and executes the cards effect.
     */
    protected void checkCards() {
        Monster currentMonster = attackingPlayer.getMonster();
        for (int i = 0; i < currentMonster.storeCards.size(); i++) {
            StoreCard storeCard = currentMonster.storeCards.get(i);
            Effect effect = storeCard.getEffect();
			if (effect.getActivation() == Activation.Attack) {
				switch (effect.getAction()) {
                    case giveStarsEnergyAndHp:
                        gameState.action.giveStarsEnergyAndHp(gameState, attackingPlayer, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, attackingPlayer, effect);
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
                        gameState.action.giveStarsEnergyAndHp(gameState, attackingPlayer, effect);
                        break;
                    case damageEveryoneElse:
                        gameState.action.damageEveryoneElse(gameState, attackingPlayer, effect);
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
