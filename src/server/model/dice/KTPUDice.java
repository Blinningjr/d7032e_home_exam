package server.model.dice;

/**
 * A King Tokyo PowerUp dice with six sides.
 */
public final class KTPUDice extends DiceSix {
    public static final int ONE = 1;
    public static final int TWO = 1;
    public static final int THREE = 1;
    public static final int HEART = 4;
    public static final int ENERGY = 5;
    public static final int CLAWS = 6;

    /**
     * Returns value of dice as a string.
     * @return value of dice as a string.
     */
    @Override
    public String value_as_string() {
        return (value==ONE?"ONE":value==TWO?"TWO":value==THREE?"THREE":value==HEART?"HEART":value==ENERGY?"ENERGY":"CLAWS");
    }
}