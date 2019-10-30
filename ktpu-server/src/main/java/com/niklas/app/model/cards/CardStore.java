package com.niklas.app.model.cards;


/**
 * Defines attributes and funtionalety of a CardStore.
 */
public class CardStore {
    private final StoreDeck deck;
    private StoreCard[] inverntory = new StoreCard[3];


    /**
     * Creates a CardStore with deck as the cards it has. 
     * And shuffles the deck.
     * @param deck is the deck that the 
     */
    public CardStore(StoreDeck deck) {
        this.deck = deck;
        deck.shuffle();
        for (int i = 0; i < inverntory.length; i++) {
            inverntory[i] = this.deck.draw_card();
        }
    }
    
    public String inverntoryToString() {
    	return "[1] " + inverntory[0].to_string() + ":" + "[2] " + inverntory[1].to_string() + ":" + "[3] " + inverntory[2].to_string() + ":";
    }


    /**
     * Gets a card from the inventory.
     * @param card_position is the postion of the card that is being bought.
     * @return the card that is on inventory position "card_position".
     */
    public StoreCard buy(int card_position) {
    	StoreCard card = inverntory[card_position];
        inverntory[card_position] = deck.draw_card();
        return card;
    }


    /**
     * Puts a card into the CardStores decks discard pile.
     * @param card is the card that is placed in the CardStores decks discard pile.
     */
    public void discardCard(StoreCard store_card) {
        deck.discard_card(store_card);
    }


    /**
     * Gets the inventory.
     * @return the inventory as a Card[].
     */
    public StoreCard[] getInventory() {
        return inverntory;
    }
}
