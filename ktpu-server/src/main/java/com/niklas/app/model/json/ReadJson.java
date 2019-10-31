package com.niklas.app.model.json;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    public StoreDeck readStoreDeckFromJson(String filepath) {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filepath)) {
            Object obj = jsonParser.parse(reader);
            JSONArray cardList = (JSONArray) obj;

            ArrayList<StoreCard> cards = new ArrayList<StoreCard>();
            for (Object Card : cardList) {
                cards.addAll(parseStoreCardFromJson( (JSONObject) Card));
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
    public ArrayList<StoreCard> parseStoreCardFromJson(JSONObject card) {
        JSONObject storeCardObject = (JSONObject) card.get("storeCard");
        
        String name = (String) storeCardObject.get("name");   
        String description = (String) storeCardObject.get("description");  
        int cost = Integer.valueOf( (String) storeCardObject.get("cost")); 
        String type = (String) storeCardObject.get("type");
        int quantity = Integer.valueOf( (String) storeCardObject.get("quantity")); 

        ArrayList<StoreCard> cards = new ArrayList<StoreCard>();
        for (int i = 0; i < quantity; i++) {
            Effect effect = pareEffect(storeCardObject); 
            cards.add(new StoreCard(name, description, effect, cost, type));
        }

        return cards;
    }


    /**
     * Parses Monster form a json file.
     * @param filepath is the file path to the json file.
     * @return a list of monster object read from the json file.
     */
    public ArrayList<Monster> readMonstersFromJson(String filepath) {
        
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(filepath))
        {
            Object obj = jsonParser.parse(reader);
            JSONArray monsterList = (JSONArray) obj;

            ArrayList<Monster> monsters = new ArrayList<Monster>();
            for (Object monster : monsterList) {
                monsters.add(parseMonsterObject( (JSONObject) monster));
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
     * @return a monster with the attributes read from the JSONObject.
     */
    private Monster parseMonsterObject(JSONObject monster) {
        JSONObject monsterObject = (JSONObject) monster.get("monster");
         
        String name = (String) monsterObject.get("name");   
        int maxHp = Integer.valueOf( (String) monsterObject.get("maxHp")); 
        int hp = Integer.valueOf( (String) monsterObject.get("hp")); 
        int energy = Integer.valueOf( (String) monsterObject.get("energy")); 
        int stars = Integer.valueOf( (String) monsterObject.get("stars")); 

        ArrayList<EvolutionCard> eCards = new ArrayList<EvolutionCard>();
        JSONArray eEeck = (JSONArray) monsterObject.get("evolutionDeck");
        for (Object card : eEeck) {
            eCards.addAll(parseEvolutionCard( (JSONObject) card));
        }
        EvolutionDeck evolutionDeck = new EvolutionDeck(eCards, new ArrayList<EvolutionCard>());
        evolutionDeck.shuffle();
        return new Monster(name, maxHp, hp, energy, stars, false, new ArrayList<StoreCard>(), evolutionDeck);
    }


    /**
     * Parses evolution card from a JSONObject and creates a EvolutionCard object.
     * @param evolutionCard is a JSONObject with the evolution card information
     * @return a EvolutionCard object that has the attributes read from the parameter evolutionCard.
     */
    private ArrayList<EvolutionCard> parseEvolutionCard(JSONObject evolutionCard) {
        JSONObject evolutionCardObject = (JSONObject) evolutionCard.get("evolutionCard");

        String name = (String) evolutionCardObject.get("name");
        String description = (String) evolutionCardObject.get("description");
        String monsterName = (String) evolutionCardObject.get("monsterName");
        String monsterType = (String) evolutionCardObject.get("monsterType");
        String duration = (String) evolutionCardObject.get("duration");
        int quantity = Integer.valueOf( (String) evolutionCardObject.get("quantity"));

        ArrayList<EvolutionCard> cards = new ArrayList<EvolutionCard>();
        for (int i = 0; i < quantity; i++) {
            Effect effect = pareEffect(evolutionCardObject);
            cards.add(new EvolutionCard(name, description, effect, monsterName, monsterType, duration));
        }
        return cards;
    }


    /**
     * Parsers effect form a JSONObject and creates a Effect object.
     * @param effect is a JSONObject with the effect infromation.
     * @return a Effect object that has the attributes read from the effect param.
     */
    private Effect pareEffect(JSONObject effect){
    	JSONObject effectObject = (JSONObject) effect.get("effect");

        String activation = (String) effectObject.get("activation");
        String action = (String) effectObject.get("action");
        Effect e = new Effect(activation, action);

        try {
			e.addDamage(Integer.valueOf( (String) effectObject.get("addedDamage")));
		} catch (NumberFormatException error) {
			
		}
        try {
			e.addArmor(Integer.valueOf( (String) effectObject.get("armor")));
		} catch (NumberFormatException error) {
			
		}
        try {
			e.addCost(Integer.valueOf( (String) effectObject.get("addedCost")));
		} catch (NumberFormatException error) {
			
		}
        try {
			e.addStars(Integer.valueOf( (String) effectObject.get("addedStars")));
		} catch (NumberFormatException error) {
			
		}
        try {
			e.addToMaxHp(Integer.valueOf( (String) effectObject.get("addedMaxHp")));
		} catch (NumberFormatException error) {
			
		}
        try {
			e.addHp(Integer.valueOf( (String) effectObject.get("addedHp")));
		} catch (NumberFormatException error) {
			
		}
        try {
			e.addEnergy(Integer.valueOf( (String) effectObject.get("addedEnergy")));
		} catch (NumberFormatException error) {
			
		}

        return e;
    }
}
