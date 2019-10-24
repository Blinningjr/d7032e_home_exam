package server.model.cards;

/**
 * Defines core attributes and funtionalety of a card.
 */
public abstract class Card {
    private final String name;
    private final String description;
    private final Efffect efffect;


    /**
     * Creates a card with the same attributes as the parametes.
     * @param name is the name of the card.
     * @param description is the description of the card.
     * @param efffect is the effect the card has on the game.
     */
    public Card(String name, String description, Efffect efffect) {
        this.name = name;
        this.description = description;
        this.efffect = efffect;
    }


    /**
     * Gets the name of the card.
     * @return the name of the card as a String.
     */
    public String get_name(){
        return name;
    }


    /**
     * Gets the description of the card.
     * @return the descropton of the card as a String.
     */
    public String get_description() {
        return description;
    }


    /**
     * Gets the effect of the card.
     * @return the effect of the card as a Effect object.
     */
    public Efffect get_efffect() {
        return efffect;
    }
}