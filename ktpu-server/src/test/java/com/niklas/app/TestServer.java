package com.niklas.app;


import com.niklas.app.logic.KTPUGame;


/**
 * Is a class fpr starting a KTPUGame in a diffrent thread so it is possiblbe to test the constructor of KTPUGame.
 */
public class TestServer extends Thread {
    private KTPUGame tKpuGame;
    private int numPlayers;
    private String monsterFilepath;
    private String storeCardFilepath;


    /**
     * Creates a test server object.
     * @param numPlayers is the number of players that are going to play the match.
     * @param monsterFilepath is the path to a json file with the monster information.
     * @param storeCardFilepath is the path to a json file with the StoreCard information.
     */
    public TestServer(int numPlayers, String monsterFilepath, String storeCardFilepath) {
        this.numPlayers = numPlayers;
        this.monsterFilepath = monsterFilepath;
        this.storeCardFilepath = storeCardFilepath;
        tKpuGame = null;
    }


    /**
     * Run the thread. This will call the constructor of KTPUGame .
     */
    @Override
    public void run() {
        tKpuGame = new KTPUGame(numPlayers, monsterFilepath, storeCardFilepath);
    }


    /**
     * Gets the KTPUGame that the server starts.
     * @return is the created KTPUGame object.
     */
    public synchronized KTPUGame getKTPUGame() {
        return tKpuGame;
    }
}
