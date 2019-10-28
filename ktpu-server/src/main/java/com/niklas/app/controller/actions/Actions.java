package com.niklas.app.controller.actions;


import com.niklas.app.model.monsters.Monster;


public class Actions {
	public Actions() {
	
	}

	public void giveStarsAndEnergy(Monster monster, int stars, int energy) {
		monster.set_stars(monster.get_stars() + stars);
		monster.set_entergy(monster.get_energy() + energy);
	}
}