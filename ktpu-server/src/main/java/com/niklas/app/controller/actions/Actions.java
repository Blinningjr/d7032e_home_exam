package com.niklas.app.controller.actions;


import com.niklas.app.controller.events.Attack;
import com.niklas.app.controller.events.AwardEnergy;
import com.niklas.app.controller.events.AwardStar;
import com.niklas.app.controller.events.Damage;
import com.niklas.app.controller.events.Defend;
import com.niklas.app.controller.events.Heal;
import com.niklas.app.controller.events.Shopping;
import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.online.Client;


public class Actions {
	public Actions() {
	
	}

	public void giveStarsEnergyAndHp(GameState gameState, Client client, Effect effect) {
		int stars = effect.get_added_stars();
		int energy = effect.get_added_energy();
		int hp = effect.get_added_hp();
		if (stars > 0) {
			AwardStar awardStar = new AwardStar(gameState, client, stars);
			awardStar.execute();
		}
		if (energy > 0) {
			AwardEnergy awardEnergy = new AwardEnergy(gameState, client, energy);
			awardEnergy.execute();
		}
		if (hp > 0) {
			Heal heal = new Heal(gameState, client, 0);
			heal.addHealing(hp);
			heal.execute();
		}
	}
	
	public void damageEveryoneElse(GameState gameState, Effect effect) {
		for (Client client : gameState.getPlayers()) {
			Damage d = new Damage(gameState, client, effect.get_added_damage());
			d.execute();
		}
	}

	public void addarmor(Defend defend, Effect effect) {
		defend.addArmor(effect.get_armor());
	}

	public void addCost(Shopping shopping, Effect effect) {
		shopping.addCost(effect.get_added_cost());
	}

	public void addDamage(Attack attack, Effect effect) {
		attack.addBonusDamage(effect.get_added_damage());
	}
}
