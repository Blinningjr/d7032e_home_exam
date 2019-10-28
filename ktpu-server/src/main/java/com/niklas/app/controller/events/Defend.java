package com.niklas.app.controller.events;


import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class Defend implements Event {
    private Comunication comunication;
    private Client attackingClient;
    private Client defendingClient;
    private int damage;
    private int armor;

    public Defend(Comunication comunication, Client attackingClient, Client defendingClient, int damage) {
        this.comunication = comunication;
        this.attackingClient = attackingClient;
        this.defendingClient = defendingClient;
        this.damage = damage;

        armor = 0;
    }

    public void execute() {
        Damage d = new Damage(comunication, defendingClient, damage - armor);
        d.execute();
    }

    public void addArmor(int addedArmor) {
        if (addedArmor > 0) {
            armor += addedArmor;
        } 
    }
}
