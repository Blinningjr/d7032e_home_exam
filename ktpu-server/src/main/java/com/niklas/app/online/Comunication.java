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


/**
 * Handles all the comunication between the server and clients and handels all the output of the game.
 */
public class Comunication {
    private Scanner sc = new Scanner(System.in);
    ServerSocket aSocket;
    

    /**
     * Creates a comunication object.
     */
    public Comunication() {
    
    }
    
    /**
     * Sends a message to a player and recives an answer.
     * @param player is the player the message is sent too.
     * @param message is the message that is sent.
     * @return is the player response.
     */
    private String sendMessage(Player player, String message) {
        String response = "";
        if(player.getConnection() != null) {
            try {
            	player.getOutToClient().writeBytes(message);
                response = player.getInFromClient().readLine();
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


    /**
     * Connects to the clients and creates client object with the clients information.
     * @param monsters is the list of monsters that the clients will be assign.
     * @return a list of players with there connecion information.
     */
    public ArrayList<Player> initComunication(ArrayList<Monster> monsters) {
        //Server stuffs
    	ArrayList<Player> clients = new ArrayList<Player>();
        try {
        	aSocket = new ServerSocket(2048);
            //assume two online clients
            for(int onlineClient=0; onlineClient < monsters.size(); onlineClient++) {
                Socket connectionSocket = aSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                outToClient.writeBytes("You are the monster: " + monsters.get(onlineClient).getName() + "\n");

                clients.add( new Player(monsters.get(onlineClient), connectionSocket, inFromClient, outToClient));
                
                System.out.println("Connected to " + clients.get(onlineClient).getMonster().getName());
            }
            System.out.println("initComunication Done");
            return clients;
        } catch (Exception e) {
        	throw new Error(e);
        }
    }
    

    /**
     * Closses the socker used for communicating with the players.
     */
    public void closeSocet() {
    	try {
            aSocket.close();
            System.out.println("socket closed");
		} catch (IOException e) {
			throw new Error("Socket coulden't be closed error");
		}
    }


    /**
     * Sends all stats to a player and a message saying that it is there turn.
     * @param currentPlayer is the player getting the message.
     * @param players are the rest of the players.
     */
    public void sendAllStats(Player currentPlayer, ArrayList<Player> players){
        String statusUpdate = "You are " + currentPlayer.getMonster().getName() + " and it is your turn. Here are the stats";
        players.add(0, currentPlayer);
        for(int i=0; i< players.size(); i++) {
            statusUpdate += ":"+players.get(i).getMonster().getName() + (players.get(i).getMonster().getInTokyo()?" is in Tokyo ":" is not in Tokyo ");
            statusUpdate += "with " + players.get(i).getMonster().getHp() + " health, " + players.get(i).getMonster().getStars() + " stars, ";
            statusUpdate += players.get(i).getMonster().getEnergy() + " energy, and owns the following cards:";
            statusUpdate += players.get(i).getMonster().cardsToString();
        }
        players.remove(0);
        this.sendMessage(currentPlayer, statusUpdate + "\n");

        System.out.println(currentPlayer.getMonster().getName() + " turn.");
    }
    
    
    /**
     * Sends a message that the player can reroll any of the dice and recives the answer.
     * @param dice are the dice the player has rolled.
     * @param player is the player getting the message.
     * @return is the answer form the player.
     */
    public int[] sendRerollDice(ArrayList<KTPUDice> dice, Player player) {
        String rolledDice = "ROLLED:You rolled:\t[1]\t[2]\t[3]\t[4]\t[5]\t[6]:";
        for(int allDice=0; allDice<dice.size(); allDice++) {
        	rolledDice+="\t" + dice.get(allDice).valueAsString();
        }
        rolledDice += ":Choose which dice to reroll, separate with comma and in decending order (e.g. 5,4,1   0 to skip)\n";
        String[] reroll = sendMessage(player, rolledDice).split(",");
        int[] diceToReroll = new int[reroll.length];
        for (int i = 0; i < reroll.length; i++) {
        	diceToReroll[i] = Integer.parseInt(reroll[i]);
        }
        return diceToReroll;
    }


    /**
     * Sends the final result of the rolled dice.
     * @param dice the dice that the player rolled.
     * @param player the player reciving the message.
     * @return the player answer.
     */
    public String sendRolledDice(ArrayList<KTPUDice> dice, Player player) {
        String rolledDice = "ROLLED:You rolled:\t[1]\t[2]\t[3]\t[4]\t[5]\t[6]:";
        for(int allDice=0; allDice<dice.size(); allDice++) {
        	rolledDice+="\t" + dice.get(allDice).valueAsString();
        }
        rolledDice += ":Press [ENTER]\n";
        return sendMessage(player, rolledDice);
    }
    

    /**
     * Sends message that ask if they wanna leav tokyo.
     * @param player the player reciving the message.
     * @return the players answer.
     */
    public String sendLeaveTokyo(Player player) {
    	return sendMessage(player, "ATTACKED:You have " + player.getMonster().getHp() 
    			+ " health left. Do you wish to leave Tokyo? [YES/NO]\n");
    }
    

    /**
     * Sends message asking player what they want to do in the store.
     * @param player the player reciving the message.
     * @param cardStore the cards store they are at.
     * @param extraCost the extra cost the player has to play for each card.
     * @return 
     */
    public String sendShopping(Player player, CardStore cardStore, int extraCost) {
        String extraCostString = "";
        if (extraCost != 0) {
            extraCostString = ":\tPrice is increased with " + extraCost + "energy.:";
        }
        String msg = "PURCHASE:Do you want to buy any of the cards from the store? (you have " 
            + player.getMonster().getEnergy() + " energy) [#/-1]:" + extraCostString + cardStore.inverntoryToString() 
            + "[0] 2 Energy too reset store.\n";

        return sendMessage(player, msg);
    }


    /**
     * Sends message to all players saying who has won by stars.
     * @param currentPlayer the player that has won.
     * @param players the rest of the players in the game.
     */
    public void sendStarsWinner(Player currentPlayer, ArrayList<Player> players) {
        String msg = "Victory: " + currentPlayer.getMonster().getName() + " has won by stars.";
        sendMessage(currentPlayer, msg + "::You win!\n");
        for (int i = 0; i < players.size(); i++) {
            sendMessage(players.get(i), msg + "::You lose!\n");
        }
        System.out.println("Game Over");
    }


    /**
     * Sends message to all players saying who has won by elimination.
     * @param currentPlayer the player that has won.
     * @param players the rest of the players in the game.
     */
    public void sendEliminationWinner(Player currentPlayer, ArrayList<Player> players) {
        String msg = "Victory: " + currentPlayer.getMonster().getName() + " has won by elimination.";
        sendMessage(currentPlayer, msg + "::You win!\n");
        for (int i = 0; i < players.size(); i++) {
            sendMessage(players.get(i), msg + "::You lose!\n");
        }
        System.out.println("Game Over");
    }


    /**
     * Sends message saying which monster that has died.
     * @param player the player whos monster has died.
     * @param players the rest of the players in the game.
     */
    public void sendMonsterDied(Player player, ArrayList<Player> players) {
        Monster monster = player.getMonster();
        String msg = monster.getName() + " has died.";
        sendMessage(player, msg + "::You are dead.\n");
        for (int i = 0; i < players.size(); i++) {
            sendMessage(players.get(i), msg + "\n");
        }
        System.out.println(msg);
    }
}
