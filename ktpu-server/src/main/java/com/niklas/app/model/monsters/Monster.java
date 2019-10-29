package com.niklas.app.model.monsters;

import java.util.ArrayList;

import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.EvolutionDeck;
import com.niklas.app.model.cards.StoreCard;


/**
 * Defines core attributes and funtionalety of a monster.
 * TODO: connetions stuff.
 * TODO: functions for cards and evolution_deck.
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
     * @param in_tokyo is if the monster is in tokyo.
     * @param cards is the cards the monster has.
     * @param evolutionDeck is the special evelution cards for this monster.
     */
    public Monster(String name, int maxHp, int hp, int energy, int stars, boolean in_tokyo, 
                    ArrayList<StoreCard> storeCards, EvolutionDeck evolutionDeck) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = hp;
        this.energy = energy;
        this.stars = stars;
        this.inTokyo = in_tokyo;
        this.storeCards = storeCards;
        this.evolutionDeck = evolutionDeck;
        isDead = false;

        evolutionCards = new ArrayList<EvolutionCard>();

        this.evolutionDeck.shuffle();
    }


    public String cards_to_string() {
        String cards_string = "";
        for (StoreCard card : storeCards) {
            cards_string += "\t" + card.to_string() + ":";
        }
        return cards_string;
    }
    
    
    public EvolutionCard drawEvolutionCard() {
    	EvolutionCard card =  evolutionDeck.draw_card();
    	return card;
    }

    
    public void discardEvolutionCard(EvolutionCard card) {
    	evolutionDeck.discard_card(card);
    }

    public void addHp(int hp) {
    	this.hp += hp;
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
     * Sets the value of in_tokyo.
     * @param in_tokyo is the new value of in_tokyo.
     */
    public void setInTokyo(boolean in_tokyo) {
        this.inTokyo = in_tokyo;
    }

    public boolean getIsDead() {
        return isDead;
    }

    public void setIsDead(boolean isDead) {
        this.isDead = isDead;
    }
}
