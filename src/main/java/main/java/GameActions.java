package main.java;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

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

    public void movePiece(String pName, String oldLocation, String newLocation) {
        // called by main.userInterface
        ClueLessUtils.makePut("");
    }

    public void makeGuess(String suspectName, String weaponName, String locationName) {
        // called by main.userInterface
        ClueLessUtils.makePut("");
    }

    public void makeAccusation(String suspectName, String weaponName, String locationName) {
        // called by main.userInterface
        ClueLessUtils.makePut("");
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
