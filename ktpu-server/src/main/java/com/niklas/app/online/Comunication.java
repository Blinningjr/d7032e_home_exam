package com.niklas.app.online;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import com.niklas.app.model.cards.CardStore;
import com.niklas.app.model.dice.KTPUDice;
import com.niklas.app.model.monsters.Monster;


public class Comunication {
    private Scanner sc = new Scanner(System.in);
    ServerSocket aSocket;
    
    public Comunication() {
    
    }
    
    // TODO: Teachers Code
    private String sendMessage(Client client, String message) {
        String response = "";
        if(client.get_connection() != null) {
            try {
            	client.get_out_to_client().writeBytes(message);
                response = client.get_in_from_client().readLine();
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

    public ArrayList<Client> initComunication(ArrayList<Monster> monsters) {
        //Server stuffs
    	ArrayList<Client> clients = new ArrayList<Client>();
        try {
        	aSocket = new ServerSocket(2048);
            //assume two online clients
            for(int onlineClient=0; onlineClient < monsters.size(); onlineClient++) {
                Socket connection_socket = aSocket.accept();
                BufferedReader in_from_client = new BufferedReader(new InputStreamReader(connection_socket.getInputStream()));
                DataOutputStream out_to_client = new DataOutputStream(connection_socket.getOutputStream());
                out_to_client.writeBytes("You are the monster: " + monsters.get(onlineClient).get_name() + "\n");

                clients.add( new Client(monsters.get(onlineClient), connection_socket, in_from_client, out_to_client));
                
                System.out.println("Connected to " + clients.get(onlineClient).getMonster().get_name());
            }
            return clients;
        } catch (Exception e) {
        	throw new Error("init_server_stuff failed");
        }
    }
    
    public void closeSocet() {
    	try {
			aSocket.close();
		} catch (IOException e) {
			throw new Error("Socket coulden't be closed error");
		}
    }

    public void sendAllStats(Client current_client, ArrayList<Client> clients){
        String statusUpdate = "You are " + current_client.getMonster().get_name() + " and it is your turn. Here are the stats";
        clients.add(0, current_client);
        for(int i=0; i< clients.size(); i++) {
            statusUpdate += ":"+clients.get(i).getMonster().get_name() + (clients.get(i).getMonster().getInTokyo()?" is in Tokyo ":" is not in Tokyo ");
            statusUpdate += "with " + clients.get(i).getMonster().getHp() + " health, " + clients.get(i).getMonster().getStars() + " stars, ";
            statusUpdate += clients.get(i).getMonster().get_energy() + " energy, and owns the following cards:";
            statusUpdate += clients.get(i).getMonster().cards_to_string();
        }
        clients.remove(0);
        this.sendMessage(current_client, statusUpdate + "\n");
    }
    
    
    public int[] sendRerollDice(ArrayList<KTPUDice> dice, Client client) {
    	// 2. Decide which dice to keep
        String rolledDice = "ROLLED:You rolled:\t[1]\t[2]\t[3]\t[4]\t[5]\t[6]:";
        for(int allDice=0; allDice<dice.size(); allDice++) {
        	rolledDice+="\t" + dice.get(allDice).value_as_string();
        }
        rolledDice += ":Choose which dice to reroll, separate with comma and in decending order (e.g. 5,4,1   0 to skip)\n";
        String[] reroll = sendMessage(client, rolledDice).split(",");
        int[] diceToReroll = new int[reroll.length];
        for (int i = 0; i < reroll.length; i++) {
        	diceToReroll[i] = Integer.parseInt(reroll[i]);
        }
        return diceToReroll;
    }

    public String sendRolledDice(ArrayList<KTPUDice> dice, Client client) {
    	// 2. Decide which dice to keep
        String rolledDice = "ROLLED:You rolled:\t[1]\t[2]\t[3]\t[4]\t[5]\t[6]:";
        for(int allDice=0; allDice<dice.size(); allDice++) {
        	rolledDice+="\t" + dice.get(allDice).value_as_string();
        }
        rolledDice += ":Press [ENTER]\n";
        return sendMessage(client, rolledDice);
    }
    
    public String send_leave_tokyo(Client client) {
    	return sendMessage(client, "ATTACKED:You have " + client.getMonster().getHp() 
    			+ " health left. Do you wish to leave Tokyo? [YES/NO]\n");
    }
    
    public String send_shopping(Client client, CardStore cardStore, int extraCost) {
        String extraCostString = "";
        if (extraCost != 0) {
            extraCostString = ":\tPrice is incrised with " + extraCost + "energy.:";
        }
        String msg = "PURCHASE:Do you want to buy any of the cards from the store? (you have " 
            + client.getMonster().get_energy() + " energy) [#/-1]:" + extraCostString + cardStore.inverntoryToString() + "\n";
    	return sendMessage(client, msg);
    }

    public void sendStarsWinner(Client currentClient, ArrayList<Client> clients) {
        String msg = "Victory: " + currentClient.getMonster().get_name() + " has won by stars.";
        sendMessage(currentClient, msg + "::You win!\n");
        for (int i = 0; i < clients.size(); i++) {
            sendMessage(clients.get(i), msg + "::You lose!\n");
        }
    }

    public void sendEliminationWinner(Client currentClient, ArrayList<Client> clients) {
        String msg = "Victory: " + currentClient.getMonster().get_name() + " has won by elimination.";
        sendMessage(currentClient, msg + "::You win!\n");
        for (int i = 0; i < clients.size(); i++) {
            sendMessage(clients.get(i), msg + "::You lose!\n");
        }
    }

    public void sendMonsterDied(Client client, ArrayList<Client> clients) {
        Monster monster = client.getMonster();
        String msg = monster.get_name() + " has died.";
        sendMessage(client, msg + "::You are dead.\n");
        for (int i = 0; i < clients.size(); i++) {
            sendMessage(clients.get(i), msg + "\n");
        }
    }
}
