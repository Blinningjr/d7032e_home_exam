package com.niklas.app.controller.events;

import com.niklas.app.model.monsters.Monster;

public class AwardStarIfInTokyo implements Event {

    private int stars;
    private Monster monster;

    public AwardStarIfInTokyo(Monster monster) {
    	this.monster = monster;
        stars = 1;
    }

    public void execute() {
        if (monster.get_in_tokyo()) {
			monster.set_stars(monster.get_stars() + stars);
		}
    }

    public void add_stars(int stars) {
        this.stars += stars;
    }
}