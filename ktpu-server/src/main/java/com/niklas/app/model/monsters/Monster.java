package com.niklas.app.model.monsters;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.niklas.app.model.cards.Card;
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
    private final int max_hp;
    private int hp;
    private int energy;
    private int stars;
    private boolean in_tokyo;
    public ArrayList<StoreCard> store_cards; //fix later
    private EvolutionDeck evolution_deck;


    /**
     * Creates a Monster.
     * @param name is the name of the monster.
     * @param max_hp is the maximum hit points of the monster.
     * @param hp is the monsters hit points.
     * @param energy is the monsters energy.
     * @param stars is the monsters stars.
     * @param in_tokyo is if the monster is in tokyo.
     * @param cards is the cards the monster has.
     * @param evolution_deck is the special evelution cards for this monster.
     */
    public Monster(String name, int max_hp, int hp, int energy, int stars, boolean in_tokyo, 
                    ArrayList<StoreCard> store_cards, EvolutionDeck evolution_deck) {
        this.name = name;
        this.max_hp = max_hp;
        this.hp = hp;
        this.energy = energy;
        this.stars = stars;
        this.in_tokyo = in_tokyo;
        this.store_cards = store_cards;
        this.evolution_deck = evolution_deck;

        evolution_deck.shuffle();
    }


    public String cards_to_string() {
        String cards_string = "{:";
        for (StoreCard card : store_cards) {
            cards_string += card.to_string();
            cards_string += ":";
        }
        return cards_string + "}";
    }
    
    
    public EvolutionCard draw_evolution_card() {
    	EvolutionCard card =  evolution_deck.draw_card();
    	return card;
    }

    
    public void discard_evolution_card(EvolutionCard card) {
    	evolution_deck.discard_card(card);
    }

    public void add_hp(int hp) {
    	this.hp += hp;
    }

    /**
     * Gets the maximum hit points the monster can have
     * @return the maximim hit points as a int.
     */
    public int get_max_hp() {
        return max_hp;
    }


    /**
     * Gets the hit points of the monster.
     * @return the hit points of this monster.
     */
    public int get_hp() {
        return hp;
    }


    /**
     * Sets the hit points of the monster.
     * @param hp is the new hit points of this monster.
     */
    public void set_hp(int hp) {
        this.hp = hp;
    }


    /**
     * Gets the name of the monster.
     * @return the name of this monster.
     */
    public String get_name() {
        return name;
    }


    /**
     * Gets the energy of the monster.
     * @return the energy this monster has.
     */
    public int get_energy() {
        return energy;
    }


    /**
     * Sets the energy of the monster
     * @param energy is this monsters new energy.
     */
    public void set_entergy(int energy) {
        this.energy = energy;
    }


    /**
     * Gets the amount of stars the monster has.
     * @return the amount of starts that this monster has
     */
    public int get_stars() {
        return stars;
    }


    /**
     * Sets the amount of stars the monster has.
     * @param stars is the new amount of stars this monster have. 
     */
    public void set_stars(int stars) {
        this.stars = stars;
    }


    /**
     * Gets if the monster is in tokyo.
     * @return true if monster is in tokyo.
     */
    public boolean get_in_tokyo() {
        return in_tokyo;
    }


    /**
     * Sets the value of in_tokyo.
     * @param in_tokyo is the new value of in_tokyo.
     */
    public void set_in_tokyo(boolean in_tokyo) {
        this.in_tokyo = in_tokyo;
    }
}
