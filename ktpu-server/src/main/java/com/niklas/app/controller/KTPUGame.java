package com.niklas.app.controller;


import java.util.ArrayList;
import java.util.Collections;

import com.niklas.app.model.cards.CardStore;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.controller.actions.Actions;
import com.niklas.app.controller.events.AwardStarIfInTokyo;
import com.niklas.app.controller.events.CheckForWinByElimination;
import com.niklas.app.controller.events.CheckForWinByStars;
import com.niklas.app.controller.events.RollDice;
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
    		check_dice(dice, current_player);
    		
//    		7. Decide to buy things for energy
			//TODO: Fix
    		shoping(current_player);

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
			AwardStarIfInTokyo asiit = new AwardStarIfInTokyo(current_client.get_monster());
			for (int i = 0; i < currentMonster.store_cards.size(); i++) {
				StoreCard storeCard = currentMonster.store_cards.get(i);
				if (storeCard.get_effect().get_activation() == Activation.inTokyo) {
					activeteGeneralAction(currentMonster, storeCard.get_effect());
					if (storeCard.get_type() == "Discard") {
						currentMonster.store_cards.remove(i);
						card_store.discard_card(storeCard);
					}
				}
			}
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
				activeteGeneralAction(currentMonster, storeCard.get_effect());
				if (storeCard.get_type() == "Discard") {
					currentMonster.store_cards.remove(i);
					card_store.discard_card(storeCard);
				}
			}
		}
		rollDice.execute();
        return rollDice.getDice();
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
	
    private void activeteGeneralAction(Monster monster, Effect effect) {
		switch (effect.get_action()) {
			case giveStarsAndEnergy:
				actions.giveStarsAndEnergy(monster, effect.get_added_stars(), effect.get_added_energy());
				break;
			default:
				throw new Error("action=" + effect.get_action() + " is not implemented");
		}
	}
    
    private void shoping(Client current_client) {
    	ArrayList<StoreCard> store_cards = new ArrayList<StoreCard>();
    	for (StoreCard storeCard : card_store.get_inventory()) {
    		store_cards.add(storeCard);
        }

        String answer = comunication.send_shoping(current_client, card_store);
        int buy = Integer.parseInt(answer);
        if(buy>0 && (current_client.get_monster().get_energy() >= store_cards.get(buy -1).get_cost())) { 
        	try {
        		current_client.get_monster().set_entergy(current_client.get_monster().get_energy() -  store_cards.get(buy - 1).get_cost());
				current_client.get_monster().store_cards.add(card_store.buy(buy-1));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    private void check_dice(ArrayList<KTPUDice> dice, Client current_client) {
    	int num_ones = 0;
    	int num_twos = 0;
    	int num_threes = 0;
    	int num_hearts = 0;
    	int num_claws = 0;
    	int num_energy = 0;
    	
    	for (KTPUDice ktpuDice : dice) {
			switch (ktpuDice.get_value()) {
			case KTPUDice.ONE:
				num_ones += 1;
				break;
			case KTPUDice.TWO:
				num_twos += 1;
				break;
			case KTPUDice.THREE:
				num_threes += 1;
				break;
			case KTPUDice.HEART:
				num_hearts += 1;
				break;
			case KTPUDice.CLAWS:
				num_claws += 1;
				break;
			case KTPUDice.ENERGY:
				num_energy += 1;
				break;
			default:
				throw new Error("Dice value:" + ktpuDice.get_value() + " is not implemented");
			}
		}
//    	6a. Hearts = health (max 10 unless a cord increases it)
    	int hp = current_client.get_monster().get_hp() + num_hearts;
    	if (hp > current_client.get_monster().get_max_hp()) {
    		current_client.get_monster().set_hp(current_client.get_monster().get_max_hp());	
    	} else {
    		current_client.get_monster().set_hp(hp);	
    	}
//    	6b. 3 hearts = power-up
    	if (num_hearts >= 3) {
//    		EvolutionCard evolutionCard = current_client.get_monster().draw_evolution_card();
//			current_client.get_monster().cards.add(evolutionCard);
    	}
    	
//    	6c. 3 of a number = victory points
    	if (num_ones >= 3) {
    		current_client.get_monster().set_stars(current_client.get_monster().get_stars() + 1 + num_ones - 3);
    	}
    	if (num_twos >= 3) {
    		current_client.get_monster().set_stars(current_client.get_monster().get_stars() + 2 + num_twos - 3);
    	}
    	if (num_threes >= 3) {
    		current_client.get_monster().set_stars(current_client.get_monster().get_stars() + 3 + num_threes - 3);
    	}
    	
//    	6d. claws = attack (if in Tokyo attack everyone, else attack monster in Tokyo)
    	if (num_claws > 0) {
    		boolean enter_tokyo = true;
    		if (current_client.get_monster().get_in_tokyo()) {
    			for (Client client : players) {
    				attack(client.get_monster(), num_claws);
				}
    		} else {
    			for (Client client : players) {
    				if (client.get_monster().get_in_tokyo()) {
						attack(client.get_monster(), num_claws);
						
						// 6e. If you were outside, then the monster inside tokyo may decide to leave Tokyo
                        String answer = comunication.send_leave_tokyo(client);
                        if(answer.equalsIgnoreCase("YES")) {
                        	client.get_monster().set_in_tokyo(false);
                        	enter_tokyo = true;
                        } else {
                        	enter_tokyo = false;
                        }
					}
				}
    			if (enter_tokyo) {
    				current_client.get_monster().set_in_tokyo(true);
    			}
    		}
    	}
    	
//    	6f. energy = energy tokens
    	current_client.get_monster().set_entergy(current_client.get_monster().get_energy() + num_energy);
    }
    
    
    private void attack(Monster monster, int damage) {
    	monster.set_hp(monster.get_hp() - damage);
    }
}