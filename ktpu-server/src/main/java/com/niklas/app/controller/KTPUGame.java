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
import com.niklas.app.controller.events.AwardStarIfInTokyo;
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
import com.niklas.app.model.cards.Activation;
import com.niklas.app.model.dice.KTPUDice;
import com.niklas.app.model.json.ReadJson;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;


public class KTPUGame {
    private ArrayList<Client> players;
    private CardStore card_store;
	private Comunication comunication;
	private Actions actions;


    public KTPUGame(int tot_num_players, String monster_filepath, String store_card_filepath) {
        ReadJson json_reader = new ReadJson();
		comunication = new Comunication();
		actions = new Actions();
        
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

        card_store = new CardStore(json_reader.read_store_deck_from_json(store_card_filepath));

        players = comunication.init_comunication(monsters);
        Collections.shuffle(players);

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
    		Client current_player = players.remove(0);
    		
//    		pre: Award a monster in Tokyo 1 star
			awardStarIfInTokyo(current_player);
    		
//    		1-5.
    		ArrayList<KTPUDice> dice = rollDice(current_player);
    		
//    		6. Sum up totals 
			//TODO: Fix
    		checkDice(dice, current_player);
    		
//    		7. Decide to buy things for energy
    		shopping(current_player);

//    		8. Check victory conditions
			is_game_on = checkIfGameIsNotOver(current_player);

    		players.add(current_player);
		}
//    	Client winner = players.remove(players.size() - 1);
//		comunication.sendWinner(winner, players);
    }
    
    private void awardStarIfInTokyo(Client current_client) {
		Monster currentMonster = current_client.get_monster();
		if (currentMonster.get_in_tokyo()) {
			AwardStarIfInTokyo asiit = new AwardStarIfInTokyo(comunication, current_client);
			asiit.execute();
		}
		comunication.send_all_stats(current_client, players);
	}
	
	private ArrayList<KTPUDice> rollDice(Client current_client) {
		RollDice rollDice = new RollDice(comunication, current_client);
		Monster currentMonster = current_client.get_monster();
        for (int i = 0; i < currentMonster.store_cards.size(); i++) {
			StoreCard storeCard = currentMonster.store_cards.get(i);
			if (storeCard.get_effect().get_activation() == Activation.rollDice) {
				activeteGeneralAction(current_client, storeCard.get_effect());
				if (storeCard.get_type() == StoreCardType.discard) {
					currentMonster.store_cards.remove(i);
					card_store.discard_card(storeCard);
				}
			}
		}
		rollDice.execute();
        return rollDice.getDice();
	}
	
	 private void shopping(Client current_client) {
			Shopping shopping = new Shopping(card_store, comunication, current_client);
			Monster currentMonster = current_client.get_monster();
	        for (int i = 0; i < currentMonster.store_cards.size(); i++) {
				StoreCard storeCard = currentMonster.store_cards.get(i);
				if (storeCard.get_effect().get_activation() == Activation.shopping) {
					activeteGeneralAction(current_client, storeCard.get_effect());
					if (storeCard.get_type() == StoreCardType.discard) {
						currentMonster.store_cards.remove(i);
						card_store.discard_card(storeCard);
					}
				}
			}
	        shopping.execute();
	        checkCards(current_client);
    }
	 
	private void checkCards(Client current_client) {
		Monster currentMonster = current_client.get_monster();
		for (int i = 0; i < currentMonster.store_cards.size(); i++) {
			StoreCard storeCard = currentMonster.store_cards.get(i);
			if (storeCard.get_effect().get_activation() == Activation.now) {
				activeteGeneralAction(current_client, storeCard.get_effect());
				if (storeCard.get_type() == StoreCardType.discard) {
					currentMonster.store_cards.remove(i);
					card_store.discard_card(storeCard);
				}
			}
		}
		for (int i = 0; i < currentMonster.evolutionCards.size(); i++) {
			EvolutionCard evolutionCard = currentMonster.evolutionCards.get(i);
			if (evolutionCard.get_effect().get_activation() == Activation.now) {
				activeteGeneralAction(current_client, evolutionCard.get_effect());
				if (evolutionCard.getDuration() == Duration.temporaryEvolution) {
					currentMonster.evolutionCards.remove(i);
					currentMonster.discard_evolution_card(evolutionCard);
				}
			}
		}
	}

	private boolean checkIfGameIsNotOver(Client current_client) {
		ArrayList<Client> clients = new ArrayList<Client>();
		clients.add(current_client);
		for (Client client : players) {
			clients.add(client);
		}
		boolean winByStars = checkForWinByStars(clients);
		boolean winByElimination = checkForWinByElimination(clients);
		return !(winByStars || winByElimination);
	}
	
	private boolean checkForWinByStars(ArrayList<Client> clients) {
		CheckForWinByStars cfwbs = new CheckForWinByStars(clients, comunication);
		cfwbs.execute();
        return cfwbs.getGameOver();
	}
	
	private boolean checkForWinByElimination(ArrayList<Client> clients) {
		CheckForWinByElimination cfwbe = new CheckForWinByElimination(clients, comunication);
		cfwbe.execute();
        return cfwbe.getGameOver();
	}
	
    private void activeteGeneralAction(Client client, Effect effect) {
		switch (effect.get_action()) {
			case giveStarsEnergyAndHp:
				actions.giveStarsEnergyAndHp(comunication, client, effect);
				break;
			case damageEveryoneElse:
				actions.damageEveryoneElse(comunication, players, effect);
				break;
			default:
				throw new Error("action=" + effect.get_action() + " is not implemented");
		}
	}
    
    private void checkDice(ArrayList<KTPUDice> dice, Client current_client) {
		CheckDice checkDice = new CheckDice(dice, comunication, current_client);
		checkDice.execute();
		
//    	6a. Hearts = health (max 10 unless a cord increases it)
		Heal heal = new Heal(comunication, current_client, checkDice.getNumHearts());
		heal.execute();
		
//    	6b. 3 hearts = power-up
		PowerUp powerUp = new PowerUp(comunication, current_client, checkDice.getNumHearts());
		powerUp.execute();
    	
//    	6c. 3 of a number = victory points
		CheckNumOfOnes cnoo = new CheckNumOfOnes(comunication, current_client, checkDice.getNumOnes());
		cnoo.execute();
    	CheckNumOfTwos cnoTwos = new CheckNumOfTwos(comunication, current_client, checkDice.getNumTwos());
		cnoTwos.execute();
    	CheckNumOfThrees cnoThrees= new CheckNumOfThrees(comunication, current_client, checkDice.getNumThrees());
		cnoThrees.execute();
    	
//    	6d. claws = attack (if in Tokyo attack everyone, else attack monster in Tokyo)
		Attack attack = new Attack(comunication, current_client, players, checkDice.getNumClaws());
		attack.execute();
    	
//    	6f. energy = energy tokens
		AwardEnergy awardEnergy = new AwardEnergy(comunication, current_client, checkDice.getNumEnergy());
		awardEnergy.execute();
    }
}