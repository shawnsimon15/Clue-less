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
        gameUUID = gUUID;
        playerName = pName;
        ClueLessUtils.makePost(gUUID, pName, 5, "joinGame");
    }

    // movePiece will be called by a player who wants to move their piece.
    public void movePiece(String gameID, String pName, String newLocation) throws IOException {
        ClueLessUtils.movePlayerPost(gameID, pName, newLocation);
    }

    // makeGuess will be called by a player when they want to make a guess
    public void makeGuess(String gameID, String pName, String suspectName,
                          String weaponName, String locationName) throws IOException {
        // called by main.userInterface
        ClueLessUtils.suggestionPost(gameID, pName, suspectName, weaponName, locationName);
    }

    //makeAccusation will be called when a player wants to make an accusation
    public String makeAccusation(String gameID, String pName, String suspectName,
                               String weaponName, String locationName) throws IOException {

        StringBuilder winningTriad = ClueLessUtils.makeGet(gameID, playerName, "makeAccusation");
        JSONObject triad = new JSONObject(winningTriad.toString());
        String suspect = triad.get("suspect").toString();
        String weapon = triad.get("weapon").toString();
        String location = triad.get("location").toString();

        if (suspect.equals(suspectName) &&
                weapon.equals(weaponName) &&
                location.equals(locationName)) {
            //Send msg to db that game is over
            ClueLessUtils.makePost(gameID, pName, 5, "gameOver");
            return "You made the correct accusation!\nThe game is over";
        }
        //Send msg to db that this player lost
        ClueLessUtils.makePost(gameID, pName, 5, "playerLost");
        return "You made the WRONG accusation\nYour game is over";

    }

    // endTurn will be called when the player wants to end their turn
    public void endTurn(String currentPlayer, String nextPlayer) throws IOException {
        playerName = currentPlayer;
        // called by main.userInterface
        ClueLessUtils.endTurnPost(gameUUID, playerName, nextPlayer);
    }
}
