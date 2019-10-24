package server.model.dice;

import java.util.Random;

/**
 * A normal dice with six sides.
 */
public class DiceSix implements Dice {
    private int value;
    private static final Random ran = new Random();;

    public DiceSix() {
        roll();
    }


    /**
     * Roll dice.
     */
    public void roll() {
        value = ran.nextInt(6) + 1;
    }


    /**
     * Returns value of dice as a string.
     * @return value of dice as a string.
     */
    public String value_as_string() {
        return String.valueOf(value);
    }


    /**
     * Get value.
     * @return value of dice.
     */
    public int get_value() {
        return value;
    }


    /**
     * Set the value of dice
     */
    public void set_value(int value) throws Exception {
        if (number < 1 || number > 6) {
            throw new Exception();
        } else {
            this.value = value;
        }
    }
}
