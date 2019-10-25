package com.niklas.app.model.cards;

/**
 * Defines attributes and funtionalety of EvolutionCard.
 */
public class EvolutionCard extends Card {
    private final String monster_name;
    private final String monster_type;
    private final String duration;


    /**
     * Creates a EvolutionCard.
     * @param name is the name of the card.
     * @param description is the description of the card.
     * @param effect is the effect the card has on the game.
     * @param monster_name is the name of the monster that the card is for.
     * @param monster_type is the monsters type.
     * @param duration is what kind of evolution it is.
     */
    public EvolutionCard(String name, String description, Effect effect, String monster_name, String monster_type,
                        String duration) {
        super(name, description, effect);
        
        this.monster_name = monster_name;
        this.monster_type = monster_type;
        this.duration = duration;
    }


    /**
     * Gets the name of the monster that the card is for.
     * @return the name of the monster that the card is for as a string.
     */
    public String get_monster_name() {
        return monster_name;
    }


    /**
     * Gets the type of the monster that the card is for.
     * @return the type of the monster that the card is for as a String
     */
    public String get_monster_type() {
        return monster_type;
    }


    /**
     * Gets the duration of the EvolutionCard.
     * @return the duration of the EvolutionCard as a String.
     */
    public String get_duration() {
        return duration;
    }
}
