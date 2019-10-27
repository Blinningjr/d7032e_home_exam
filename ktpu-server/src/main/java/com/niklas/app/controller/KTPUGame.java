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
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.Card;
import com.niklas.app.model.dice.KTPUDice;
import com.niklas.app.model.json.ReadJson;
import com.niklas.app.model.monsters.Monster;

public class KTPUGame {
     ArrayList<Monster> players;
     CardStore card_store;
    private static final int NUM_STARS_NEEDED_TO_WIN = 20;
    
    private Scanner sc = new Scanner(System.in);


    public KTPUGame(int tot_num_players, String monster_filepath, String store_card_filepath) {
        ReadJson json_reader = new ReadJson();
        
        ArrayList<Monster> monsters = json_reader.read_monsters_from_json(monster_filepath);
        Collections.shuffle(monsters);

        if (tot_num_players > monsters.size()) {
            throw new Error(String.format("There is %2d more players then ther are monsters.", 
                tot_num_players - monsters.size()));
        }
        
        players = new ArrayList<Monster>();
        for (int i = 0; i < tot_num_players; i++) {
            players.add(monsters.get(i));
        }

        card_store = new CardStore(json_reader.read_store_deck_from_json(store_card_filepath));

        init_server_stuff(tot_num_players);
        Collections.shuffle(players);
        System.out.println("init done");
        System.out.println(tot_num_players);
        
        game_loop();
    }

    private void init_server_stuff(int tot_num_players) {
        //Server stuffs
        try {
        	ServerSocket aSocket = new ServerSocket(2048);
            //assume two online clients
            for(int onlineClient=0; onlineClient < tot_num_players - 1; onlineClient++) {
                Socket connectionSocket = aSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                outToClient.writeBytes("You are the monster: " + players.get(onlineClient).get_name() + "\n");
                players.get(onlineClient).connection = connectionSocket;
                players.get(onlineClient).inFromClient = inFromClient;
                players.get(onlineClient).outToClient = outToClient;
                System.out.println("Connected to " + players.get(onlineClient).get_name());
            }
        } catch (Exception e) {
        	throw new Error("init_server_stuff failed");
        }
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
    		Monster current_monster = players.remove(0);
    		
//    		pre: Award a monster in Tokyo 1 star
    		awards_star_is_monster_is_in_tokyo(current_monster);
    		print_stats(current_monster);
    		
//    		1-5.
    		ArrayList<KTPUDice> dice = role_dice(current_monster, 6, 3);
    		
//    		6. Sum up totals
    		check_dice(dice, current_monster);
    		
//    		7. Decide to buy things for energy
    		shoping(current_monster);
    		
//    		8. Check victory conditions
    		is_game_on =  check_if_a_monster_has_won(current_monster);
    		if (!is_game_on) {
    			System.out.println("Game Over");
    			return;
    		}
    		
    		players.add(current_monster);
    	}
    }
    
    public void shoping(Monster current_monster) {
    	ArrayList<StoreCard> store_cards = new ArrayList<StoreCard>();
    	for (StoreCard storeCard : card_store.get_inventory()) {
    		store_cards.add(storeCard);
		}
    	String msg = "PURCHASE:Do you want to buy any of the cards from the store? (you have " + current_monster.get_energy() + " energy) [#/-1]:" + store_cards.toString() + "\n";
        String answer = sendMessage(current_monster, msg);
        int buy = Integer.parseInt(answer);
        if(buy>0 && (current_monster.get_energy() >= store_cards.get(buy -1).get_cost())) { 
        	try {
        		current_monster.set_entergy(current_monster.get_energy() -  store_cards.get(buy -1).get_cost());
				current_monster.cards.add(card_store.buy(buy));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    public void check_dice(ArrayList<KTPUDice> dice, Monster current_monster) {
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
    	int hp = current_monster.get_hp() + num_hearts;
    	if (hp > current_monster.get_max_hp()) {
    		current_monster.set_hp(current_monster.get_max_hp());	
    	} else {
    		current_monster.set_hp(hp);	
    	}
//    	6b. 3 hearts = power-up
    	if (num_hearts >= 3) {
    		EvolutionCard evolutionCard = current_monster.draw_evolution_card();
			current_monster.cards.add(evolutionCard);
    	}
    	
//    	6c. 3 of a number = victory points
    	if (num_ones >= 0) {
    		current_monster.set_stars(current_monster.get_stars() + 1 + num_ones - 3);
    	}
    	if (num_twos >= 0) {
    		current_monster.set_stars(current_monster.get_stars() + 2 + num_twos - 3);
    	}
    	if (num_threes >= 0) {
    		current_monster.set_stars(current_monster.get_stars() + 3 + num_threes - 3);
    	}
    	
//    	6d. claws = attack (if in Tokyo attack everyone, else attack monster in Tokyo)
    	if (num_claws > 0) {
    		boolean enter_tokyo = true;
    		if (current_monster.get_in_tokyo()) {
    			for (Monster monster : players) {
    				attack(monster, num_claws);
				}
    		} else {
    			for (Monster monster : players) {
    				if (monster.get_in_tokyo()) {
						attack(monster, num_claws);
						
						// 6e. If you were outside, then the monster inside tokyo may decide to leave Tokyo
                        String answer = sendMessage(monster, "ATTACKED:You have " + 
                        		monster.get_hp() + " health left. Do you wish to leave Tokyo? [YES/NO]\n");
                        if(answer.equalsIgnoreCase("YES")) {
                        	monster.set_in_tokyo(false);
                        	enter_tokyo = true;
                        } else {
                        	enter_tokyo = false;
                        }
					}
				}
    			if (enter_tokyo) {
    				current_monster.set_in_tokyo(true);
    			}
    		}
    	}
    	
//    	6f. energy = energy tokens
    	current_monster.set_entergy(current_monster.get_energy() + num_energy);
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
    
    
    public ArrayList<KTPUDice> role_dice(Monster monster,int num_dice, int num_rerolls) {
//    	1. Roll 6 dice
    	ArrayList<KTPUDice> dice = new ArrayList<KTPUDice>();
        for (int i = 0; i < num_dice; i++) {
        	dice.add(new KTPUDice());
        }
        for (int i = 0; i < num_rerolls; i++) {
        	boolean done = reroll_dice(dice, monster);
        	if (done) {
        		return dice;
        	}
        }
        return dice;
    }
    
    public boolean reroll_dice(ArrayList<KTPUDice> dice, Monster monster) {
    	// 2. Decide which dice to keep
        String rolledDice = "ROLLED:You rolled:\t[1]\t[2]\t[3]\t[4]\t[5]\t[6]:";
        for(int allDice=0; allDice<dice.size(); allDice++) {
        	rolledDice+="\t[" + dice.get(allDice).value_as_string() + "]";
        }
        rolledDice += ":Choose which dice to reroll, separate with comma and in decending order (e.g. 5,4,1   0 to skip)\n";
        String[] reroll = sendMessage(monster, rolledDice).split(",");
        
        if(Integer.parseInt(reroll[0]) != 0) {
            for(int j=0; j<reroll.length; j++) {
            	dice.get(Integer.parseInt(reroll[j])-1).roll();
    		}
            return false;
        } else {
        	return true;
        }
    }
    
    public void print_stats(Monster monster) {
    	String statusUpdate = "You are " + monster.get_name() + " and it is your turn. Here are the stats";
    	statusUpdate += ":"+ monster.get_name() + (monster.get_in_tokyo()?" is in Tokyo ":" is not in Tokyo ");
        statusUpdate += "with " + monster.get_hp() + " health, " + monster.get_stars() + " stars, ";
        statusUpdate += monster.get_energy() + " energy, and owns the following cards:";
        statusUpdate += monster.cards.toString();
        for(int count=0; count<players.size(); count++) {
            statusUpdate += ":"+players.get(count).get_name() + (players.get(count).get_in_tokyo()?" is in Tokyo ":" is not in Tokyo ");
            statusUpdate += "with " + players.get(count).get_hp() + " health, " + players.get(count).get_stars() + " stars, ";
            statusUpdate += players.get(count).get_energy() + " energy, and owns the following cards:";
            statusUpdate += players.get(count).cards.toString();
        }
        sendMessage(monster, statusUpdate+"\n");
    }
    
    // TODO: Teachers Code
    private String sendMessage(Monster monster, String message) {
        String response = "";
        if(monster.connection != null) {
            try {
            	monster.outToClient.writeBytes(message);
                response = monster.inFromClient.readLine();
            } catch (Exception e) {}
        } else {
            String[] theMessage = message.split(":");
            for(int i=0; i<theMessage.length; i++) {System.out.println(theMessage[i].toString());}
            if(!(theMessage[0].equals("ATTACKED") || theMessage[0].equals("ROLLED") || theMessage[0].equals("PURCHASE")))
                System.out.println("Press [ENTER]");
            response = sc.nextLine();
        }
        return response;
    }
}