package com.niklas.app;


import com.niklas.app.logic.KTPUGame;


public class App 
{
    public static void main( String[] args ) {
        KTPUGame game = new KTPUGame(3, 
            "./src/main/java/com/niklas/app/model/json/Monster.json", 
            "./src/main/java/com/niklas/app/model/json/StoreDeck.json");
         game.startGame();
        System.exit(0);
    }
}
