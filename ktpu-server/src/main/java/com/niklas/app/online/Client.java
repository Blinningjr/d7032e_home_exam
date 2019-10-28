package com.niklas.app.online;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;

import com.niklas.app.model.monsters.Monster;

public class Client {
	private Monster monster;
    private Socket connection = null;
    private BufferedReader in_from_client = null;
    private DataOutputStream out_to_client = null;

    public Client(Monster monster, Socket connection, BufferedReader in_from_client, DataOutputStream out_to_client) {
    	this.monster = monster;
        this.connection = connection;
        this.in_from_client = in_from_client;
        this.out_to_client = out_to_client;
    }
    
    public Monster get_monster() {
    	return monster;
    }
    
    public Socket get_connection() {
    	return connection;
    }

    public BufferedReader get_in_from_client() {
        return in_from_client;
    }


    public DataOutputStream get_out_to_client() {
        return out_to_client;
    }
}