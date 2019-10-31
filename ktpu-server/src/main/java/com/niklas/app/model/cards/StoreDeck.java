package com.niklas.app.model.cards;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Defines attributes and funtionalety of a StoreDeck.
 */
public class StoreDeck {
    private ArrayList<StoreCard> deck;
    private ArrayList<StoreCard> discardPile;


    /**
     * Creates a deck of store cards and a discard pile.
     * @param deck is a arraylist of all the cards that are going to be in the deck at the start.
     * @param discardPile is a arraylist of all the cards that are going to be in the discard pile at the start.
     */
    public StoreDeck(ArrayList<StoreCard> deck, ArrayList<StoreCard> discardPile) {
        this.deck = deck;
        this.discardPile = discardPile;
    }


    /**
     * Draws the first card in the deck,
     * and if the deck is empty the discard is put in to the deck and shuffled.
     * @return the first card of in the deck.
     */
    public StoreCard drawCard() {
        if (deck.size() == 0) {
            deck.addAll(discardPile);
            shuffle();
            discardPile.clear();
        }

        StoreCard card = deck.remove(0);

        return card;
    }


    /**
     * Added a card to the discard pile.
     * @param card is the card that will be added to the discard pile.
     */
    public void discardCard(StoreCard storeCard) {
        discardPile.add(storeCard);
    }


    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(deck);
    }
}
