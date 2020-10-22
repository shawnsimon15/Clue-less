package main.java;

import javafx.util.Pair;

import java.util.List;
import java.util.Optional;


public class Main {

    private GameActions gameActions;
    private AutoMessageCheck autoMessageCheck;
    private static GameStatus gameStatus;
    private String playerName;


    public void Main(String[] args) {
        System.out.println("Starting ClueLess!");

        gameActions = new GameActions();
        autoMessageCheck = new AutoMessageCheck();
        gameStatus = new GameStatus();

        userInterface();
    }

    public void userInterface(){
        // Create GUI
        /*gameActions.createGame(playerName, numberOfPlayers);
        gameActions.joinGame(uuid, playerName);
        gameActions.movePiece(playerName, oldLocation, newLocation);
        gameActions.makeGuess(susName, weaponName, locationName);
        gameActions.makeAccusation(susName, weaponName, locationName);
        gameActions.endTurn(nextPlayer);
        gameActions.respondToAccusation(susName, weaponName, locationName);*/

    }

    public static void startGame(List<PlayerStatus> playerList){
        // Start the game
        gameStatus.setActivePlayerList(playerList);
    }

    public static void updateGameBoard(List<Pair<String, String>> playerPositionUpdates,
                                String currentPlayerTurn,
                                Optional<String> notificationType,
                                Optional<String> notificationMessage) {
        // Display player movement
    }
}
