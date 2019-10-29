package com.niklas.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.niklas.app.controller.KTPUGame;
import com.niklas.app.controller.events.AwardStarIfCurrentPlayerInTokyo;
import com.niklas.app.model.GameState;
import com.niklas.app.model.monsters.Monster;


public class AppTest {
    private GameState gameState;
    
    TestServer ts;
    TestClient testClient1;
    TestClient testClient2;
    TestClient testClient3;
    
    @Before
    public void before() {
    	System.out.println("Before Start");
    	
    	ts = new TestServer(3, 
        		"./src/main/java/com/niklas/app/model/json/Monster.json",
        		"./src/main/java/com/niklas/app/model/json/StoreDeck.json");
    	ts.start();
    	
    	try
    	{
    	    Thread.sleep(1000);
    	}
    	catch(InterruptedException ex)
    	{
    	    Thread.currentThread().interrupt();
    	}
    	
        testClient1 = new TestClient();
        testClient1.start();
        testClient2 = new TestClient();
        testClient2.start();
        testClient3 = new TestClient(); 
        testClient3.start();
        
       while (ts.getKTPUGame() == null) {
    	   
       }
        gameState = ts.getKTPUGame().getGameState();
        System.out.println("Before End");
    }
 
    @After
    public void after() {
    	ts.interrupt();
    	testClient1.interrupt();
    	testClient2.interrupt();
    	testClient3.interrupt();
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
        asicoit.execute();
        assertEquals(expectedStars, currentMonster.getStars());
    }
}
