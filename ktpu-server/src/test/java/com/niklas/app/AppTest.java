package com.niklas.app;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.niklas.app.controller.events.Attack;
import com.niklas.app.controller.events.AwardEnergy;
import com.niklas.app.controller.events.AwardStarIfCurrentPlayerInTokyo;
import com.niklas.app.controller.events.CheckNumHearts;
import com.niklas.app.controller.events.CheckNumOfOnes;
import com.niklas.app.controller.events.CheckNumOfThrees;
import com.niklas.app.controller.events.CheckNumOfTwos;
import com.niklas.app.controller.events.PowerUp;
import com.niklas.app.controller.events.Shopping;
import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;


public class AppTest {
    private GameState gameState;
    Random rnd = new Random();
    
    TestClient testClient1 = new TestClient();
    TestClient testClient2 = new TestClient();
    TestClient testClient3 = new TestClient();
    TestServer ts = new TestServer(3, 
        		"./src/test/java/com/niklas/app/TestMonster.json",
                "./src/test/java/com/niklas/app/TestStoreDeck.json");
    
    @Before
    public void before() {
        System.out.println("\n\n\nBefore Start");
    	ts.start();
    	try {
    	    Thread.sleep(500);
    	}
    	catch(InterruptedException ex) {
    	    Thread.currentThread().interrupt();
        }
        testClient1.start();
        testClient2.start();
        testClient3.start();
        try {
			ts.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    public void testRollNoEnergy() {
        Client client = gameState.getCurrentPlayer();
        int expectedEnergy = client.getMonster().getEnergy();
        AwardEnergy ae = new AwardEnergy(gameState, client, 0);
        ae.execute();

        assertEquals(expectedEnergy, client.getMonster().getEnergy());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each energy = 1 energy
     */
    @Test
    public void testRollEnergy() {
        int energy = rnd.nextInt(6) + 1;
        Client client = gameState.getCurrentPlayer();
        int expectedEnergy = client.getMonster().getEnergy() + energy;
        AwardEnergy ae = new AwardEnergy(gameState, client, energy);
        ae.execute();

        assertEquals(expectedEnergy, client.getMonster().getEnergy());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each heart
     *          i.Inside Tokyo –no extra health
     */
    @Test
    public void testRollNoHeartsIntokyo() {
        int hearts = 0;
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        monster.setInTokyo(true);
        monster.setHp(5);
        int expectedHp = monster.getHp() + hearts;
        CheckNumHearts cnh = new CheckNumHearts(gameState, hearts);
        cnh.execute();

        assertEquals(expectedHp, client.getMonster().getHp());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each heart
     *          ii.Outside Tokyo -+1 health (up to your max life, normally 10 unless altered by a card)
     */
    @Test
    public void testRollNoHearts() {
        int hearts = 0;
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        monster.setInTokyo(false);
        monster.setHp(5);
        int expectedHp = monster.getHp() + hearts;
        CheckNumHearts cnh = new CheckNumHearts(gameState, hearts);
        cnh.execute();

        assertEquals(expectedHp, client.getMonster().getHp());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each heart
     *      i.Inside Tokyo –no extra health
     */
    @Test
    public void testRollHeartInTokyo() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        int hearts = rnd.nextInt(monster.getMaxHp() / 2 -1) + 1;
        monster.setInTokyo(true);
        monster.setHp(monster.getMaxHp() / 2 -1);
        int expectedHp = monster.getHp();
        CheckNumHearts cnh = new CheckNumHearts(gameState, hearts);
        cnh.execute();

        assertEquals(expectedHp, client.getMonster().getHp());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each heart
     *      ii.Outside Tokyo -+1 health (up to your max life, normally 10 unless altered by a card)
     */
    @Test
    public void testRollHearts() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        int hearts = rnd.nextInt(monster.getMaxHp() / 2 -1) + 1;
        monster.setInTokyo(false);
        monster.setHp(monster.getMaxHp() / 2 -1);
        int expectedHp = monster.getHp() + hearts;
        CheckNumHearts cnh = new CheckNumHearts(gameState, hearts);
        cnh.execute();

        assertEquals(expectedHp, client.getMonster().getHp());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each heart
     *      ii.Outside Tokyo -+1 health (up to your max life, normally 10 unless altered by a card)
     */
    @Test
    public void testRollHeartsOverHeal() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        int hearts = rnd.nextInt(6) + 3;
        monster.setInTokyo(false);
        monster.setHp(monster.getMaxHp() - 2);
        int expectedHp = monster.getMaxHp();
        CheckNumHearts cnh = new CheckNumHearts(gameState, hearts);
        cnh.execute();

        assertEquals(expectedHp, client.getMonster().getHp());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Tripple hearts = Draw an Evolution Card(todo: add support for more evolution cards).
     */
    @Test
    public void testTrippleHeartsLess() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        int hearts = 2;

        int expected = monster.evolutionCards.size();

        PowerUp pu = new PowerUp(gameState, hearts);
        pu.execute();

        assertEquals(expected, monster.evolutionCards.size());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Tripple hearts = Draw an Evolution Card(todo: add support for more evolution cards).
     */
    @Test
    public void testTrippleHeartsEqual() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        int hearts = 3;

        int expected = monster.evolutionCards.size() + 1;

        PowerUp pu = new PowerUp(gameState, hearts);
        pu.execute();

        assertEquals(expected, monster.evolutionCards.size());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Tripple hearts = Draw an Evolution Card(todo: add support for more evolution cards).
     */
    @Test
    public void testTrippleHeartsLarg() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        int hearts = 4;

        int expected = monster.evolutionCards.size() + 1;

        PowerUp pu = new PowerUp(gameState, hearts);
        pu.execute();

        assertEquals(expected, monster.evolutionCards.size());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each claw
     *          i.Inside Tokyo –1 damage dealt to each monster outside of Tokyo
     */
    @Test
    public void testNoClawsInTokyo() {
        Client client = gameState.getCurrentPlayer();
        client.getMonster().setInTokyo(true);
        Monster m0 = gameState.getPlayers().get(0).getMonster();
        Monster m1 = gameState.getPlayers().get(1).getMonster();

        int claws = 0;
        int expectedHpM0 = m0.getHp() - claws;
        int expectedHpM1 = m1.getHp() - claws;

        Attack attack = new Attack(gameState, client, gameState.getPlayers(), claws);
        attack.execute();
    
        assertEquals(expectedHpM0, m0.getHp());
        assertEquals(expectedHpM1, m1.getHp());
        assertTrue(client.getMonster().getInTokyo());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each claw
     *          i.Inside Tokyo –1 damage dealt to each monster outside of Tokyo
     */
    @Test
    public void testClawsInTokyo() {
        Client client = gameState.getCurrentPlayer();
        client.getMonster().setInTokyo(true);
        Monster m0 = gameState.getPlayers().get(0).getMonster();
        Monster m1 = gameState.getPlayers().get(1).getMonster();

        int claws = rnd.nextInt(5) + 1;
        int expectedHpM0 = m0.getHp() - claws;
        int expectedHpM1 = m1.getHp() - claws;

        Attack attack = new Attack(gameState, client, gameState.getPlayers(), claws);
        attack.execute();
    
        assertEquals(expectedHpM0, m0.getHp());
        assertEquals(expectedHpM1, m1.getHp());
        assertTrue(client.getMonster().getInTokyo());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each claw
     *          ii.Outside Tokyo
     *              1.Tokyo Unoccupied = Move into Tokyo and Gain 1 star
     */
    @Test
    public void testNoClawsUnoccupied() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        for (Client c : gameState.getPlayers()) {
            c.getMonster().setInTokyo(false);
        }
        monster.setInTokyo(false);
        monster.setStars(0);
        int claws = 0;
        int expectedStars = 0;
        boolean expectedInTokyo = false;

        Attack attack = new Attack(gameState, client, gameState.getPlayers(), claws);
        attack.execute();
    
        assertEquals(expectedStars, monster.getStars());
        assertEquals(expectedInTokyo, monster.getInTokyo());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each claw
     *          ii.Outside Tokyo
     *              1.Tokyo Unoccupied = Move into Tokyo and Gain 1 star
     */
    @Test
    public void testClawsUnoccupied() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        for (Client c : gameState.getPlayers()) {
            c.getMonster().setInTokyo(false);
        }
        monster.setInTokyo(false);
        monster.setStars(0);
        int claws = rnd.nextInt(5) + 1;

        Attack attack = new Attack(gameState, client, gameState.getPlayers(), claws);
        attack.execute();
    
        assertEquals(1, monster.getStars());
        assertEquals(true, monster.getInTokyo());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each claw
     *          ii.Outside Tokyo
     *              2.Tokyo Occupied
     *                  a.1 damage dealt to the monster inside Tokyo
     *                  b.Monsters damaged may choose to leave Tokyo
     *                  c.If there is an open spot in Tokyo –Move into Tokyo and Gain 1 star
     */
    @Test
    public void testNoClawsOccupiedEnter() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        for (Client c : gameState.getPlayers()) {
            c.getMonster().setInTokyo(false);
        }

        Monster monsterInTokyo = gameState.getPlayers().get(0).getMonster();
        monsterInTokyo.setInTokyo(true);
        testClient1.setLeaveTokyo(true);
        testClient2.setLeaveTokyo(true);
        testClient3.setLeaveTokyo(true);

        monster.setInTokyo(false);
        monster.setStars(0);
        int claws = 0;
        int monsterInTokyoHP = monsterInTokyo.getHp();

        Attack attack = new Attack(gameState, client, gameState.getPlayers(), claws);
        attack.execute();
    
        assertEquals(0, monster.getStars());
        assertEquals(false, monster.getInTokyo());
        assertEquals(true, monsterInTokyo.getInTokyo());
        assertEquals(monsterInTokyoHP, monsterInTokyo.getHp());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each claw
     *          ii.Outside Tokyo
     *              2.Tokyo Occupied
     *                  a.1 damage dealt to the monster inside Tokyo
     *                  b.Monsters damaged may choose to leave Tokyo
     *                  c.If there is an open spot in Tokyo –Move into Tokyo and Gain 1 star
     */
    @Test
    public void testClawsOccupiedEnter() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        for (Client c : gameState.getPlayers()) {
            c.getMonster().setInTokyo(false);
        }

        Monster monsterInTokyo = gameState.getPlayers().get(0).getMonster();
        monsterInTokyo.setInTokyo(true);
        testClient1.setLeaveTokyo(true);
        testClient2.setLeaveTokyo(true);
        testClient3.setLeaveTokyo(true);

        monster.setInTokyo(false);
        monster.setStars(0);
        int claws = 2;
        int monsterInTokyoHP = monsterInTokyo.getHp()- claws;

        Attack attack = new Attack(gameState, client, gameState.getPlayers(), claws);
        attack.execute();
    
        assertEquals(1, monster.getStars());
        assertEquals(true, monster.getInTokyo());
        assertEquals(false, monsterInTokyo.getInTokyo());
        assertEquals(monsterInTokyoHP, monsterInTokyo.getHp());
    }

    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each claw
     *          ii.Outside Tokyo
     *              2.Tokyo Occupied
     *                  a.1 damage dealt to the monster inside Tokyo
     *                  b.Monsters damaged may choose to leave Tokyo
     *                  c.If there is an open spot in Tokyo –Move into Tokyo and Gain 1 star
     */
    @Test
    public void testNoClawsOccupied() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        for (Client c : gameState.getPlayers()) {
            c.getMonster().setInTokyo(false);
        }

        Monster monsterInTokyo = gameState.getPlayers().get(0).getMonster();
        monsterInTokyo.setInTokyo(true);
        testClient1.setLeaveTokyo(false);
        testClient2.setLeaveTokyo(false);
        testClient3.setLeaveTokyo(false);

        monster.setInTokyo(false);
        monster.setStars(0);
        int claws = 0;
        int monsterInTokyoHP = monsterInTokyo.getHp()- claws;

        Attack attack = new Attack(gameState, client, gameState.getPlayers(), claws);
        attack.execute();
    
        assertEquals(0, monster.getStars());
        assertEquals(false, monster.getInTokyo());
        assertEquals(true, monsterInTokyo.getInTokyo());
        assertEquals(monsterInTokyoHP, monsterInTokyo.getHp());
    }
     
    /**
     * 12.Sum up the dice and assign stars, health, or damage
     *      Each claw
     *          ii.Outside Tokyo
     *              2.Tokyo Occupied
     *                  a.1 damage dealt to the monster inside Tokyo
     *                  b.Monsters damaged may choose to leave Tokyo
     *                  c.If there is an open spot in Tokyo –Move into Tokyo and Gain 1 star
     */
    @Test
    public void testClawsOccupied() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        for (Client c : gameState.getPlayers()) {
            c.getMonster().setInTokyo(false);
        }

        Monster monsterInTokyo = gameState.getPlayers().get(0).getMonster();
        monsterInTokyo.setInTokyo(true);
        testClient1.setLeaveTokyo(false);
        testClient2.setLeaveTokyo(false);
        testClient3.setLeaveTokyo(false);

        monster.setInTokyo(false);
        monster.setStars(0);
        int claws = 2;
        int monsterInTokyoHP = monsterInTokyo.getHp() - claws;

        Attack attack = new Attack(gameState, client, gameState.getPlayers(), claws);
        attack.execute();
    
        assertEquals(0, monster.getStars());
        assertEquals(false, monster.getInTokyo());
        assertEquals(true, monsterInTokyo.getInTokyo());
        assertEquals(monsterInTokyoHP, monsterInTokyo.getHp());
    }

    /**
     *  13.Buying Cards (As long as long as you have the Energy, you can take any of the following actions)
     *      Purchase a card = Pay energy equal to the card cost(replace purchased cards with new from the deck)
     */
    @Test
    public void testNoPurchase() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        StoreCard[] inventory = gameState.getCardStore().getInventory();
        StoreCard[] oldInventory = new StoreCard[inventory.length];
        for (int i = 0; i < oldInventory.length; i++) {
            oldInventory[i] = inventory[i];
        }
        int cardCost = inventory[0].getCost();
        String input = "-1\n";
        int energy = cardCost - 1;
        int expectedNumCards = monster.storeCards.size();
        
        monster.setEnergy(energy);

        testClient1.setStoreInput(input);
        testClient2.setStoreInput(input);
        testClient3.setStoreInput(input);

        Shopping shopping = new Shopping(gameState);
        shopping.execute();

        assertEquals(energy, monster.getEnergy());
        assertEquals(expectedNumCards, monster.storeCards.size());
        assertArrayEquals(oldInventory, inventory);
    }

    /**
     *  13.Buying Cards (As long as long as you have the Energy, you can take any of the following actions)
     *      Purchase a card = Pay energy equal to the card cost(replace purchased cards with new from the deck)
     */
    @Test
    public void testPurchaseLow() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        StoreCard[] inventory = gameState.getCardStore().getInventory();
        StoreCard[] oldInventory = new StoreCard[inventory.length];
        for (int i = 0; i < oldInventory.length; i++) {
            oldInventory[i] = inventory[i];
        }
        int pos = 2;
        int cardCost = inventory[pos-1].getCost();
        String input = pos + "\n";
        int energy = cardCost - 1;
        int expectedNumCards = monster.storeCards.size();
        
        monster.setEnergy(energy);

        testClient1.setStoreInput(input);
        testClient2.setStoreInput(input);
        testClient3.setStoreInput(input);

        Shopping shopping = new Shopping(gameState);
        shopping.execute();

        assertEquals(energy, monster.getEnergy());
        assertEquals(expectedNumCards, monster.storeCards.size());
        assertArrayEquals(oldInventory, inventory);
    }

     /**
     *  13.Buying Cards (As long as long as you have the Energy, you can take any of the following actions)
     *      Purchase a card = Pay energy equal to the card cost(replace purchased cards with new from the deck)
     */
    @Test
    public void testPurchaseEqual() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        StoreCard[] inventory = gameState.getCardStore().getInventory();
        StoreCard[] oldInventory = new StoreCard[inventory.length];
        for (int i = 0; i < oldInventory.length; i++) {
            oldInventory[i] = inventory[i];
        }
        int pos = 1;
        int cardCost = inventory[pos-1].getCost();
        String input = pos + "\n";
        int energy = cardCost;
        int expectedNumCards = monster.storeCards.size() + 1;
        
        monster.setEnergy(energy);

        testClient1.setStoreInput(input);
        testClient2.setStoreInput(input);
        testClient3.setStoreInput(input);

        Shopping shopping = new Shopping(gameState);
        shopping.execute();

        assertEquals(0, monster.getEnergy());
        assertEquals(expectedNumCards, monster.storeCards.size());
        assertNotEquals(oldInventory[pos-1], inventory[pos-1]);
    }

    /**
     *  13.Buying Cards (As long as long as you have the Energy, you can take any of the following actions)
     *      Purchase a card = Pay energy equal to the card cost(replace purchased cards with new from the deck)
     */
    @Test
    public void testPurchaseLarg() {
        Client client = gameState.getCurrentPlayer();
        Monster monster = client.getMonster();
        StoreCard[] inventory = gameState.getCardStore().getInventory();
        StoreCard[] oldInventory = new StoreCard[inventory.length];
        for (int i = 0; i < oldInventory.length; i++) {
            oldInventory[i] = inventory[i];
        }
        int pos = 3;
        int cardCost = inventory[pos - 1].getCost();
        String input = pos + "\n";
        int energy = cardCost + 1;
        int expectedNumCards = monster.storeCards.size() + 1;
        
        monster.setEnergy(energy);

        testClient1.setStoreInput(input);
        testClient2.setStoreInput(input);
        testClient3.setStoreInput(input);

        Shopping shopping = new Shopping(gameState);
        shopping.execute();

        assertEquals(1, monster.getEnergy());
        assertEquals(expectedNumCards, monster.storeCards.size());
        assertNotEquals(oldInventory[pos-1], inventory[pos-1]);
    }

    /**
     *  13.Buying Cards (As long as long as you have the Energy, you can take any of the following actions)
     *      Reset store –pay 2 energy
     */
    @Test
    public void testResetStoreLow() {
        Monster monster = gameState.getCurrentPlayer().getMonster();
        StoreCard[] inventory = gameState.getCardStore().getInventory();
        StoreCard[] oldInventory = new StoreCard[inventory.length];
        for (int i = 0; i < oldInventory.length; i++) {
            oldInventory[i] = inventory[i];
        }
        String input = 0 + "\n";
        int energy = 1;
        monster.setEnergy(energy);

        testClient1.setStoreInput(input);
        testClient2.setStoreInput(input);
        testClient3.setStoreInput(input);

        Shopping shopping = new Shopping(gameState);
        shopping.execute();

        assertEquals(energy, monster.getEnergy());
        assertArrayEquals(oldInventory, inventory);
    }

    /**
     *  13.Buying Cards (As long as long as you have the Energy, you can take any of the following actions)
     *      Reset store –pay 2 energy
     */
    @Test
    public void testResetStoreEqual() {
        Monster monster = gameState.getCurrentPlayer().getMonster();
        StoreCard[] inventory = gameState.getCardStore().getInventory();
        StoreCard[] oldInventory = new StoreCard[inventory.length];
        for (int i = 0; i < oldInventory.length; i++) {
            oldInventory[i] = inventory[i];
        }
        String input = 0 + "\n";
        monster.setEnergy(2);

        testClient1.setStoreInput(input);
        testClient2.setStoreInput(input);
        testClient3.setStoreInput(input);

        Shopping shopping = new Shopping(gameState);
        shopping.execute();

        assertEquals(0, monster.getEnergy());
        assertNotEquals(oldInventory, inventory);
    }

    /**
     *  13.Buying Cards (As long as long as you have the Energy, you can take any of the following actions)
     *      Reset store –pay 2 energy
    */
    @Test
    public void testResetStoreLarg() {
        Monster monster = gameState.getCurrentPlayer().getMonster();
        StoreCard[] inventory = gameState.getCardStore().getInventory();
        StoreCard[] oldInventory = new StoreCard[inventory.length];
        for (int i = 0; i < oldInventory.length; i++) {
            oldInventory[i] = inventory[i];
        }
        String input = 0 + "\n";
        monster.setEnergy(3);

        testClient1.setStoreInput(input);
        testClient2.setStoreInput(input);
        testClient3.setStoreInput(input);

        Shopping shopping = new Shopping(gameState);
        shopping.execute();

        assertEquals(1, monster.getEnergy());
        assertNotEquals(oldInventory, inventory);
    }
}
