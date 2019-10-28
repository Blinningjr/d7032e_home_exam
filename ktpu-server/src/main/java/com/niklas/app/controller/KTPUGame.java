package com.niklas.app.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import com.niklas.app.model.cards.CardStore;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.Card;
import com.niklas.app.model.dice.KTPUDice;
import com.niklas.app.model.json.ReadJson;
import com.niklas.app.model.monsters.Monster;
import com.niklas.app.online.Client;
import com.niklas.app.online.Comunication;

public class KTPUGame {
    private static final int NUM_STARS_NEEDED_TO_WIN = 20;
    private ArrayList<Client> players;
    private CardStore card_store;
    private Comunication comunication;


    public KTPUGame(int tot_num_players, String monster_filepath, String store_card_filepath) {
        ReadJson json_reader = new ReadJson();
        comunication = new Comunication();
        
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
    
    public void game_loop() {
    	boolean is_game_on = true;
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
    	while (is_game_on) {
    		Client current_client = players.remove(0);
    		
//    		pre: Award a monster in Tokyo 1 star
    		awards_star_is_monster_is_in_tokyo(current_client.get_monster());
    		comunication.send_all_stats(current_client, players);
    		
//    		1-5.
    		ArrayList<KTPUDice> dice = role_dice(current_client, 6, 3);
    		
//    		6. Sum up totals
    		check_dice(dice, current_client);
    		
//    		7. Decide to buy things for energy
    		shoping(current_client);
    		
//    		8. Check victory conditions
    		is_game_on =  check_if_a_monster_has_won(current_client.get_monster());
    		if (!is_game_on) {
    			System.out.println("Game Over");
    			return;
    		}
    		
    		players.add(current_client);
    	}
    }
    
    public void add_effect_boost(Client client, Effect effect) {
    	
    }
    
    public void shoping(Client current_client) {
    	ArrayList<StoreCard> store_cards = new ArrayList<StoreCard>();
    	for (StoreCard storeCard : card_store.get_inventory()) {
    		store_cards.add(storeCard);
        }

        String answer = comunication.send_shoping(current_client, card_store);
        int buy = Integer.parseInt(answer);
        if(buy>0 && (current_client.get_monster().get_energy() >= store_cards.get(buy -1).get_cost())) { 
        	try {
        		current_client.get_monster().set_entergy(current_client.get_monster().get_energy() -  store_cards.get(buy - 1).get_cost());
				current_client.get_monster().store_cards.add(card_store.buy(buy));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    public void check_dice(ArrayList<KTPUDice> dice, Client current_client) {
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
    	if (num_ones >= 0) {
    		current_client.get_monster().set_stars(current_client.get_monster().get_stars() + 1 + num_ones - 3);
    	}
    	if (num_twos >= 0) {
    		current_client.get_monster().set_stars(current_client.get_monster().get_stars() + 2 + num_twos - 3);
    	}
    	if (num_threes >= 0) {
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
    
    
    public void attack(Monster monster, int damage) {
    	monster.set_hp(monster.get_hp() - damage);
    }
    
    
//    pre: Award a monster in Tokyo 1 star
    public void awards_star_is_monster_is_in_tokyo(Monster monster) {
		if (monster.get_in_tokyo()) {
			monster.set_stars(monster.get_stars() + 1);
		}
    }
    
    public boolean check_if_a_monster_has_won(Monster monster) {
    	if (monster.get_stars() >= NUM_STARS_NEEDED_TO_WIN) {
    		return false;
    	}
    	return true;
    }
    
    
    public ArrayList<KTPUDice> role_dice(Client client,int num_dice, int num_rerolls) {
//    	1. Roll 6 dice
    	ArrayList<KTPUDice> dice = new ArrayList<KTPUDice>();
        for (int i = 0; i < num_dice; i++) {
        	dice.add(new KTPUDice());
        }
        for (int i = 0; i < num_rerolls; i++) {
        	int[] reroll = comunication.send_reroll_dice(dice, client);
        	if (reroll.length > 0) {
        		for (int j : reroll) {
					dice.get(j).roll();
				}
        	} else{
        		return dice;
        	}
        }
        return dice;
    }
}