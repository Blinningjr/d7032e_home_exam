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
import com.niklas.app.online.Client;


/**
 * Attack class is a event which handels the logic of attacking.
 */
public class Attack extends Event {
    private GameState gameState;
    private Client attackingClient;
    private ArrayList<Client> clients;
    private int numClaws;
    private int bonusDamage;


    /**
     * Creates a Attack event with the given parameters.
     * @param gameState is the games state which has all the information about the current game.
     * @param attackingClient is the client for the monster that is attacking.
     * @param numClaws is the number of claws the monster has rolled(numClaws = damage).
     */
    public Attack(GameState gameState, Client attackingClient, int numClaws) {
        this.gameState = gameState;
        this.attackingClient = attackingClient;
        this.numClaws = numClaws;

        ArrayList<Client> theRestOfTheClients = new ArrayList<Client>();
        theRestOfTheClients.add(gameState.getCurrentPlayer());
        theRestOfTheClients.addAll(gameState.getPlayers());
        theRestOfTheClients.remove(attackingClient);

        clients = theRestOfTheClients;

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
                        AwardStar aw = new AwardStar(gameState, attackingClient, 1);
                        aw.execute();
                        attackingClient.getMonster().setInTokyo(true);
                    }
                }
            }
        }
    }


    /**
     * Attacks a specific clients monster.
     * @param defendingClient the client which monster will be attackt.
     * @param damage the damage of the attack.
     */
    private void attack(Client defendingClient, int damage) {
        Defend defend = new Defend(gameState, attackingClient, defendingClient, damage);
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
     * Checks all the attacking clients cards for cards that should activate at this event
     * and executes the cards effect.
     */
    protected void checkCards() {
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
                        gameState.action.damageEveryoneElse(gameState, attackingClient, effect);
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
                        gameState.action.damageEveryoneElse(gameState, attackingClient, effect);
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
