package com.niklas.app.controller.events;

import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;

public class AwardStarIfInTokyo implements Event {
    private Comunication comunication;
    private Client client;
    private int stars;

    public AwardStarIfInTokyo(Comunication comunication, Client client) {
    	this.comunication = comunication;
        this.client = client;
        stars = 1;
    }

    public void execute() {
        if (client.get_monster().get_in_tokyo()) {
            AwardStar awardStar = new AwardStar(comunication, client, stars);
            awardStar.execute();
		}
    }

    public void add_stars(int stars) {
        this.stars += stars;
    }
}
