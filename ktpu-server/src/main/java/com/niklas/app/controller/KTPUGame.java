package com.niklas.app.controller;


import java.util.ArrayList;
import java.util.Collections;

import com.niklas.app.model.cards.CardStore;
import com.niklas.app.model.cards.Duration;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreCardType;
import com.niklas.app.controller.actions.Actions;
import com.niklas.app.controller.events.Attack;
import com.niklas.app.controller.events.AwardEnergy;
import com.niklas.app.controller.events.AwardStarIfCurrentPlayerInTokyo;
import com.niklas.app.controller.events.CheckDice;
import com.niklas.app.controller.events.CheckForWinByElimination;
import com.niklas.app.controller.events.CheckForWinByStars;
import com.niklas.app.controller.events.CheckNumOfOnes;
import com.niklas.app.controller.events.CheckNumOfThrees;
import com.niklas.app.controller.events.CheckNumOfTwos;
import com.niklas.app.controller.events.Heal;
import com.niklas.app.controller.events.PowerUp;
import com.niklas.app.controller.events.RollDice;
import com.niklas.app.controller.events.Shopping;
import com.niklas.app.model.GameState;
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.dice.KTPUDice;
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
        ArrayList<Client> players = comunication.init_comunication(monsters);
		Collections.shuffle(players);
		CardStore cardStore = new CardStore(json_reader.read_store_deck_from_json(store_card_filepath));
		
		gameState = new GameState(players, cardStore, comunication);

        game_loop();
    }
    
    private void game_loop() {
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
		boolean is_game_on = true;
    	while (is_game_on) {
    		Client current_player = gameState.getCurrentPlayer();
//    		pre: Award a monster in Tokyo 1 star
			awardStarIfInTokyo();
    		
//    		1-5.
    		ArrayList<KTPUDice> dice = rollDice();
    		
//    		6. Sum up totals 
			//TODO: Fix
    		checkDice(dice);
    		
//    		7. Decide to buy things for energy
    		shopping();

//    		8. Check victory conditions
			is_game_on = checkIfGameIsNotOver();

    		gameState.nextTurn();
		}
//    	Client winner = players.remove(players.size() - 1);
//		comunication.sendWinner(winner, players);
    }
    
    private void awardStarIfInTokyo() {
		AwardStarIfCurrentPlayerInTokyo asicpit = new AwardStarIfCurrentPlayerInTokyo(gameState);
		asicpit.execute();
	}
	
	private ArrayList<KTPUDice> rollDice() {
		RollDice rollDice = new RollDice(gameState);
		rollDice.execute();
        return rollDice.getDice();
	}

	private void checkDice(ArrayList<KTPUDice> dice) {
		CheckDice checkDice = new CheckDice(gameState, dice);
		checkDice.execute();
		
		// 6a. Hearts = health (max 10 unless a cord increases it)
		Heal heal = new Heal(gameState, gameState.getCurrentPlayer(), checkDice.getNumHearts());
		heal.execute();
		
		// 6b. 3 hearts = power-up
		PowerUp powerUp = new PowerUp(gameState, checkDice.getNumHearts());
		powerUp.execute();
    	
		// 6c. 3 of a number = victory points
		CheckNumOfOnes cnoo = new CheckNumOfOnes(gameState, checkDice.getNumOnes());
		cnoo.execute();
    	CheckNumOfTwos cnoTwos = new CheckNumOfTwos(gameState, checkDice.getNumTwos());
		cnoTwos.execute();
    	CheckNumOfThrees cnoThrees= new CheckNumOfThrees(gameState, checkDice.getNumThrees());
		cnoThrees.execute();
    	
		// 6d. claws = attack (if in Tokyo attack everyone, else attack monster in Tokyo)
		Attack attack = new Attack(gameState, gameState.getCurrentPlayer(), gameState.getPlayers(), checkDice.getNumClaws());
		attack.execute();
    	
		// 6f. energy = energy tokens
		AwardEnergy awardEnergy = new AwardEnergy(gameState, gameState.getCurrentPlayer(), checkDice.getNumEnergy());
		awardEnergy.execute();
    }
	
	 private void shopping() {
			Shopping shopping = new Shopping(gameState);
	        shopping.execute();
    }

	private boolean checkIfGameIsNotOver() {
		boolean winByStars = checkForWinByStars();
		boolean winByElimination = checkForWinByElimination();
		return !(winByStars || winByElimination);
	}
	
	private boolean checkForWinByStars() {
		CheckForWinByStars cfwbs = new CheckForWinByStars(gameState);
		cfwbs.execute();
        return cfwbs.getGameOver();
	}
	
	private boolean checkForWinByElimination() {
		CheckForWinByElimination cfwbe = new CheckForWinByElimination(gameState);
		cfwbe.execute();
        return cfwbe.getGameOver();
	}
}