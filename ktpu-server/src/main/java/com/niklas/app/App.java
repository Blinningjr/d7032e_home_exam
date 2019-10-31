package com.niklas.app;


import com.niklas.app.logic.KTPUGame;


/**
 * Handels starting the game and geting the inputs form console.
 */
public class App 
{
    /**
     * Starts a game of KTPU.
     * @param args
     */
    public static void main( String[] args ) {
        KTPUGame game = new KTPUGame(3, 
            "./src/main/java/com/niklas/app/model/json/Monster.json", 
            "./src/main/java/com/niklas/app/model/json/StoreDeck.json");
         game.startGame();
        System.exit(0);
    }
}
