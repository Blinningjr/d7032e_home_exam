package com.niklas.app.model.dice;

import java.util.Random;

/**
 * A normal dice with six sides.
 */
public class DiceSix implements Dice {
    protected int value;
    protected static final Random ran = new Random();;

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
    public String valueAsString() {
        return String.valueOf(value);
    }


    /**
     * Get value.
     * @return value of dice.
     */
    public int getValue() {
        return value;
    }


    /**
     * Set the value of dice
     */
    public void setValue(int value) throws Exception {
        if (value < 1 || value > 6) {
            throw new Exception();
        } else {
            this.value = value;
        }
    }
}
