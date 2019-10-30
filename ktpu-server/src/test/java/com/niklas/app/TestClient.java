package com.niklas.app;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;

public class TestClient extends Thread {

    private ArrayList<Integer> rerolled;
    private Scanner sc = new Scanner(System.in);
    private boolean flag;

    public TestClient() {
       
    }

    @Override
    public void run() {
        boolean bot = true;
        String name = "";
        Random rnd = ThreadLocalRandom.current();
        rerolled = new ArrayList<Integer>();
        flag = false;
        //Server stuffs
        try {
            Socket aSocket = new Socket("localhost", 2048);
            DataOutputStream outToServer = new DataOutputStream(aSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
            name = inFromServer.readLine();

            while(!flag) {
                String[] message = inFromServer.readLine().split(":");
                if(message[0].equalsIgnoreCase("VICTORY")) {
                    outToServer.writeBytes("Bye!\n");
                } else if(message[0].equalsIgnoreCase("ATTACKED")) {
                    if(bot) {
                        outToServer.writeBytes("YES\n");
                    }
                    else {
                        outToServer.writeBytes(sc.nextLine() + "\n");
                    }
                } else if(message[0].equalsIgnoreCase("ROLLED")) {
                    if(bot) {
                        rnd = ThreadLocalRandom.current();
                        int num1 = rnd.nextInt(6) + 1; 
                        rerolled.add(num1);
                        String reroll = num1+ "\n";                  
                        outToServer.writeBytes(reroll);// Some randomness at least
                    } else {
                        outToServer.writeBytes(sc.nextLine() + "\n");
                    }
                } else if(message[0].equalsIgnoreCase("PURCHASE")) {
                    if(bot) {
                        outToServer.writeBytes("-1\n");
                    }
                    else
                        outToServer.writeBytes(sc.nextLine() + "\n");
                } else {
                    if(bot) {
                        outToServer.writeBytes("OK\n");
                    }
                    else {
                        sc.nextLine();
                        outToServer.writeBytes("OK\n");
                    }
                }
            }
            outToServer.writeBytes("OK\n");
        } catch(Exception e) {}
    }

    public synchronized ArrayList<Integer> getReRolled() {
        return rerolled;
    }

    public synchronized void setFlag() {
        flag = true;
    }

}
