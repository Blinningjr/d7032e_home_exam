package com.niklas.app.controller.events;


import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class Damage implements Event {
    private Comunication comunication;
    private Client client;
    private int damage;

    public Damage(Comunication comunication, Client client, int damage) {
        this.comunication = comunication;
        this.client = client;
        this.damage = damage;
    }

    public void execute() {
        if (damage > 0) {
    		client.get_monster().set_hp(client.get_monster().get_hp() - damage);
    	}
    }
}
