package com.niklas.app;


import java.io.*;
import java.net.*;
import java.util.Scanner;


/**
 * Is a KTPU test client so that test can be run.
 */
public class TestClient extends Thread {
    private Scanner sc = new Scanner(System.in);
    private boolean flag;
    private boolean leaveTokyo;
    private String name;
    private String storeInput;
    private String rerollInput;
    private int numRerolls;


    /**
     * Creats a test client.
     */
    public TestClient() {
       
    }

    /**
     * Starts the thread and starts the client.
     */
    @Override
    public void run() {
        boolean bot = true;
        name = "";
        flag = false;
        leaveTokyo = true;
        storeInput = "-1\n";
        numRerolls = 0;
        rerollInput = "0\n";
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
                        if (leaveTokyo) {
                            outToServer.writeBytes("YES\n");
                        } else {
                            outToServer.writeBytes("No\n");
                        }
                        
                    }
                    else {
                        outToServer.writeBytes(sc.nextLine() + "\n");
                    }
                } else if(message[0].equalsIgnoreCase("ROLLED")) {
                    if(bot) {
                        outToServer.writeBytes(rerollInput);// Some randomness at least
                        numRerolls +=1;
                    } else {
                        outToServer.writeBytes(sc.nextLine() + "\n");
                    }
                } else if(message[0].equalsIgnoreCase("PURCHASE")) {
                    if(bot) {
                        outToServer.writeBytes(storeInput);
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
            aSocket.close();
        } catch(Exception e) {}
    }


    /**
     * Gets the number of time this client has been askt to reroll and has rerolled.
     * @return the numver of rerolls.
     */
    public synchronized int getNumRerolls() {
        return numRerolls;
    }


    /**
     * Sets a flag to make the client stop.
     */
    public synchronized void setFlag() {
        flag = true;
    }


    /**
     * Sets if the client should leave if it is in tokyo.
     * @param leave is where the client should leav tokyo.
     */
    public synchronized void setLeaveTokyo(boolean leave) {
        leaveTokyo = leave;
    }


    /**
     * Gets the name of the monster the clietn has.
     * @return returns the monster name.
     */
    public synchronized String getMonterName() {
        return name;
    }


    /**
     * Sets the response the client will send if it is at the store.
     * @param input is the response.
     */
    public synchronized void setStoreInput(String input) {
        storeInput = input;
    }


    /**
     * Sets the respons the client will send when ask to reroll.
     * @param input is the response.
     */
    public synchronized void setRerollInput(String input) {
        rerollInput = input;
    }
}
