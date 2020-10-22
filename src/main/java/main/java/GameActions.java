package main.java;
import java.util.Optional;
import java.util.UUID;

public class GameActions {
    private UUID gameUUID;
    private String playerName;

    public void createGame(String pName, int numberOfPlayers) {
        // called by Main.userInterface
        gameUUID = UUID.randomUUID();
        playerName = pName;
        ClueLessUtils.makePost(gameUUID, playerName, numberOfPlayers);
    }

    public void joinGame(UUID gameUUID, String playerName) {
        // called by Main.userInterface
        ClueLessUtils.makeGet("", "");
    }

    public void movePiece(String playerName, String oldLocation, String newLocation) {
        // called by Main.userInterface
        ClueLessUtils.makePut("", "");
    }

    public void makeGuess(String suspectName, String weaponName, String locationName) {
        // called by Main.userInterface
        ClueLessUtils.makePut("", "");
    }

    public void makeAccusation(String suspectName, String weaponName, String locationName) {
        // called by Main.userInterface
        ClueLessUtils.makePut("", "");
    }

    public void endTurn(String nextPlayer) {
        // called by Main.userInterface
        ClueLessUtils.makePut("", "");
    }

    public void respondToAccusation(Optional<String> suspectName, Optional<String> weaponName,
                                    Optional<String> locationName) {
        ClueLessUtils.makePut("", "");
    }

    public void exitGame() {
        // figure out what to do here
    }
}
