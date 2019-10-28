package com.niklas.app.model.cards;


public class Effect {
    
  private Activation activation;
  private Action action;
	
	private int added_damage = 0;
	private int armor = 0;
	private int added_cost = 0;
	private int added_stars = 0;
	private int added_max_hp = 0;
	private int added_hp = 0;
  private int added_energy = 0;
  // private int extra_dice = 0;
  
    public Effect(String activation, String action) {
      switch (activation) {
        case "inTokyo":
          this.activation = Activation.inTokyo;
          break;
        case "rollDice":
          this.activation = Activation.rollDice;
          break;
        case "checkForWinByStars":
          this.activation = Activation.checkForWinByStars;
          break;
        case "checkForWinByElimination":  
          this.activation = Activation.checkForWinByElimination;
          break;
        case "shopping":  
          this.activation = Activation.shopping;
          break;
        
        case "attack":
          this.activation = Activation.attack;
          break;
        case "defend":
          this.activation = Activation.defend;
          break;
        case "now":
          this.activation = Activation.now;
          break;
        case "rolled":
          this.activation = Activation.rolled;
          break;
        case "buying":
          this.activation = Activation.rolled;
          break;
        default:
          throw new Error("activation= "+ activation + " is not implemented");
      }

      switch (action) {
        case "giveStarsAndEnergy":
          this.action = Action.giveStarsAndEnergy;
          break;
        case "stat":
          this.action = Action.stat;
          break;
        default:
        throw new Error("action= "+ action + " is not implemented");
      }
    }
    
    public void add_damage(int damage) {
		  added_damage += damage;
    }
    
    public void add_armor(int armor) {
		  this.armor += armor;
    }
    
    public void add_cost(int cost) {
    	added_cost += cost;
    }
    
    public void add_stars(int stars) {
		this.added_stars += stars;
    }
    
    public void add_to_max_hp(int max_hp) {
		  added_max_hp += max_hp;
    }
    
    public void add_hp(int hp) {
		  added_hp += hp;
    }
    
    public void add_energy(int energy) {
    	added_energy += energy;
    }
    
    
    public Activation get_activation() {
    	return activation;
    }

    public Action get_action() {
    	return action;
    }
    
    public int get_added_damage() {
    	return added_damage;
    }
    
    public int get_armor() {
    	return armor;
    }
    
    public int get_added_cost() {
    	return added_cost;
    }
    
    public int get_added_stars() {
    	return added_stars;
    }
    
    public int get_added_max_hp() {
    	return added_max_hp;
    }
    
    public int get_added_hp() {
    	return added_hp;
    }
    
    public int get_added_energy() {
    	return added_energy;
    }
}