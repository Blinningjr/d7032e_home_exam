package com.niklas.app;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.niklas.app.controller.events.Attack;
import com.niklas.app.controller.events.AwardEnergy;
import com.niklas.app.controller.events.AwardStar;
import com.niklas.app.controller.events.AwardStarIfCurrentPlayerInTokyo;
import com.niklas.app.controller.events.CheckNumHearts;
import com.niklas.app.controller.events.CheckNumOfOnes;
import com.niklas.app.controller.events.CheckNumOfThrees;
import com.niklas.app.controller.events.CheckNumOfTwos;
import com.niklas.app.controller.events.Damage;
import com.niklas.app.controller.events.PowerUp;
import com.niklas.app.controller.events.RollDice;
import com.niklas.app.controller.events.Shopping;
import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.dice.KTPUDice;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;


public class AppTest {
    private GameState gameState;
    private Random rnd = new Random();
    private final static int NUMPLAYERS = 3;
    
    private TestClient testClient1 = new TestClient();
    private TestClient testClient2 = new TestClient();
    private TestClient testClient3 = new TestClient();
    private TestServer ts = new TestServer(NUMPLAYERS, 
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
     * 1.Each player is assigned a monster.
     */
    @Test
    public void testAllPlayersGotMonsters() {
        ArrayList<Client> players = new ArrayList<Client>();
        players.add(gameState.getCurrentPlayer());
        players.addAll(gameState.getPlayers());

        assertEquals(NUMPLAYERS, players.size());
        
        for (int i = 0; i < NUMPLAYERS; i++) {
            assertNotEquals(players.get(i).getMonster(), null);
        }
    }

    /**
     * 2.Set Victory Points to 0.
     */
    @Test
    public void testZeroStars() {
        ArrayList<Client> players = new ArrayList<Client>();
        players.add(gameState.getCurrentPlayer());
        players.addAll(gameState.getPlayers());
        
        for (int i = 0; i < NUMPLAYERS; i++) {
            assertEquals(0, players.get(i).getMonster().getStars());
        }
    }

    /**
     * 3.Set Life to 10.
     */
    @Test
    public void testLifeEqualsTen() {
        ArrayList<Client> players = new ArrayList<Client>();
        players.add(gameState.getCurrentPlayer());
        players.addAll(gameState.getPlayers());
        
        for (int i = 0; i < NUMPLAYERS; i++) {
            assertEquals(10, players.get(i).getMonster().getHp());
        }
    }

    /**
     *  4.Shuffle the store cards (contained in the deck)(todo: add support for more store cards)
     *      •Start with 3 cards face up (available for purchase).
     */
    @Test
    public void testStoreInventory() {
        StoreCard[] storeCards = gameState.getCardStore().getInventory();
        assertEquals(3, storeCards.length);
        for (int i = 0; i < storeCards.length; i++) {
            assertNotEquals(storeCards[i], null);
        }
    }

    /** 
     *  5.Shuffle the evolution cards for the respective monsters (todo: add support for more evolution cards) 
     *  Can't test with only one card and because the result is random.
     */
     
    /**
     * 6.Randomise which monster starts the game.
     * Can't test because the result is random.
     */

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
    
    /**
     * 8.Roll your 6 dice
     */
    @Test
    public void testRollSixDice() {
        String input = "0\n";
        testClient1.setRerollInput(input);
        testClient2.setRerollInput(input);
        testClient3.setRerollInput(input);

        RollDice rollDice = new RollDice(gameState);
        rollDice.execute();
        ArrayList<KTPUDice> dice = rollDice.getDice();

        assertEquals(6, dice.size());
        for (int i = 0; i < dice.size(); i++) {
            assertTrue(dice.get(i).getValue() == 1 ||
                dice.get(i).getValue() == 2 ||
                dice.get(i).getValue() == 3 ||
                dice.get(i).getValue() == 4 ||
                dice.get(i).getValue() == 5 ||
                dice.get(i).getValue() == 6);
        }
    }

    /**
     * 9. Select which of your 6 dice to reroll.
     * 10. Reroll the selected dice
     */
    @Test
    public void testRerollDice() {
        String input = "1\n";
        testClient1.setRerollInput(input);
        testClient2.setRerollInput(input);
        testClient3.setRerollInput(input);

        RollDice rollDice = new RollDice(gameState);
        rollDice.execute();

        input = "2\n";
        testClient1.setRerollInput(input);
        testClient2.setRerollInput(input);
        testClient3.setRerollInput(input);

        rollDice = new RollDice(gameState);
        rollDice.execute();

        input = "3\n";
        testClient1.setRerollInput(input);
        testClient2.setRerollInput(input);
        testClient3.setRerollInput(input);

        rollDice = new RollDice(gameState);
        rollDice.execute();

        input = "4\n";
        testClient1.setRerollInput(input);
        testClient2.setRerollInput(input);
        testClient3.setRerollInput(input);

        rollDice = new RollDice(gameState);
        rollDice.execute();

        input = "5\n";
        testClient1.setRerollInput(input);
        testClient2.setRerollInput(input);
        testClient3.setRerollInput(input);

        rollDice = new RollDice(gameState);
        rollDice.execute();

        input = "6\n";
        testClient1.setRerollInput(input);
        testClient2.setRerollInput(input);
        testClient3.setRerollInput(input);

        rollDice = new RollDice(gameState);
        rollDice.execute();

        input = "1,2,3,4,5,6\n";
        testClient1.setRerollInput(input);
        testClient2.setRerollInput(input);
        testClient3.setRerollInput(input);

        rollDice = new RollDice(gameState);
        rollDice.execute();

        input = "5,2,4,1\n";
        testClient1.setRerollInput(input);
        testClient2.setRerollInput(input);
        testClient3.setRerollInput(input);

        rollDice = new RollDice(gameState);
        rollDice.execute();
    }

    /**
     * 11.Repeat step 9 and 10once
     */
    @Test
    public void testNumRerolls() {
        String input = "3\n";
        testClient1.setRerollInput(input);
        testClient2.setRerollInput(input);
        testClient3.setRerollInput(input);
        
        RollDice rollDice = new RollDice(gameState);
        rollDice.execute();

        gameState.nextTurn();

        rollDice = new RollDice(gameState);
        rollDice.execute();

        gameState.nextTurn();

        rollDice = new RollDice(gameState);
        rollDice.execute();

        assertEquals(2, testClient1.getNumRerolls());
        assertEquals(2, testClient2.getNumRerolls());
        assertEquals(2, testClient3.getNumRerolls());
    }

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

    /**
     *  14.A store card can be of either type “Keep” or “Discard”. “Discard” cards take effect immediately 
     *  when purchased, and “Keep” cards may either beplayed when the owner desiresor provides an active 
     *  power/ability.
     */
    @Test
    public void testDiscardCard() {
        Monster monster = gameState.getCurrentPlayer().getMonster();
        Effect effect = new Effect("Now", "giveStarsEnergyAndHp");
        int stars = 10;
        effect.add_stars(stars);
        StoreCard storeCard = new StoreCard("Test Card", "Test Card", effect, 1, "discard");
        int pos = 1;
        gameState.getCardStore().getInventory()[pos -1] = storeCard;
        String input = pos + "\n";
        monster.setEnergy(3);
        monster.setStars(0);

        int expectedCardsSize = monster.storeCards.size();

        testClient1.setStoreInput(input);
        testClient2.setStoreInput(input);
        testClient3.setStoreInput(input);

        Shopping shopping = new Shopping(gameState);
        shopping.execute();

        assertEquals(stars, monster.getStars());
        assertEquals(expectedCardsSize, monster.storeCards.size());
    }

    /**
     *  14.A store card can be of either type “Keep” or “Discard”. “Discard” cards take effect immediately 
     *  when purchased, and “Keep” cards may either beplayed when the owner desiresor provides an active 
     *  power/ability.
     */
    @Test
    public void testKeepCard() {
        Monster monster = gameState.getCurrentPlayer().getMonster();
        Effect effect = new Effect("Now", "giveStarsEnergyAndHp");
        int stars = 10;
        effect.add_stars(stars);
        StoreCard storeCard = new StoreCard("Test Card", "Test Card", effect, 1, "keep");
        int pos = 1;
        gameState.getCardStore().getInventory()[pos -1] = storeCard;
        String input = pos + "\n";
        monster.setEnergy(3);
        monster.setStars(0);

        int expectedCardsSize = monster.storeCards.size() + 1;

        testClient1.setStoreInput(input);
        testClient2.setStoreInput(input);
        testClient3.setStoreInput(input);

        Shopping shopping = new Shopping(gameState);
        shopping.execute();

        assertEquals(stars, monster.getStars());
        assertEquals(expectedCardsSize, monster.storeCards.size());
    }
    
    /**
     *  15.End of turn.
     */
    @Test
    public void testNextTurn() {
        Client client = gameState.getCurrentPlayer();
        gameState.nextTurn();
        assertNotEquals(client, gameState.getCurrentPlayer());
    }

    /**
     *  16.First monster to get 20 stars win the game
     */
    @Test
    public void testWinByStarsLess() {
        Client client = gameState.getCurrentPlayer();
        client.getMonster().setStars(0);

        int stars = 19;

        assertTrue(gameState.getIsGameOn());

        AwardStar as = new AwardStar(gameState, client, stars);
        as.execute();

        assertEquals(stars, client.getMonster().getStars());
        assertTrue(gameState.getIsGameOn());
    }

    /**
     *  16.First monster to get 20 stars win the game
     */
    @Test
    public void testWinByStarsEqual() {
        Client client = gameState.getCurrentPlayer();
        client.getMonster().setStars(0);

        int stars = 20;

        assertTrue(gameState.getIsGameOn());

        AwardStar as = new AwardStar(gameState, client, stars);
        as.execute();

        assertEquals(stars, client.getMonster().getStars());
        assertFalse(gameState.getIsGameOn());
    }

    /**
     *  16.First monster to get 20 stars win the game
     */
    @Test
    public void testWinByStarsLarg() {
        Client client = gameState.getCurrentPlayer();
        client.getMonster().setStars(0);

        int stars = 21;

        assertTrue(gameState.getIsGameOn());

        AwardStar as = new AwardStar(gameState, client, stars);
        as.execute();

        assertEquals(stars, client.getMonster().getStars());
        assertFalse(gameState.getIsGameOn());
    }

    /**
     *  17.The sole surviving monster wins the game (other monsters at 0 or less health)
     */
    @Test
    public void testWinByEliminationLess() {
        Client currentClient = gameState.getCurrentPlayer();
        ArrayList<Client> clients = gameState.getPlayers();
        
        assertTrue(gameState.getIsGameOn());
        
        assertFalse(currentClient.getMonster().getIsDead());
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            assertFalse(client.getMonster().getIsDead());
            
            if (i != 0) {
                Damage damage = new Damage(gameState, client, client.getMonster().getHp());
                damage.execute();
            }
        }

        assertTrue(gameState.getIsGameOn());
        assertFalse(currentClient.getMonster().getIsDead());
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            if (i != 0) {
                assertTrue(client.getMonster().getIsDead());
            } else {
                assertFalse(client.getMonster().getIsDead());
            }
            
        }
    }

    /**
     *  17.The sole surviving monster wins the game (other monsters at 0 or less health)
     */
    @Test
    public void testWinByEliminationEqual() {
        Client currentClient = gameState.getCurrentPlayer();
        ArrayList<Client> clients = gameState.getPlayers();
        
        assertTrue(gameState.getIsGameOn());
        
        assertFalse(currentClient.getMonster().getIsDead());
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            assertFalse(client.getMonster().getIsDead());
            
            Damage damage = new Damage(gameState, client, client.getMonster().getHp());
            damage.execute();
        }

        assertFalse(gameState.getIsGameOn());
        assertFalse(currentClient.getMonster().getIsDead());
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            assertTrue(client.getMonster().getIsDead());
        }
    }

    /**
     *  17.The sole surviving monster wins the game (other monsters at 0 or less health)
     */
    @Test
    public void testWinByEliminationLarg() {
        Client currentClient = gameState.getCurrentPlayer();
        ArrayList<Client> clients = gameState.getPlayers();
        
        assertTrue(gameState.getIsGameOn());
        
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            assertFalse(client.getMonster().getIsDead());
            
            Damage damage = new Damage(gameState, client, client.getMonster().getHp());
            damage.execute();
        }

        assertFalse(currentClient.getMonster().getIsDead());
        Damage damage = new Damage(gameState, currentClient, currentClient.getMonster().getHp());
        damage.execute();

        assertFalse(gameState.getIsGameOn());
        assertTrue(currentClient.getMonster().getIsDead());
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            assertTrue(client.getMonster().getIsDead());
        }
    }
}
