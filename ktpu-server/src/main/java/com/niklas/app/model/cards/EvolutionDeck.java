package com.niklas.app.model.cards;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Defines attributes and funtionalety of a evolution deck.
 */
public class EvolutionDeck {
    private ArrayList<EvolutionCard> deck;
    private ArrayList<EvolutionCard> discardPile;


    /**
     * Creates a deck of cards and a discard pile.
     * @param deck is a arraylist of all the cards that are going to be in the deck at the start.
     * @param discardPile is a arraylist of all the cards that are going to be in the discard pile at the start.
     */
    public EvolutionDeck(ArrayList<EvolutionCard> deck, ArrayList<EvolutionCard> discardPile) {
        this.deck = deck;
        this.discardPile = discardPile;
    }


    /**
     * Draws the first card in the deck,
     * and if the deck is empty the discard is put in to the deck and shuffled.
     * @return the first card of in the deck.
     */
    public EvolutionCard drawCard() {
        if (deck.size() == 0) {
            deck.addAll(discardPile);
            shuffle();
            discardPile.clear();
        }

        EvolutionCard card = deck.get(0);
        deck.remove(0);

        return card;
    }


    /**
     * Added a card to the discard pile.
     * @param card is the card that will be added to the discard pile.
     */
    public void discardCard(EvolutionCard evolutionCard) {
        discardPile.add(evolutionCard);
    }


    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(deck);
    }
}
