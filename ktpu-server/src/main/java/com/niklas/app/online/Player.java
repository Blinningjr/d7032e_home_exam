package com.niklas.app.online;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;

import com.niklas.app.model.monsters.Monster;


/**
 * Stores all the players connection infromation and there monster.
 */
public class Player {
	private Monster monster;
    private Socket connection = null;
    private BufferedReader inFromClient = null;
    private DataOutputStream outToClient = null;


    /**
     * Creates a object with the players connection information and monster.
     * @param monster
     * @param connection
     * @param inFromClient
     * @param outToClient
     */
    public Player(Monster monster, Socket connection, BufferedReader inFromClient, DataOutputStream outToClient) {
    	this.monster = monster;
        this.connection = connection;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
    }
    

    /**
     * Gets the players monster.
     * @return the players monster
     */
    public Monster getMonster() {
    	return monster;
    }
    

    /**
     * Gets the players socket.
     * @return the players socket.
     */
    public Socket getConnection() {
    	return connection;
    }


    /**
     * Gets the players BufferedReader.
     * @return the players BufferedReader.
     */
    public BufferedReader getInFromClient() {
        return inFromClient;
    }


    /**
     * Gets the players DataOutputStream.
     * @return the players DataOutputStream
     */
    public DataOutputStream getOutToClient() {
        return outToClient;
    }
}
