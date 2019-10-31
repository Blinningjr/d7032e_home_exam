package com.niklas.app.model.monsters;


import java.util.ArrayList;

import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.EvolutionDeck;
import com.niklas.app.model.cards.StoreCard;


/**
 * Defines what all the attributes of a Monster.
 */
public class Monster {
    private String name;
    private final int maxHp;
    private int hp;
    private int energy;
    private int stars;
    private boolean inTokyo;
    private boolean isDead;
    public ArrayList<StoreCard> storeCards; //fix later
    public ArrayList<EvolutionCard> evolutionCards; //fix later
    private EvolutionDeck evolutionDeck;


    /**
     * Creates a Monster.
     * @param name is the name of the monster.
     * @param maxHp is the maximum hit points of the monster.
     * @param hp is the monsters hit points.
     * @param energy is the monsters energy.
     * @param stars is the monsters stars.
     * @param inTokyo is if the monster is in tokyo.
     * @param cards is the cards the monster has.
     * @param evolutionDeck is the special evelution cards for this monster.
     */
    public Monster(String name, int maxHp, int hp, int energy, int stars, boolean inTokyo, 
                    ArrayList<StoreCard> storeCards, EvolutionDeck evolutionDeck) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = hp;
        this.energy = energy;
        this.stars = stars;
        this.inTokyo = inTokyo;
        this.storeCards = storeCards;
        this.evolutionDeck = evolutionDeck;
        isDead = false;

        evolutionCards = new ArrayList<EvolutionCard>();

        this.evolutionDeck.shuffle();
    }


    /**
     * Converts all the cards into a String.
     * @return is all the cards represented in a String.
     */
    public String cardsToString() {
        String cardsString = "";
        for (StoreCard card : storeCards) {
            cardsString += "\t" + card.toString() + ":";
        }
        for (EvolutionCard card : evolutionCards) {
            cardsString += "\t" + card.toString() + ":";
        }
        return cardsString;
    }
    
    
    /**
     * Draws the top Evolusion cards form the deck.
     * @return a evolusion card drawn from the deck.
     */
    public EvolutionCard drawEvolutionCard() {
    	EvolutionCard card =  evolutionDeck.drawCard();
    	return card;
    }

    
    /**
     * Adds a EvolutionCards to the monsters EvolutionDeck discard pile.
     * @param card the cards that will be added to the discard pile.
     */
    public void discardEvolutionCard(EvolutionCard card) {
    	evolutionDeck.discardCard(card);
    }


    /**
     * Gets the maximum hit points the monster can have
     * @return the maximim hit points as a int.
     */
    public int getMaxHp() {
        return maxHp;
    }


    /**
     * Gets the hit points of the monster.
     * @return the hit points of this monster.
     */
    public int getHp() {
        return hp;
    }


    /**
     * Sets the hit points of the monster.
     * @param hp is the new hit points of this monster.
     */
    public void setHp(int hp) {
        this.hp = hp;
    }


    /**
     * Gets the name of the monster.
     * @return the name of this monster.
     */
    public String getName() {
        return name;
    }


    /**
     * Gets the energy of the monster.
     * @return the energy this monster has.
     */
    public int getEnergy() {
        return energy;
    }


    /**
     * Sets the energy of the monster
     * @param energy is this monsters new energy.
     */
    public void setEnergy(int energy) {
        this.energy = energy;
    }


    /**
     * Gets the amount of stars the monster has.
     * @return the amount of starts that this monster has
     */
    public int getStars() {
        return stars;
    }


    /**
     * Sets the amount of stars the monster has.
     * @param stars is the new amount of stars this monster have. 
     */
    public void setStars(int stars) {
        this.stars = stars;
    }


    /**
     * Gets if the monster is in tokyo.
     * @return true if monster is in tokyo.
     */
    public boolean getInTokyo() {
        return inTokyo;
    }


    /**
     * Sets the value of inTokyo.
     * @param inTokyo is the new value of inTokyo.
     */
    public void setInTokyo(boolean inTokyo) {
        this.inTokyo = inTokyo;
    }


    /**
     * Get a boolean that says if the monster is dead.
     * @return a boolean that is true if the monster is dead.
     */
    public boolean getIsDead() {
        return isDead;
    }


    /**
     * Sets the value of isDead. True if monster is dead.
     * @param isDead will be the new isDead value.
     */
    public void setIsDead(boolean isDead) {
        this.isDead = isDead;
    }
}
