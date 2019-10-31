package com.niklas.app.model.cards;


/**
 * Stores all information about a card store..
 */
public class CardStore {
    private final StoreDeck deck;
    private StoreCard[] inverntory = new StoreCard[3];


    /**
     * Creates a CardStore with deck as the cards it has. 
     * And shuffles the deck.
     * @param deck is the deck that the card store will sell cards from.
     */
    public CardStore(StoreDeck deck) {
        this.deck = deck;
        deck.shuffle();
        for (int i = 0; i < inverntory.length; i++) {
            inverntory[i] = this.deck.drawCard();
        }
    }
    

    /**
     * Gets the inventory as a string.
     * @return all the cards in the inventory as a String.
     */
    public String inverntoryToString() {
    	return "[1] " + inverntory[0].toString() + ":" + "[2] " + inverntory[1].toString() + ":" + "[3] " + inverntory[2].toString() + ":";
    }


    /**
     * Gets a card from the inventory.
     * @param cardPosition is the postion of the card that is being bought.
     * @return the card that is on inventory position "cardPosition".
     */
    public StoreCard buy(int cardPosition) {
    	StoreCard card = inverntory[cardPosition];
        inverntory[cardPosition] = deck.drawCard();
        return card;
    }


    /**
     * Puts a card into the CardStores decks discard pile.
     * @param storeCard is the card that is placed in the CardStores decks discard pile.
     */
    public void discardCard(StoreCard storeCard) {
        deck.discardCard(storeCard);
    }


    /**
     * Gets the inventory.
     * @return the inventory as a Card[].
     */
    public StoreCard[] getInventory() {
        return inverntory;
    }
}
