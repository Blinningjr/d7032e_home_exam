package com.niklas.app.model.cards;

/**
 * Defines attributes and funtionalety of a CardStore.
 */
public class CardStore {
    private final Deck deck;
    private Card[] inverntory = new Card[3];


    /**
     * Creates a CardStore with deck as the cards it has. 
     * @param deck is the deck that the 
     */
    public CardStore(Deck deck) {
        this.deck = deck;
        deck.shuffle();
        for (int i = 0; i < inverntory.length; i++) {
            inverntory[i] = this.deck.draw_card();
        }
    }


    /**
     * Gets a card from the inventory.
     * @param card_position is the postion of the card that is being bought.
     * @return the card that is on inventory position "card_position".
     * @throws Exception if the card_position is not in the arrey
     */
    public Card buy(int card_position) throws Exception {
        if (card_position < 0 || card_position > 2) {
            throw new Exception();
        } else {
            Card card = inverntory[card_position];
            inverntory[card_position] = deck.draw_card();
            return card;
        }
    }


    /**
     * Puts a card into the CardStores decks discard pile.
     * @param card is the card that is placed in the CardStores decks discard pile.
     */
    public void discard_card(Card card) {
        deck.discard_card(card);
    }


    /**
     * Gets the inventory.
     * @return the inventory as a Card[].
     */
    public Card[] get_inventory() {
        return inverntory;
    }
}
