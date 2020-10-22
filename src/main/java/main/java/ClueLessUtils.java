package main.java;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClueLessUtils {
    public static String makePost(UUID gameUUID, String playerName, int numberOfPlayers){
        // make http request
        return "";
    }

    public static String makePut(String jsonMessage, String putURL) {
        // make http request
        return "";
    }

    public static String makeGet(String jsonMessage, String putURL) {
        // make http request
        parseMessages(jsonMessage); // what is received from GET call
        return "";
    }

    public static String parseMessages(String jsonMessage) {
        //
        JsonParser parser = new JsonParser();
        JsonObject event = (JsonObject) parser.parse(jsonMessage);

        if(event.get("messageType").toString() == "playerTurnUpdate") {
            // get playerName from currentTurn
            // Main.updateGameBoard(playerName);
        }
        else if(event.get("messageType").toString() == "locationUpdate") {
            // get location from playerName1
            // get location from playerName2
            // Main.updateGameBoard(playerName ,);
        }
        else if(event.get("messageType").toString() == "suggestionMade") {
            // get playerName from playerWhoSuggested
            // get suspect from cardSuggested
            // get weapon from cardSuggested
            // get location from cardSuggested
            // get UUID from suggestionID
        }
        else if(event.get("messageType").toString() == "contradictSuggestion") {
            // get playerName from playerWhoSuggested
            // get UUID from suggestionID
        }
        else if(event.get("messageType").toString() == "startGame") {
            // get players from activePlayers
            // get cards from assignedCards
            // Main.startGame(pList); // playerList from http request
        }
        else {
            System.out.println("Did not recognize JSON");
        }


        return "";
    }
}
