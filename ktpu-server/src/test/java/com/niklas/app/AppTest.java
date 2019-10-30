package com.niklas.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.niklas.app.controller.KTPUGame;
import com.niklas.app.controller.events.AwardStarIfCurrentPlayerInTokyo;
import com.niklas.app.controller.events.RollDice;
import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.CardStore;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.EvolutionDeck;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreDeck;
import com.niklas.app.model.dice.KTPUDice;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class AppTest {
    private GameState gameState;
    
   
    
    @Before
    public void before() {
        System.out.println("Before Start");
        
        ArrayList<Client> players = new ArrayList<Client>();
        players.add(
            new Client(
                new Monster("test1", 10, 10, 0, 0, false, new ArrayList<StoreCard>(), 
                    new EvolutionDeck(new ArrayList<EvolutionCard>(), new ArrayList<EvolutionCard>())), null, null, null));
        ArrayList<StoreCard> storeCards = new ArrayList<StoreCard>();
        storeCards.add(null);
        storeCards.add(null);
        storeCards.add(null);
        CardStore cardStore = new CardStore(new StoreDeck(storeCards, new ArrayList<StoreCard>()));
        Comunication comunication = new Comunication();
        gameState = new GameState(players, cardStore, comunication);
        
        System.out.println("Before End");
    }
 
   
	
	/**
	 * 7.If your monster is inside of Tokyo –Gain 1 star
	 */
    @Test
    public void testRule7() {
    	Monster currentMonster = gameState.getCurrentPlayer().getMonster();
    	currentMonster.setInTokyo(true);
        int expectedStars = currentMonster.getStars() + 1;
        AwardStarIfCurrentPlayerInTokyo asicoit = new AwardStarIfCurrentPlayerInTokyo(gameState);
        asicoit.giveStarIfInTokyo(gameState.getCurrentPlayer());
        assertEquals(expectedStars, currentMonster.getStars());
    }
    
    /**
     * 8.Roll your 6 dice
     */
    @Test
    public void testRule8() {
    	RollDice rollDice = new RollDice(gameState);
        ArrayList<KTPUDice> dice1 = new ArrayList<KTPUDice>();
        ArrayList<KTPUDice> dice2 = new ArrayList<KTPUDice>();
        rollDice.createDice();
        dice1.addAll(rollDice.getDice());
        rollDice.createDice();
        dice2.addAll(rollDice.getDice());

        for (int i = 0; i < 100; i++) {
            if (dice1.equals(dice2)) {
                dice2.clear();
                rollDice.createDice();
                dice2.addAll(rollDice.getDice());
            }
        }

        assertEquals(6, dice1.size());
        assertEquals(6, dice2.size());
        assertNotEquals(dice1, dice2);
    }

    /**
     * 9. Select which of your 6 dice to reroll
     */
    @Test
    public void testRule9() {
    	
    }

    /**
     * 10. Reroll the selected dice
     */
    @Test
    public void testRule10() {
        int[] reroll0 = {};
        helpTestRule10(reroll0);
        int[] reroll1 = {1};
        helpTestRule10(reroll1);
        int[] reroll2 = {2,3};
        helpTestRule10(reroll2);
        int[] reroll3 = {1,6,4};
        helpTestRule10(reroll3);
        int[] reroll4 = {5,1,2,1};
        helpTestRule10(reroll4);
    }

    private void helpTestRule10(int[] reroll) {
        RollDice rollDice = new RollDice(gameState);
        int[] dice1 = new int[6];
        int[] dice2 = new int[6];

        rollDice.createDice();
        for (int i = 0; i < 6; i++) {
            dice1[i] = rollDice.getDice().get(i).get_value();
        }
        rollDice.rerollDice(reroll);
        for (int i = 0; i < 6; i++) {
            dice2[i] = rollDice.getDice().get(i).get_value();
        }

        for (int i = 0; i < 6; i++) {
            boolean inRerol = false;
            for (int j = 0; j < reroll.length; j++) {
                if (i+1 == reroll[j]) {
                    inRerol = true;
                    j = reroll.length;
                }
            }
            if (inRerol) {
                assertNotEquals(dice1[i], dice2[i]);
            } else {
                assertEquals(dice1[i], dice2[i]);
            }
        }
    }
}
