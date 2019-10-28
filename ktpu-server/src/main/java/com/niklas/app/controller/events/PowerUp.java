package com.niklas.app.controller.events;


import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.cards.Duration;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.monsters.Monster;


public class PowerUp implements Event {
    private GameState gameState;
    private int numHearts;
    private int heartsNeeded;

    public PowerUp(GameState gameState, int numHearts) {
        this.gameState = gameState;
        this.numHearts = numHearts;
        heartsNeeded = 3;
    }

    public void execute(){
        Monster monster = gameState.getCurrentPlayer().getMonster();
        if (numHearts >= heartsNeeded) {
            EvolutionCard evolutionCard = monster.draw_evolution_card();
            monster.evolutionCards.add(evolutionCard);
            checkNewCard();
        }
    }

    private void checkNewCard() {
        Monster currentMonster = gameState.getCurrentPlayer().getMonster();
        EvolutionCard evolutionCard = currentMonster.evolutionCards.get(currentMonster.storeCards.size() - 1);
        Effect effect = evolutionCard.getEffect();
        if (effect.getActivation() == Activation.Now) {
            switch (effect.getAction()) {
                case giveStarsEnergyAndHp:
                    gameState.action.giveStarsEnergyAndHp(gameState,
                        gameState.getCurrentPlayer(), effect);
                    break;
                case damageEveryoneElse:
                    gameState.action.damageEveryoneElse(gameState, effect);
                    break;
                default:
                    throw new Error("action=" + effect.getAction() 
                        + " is not implemented for event PowerUp");
            }
            if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
                currentMonster.evolutionCards.remove(currentMonster.storeCards.size() - 1);
                currentMonster.discard_evolution_card(evolutionCard);
            }
        }
    }

    public void addHeartsNeeded(int addedHeartsNeeded) {
        heartsNeeded += addedHeartsNeeded;
    }
}
