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
    
    
    public Activation getActivation() {
    	return activation;
    }

    public Action getAction() {
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