package com.niklas.app;

import com.niklas.app.logic.KTPUGame;

public class TestServer extends Thread {
    private KTPUGame tKpuGame;
    private int numPlayers;
    private String monsterFilepath;
    private String storeCardFilepath;

    public TestServer(int numPlayers, String monsterFilepath, String storeCardFilepath) {
        this.numPlayers = numPlayers;
        this.monsterFilepath = monsterFilepath;
        this.storeCardFilepath = storeCardFilepath;
        tKpuGame = null;
    }

    @Override
    public void run() {
        tKpuGame = new KTPUGame(numPlayers, monsterFilepath, storeCardFilepath);
    }

    public synchronized KTPUGame getKTPUGame() {
        return tKpuGame;
    }
}