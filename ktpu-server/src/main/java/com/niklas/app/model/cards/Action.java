package com.niklas.app.model.cards;


/**
 * All the different actions that are implemented.
 * Used to see which method to call in Actions when a card is activated.
 */
public enum Action {
    addArmor,
    addCost,
    addDamage,
    damageEveryoneElse,
    giveStarsEnergyAndHp,
}
