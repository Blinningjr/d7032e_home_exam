package com.niklas.app.model.dice;


/**
 * A King Tokyo PowerUp dice.
 */
public final class KTPUDice extends DiceSix {
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int HEART = 4;
    public static final int ENERGY = 5;
    public static final int CLAWS = 6;
    

    /**
     * Creates a KTPUDice and rolls it.
     */
    public KTPUDice() {
    	super();
    }

    /**
     * Returns value the dice as a string.
     * @return value the dice as a string.
     */
    @Override
    public String valueAsString() {
        return (value==ONE?"ONE":value==TWO?"TWO":value==THREE?"THREE":value==HEART?"HEART":value==ENERGY?"ENERGY":"CLAWS");
    }
}
