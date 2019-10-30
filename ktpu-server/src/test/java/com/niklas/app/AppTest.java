package com.niklas.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.niklas.app.controller.KTPUGame;
import com.niklas.app.controller.events.AwardEnergy;
import com.niklas.app.controller.events.AwardStarIfCurrentPlayerInTokyo;
import com.niklas.app.controller.events.CheckNumOfOnes;
import com.niklas.app.controller.events.CheckNumOfThrees;
import com.niklas.app.controller.events.CheckNumOfTwos;
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
    Random rnd = new Random();
    
    TestClient testClient1 = new TestClient();
    TestClient testClient2 = new TestClient();
    TestClient testClient3 = new TestClient();
    TestServer ts = new TestServer(3, 
        		"./src/main/java/com/niklas/app/model/json/Monster.json",
                "./src/main/java/com/niklas/app/model/json/StoreDeck.json");
    
    @Before
    public void before() {
        System.out.println("\n\n\nBefore Start");
    	ts.start();
        System.out.println("1");
    	try {
    	    Thread.sleep(500);
    	}
    	catch(InterruptedException ex) {
    	    Thread.currentThread().interrupt();
        }
        System.out.println("2");
        testClient1.start();
        testClient2.start();
        testClient3.start();
        try {
			ts.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("5");
        gameState = ts.getKTPUGame().getGameState();
        System.out.println("Before End");
     }

     @After
     public void after() {
        System.out.println("after Start");
        testClient1.setFlag();
        testClient2.setFlag();
        testClient3.setFlag();
        gameState.getComunication().sendStarsWinner(gameState.getCurrentPlayer(), gameState.getPlayers());
        try {
            testClient1.join();
            testClient2.join();
            testClient3.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        gameState.getComunication().closeSocet();
        System.out.println("after End");
     }
     
	/**
	 * 7.If your monster is inside of Tokyo –Gain 1 star
	 */
    @Test
    public void testGetStarInTokyo() {
    	Monster currentMonster = gameState.getCurrentPlayer().getMonster();
    	currentMonster.setInTokyo(true);
        int expectedStars = currentMonster.getStars() + 1;
        AwardStarIfCurrentPlayerInTokyo asicoit = new AwardStarIfCurrentPlayerInTokyo(gameState);
        asicoit.execute();
        
        assertEquals(expectedStars, currentMonster.getStars());
    }

    /**
	 * 7.If your monster is inside of Tokyo –Gain 1 star
	 */
    @Test
    public void testNoStarInTokyo() {
    	Monster currentMonster = gameState.getCurrentPlayer().getMonster();
    	currentMonster.setInTokyo(false);
        int expectedStars = currentMonster.getStars();
        AwardStarIfCurrentPlayerInTokyo asicoit = new AwardStarIfCurrentPlayerInTokyo(gameState);
        asicoit.execute();
        assertEquals(expectedStars, currentMonster.getStars());
    }
    
    // /**
    //  * 8.Roll your 6 dice
    //  * 9. Select which of your 6 dice to reroll
    //  * 10. Reroll the selected dice
    //  * 11.Repeat step 9 and 10once
    //  */
    // @Test
    // public void testRule8() {
    // 	RollDice rollDice = new RollDice(gameState);
    //     ArrayList<KTPUDice> dice1 = new ArrayList<KTPUDice>();
    //     ArrayList<KTPUDice> dice2 = new ArrayList<KTPUDice>();
    //     rollDice.execute();
    //     dice1.addAll(rollDice.getDice());
    //     rollDice.createDice();
    //     dice2.addAll(rollDice.getDice());

    //     for (int i = 0; i < 100; i++) {
    //         if (dice1.equals(dice2)) {
    //             dice2.clear();
    //             rollDice.createDice();
    //             dice2.addAll(rollDice.getDice());
    //         }
    //     }

    //     assertEquals(6, dice1.size());
    //     assertEquals(6, dice2.size());
    //     assertNotEquals(dice1, dice2);
    // }


    // private void helpTestRule10(int[] reroll) {
    //     RollDice rollDice = new RollDice(gameState);
    //     int[] dice1 = new int[6];
    //     int[] dice2 = new int[6];

    //     rollDice.createDice();
    //     for (int i = 0; i < 6; i++) {
    //         dice1[i] = rollDice.getDice().get(i).get_value();
    //     }
    //     rollDice.rerollDice(reroll);
    //     for (int i = 0; i < 6; i++) {
    //         dice2[i] = rollDice.getDice().get(i).get_value();
    //     }

    //     for (int i = 0; i < 6; i++) {
    //         boolean inRerol = false;
    //         for (int j = 0; j < reroll.length; j++) {
    //             if (i+1 == reroll[j]) {
    //                 inRerol = true;
    //                 j = reroll.length;
    //             }
    //         }
    //         if (inRerol) {
    //             assertNotEquals(dice1[i], dice2[i]);
    //         } else {
    //             assertEquals(dice1[i], dice2[i]);
    //         }
    //     }
    // }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Tripple 1’s = 1 StarEach additional 1 equals +1 star
     */
    @Test
    public void testTrippleOnesLess() {
        int expectedStars = gameState.getCurrentPlayer().getMonster().getStars();
        CheckNumOfOnes cnoo = new CheckNumOfOnes(gameState, 2);
        cnoo.execute();

        assertEquals(expectedStars, gameState.getCurrentPlayer().getMonster().getStars());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Tripple 1’s = 1 StarEach additional 1 equals +1 star
     */
    @Test
    public void testTrippleOnesEqual() {
        int expectedStars = gameState.getCurrentPlayer().getMonster().getStars() + 1;
        CheckNumOfOnes cnoo = new CheckNumOfOnes(gameState, 3);
        cnoo.execute();

        assertEquals(expectedStars, gameState.getCurrentPlayer().getMonster().getStars());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *        Tripple 1’s = 1 Star. Each additional 1 equals +1 star
     */
    @Test
    public void testTrippleOnesLarg() {
        int expectedStars = gameState.getCurrentPlayer().getMonster().getStars() + 2;
        CheckNumOfOnes cnoo = new CheckNumOfOnes(gameState, 4);
        cnoo.execute();

        assertEquals(expectedStars, gameState.getCurrentPlayer().getMonster().getStars());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Tripple 2’s = 2 StarsEach additional 2 equals +1 star
     */
    @Test
    public void testTrippleTwosLess() {
        int expectedStars = gameState.getCurrentPlayer().getMonster().getStars();
        CheckNumOfTwos cnot = new CheckNumOfTwos(gameState, 2);
        cnot.execute();

        assertEquals(expectedStars, gameState.getCurrentPlayer().getMonster().getStars());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Tripple 2’s = 2 Stars. Each additional 2 equals +1 star
     */
    @Test
    public void testTrippleTwosEqual() {
        int expectedStars = gameState.getCurrentPlayer().getMonster().getStars() + 2;
        CheckNumOfTwos cnot = new CheckNumOfTwos(gameState, 3);
        cnot.execute();

        assertEquals(expectedStars, gameState.getCurrentPlayer().getMonster().getStars());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Tripple 2’s = 2 Stars. Each additional 2 equals +1 star
     */
    @Test
    public void testTrippleTwosLarg() {
        int expectedStars = gameState.getCurrentPlayer().getMonster().getStars() + 3;
        CheckNumOfTwos cnot = new CheckNumOfTwos(gameState, 4);
        cnot.execute();

        assertEquals(expectedStars, gameState.getCurrentPlayer().getMonster().getStars());
    }

     /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Tripple 3’s = 3 Stars. Each additional 3 equals +1 star
     */
    @Test
    public void testTrippleThreesLess() {
        int expectedStars = gameState.getCurrentPlayer().getMonster().getStars();
        CheckNumOfThrees cnot = new CheckNumOfThrees(gameState, 2);
        cnot.execute();

        assertEquals(expectedStars, gameState.getCurrentPlayer().getMonster().getStars());
    }

     /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Tripple 3’s = 3 Stars. Each additional 3 equals +1 star
     */
    @Test
    public void testTrippleThreesEqual() {
        int expectedStars = gameState.getCurrentPlayer().getMonster().getStars() + 3;
        CheckNumOfThrees cnot = new CheckNumOfThrees(gameState, 3);
        cnot.execute();

        assertEquals(expectedStars, gameState.getCurrentPlayer().getMonster().getStars());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Tripple 3’s = 3 Stars. Each additional 3 equals +1 star
     */
    @Test
    public void testTrippleThreesLarg() {
        int expectedStars = gameState.getCurrentPlayer().getMonster().getStars() + 4;
        CheckNumOfThrees cnot = new CheckNumOfThrees(gameState, 4);
        cnot.execute();

        assertEquals(expectedStars, gameState.getCurrentPlayer().getMonster().getStars());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each energy = 1 energy
     */
    @Test
    public void testGetNoEnergy() {
        Client client = gameState.getCurrentPlayer();
        int expectedStars = client.getMonster().getEnergy();
        AwardEnergy aw = new AwardEnergy(gameState, client, 0);
        aw.execute();

        assertEquals(expectedStars,client.getMonster().getEnergy());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each energy = 1 energy
     */
    @Test
    public void testGetEnergy() {
        int energy = rnd.nextInt(6) + 1;
        Client client = gameState.getCurrentPlayer();
        int expectedStars = client.getMonster().getEnergy() + energy;
        AwardEnergy aw = new AwardEnergy(gameState, client, energy);
        aw.execute();

        assertEquals(expectedStars,client.getMonster().getEnergy());
    }
}
