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
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class KTPUGame {
    private GameState gameState;
    

    public KTPUGame(int tot_num_players, String monster_filepath, String store_card_filepath) {
        ReadJson json_reader = new ReadJson();
        
        ArrayList<Monster> monsters = json_reader.read_monsters_from_json(monster_filepath);
        Collections.shuffle(monsters);
        int tot_num_monster = monsters.size();

        if (tot_num_players > tot_num_monster) {
            throw new Error(String.format("There is %2d more players then ther are monsters.", 
                tot_num_players - tot_num_monster));
        }
        
        for (int i = 0; i < tot_num_monster - tot_num_players; i++) {
            monsters.remove(0);
        }

		Comunication comunication = new Comunication();
        ArrayList<Client> players = comunication.initComunication(monsters);
		Collections.shuffle(players);
		CardStore cardStore = new CardStore(json_reader.read_store_deck_from_json(store_card_filepath));
		
		gameState = new GameState(players, cardStore, comunication);
    }
    
    public void startGame() {
    	/*
	        Game loop:
	        pre: Award a monster in Tokyo 1 star
	        1. Roll 6 dice
	        2. Decide which dice to keep
	        3. Reroll remaining dice
	        4. Decide which dice to keep 
	        5. Reroll remaining dice
	        6. Sum up totals
	          6a. Hearts = health (max 10 unless a cord increases it)
	          6b. 3 hearts = power-up
	          6c. 3 of a number = victory points
	          6d. claws = attack (if in Tokyo attack everyone, else attack monster in Tokyo)
	          6e. If you were outside, then the monster inside tokyo may decide to leave Tokyo
	          6f. energy = energy tokens
	        7. Decide to buy things for energy
	          7a. Play "DISCARD" cards immediately
	        8. Check victory conditions
		*/
    	while (gameState.getIsGameOn()) {
			
			if (!gameState.getCurrentPlayer().getMonster().getIsDead()) {
				// pre: Award a monster in Tokyo 1 star
				AwardStarIfCurrentPlayerInTokyo asicpit = new AwardStarIfCurrentPlayerInTokyo(gameState);
				asicpit.execute();
				
				// 1-5.
				RollDice rollDice = new RollDice(gameState);
				rollDice.execute();
				
				// 7. Decide to buy things for energy
				Shopping shopping = new Shopping(gameState);
	        	shopping.execute();
			}

			gameState.nextTurn();
		}
		gameState.getComunication().closeSocet();
	}
	
	public GameState getGameState() {
		return gameState;
	}
}
