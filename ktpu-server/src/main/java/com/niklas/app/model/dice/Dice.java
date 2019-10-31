package com.niklas.app.model.dice;

/**
 * Interface for defining core methods of a Dice.
 */
interface Dice {
    /**
     * Roll dice.
     */
    public void roll();


    /**
     * Returns value of dice as a string.
     * @return value of dice as a string.
     */
    public String valueAsString();


    /**
     * Get value.
     * @return value of dice.
     */
    public int getValue();


    /**
     * Set the value of dice
     */
    public void setValue(int number) throws Exception;
}
