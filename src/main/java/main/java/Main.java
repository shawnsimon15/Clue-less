package main.java;

//import javafx.util.Pair;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    private GameActions gameActions;
    private AutoMessageCheck autoMessageCheck;
    private static GameStatus gameStatus;
    private String playerName;


    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Starting ClueLess!");
        URL myUrl = new URL("https://jroe630mfb.execute-api.us-east-2.amazonaws.com/Test/game");

        /*ClueLessUtils.makePost(555555, "JakeFromStateFarm", 5);
        //autoMessageCheck = new AutoMessageCheck();
        gameStatus = new GameStatus();

        //userInterface();*/
        /********** Create Game **********/
        ClueLessUtils.makePost(444444, "Rick", 5, "createGame");
        ClueLessUtils.makePost(444444, "Rick", 5, "disproveSuggestion");
        ClueLessUtils.makePost(444444, "Rick", 5, "endTurn");
        ClueLessUtils.makePost(444444, "Steve", 5, "joinGame");
        ClueLessUtils.makePost(444444, "Rick", 5, "movePlayer");
        ClueLessUtils.makePost(444444, "Rick", 5, "passSuggestion");
        ClueLessUtils.makePost(444444, "Rick", 5, "suggestion");

        ClueLessUtils.makeGet("", "makeAccusation");
        ClueLessUtils.makeGet("", "startGame");
        ClueLessUtils.makeGet("", "turnUpdate");
        ClueLessUtils.makeGet("", "locationUpdate");
        ClueLessUtils.makeGet("", "suggestion");
        ClueLessUtils.makeGet("", "contradict");
    }

    public void userInterface() throws IOException {
        // Create GUI
        Scanner input = new Scanner(System.in);
        System.out.println("Would you like to Create or Join a game? ");

        String gameType = input.nextLine();

        if (gameType == "Create Game") {
            System.out.println("How many players will be in the game (including you)? ");
            String numberOfPlayers = input.nextLine();

            gameActions.createGame(playerName, Integer.parseInt(numberOfPlayers));
        }
        else {
            // Joining a Game
            System.out.println("What is the game UUID? ");
            String uuidString = input.nextLine();
            UUID uuid = UUID.fromString(uuidString);
            gameActions.joinGame(uuid, playerName);
        }

        String playerName = "Bob";
        String oldLocation = "Room1";
        String newLocation = "Room2";
        gameActions.movePiece(playerName, oldLocation, newLocation);

        String susName1 = "sus";
        String weaponName1 = "paper";
        String locationName1 = "Room3";
        gameActions.makeGuess(susName1, weaponName1, locationName1);

        String susName2 = "Eh";
        String weaponName2 = "leaf";
        String locationName2 = "Room4";
        gameActions.makeAccusation(susName2, weaponName2, locationName2);

        String nextPlayer = "Linda";
        gameActions.endTurn(nextPlayer);

        String susName3 = "verySus";
        String weaponName3 = "air";
        String locationName3 = "Room5";
        gameActions.respondToAccusation(Optional.of(susName3),
                Optional.of(weaponName3), Optional.of(locationName3));

    }

    public static void startGame(List<PlayerStatus> playerList){
        // Start the game
        gameStatus.setActivePlayerList(playerList);
    }

    /*public static void updateGameBoard(List<Pair<String, String>> playerPositionUpdates,
                                String currentPlayerTurn,
                                Optional<String> notificationType,
                                Optional<String> notificationMessage) {
        // Display player movement
    }*/
}
