package com.niklas.app.model.json;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.niklas.app.model.cards.Card;
import com.niklas.app.model.cards.Effect;
import com.niklas.app.model.cards.EvolutionCard;
import com.niklas.app.model.cards.EvolutionDeck;
import com.niklas.app.model.cards.StoreCard;
import com.niklas.app.model.cards.StoreDeck;
import com.niklas.app.model.monsters.Monster;

import java.util.ArrayList;


/**
 * A class for reading in monsters and cards from json files.
 */
public class ReadJson {

	/**
	 * Creates a ReadJson object.
	 */
    public ReadJson() {}

    
    /**
     * Parse cards from json file.
     * @param filepath is the file path to the json file.
     * @return a unshuffled deck with all cards from the json file.
     */
    public StoreDeck read_store_deck_from_json(String filepath) {
        
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(filepath))
        {
            Object obj = jsonParser.parse(reader);
            JSONArray card_list = (JSONArray) obj;

            ArrayList<StoreCard> cards = new ArrayList<StoreCard>();
            for (Object Card : card_list) {
                cards.addAll(parse_store_card_from_json( (JSONObject) Card));
            }
            return new StoreDeck(cards, new ArrayList<StoreCard>());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return new StoreDeck(new ArrayList<StoreCard>(), new ArrayList<StoreCard>());
    }


    /**
     * Parses a StoreCard from a json object.
     * @param card is a JSONObject with the StoreCard infromation.
     * @return one or multiple cards of the same type.
     */
    public ArrayList<StoreCard> parse_store_card_from_json(JSONObject card) {
        JSONObject store_card_object = (JSONObject) card.get("store_card");
        
        String name = (String) store_card_object.get("name");   
        String description = (String) store_card_object.get("description");  
        String _effect_string = (String) store_card_object.get("effect");
        int cost = Integer.valueOf( (String) store_card_object.get("cost")); 
        String type = (String) store_card_object.get("type");
        int quantity = Integer.valueOf( (String) store_card_object.get("quantity")); 

        ArrayList<StoreCard> cards = new ArrayList<StoreCard>();
        for (int i = 0; i < quantity; i++) {
            Effect effect = new Effect(); 
            cards.add(new StoreCard(name, description, effect, cost, type));
        }

        return cards;
    }


    /**
     * Parses Monster form a json file.
     * @param filepath is the file path to the json file.
     * @return a list of monster object read from the json file.
     */
    public ArrayList<Monster> read_monsters_from_json(String filepath) {
        
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(filepath))
        {
            Object obj = jsonParser.parse(reader);
            JSONArray monster_list = (JSONArray) obj;

            ArrayList<Monster> monsters = new ArrayList<Monster>();
            for (Object monster : monster_list) {
                monsters.add(parse_monster_object( (JSONObject) monster));
            }
            return monsters;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return new ArrayList<Monster>();
    }
 
    
    /**
     * Parses a monster from a JSONObject and creates a Monster object.
     * @param monster is a JSONObject with the Monster information.
     * @return
     */
    private Monster parse_monster_object(JSONObject monster)
    {
        JSONObject monster_object = (JSONObject) monster.get("monster");
         
        String name = (String) monster_object.get("name");   
        int max_hp = Integer.valueOf( (String) monster_object.get("max_hp")); 
        int hp = Integer.valueOf( (String) monster_object.get("hp")); 
        int energy = Integer.valueOf( (String) monster_object.get("energy")); 
        int stars = Integer.valueOf( (String) monster_object.get("stars")); 

        ArrayList<EvolutionCard> e_cards = new ArrayList<EvolutionCard>();
        JSONArray e_deck = (JSONArray) monster_object.get("evolution_deck");
        for (Object card : e_deck) {
            e_cards.add(parse_evolution_card( (JSONObject) card));
        }
        EvolutionDeck evolution_deck = new EvolutionDeck(e_cards, new ArrayList<EvolutionCard>());
        evolution_deck.shuffle();
        return new Monster(name, max_hp, hp, energy, stars, false, new ArrayList<Card>(), evolution_deck);
    }

    /**
     * Parses evolution card from a JSONObject and creates a EvolutionCard object.
     * @param evolution_card is a JSONObject with the evolution card information
     * @return a EvolutionCard object that has the attributes read from the parameter evolution_card.
     */
    private EvolutionCard parse_evolution_card(JSONObject evolution_card)
    {
        JSONObject evolution_card_object = (JSONObject) evolution_card.get("evolution_card");
         
        String name = (String) evolution_card_object.get("name");   
        String description = (String) evolution_card_object.get("description");  
        String _effect_string = (String) evolution_card_object.get("effect");  
        String monster_name = (String) evolution_card_object.get("monster_name");  
        String monster_type = (String) evolution_card_object.get("monster_type");  
        String duration = (String) evolution_card_object.get("duration");  

        Effect effect = new Effect(); 
        return new EvolutionCard(name, description, effect, monster_name, monster_type, duration);
    }
}
