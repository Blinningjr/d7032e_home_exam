package com.niklas.app.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

import com.niklas.app.model.cards.CardStore;
import com.niklas.app.model.json.ReadJson;
import com.niklas.app.model.monsters.Monster;

public class KTPUGame {
    ArrayList<Monster> players;
    CardStore card_store;


    public KTPUGame(int tot_num_players, int num_bots, String monster_filepath, String store_card_filepath) {
        if (num_bots > tot_num_players) {
            throw new Error("The number of bot needs to be fewer then the total number of players");
        }
        
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

        card_store = new CardStore(json_reader.read_deck_from_json(store_card_filepath));

        init_server_stuff(tot_num_players, num_bots);
        Collections.shuffle(players);
        System.out.println("init done");
        
        game_loop();
    }

    private void init_server_stuff(int tot_num_players, int num_bots) {
        //Server stuffs
        try {
            ServerSocket aSocket = new ServerSocket(2048);
            for(int onlineClient=0; onlineClient<tot_num_players - num_bots; onlineClient++) {
                Socket connectionSocket = aSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                outToClient.writeBytes("You are the monster: " + players.get(onlineClient).get_name() + "\n");
                players.get(onlineClient).connection = connectionSocket;
                players.get(onlineClient).inFromClient = inFromClient;
                players.get(onlineClient).outToClient = outToClient;
                System.out.println("Connected to " + players.get(onlineClient).get_name());
            }
        } catch (Exception e) {}
    }
    
    public void game_loop() {
    	boolean is_game_on = true;
    	while (is_game_on) {
    		
    	}
    }
}