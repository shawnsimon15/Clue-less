package main.java;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class GameActions {
    private UUID gameUUID;
    private String playerName;

    public void createGame(String pName, int numberOfPlayers) throws IOException {
        // called by main.userInterface
        gameUUID = UUID.randomUUID();
        int ID = 123456;
        playerName = pName;
        //ClueLessUtils.makePost(ID, playerName, numberOfPlayers);
    }

    public void joinGame(UUID gUUID, String pName) {
        // called by main.userInterface
        gameUUID = gUUID;
        playerName = pName;
        //ClueLessUtils.makeGet("");
    }

    public void movePiece(String gameID, String pName, String oldLocation, String newLocation) throws IOException {
        // called by main.userInterface
        StringBuilder response = ClueLessUtils.makeGet(gameID, "locationUpdate");
        JSONObject responseJSON = new JSONObject(response.toString());
        JSONObject locationUpdate = (JSONObject) responseJSON.get("positionUpdates");

        ArrayList<String> locations =new ArrayList<String>();
        Iterator<String> keys = locationUpdate.keys();

        for(Iterator iterator = locationUpdate.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            locations.add(locationUpdate.get(key).toString());
        }

        boolean goodToMove = true;
        for(int i = 0; i < locations.size(); i++) {
            if (newLocation.equals(locations.get(i))) {
                goodToMove = false;
            }
        }

        if (goodToMove) {
            System.out.println(pName + " has moved to " + newLocation);
            ClueLessUtils.makePost(12348, pName, 4, "movePlayer");

        } else {
            System.out.println(pName + " cannot move to " + newLocation);
        }
    }

    public void makeGuess(String gameID, String suspectName, String weaponName, String locationName) throws IOException {
        // called by main.userInterface
        StringBuilder response = ClueLessUtils.makeGet(gameID, "locationUpdate");
        JSONObject responseJSON = new JSONObject(response.toString());
        JSONObject locationUpdate = (JSONObject) responseJSON.get("positionUpdates");

        // need to set playerName
        playerName = "Nuke";
        String currentLocation = locationUpdate.get(playerName).toString();

        String badLocations = "hallway";
        if (currentLocation.equals(badLocations)) {
            System.out.println("You cannot make a suggestion from the " + badLocations);
            return;
        } else {
            ClueLessUtils.makePost(12348, playerName,5,"suggestion");
        }
    }

    public void makeAccusation(String gameID, String suspectName, String weaponName, String locationName) throws IOException {
        StringBuilder response = ClueLessUtils.makeGet(gameID, "locationUpdate");
        JSONObject responseJSON = new JSONObject(response.toString());
        JSONObject locationUpdate = (JSONObject) responseJSON.get("positionUpdates");

        // need to set playerName
        playerName = "Sane";
        String currentLocation = locationUpdate.get(playerName).toString();

        String badLocations = "hallway";
        if (currentLocation.equals(badLocations)) {
            System.out.println("You cannot make an accusation from the " + badLocations);
            return;
        } else {
            StringBuilder winningTriad = ClueLessUtils.makeGet("123458","makeAccusation");
            JSONObject triad = new JSONObject(winningTriad.toString());
            String suspect = triad.get("suspect").toString();
            String weapon = triad.get("weapon").toString();
            String location = triad.get("location").toString();

            if (suspect.equals(suspectName) &&
                weapon.equals(weaponName)   &&
                location.equals(locationName)) {
                System.out.println("You made the correct accusation!");
                System.out.println("The game is over");
                // send msg to db that game is over
            } else {
                System.out.println("You made the WRONG accusation");
                System.out.println("Your game is over");
                // send msg to db that this player lost
            }

        }
    }

    public void endTurn(String nextPlayer) {
        // called by main.userInterface
        ClueLessUtils.makePut("");
    }

    public void respondToAccusation(Optional<String> suspectName, Optional<String> weaponName,
                                    Optional<String> locationName) {
        ClueLessUtils.makePut("");
    }

    public void exitGame() {
        // figure out what to do here
    }
}
