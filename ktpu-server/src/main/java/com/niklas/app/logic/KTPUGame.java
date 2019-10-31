package com.niklas.app.logic;


import java.util.ArrayList;
import java.util.Collections;

import com.niklas.app.model.cards.CardStore;
import com.niklas.app.logic.events.AwardStarIfCurrentPlayerInTokyo;
import com.niklas.app.logic.events.RollDice;
import com.niklas.app.logic.events.Shopping;
import com.niklas.app.model.GameState;
import com.niklas.app.model.json.ReadJson;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Player;
import com.niklas.app.online.Comunication;


/**
 * KTPUGame class brings all he code togheter to make a game, it initilizes every thing that is needed and has the game loop with the game logic
 */
public class KTPUGame {
    private GameState gameState;
    

	/**
	 * Creates a KTPUGame object and initializes all the needed reasoursed for the game.
	 * @param totNumPlayers is the number of plyers that are going to play this match.
	 * @param monsterFilepath is the filepath to a json file with the monsters and evolution cards.
	 * @param storeCardFilepath is the filepath to a json file with all the store cards.
	 */
    public KTPUGame(int totNumPlayers, String monsterFilepath, String storeCardFilepath) {
        ReadJson json_reader = new ReadJson();
        
        ArrayList<Monster> monsters = json_reader.readMonstersFromJson(monsterFilepath);
        Collections.shuffle(monsters);
        int tot_num_monster = monsters.size();

        if (totNumPlayers > tot_num_monster) {
            throw new Error(String.format("There is %2d more players then ther are monsters.", 
                totNumPlayers - tot_num_monster));
        }
        
        for (int i = 0; i < tot_num_monster - totNumPlayers; i++) {
            monsters.remove(0);
        }

		Comunication comunication = new Comunication();
        ArrayList<Player> players = comunication.initComunication(monsters);
		Collections.shuffle(players);
		CardStore cardStore = new CardStore(json_reader.readStoreDeckFromJson(storeCardFilepath));
		
		gameState = new GameState(players, cardStore, comunication);
    }
	
	
	/**
	 * Starts the game and game loop.
	 */
    public void startGame() {
    	while (gameState.getIsGameOn()) {
			if (!gameState.getCurrentPlayer().getMonster().getIsDead()) {
				
				AwardStarIfCurrentPlayerInTokyo asicpit = new AwardStarIfCurrentPlayerInTokyo(gameState);
				asicpit.execute();
				
				RollDice rollDice = new RollDice(gameState);
				rollDice.execute();
				
				Shopping shopping = new Shopping(gameState);
	        	shopping.execute();
			}

			gameState.nextTurn();
		}

		gameState.getComunication().closeSocet();
	}
	

	/**
	 * Returns the games state which has all the infomation about the game.
	 * @return the games state.
	 */
	public GameState getGameState() {
		return gameState;
	}
}
