package main.java;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class GameActions {
    private String gameUUID;
    private String playerName;


    /**
     * Function: createGame(String, int)
     * Description: Calls API function for creating game to send HTTP request
     *              Called by main.userInterface
     * Does not return
     **/
    public void createGame(String pName, int numberOfPlayers) throws IOException {
        gameUUID = (UUID.randomUUID()).toString();
        playerName = pName;
        ClueLessUtils.makePost(gameUUID, pName, numberOfPlayers, "createGame");
    }

    public String getGameUUID() {
        return gameUUID;
    }

    /**
     * Function: joinGame(String, String)
     * Description: Calls API function for joining game to send HTTP request
     *              Called by main.userInterface
     * Does not return
     **/
    public void joinGame(String gUUID, String pName) throws IOException {
        playerName = pName;
        ClueLessUtils.makePost(gUUID, pName, 5, "joinGame");
    }

    // movePiece will be called by a player who wants to move their piece.
    public void movePiece(String gameID, String pName, String newLocation) throws IOException {
        System.out.println(pName + " has moved to " + newLocation);
        ClueLessUtils.makePost(gameID, pName, 4, "movePlayer");
    }

    // makeGuess will be called by a player when they want to make a guess
    public void makeGuess(String gameID, String suspectName,
                          String weaponName, String locationName) throws IOException {
        // called by main.userInterface
        StringBuilder response = ClueLessUtils.makeGet(gameID, "locationUpdate");
        JSONObject responseJSON = new JSONObject(response.toString());
        JSONObject locationUpdate = (JSONObject) responseJSON.get("positionUpdates");

        String currentLocation = locationUpdate.get(playerName).toString();

        String badLocations = "hallway"; // TODO: update the locations where you cannot make suggestion
        if (currentLocation.equals(badLocations)) {
            System.out.println("You cannot make a suggestion from the " + badLocations);
            return;
        } else {
            ClueLessUtils.makePost(gameID, playerName,5,"suggestion");
        }
    }

    //makeAccusation will be called when a player wants to make an accusation
    public void makeAccusation(String gameID, String suspectName,
                               String weaponName, String locationName) throws IOException {

        /********* Get location of user on the board *********/
        StringBuilder response = ClueLessUtils.makeGet(gameID, "locationUpdate");
        JSONObject responseJSON = new JSONObject(response.toString());
        JSONObject locationUpdate = (JSONObject) responseJSON.get("positionUpdates");

        String currentLocation = locationUpdate.get(playerName).toString();
        /********* Get location of user on the board *********/

        String badLocations = "hallway";
        if (currentLocation.equals(badLocations)) {
            System.out.println("You cannot make an accusation from the " + badLocations);
            return;
        } else {
            StringBuilder winningTriad = ClueLessUtils.makeGet(gameID,"makeAccusation");
            JSONObject triad = new JSONObject(winningTriad.toString());
            String suspect = triad.get("suspect").toString();
            String weapon = triad.get("weapon").toString();
            String location = triad.get("location").toString();

            if (suspect.equals(suspectName) &&
                weapon.equals(weaponName)   &&
                location.equals(locationName)) {
                System.out.println("You made the correct accusation!");
                System.out.println("The game is over");
                //Send msg to db that game is over
                ClueLessUtils.makePost(gameID, playerName, 5, "gameOver");
            } else {
                System.out.println("You made the WRONG accusation");
                System.out.println("Your game is over");
                //Send msg to db that this player lost
                ClueLessUtils.makePost(gameID, playerName, 5, "playerLost");

            }

        }
    }

    // endTurn will be called when the player wants to end their turn
    public void endTurn(String nextPlayer) {
        // called by main.userInterface
        ClueLessUtils.makePut("");
    }

    // respondToAccusation will be called when a player tries to respond to an accusation
    public void respondToAccusation(Optional<String> suspectName, Optional<String> weaponName,
                                    Optional<String> locationName) {
        ClueLessUtils.makePut("");
    }

    public void exitGame() {
        // figure out what to do here
    }
}
