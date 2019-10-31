package com.niklas.app.model.cards;


/**
 * Stores information about the cards effect.
 */
public class Effect {
    private Activation activation;
    private Action action;
    private int addedDamage = 0;
    private int armor = 0;
    private int addedCost = 0;
    private int addedStars = 0;
    private int addedMaxHp = 0;
    private int addedHp = 0;
    private int addedEnergy = 0;
  

    /**
     * Creates a effect object.
     * @param activation is when the effect should be activated.
     * @param action is what action the effect will use.
     */
    public Effect(String activation, String action) {
      switch (activation) {
        case "Attack":
          this.activation = Activation.Attack;
          break;
        case "AwardEnergy":
          this.activation = Activation.AwardEnergy;
          break;
        case "AwardStar":
          this.activation = Activation.AwardStar;
          break;
        case "AwardStarIfCurrentPlayerInTokyo":
          this.activation = Activation.AwardStarIfCurrentPlayerInTokyo;
          break;
        case "CheckDice":
          this.activation = Activation.CheckDice;
          break;
        case "CheckForWinByElimination":
          this.activation = Activation.CheckForWinByElimination;
          break;
        case "CheckForWinByStars":
          this.activation = Activation.CheckForWinByStars;
          break;
        case "CheckNumOfOnes":
          this.activation = Activation.CheckNumOfOnes;
          break;
        case "CheckNumOfThrees":
          this.activation = Activation.CheckNumOfThrees;
          break;
        case "CheckNumOfTwos":
          this.activation = Activation.CheckNumOfTwos;
          break;
        case "Damage":
          this.activation = Activation.Damage;
          break;
        case "Defend":
          this.activation = Activation.Defend;
          break;
        case "Heal":
          this.activation = Activation.Heal;
          break;
        case "HealingNotInTokyo":
          this.activation = Activation.HealingNotInTokyo;
          break;
        case "Now":
          this.activation = Activation.Now;
          break;
        case "PowerUp":
          this.activation = Activation.PowerUp;
          break;
        case "RerollDice":
          this.activation = Activation.RerollDice;
          break;
        case "ResetStore":
          this.activation = Activation.ResetStore;
          break;
        case "RollDice":
          this.activation = Activation.RollDice;
          break;
        case "Shopping":
          this.activation = Activation.Shopping;
          break;

        default:
          throw new Error("activation= "+ activation + " is not implemented");
      }

      switch (action) {
        case "giveStarsEnergyAndHp":
          this.action = Action.giveStarsEnergyAndHp;
          break;
        case "damageEveryoneElse":
            this.action = Action.damageEveryoneElse;
            break;
        case "addArmor":
          this.action = Action.addArmor;
          break;
        case "addCost":
          this.action = Action.addCost;
          break;
        case "addDamage":
          this.action = Action.addDamage;
          break;
        default:
        throw new Error("action= "+ action + " is not implemented");
      }
    }
    

    /**
     * Adds to the damage that the effect will inflict.
     * @param damage the damage added.
     */
    public void addDamage(int damage) {
		  addedDamage += damage;
    }
    

    /**
     * Adds armor too the effects action 
     * @param armor the armor added.
     */
    public void addArmor(int armor) {
		  this.armor += armor;
    }
    

    /**
     * Adds cost to the effects action.
     * @param cost the added cost.
     */
    public void addCost(int cost) {
    	addedCost += cost;
    }
    

    /**
     * Adds stars too the effects action.
     * @param stars the added stars
     */
    public void addStars(int stars) {
		this.addedStars += stars;
    }
    

    /**
     * Adds max hp too the effects action.
     * @param maxHp the added max hp.
     */
    public void addToMaxHp(int maxHp) {
		  addedMaxHp += maxHp;
    }
    

    /**
     * Adds hp too the effects action.
     * @param hp the added hp.
     */
    public void addHp(int hp) {
		  addedHp += hp;
    }
    

    /**
     * Adds energy too the effects action.
     * @param energy
     */
    public void addEnergy(int energy) {
    	addedEnergy += energy;
    }
    
    
    /**
     * Gets the effects activation.
     * @return the effects activation.
     */
    public Activation getActivation() {
    	return activation;
    }


    /**
     * Gets the effects action.
     * @return the effects action.
     */
    public Action getAction() {
    	return action;
    }
    

    /**
     * Gets the effects added damage.
     * @return the effects added damage.
     */
    public int getAddedDamage() {
    	return addedDamage;
    }
    

    /**
     * Gets the effects armor.
     * @return the effects armor.
     */
    public int getArmor() {
    	return armor;
    }
    

    /**
     * Gets the effects added Cost.
     * @return the effects cots.
     */
    public int getAddedCost() {
    	return addedCost;
    }
    

    /**
     * Gets the effects added stars
     * @return the effects added stars.
     */
    public int getAddedStars() {
    	return addedStars;
    }
    

    /**
     * Gets the efffects addedMaxHp.
     * @return the effects addedMaxHp.
     */
    public int getAddedMaxHp() {
    	return addedMaxHp;
    }
    

    /**
     * Gets the effects added hp.
     * @return the effects added hp.
     */
    public int getAddedHp() {
    	return addedHp;
    }
    

    /**
     * Gets the effects added energy.
     * @return the effects added energy.
     */
    public int getAddedEnergy() {
    	return addedEnergy;
    }
}
