package com.niklas.app.model.cards;


/**
 * All the diffrent activations that are implemented.
 * Used in events to see which cards are suppose to be activated when the events are executed.
 */
public enum Activation {
    Attack,
    AwardEnergy,
    AwardStar,
    AwardStarIfCurrentPlayerInTokyo,
    CheckDice,
    CheckForWinByElimination,
    CheckForWinByStars,
    CheckNumOfOnes,
    CheckNumOfThrees,
    CheckNumOfTwos,
    Damage,
    Defend,
    Heal,
    HealingNotInTokyo,
    Now,
    PowerUp,
    RerollDice,
    ResetStore,
    RollDice,
    Shopping,
}
