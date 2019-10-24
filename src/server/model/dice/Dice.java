/**
 * Interface for defining core methods of a Dice
 */
interface Dice {
    /**
     * Roll dice.
     */
    public void roll();


    /**
     * Get value of dice as a string.
     * @return value of dice as a string.
     */
    public String value_as_string();


    /**
     * Get value.
     * @return value of dice.
     */
    public int get_value();

    
    /**
     * Set the value of dice
     */
    public void set_value(int number);
}