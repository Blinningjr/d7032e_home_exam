package com.niklas.app.model.cards;

/**
 * Defines attributes and funtionalety of StoreCard.
 */
public class StoreCard extends Card {
    private final int cost;
    private final StoreCardType type;


    /**
     * Creates a StoreCard.
     * @param name is the name of the card.
     * @param description is the description of the card.
     * @param effect is the effect the card has on the game.
     * @param cost is the energy cost of the StoreCard.
     * @param type is the type of StoreCard.
     */
    public StoreCard(String name, String description, Effect effect, int cost, String type) {
        super(name, description, effect);

        this.cost = cost;

        switch (type) {
		case "keep":
			this.type = StoreCardType.keep;
			break;
        case "discard":
			this.type = StoreCardType.discard;
			break;
		default:
            throw new Error("StoreCardType= "+ type + " is not implemented");
		}
    }


    public String to_string() {
        return "[" + super.getName() + ", Cost= " + cost + ", Type= " + type + ", Description= " + super.get_description() + "]";
    }


    /**
     * Gets the energy cost of the StoreCard.
     * @return the cost of the StoreCard as a int.
     */
    public int getCost() {
        return cost;
    }


    /**
     * Gets the type of StoreCard.
     * @return the type of StoreCard.
     */
    public StoreCardType getType() {
        return type;
    }
}
