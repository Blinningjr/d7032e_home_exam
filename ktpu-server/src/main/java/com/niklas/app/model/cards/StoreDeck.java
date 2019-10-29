package com.niklas.app.model.cards;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Defines attributes and funtionalety of a Deck.
 */
public class StoreDeck {
    private ArrayList<StoreCard> deck;
    private ArrayList<StoreCard> discard_pile;


    /**
     * Creates a deck of cards and a discard pile.
     * @param deck is a arraylist of all the cards that are going to be in the deck at the start.
     * @param discard_pile is a arraylist of all the cards that are going to be in the discard pile at the start.
     */
    public StoreDeck(ArrayList<StoreCard> deck, ArrayList<StoreCard> discard_pile) {
        this.deck = deck;
        this.discard_pile = discard_pile;
    }


    /**
     * Draws the first card in the deck,
     * and if the deck is empty the discard is put in to the deck and shuffled.
     * @return the first card of in the deck.
     */
    public StoreCard draw_card() {
        if (deck.size() == 0) {
            deck.addAll(discard_pile);
            shuffle();
            discard_pile.clear();
        }

        StoreCard card = deck.remove(0);

        return card;
    }


    /**
     * Added a card to the discard pile.
     * @param card is the card that will be added to the discard pile.
     */
    public void discard_card(StoreCard store_card) {
        discard_pile.add(store_card);
    }


    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(deck);
    }
}
