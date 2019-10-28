package com.niklas.app.controller.actions;


import java.util.ArrayList;

import com.niklas.app.controller.events.Attack;
import com.niklas.app.controller.events.AwardEnergy;
import com.niklas.app.controller.events.AwardStar;
import com.niklas.app.controller.events.Damage;
import com.niklas.app.controller.events.Defend;
import com.niklas.app.controller.events.Heal;
import com.niklas.app.controller.events.Shopping;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class Actions {
	public Actions() {
	
	}

	public void giveStarsEnergyAndHp(Comunication comunication, Client client, Effect effect) {
		int stars = effect.get_added_stars();
		int energy = effect.get_added_energy();
		int hp = effect.get_added_hp();
		if (stars > 0) {
			AwardStar awardStar = new AwardStar(comunication, client, stars);
			awardStar.execute();
		}
		if (energy > 0) {
			AwardEnergy awardEnergy = new AwardEnergy(comunication, client, energy);
			awardEnergy.execute();
		}
		if (hp > 0) {
			Heal heal = new Heal(comunication, client, 0);
			heal.addHealing(hp);
			heal.execute();
		}
	}
	
	public void damageEveryoneElse(Comunication comunication, ArrayList<Client> clients, Effect effect) {
		for (Client client : clients) {
			Damage d = new Damage(comunication, client, effect.get_added_damage());
			d.execute();
		}
	}

	public void addarmor(Defend defend, Effect effect) {
		defend.addArmor(effect.get_armor());
	}

	public void addCost(Shopping shopping, Effect effect) {
		shopping.add_cost(effect.get_added_cost());
	}

	public void addDamage(Attack attack, Effect effect) {
		attack.addBonusDamage(effect.get_added_damage());
	}
}
