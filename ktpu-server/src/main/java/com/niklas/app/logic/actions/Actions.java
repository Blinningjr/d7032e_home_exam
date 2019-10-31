package com.niklas.app.logic.actions;


import java.util.ArrayList;

import com.niklas.app.logic.events.Attack;
import com.niklas.app.logic.events.AwardEnergy;
import com.niklas.app.logic.events.AwardStar;
import com.niklas.app.logic.events.Damage;
import com.niklas.app.logic.events.Defend;
import com.niklas.app.logic.events.Heal;
import com.niklas.app.logic.events.Shopping;
import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.online.Player;


/**
 * Actions is a class with all the different actions cards can activate to effect the current game.
 */
public class Actions {

	/**
	 * Creates a action object.
	 */
	public Actions() {
	
	}


	/**
	 * Adds stars, energy and hp to the players monster.
	 * @param gameState is the game state that holds all the information about the current game.
	 * @param player is the player which monster will recive the stars, energy and hp.
	 * @param effect has the num of stars, energy and hp that will be added.
	 */
	public void giveStarsEnergyAndHp(GameState gameState, Player player, Effect effect) {
		int stars = effect.getAddedStars();
		int energy = effect.getAddedEnergy();
		int hp = effect.getAddedHp();
		if (stars > 0) {
			AwardStar awardStar = new AwardStar(gameState, player, stars);
			awardStar.execute();
		}
		if (energy > 0) {
			AwardEnergy awardEnergy = new AwardEnergy(gameState, player, energy);
			awardEnergy.execute();
		}
		if (hp > 0) {
			Heal heal = new Heal(gameState, player, 0);
			heal.addHealing(hp);
			heal.execute();
		}
	}
	

	/**
	 * Deals damage to every monster execpt the damageDealers monster.
	 * @param gameState is the game state that holds all the information about the current game.
	 * @param damageDealer is the player which monster will not be damaged.
	 * @param effect has the num of damage that will be dealt to the other monsters.
	 */
	public void damageEveryoneElse(GameState gameState, Player damageDealer, Effect effect) {
		ArrayList<Player> players = new ArrayList<Player>();
		players.addAll(gameState.getPlayers());
		players.add(gameState.getCurrentPlayer());
		players.remove(damageDealer);

		for (Player player : players) {
			Damage d = new Damage(gameState, player, effect.getAddedDamage());
			d.execute();
		}
	}


	/**
	 * Addes armor to the currently defending monster.
	 * @param defend is the defend event which the defender in will get extra armor.
	 * @param effect has the num of armor that will be added.
	 */
	public void addarmor(Defend defend, Effect effect) {
		int armor = effect.getArmor();
		if (armor > 0) {
			defend.addArmor(effect.getArmor());
		}
	}


	/**
	 * Addes cost for the cards, for the current monster shopping in the shopping event.
	 * @param shopping is the shopping event where the total cost is calculated.
	 * @param effect has the extra cost that will be added to the cards(Can be negative for reduced cost).
	 */
	public void addCost(Shopping shopping, Effect effect) {
		shopping.addCost(effect.getAddedCost());
	}


	/**
	 * Addes damaged to an attack event.
	 * @param attack is the attack event where the damage calculation is done.
	 * @param effect has the extra damage that will be added.
	 */
	public void addDamage(Attack attack, Effect effect) {
		int addedDamage = effect.getAddedDamage();
		if (addedDamage > 0) {
			attack.addBonusDamage(addedDamage);
		}
	}
}
