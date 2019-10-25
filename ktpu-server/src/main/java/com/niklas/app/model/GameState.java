package com.niklas.app.model;

import com.niklas.app.model.cards.CardStore;
import com.niklas.app.model.monsters.Monster;

/**
 * Defines attributes and funtiunalety of the GameState.
 * GameState holdes all the information about the game.
 */
public class GameState {
    private Monster[] monsters;
    private int current_player;
    private int next_player;
    private boolean clockwise;
    private CardStore card_store;


    /**
     * Creates a GameState.
     * @param monsters is the list of monsters playing this match.
     * @param current_player is a pointer to the current monsters turn.
     * @param clockwise defines which dirction the plyers turn are.
     * @param card_store is the card store for this match.
     */
    public GameState(Monster[] monsters, int current_player, boolean clockwise, CardStore card_store) {
        this.monsters = monsters;
        this.current_player = current_player;
        this.next_player = -1;
        this.clockwise = clockwise;
        this.card_store = card_store;
    }


    /**
     * Gets the current plyers monster. 
     * @return the curent players monster as a Monster object.
     */
    public Monster current_Monster() {
        return monsters[current_player];
    }


    /**
     * Switches the dirrection the plyers turns are determaind.
     */
    public void switch_direction() {
        clockwise = !clockwise;
    }


    /**
     * Gets the next player pointer.
     * @return a pointer to the next turns player as a int.
     */
    public int get_next_player() {
        return next_player;
    }

    /**
     * Sets the pointer to the next players turn.
     * @param next_player is a pointer to the next players monster in the monster array.
     */
    public void set_next_player(int next_player){
        this.next_player = next_player;
    }
}
