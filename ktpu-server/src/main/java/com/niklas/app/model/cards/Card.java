package com.niklas.app.model.cards;


/**
 * Stores all the infromation about a card.
 */
public abstract class Card {
    private final String name;
    private final String description;
    private final Effect effect;


    /**
     * Creates a card.
     * @param name is the name of the card.
     * @param description is the description of the card.
     * @param effect is the effect the card has on the game.
     */
    public Card(String name, String description, Effect effect) {
        this.name = name;
        this.description = description;
        this.effect = effect;
    }


    /**
     * Gets the card as a String.
     * @return the card as a String.
     */
    @Override
    public String toString() {
        return "[" + name  + ", Description= " + description + "]";
    }


    /**
     * Gets the name of the card.
     * @return the name of the card as a String.
     */
    public String getName(){
        return name;
    }


    /**
     * Gets the description of the card.
     * @return the descropton of the card as a String.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Gets the effect of the card.
     * @return the effect of the card as a Effect object.
     */
    public Effect getEffect() {
        return effect;
    }
}
