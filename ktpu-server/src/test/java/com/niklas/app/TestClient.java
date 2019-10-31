package com.niklas.app;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TestClient extends Thread {
    private Scanner sc = new Scanner(System.in);
    private boolean flag;
    private boolean leaveTokyo;
    private String name;
    private String storeInput;
    private String rerollInput;
    private int numRerolls;

    public TestClient() {
       
    }

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

    public synchronized int getNumRerolls() {
        return numRerolls;
    }



    public synchronized void setFlag() {
        flag = true;
    }

    public synchronized void setLeaveTokyo(boolean leave) {
        leaveTokyo = leave;
    }

    public synchronized String getMonterName() {
        return name;
    }

    public synchronized void setStoreInput(String input) {
        storeInput = input;
    }

    public synchronized void setRerollInput(String input) {
        rerollInput = input;
    }
}
