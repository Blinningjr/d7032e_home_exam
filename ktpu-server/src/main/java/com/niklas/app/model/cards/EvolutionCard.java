package com.niklas.app.model.cards;


/**
 * Stores all the information about the evolution card.
 */
public class EvolutionCard extends Card {
    private final String monsterName;
    private final String monsterType;
    private final Duration duration;


    /**
     * Creates a EvolutionCard.
     * @param name is the name of the card.
     * @param description is the description of the card.
     * @param effect is the effect the card has on the game.
     * @param monsterName is the name of the monster that the card is for.
     * @param monsterType is the monsters type.
     * @param duration is what kind of evolution it is.
     */
    public EvolutionCard(String name, String description, Effect effect, String monsterName, String monsterType,
                        String duration) {
        super(name, description, effect);
        
        this.monsterName = monsterName;
        this.monsterType = monsterType;
        switch (duration) {
            case "permanentEvolution":
                this.duration = Duration.permanentEvolution;
                break;
            case "temporaryEvolution":
                this.duration = Duration.temporaryEvolution;
                break;
            default:
                throw new Error("StoreCardType= "+ duration + " is not implemented");
            }
    }


    /**
     * Gets the evolution card as a string.
     * @return the evolution card as a string.
     */
    @Override
    public String toString() {
        return "[" + super.getName() + ", Monster type= " + monsterType + ", Duration= " + duration + ", Description= " + getDescription() + "]";
    }


    /**
     * Gets the name of the monster that the card is for.
     * @return the name of the monster that the card is for as a string.
     */
    public String getMonsterName() {
        return monsterName;
    }


    /**
     * Gets the type of the monster that the card is for.
     * @return the type of the monster that the card is for as a String
     */
    public String getMonsterType() {
        return monsterType;
    }


    /**
     * Gets the duration of the EvolutionCard.
     * @return the duration of the EvolutionCard as a String.
     */
    public Duration getDuration() {
        return duration;
    }
}
