package com.niklas.app;

import java.util.ArrayList;

import com.niklas.app.controller.KTPUGame;
import com.niklas.app.model.cards.Deck;
import com.niklas.app.model.json.ReadJson;
import com.niklas.app.model.monsters.Monster;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        KTPUGame game = new KTPUGame(3, 0, 
            "./src/main/java/com/niklas/app/model/json/Monster.json", 
            "./src/main/java/com/niklas/app/model/json/StoreDeck.json");
        // ReadJson r = new ReadJson();
        // ArrayList<Monster> ms = r.read_monsters_from_json("./src/main/java/com/niklas/app/model/json/Monster.json");
        // for (Monster monster : ms) {
		// 	System.out.println(monster.get_name());
		// }
        // System.out.println("---------------------------------------");
        // Deck deck = r.read_deck_from_json("./src/main/java/com/niklas/app/model/json/StoreDeck.json");
        // deck.shuffle();
        // for (int i = 0; i < 16; i++) {
		// 	System.out.println(deck.draw_card().get_name());
		// }
    }
}
