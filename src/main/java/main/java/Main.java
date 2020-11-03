package main.java;

//import javafx.util.Pair;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


public class Main {

    private static GameActions gameActions;
    private static AutoMessageCheck autoMessageCheck;
    private static GameStatus gameStatus;
    private static String playerName;


    // main will run be the application run by a player
    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Welcome to ClueLess!");

        autoMessageCheck = new AutoMessageCheck();
        gameStatus = new GameStatus();
        gameActions = new GameActions();
        ClueLessConstants constants = new ClueLessConstants(); // Calls constructor to initialize constants

        userInterface();
        /********** Testing db **********
        ClueLessUtils.makePost("444444", "Rick", 5, "disproveSuggestion");
        ClueLessUtils.makePost("444444", "Rick", 5, "endTurn");

        ClueLessUtils.makePost("444444", "Rick", 5, "passSuggestion");
        ClueLessUtils.makePost("444444", "Rick", 5, "suggestion");

        gameActions.createGame("Steve", 4);
        gameActions.joinGame("444444", "Rock");
        gameActions.joinGame("123458", "Rock");

        System.out.println(ClueLessUtils.makeGet("123458", "makeAccusation"));
        System.out.println(ClueLessUtils.makeGet("123458", "startGame"));
        System.out.println(ClueLessUtils.makeGet("123458", "turnUpdate"));
        //ClueLessUtils.makeGet("", "locationUpdate");
        gameActions.movePiece("123458", "Luke", "location1", "Room1");
        gameActions.makeGuess("123458","Luke", "feather", "location1");
        gameActions.makeAccusation("123458","Luke", "feather", "location1");
        gameActions.makeAccusation("123458","Person", "Weapon", "Location");

        System.out.println(ClueLessUtils.makeGet("123458", "suggestion"));
        System.out.println(ClueLessUtils.makeGet("123458", "contradict"));
         ********** Testing db **********/
    }

    /**
     * Function: userInterface()
     * Description: Interacts with user for gameplay experience (test-based or GUI)
     * Does not return
     **/
    public static void userInterface() throws IOException {
        // Create GUI
        Scanner input = new Scanner(System.in);
        System.out.println("Would you like to Create or Join a game? ");

        String gameType = input.nextLine();

        int suspectIndex = (int) ((Math.random() * (5)));
        playerName = ClueLessConstants.SUSPECT_LIST.get(suspectIndex);
        System.out.println("You will be: " + playerName);

        /********* Creating/Joining Game *********/
        if (gameType.toLowerCase().equals("create game") ||
                gameType.toLowerCase().equals("creategame") ||
                gameType.toLowerCase().equals("cg")) {
            System.out.println("How many players will be in the game (including you)? ");
            String numberOfPlayers = input.nextLine();

            gameActions.createGame(playerName, Integer.parseInt(numberOfPlayers));
        }
        else if (gameType.toLowerCase().equals("join game") ||
                gameType.toLowerCase().equals("joingame") ||
                gameType.toLowerCase().equals("jg")){
            // Joining a Game
            System.out.println("What is the game UUID? ");
            String uuidString = input.nextLine();
            UUID uuid = UUID.fromString(uuidString);
            gameActions.joinGame(uuid.toString(), playerName);
        } else {
            System.out.println("Please enter a valid statement. (i.e. Create Game, Join Game");
            gameType = input.nextLine();
        }
        /********* Creating/Joining Game *********/

        //Check db to see if start game has happened
        //Use AutoMessageCheck to make get request for start game
        autoMessageCheck.startAutoMessageCheck(gameActions.getGameUUID());

        //after you get all player info from startGame GET, then enter
        //  while loop that will end when it gets strings gameOver or youLost
        int i = 0;
        StringBuilder startGameResponse = null;
        String msg = " ";
        ArrayList<String> players = new ArrayList<String>();
        JSONObject startGameJSON = null;

        System.out.println("Waiting for players to join....");
        while(!msg.equals("startGame")) {
            startGameResponse = autoMessageCheck.getResponse();
            if (startGameResponse != null) {
                // TODO: Take this out; only for testing
                if (i == 0) {
                    gameActions.joinGame(gameActions.getGameUUID(), "Shawn");
                    i++;
                }

                startGameJSON = new JSONObject(startGameResponse.toString());
                msg = startGameJSON.get("messageType").toString();
                if (msg.equals("startGame")) {
                    JSONObject responseJSON = new JSONObject(startGameResponse.toString());
                    JSONObject activePlayers = (JSONObject) responseJSON.get("activePlayers");

                    for (Iterator iterator = activePlayers.keySet().iterator(); iterator.hasNext(); ) {
                        String key = (String) iterator.next();
                        players.add(activePlayers.get(key).toString()); // all active players in game
                    }
                    //gameStatus.setActivePlayerList(players);
                }
            }
        }

        System.out.println("All players have joined!");
        System.out.print("Lobby includes: ");
        for (int j=0; j < players.size(); ++j){
            if (j != (players.size() - 1) ){
                System.out.print(players.get(j) + ", ");
            } else {
                System.out.print(players.get(j));
            }
        }


        JSONObject cardsAssigned = (JSONObject) startGameJSON.get("cardsAssigned");
        ArrayList<String> cards = new ArrayList<String>();

        for (Iterator iterator = cardsAssigned.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            cards.add(cardsAssigned.get(key).toString()); // cards for player
        }

        System.out.print("\nYour cards are: ");
        for (int j=0; j < cards.size(); ++j){
            if (j != (cards.size() -1) ){
                System.out.print(cards.get(j) + ", ");
            } else {
                System.out.print(cards.get(j));
            }
        }

        System.out.println();

        /*PlayerStatus playerStatus = new PlayerStatus();
        playerStatus.setPlayerName(playerName);
        playerStatus.setPlayerHand(cards);
        playerStatus.setPlayerLocation(playerName + "Start");*/ // TODO: define locations

        //TODO: how will we create an instance of gameStatus if we don't know each player's hand since
        //      activePlayerList is a list of PlayerStatus' which requires knowing a player's hand

    }

    public static void startGame(List<PlayerStatus> playerList){
        // Start the game
        //gameStatus.setActivePlayerList(playerList);
    }

    /*public static void updateGameBoard(List<Pair<String, String>> playerPositionUpdates,
                                String currentPlayerTurn,
                                Optional<String> notificationType,
                                Optional<String> notificationMessage) {
        // Display player movement
    }*/
}
