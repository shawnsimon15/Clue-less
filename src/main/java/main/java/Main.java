package main.java;

//import javafx.util.Pair;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


public class Main {

    private static GameActions gameActions;
    private static AutoMessageCheck autoMessageCheck;
    private static GameStatus gameStatus;
    private static String playerName;

    public static final ArrayList<String> turnOrder = new ArrayList<String>(Arrays
            .asList("Miss Scarlet", "Colonel Mustard", "Mrs.White",
            "Mr.Green", "Mrs.Peacock", "Professor Plum")); // Clockwise from Miss Scarlet


    // main will run be the application run by a player
    public static void main(String[] args) throws IOException {
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
        String invalidInput = "invalid";
        while (invalidInput.equals("invalid")) {
            if (gameType.toLowerCase().equals("create game") ||
                    gameType.toLowerCase().equals("creategame") ||
                    gameType.toLowerCase().equals("cg")) {
                System.out.println("How many players will be in the game (including you)? ");
                String numberOfPlayers = input.nextLine();

                gameActions.createGame(playerName, Integer.parseInt(numberOfPlayers));
                System.out.println("Your game ID is: " + gameActions.getGameUUID());
                invalidInput = "VALID";
            } else if (gameType.toLowerCase().equals("join game") ||
                    gameType.toLowerCase().equals("joingame") ||
                    gameType.toLowerCase().equals("jg")) {
                // Joining a Game
                System.out.println("What is the game UUID? ");
                String uuidString = input.nextLine();
                UUID uuid = UUID.fromString(uuidString);
                gameActions.joinGame(uuid.toString(), playerName);
                invalidInput = "VALID";
            } else {
                System.out.println("Please enter a valid statement. (i.e. Create Game, Join Game");
                gameType = input.nextLine();
            }
        }
        /********* Creating/Joining Game *********/

        //Check db to see if start game has happened
        //Use AutoMessageCheck to make get request for start game
        autoMessageCheck.startAutoMessageCheck(gameActions.getGameUUID());

        //after you get all player info from startGame GET, then enter
        //  while loop that will end when it gets strings gameOver or youLost
        int i = 0;
        StringBuilder startGameResponse;
        String msg = " ";
        ArrayList<String> players = new ArrayList<>();
        JSONObject startGameJSON = null;

        System.out.println("Waiting for players to join....");
        while(!msg.equals("startGame")) {
            startGameResponse = autoMessageCheck.getResponse();
            if (startGameResponse != null) {
                // TODO: Take this out; only for testing
                if (i == 0) {
                    gameActions.joinGame(gameActions.getGameUUID(), "Mrs.White");
                    i++;
                }

                startGameJSON = new JSONObject(startGameResponse.toString());
                msg = startGameJSON.get("messageType").toString();
                if (msg.equals("startGame")) {
                    JSONObject responseJSON = new JSONObject(startGameResponse.toString());
                    JSONObject activePlayers = (JSONObject) responseJSON.get("activePlayers");

                    for (String key : activePlayers.keySet()) {
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
        ArrayList<String> cards = new ArrayList<>();

        for (String key : cardsAssigned.keySet()) {
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

        ArrayList<String> turnOrderForGame = new ArrayList<>();
        for (String plyr : turnOrder) {
            if (players.contains(plyr)) {
                turnOrderForGame.add(plyr);
            }
        }

        System.out.print("The order for the game will be: ");
        for (int j=0; j < turnOrderForGame.size(); ++j){
            if (j != (turnOrderForGame.size() -1) ){
                System.out.print(turnOrderForGame.get(j) + ", ");
            } else {
                System.out.print(turnOrderForGame.get(j));
            }
        }
        System.out.println();

        /********* Determine order for the game *********/
        ArrayList<PlayerStatus> gameStatusPL = new ArrayList<>();
        for (String plyr : turnOrderForGame) {
            PlayerStatus playerStatus = new PlayerStatus();
            playerStatus.setPlayerName(plyr);
            if (plyr.equals(playerName)) {
                playerStatus.setPlayerHand(cards);
            } else {
                ArrayList<String> emptyList = new ArrayList<>();
                playerStatus.setPlayerHand(emptyList);
            }
            playerStatus.setPlayerLocation(plyr + "Start"); // TODO: define locations
            gameStatusPL.add(playerStatus);
        }
        /********* Determine order for the game *********/

        gameStatus.setActivePlayerList(gameStatusPL); // add ordered active players to GameStatus

        startGame(gameStatus.getActivePlayerList());
    }

    public static void startGame(List<PlayerStatus> playerList) throws IOException {
        // Start the game
        String endOfGame = " ";
        int whoseTurn = 0;
        while (!endOfGame.equals("playerLost") || !endOfGame.equals("gameOver")) {
            // TODO: print board on screen

            if (playerList.get(whoseTurn).getPlayerName().equals(playerName)) {
                Scanner input = new Scanner(System.in);
                System.out.println("What would you like to do? ");
                System.out.println("    a) Move Player");
                System.out.println("    b) Make a Suggestion");
                System.out.println("    c) Make an Accusation");
                System.out.println("    d) Make a Contradiction");
                System.out.println("    e) Disprove a Suggestion");
                System.out.println("    f) Pass Suggestion");

                String choice = input.nextLine();

                switch (choice.toLowerCase()) {
                    case "a":
                        String currentLocation = playerList.get(whoseTurn).getPlayerLocation();
                        Scanner moveTo = new Scanner(System.in);
                        System.out.println("Where would you like to move?");
                        String newLocation = moveTo.nextLine();
                        // TODO: take input and convert to accepted string in validMove()
                        boolean validMove = validMove(currentLocation, newLocation);
                        if (validMove) {
                            gameActions.movePiece(gameActions.getGameUUID(), playerName, newLocation);
                        }
                        break;
                    case "b":
                        break;
                    case "c":
                        break;
                    case "d":
                        break;
                    case "e":
                        break;
                    case "f":
                        break;
                }

                // TODO: send endTurn msg
                // TODO: start threads to receive GETs
                // TODO: update string endOfGame when needed
            } else {
                System.out.println("It is not your turn. Please Wait.");
            }
            /********* Keeping track of whose turn it is *********/
            if (whoseTurn == (playerList.size() - 1)) {
                whoseTurn = 0;
            } else {
                whoseTurn++;
            }
            /********* Determine order for the game *********/

            gameActions.endTurn(playerList.get(whoseTurn).getPlayerName());
        }
    }

    /*public static void updateGameBoard(List<Pair<String, String>> playerPositionUpdates,
                                String currentPlayerTurn,
                                Optional<String> notificationType,
                                Optional<String> notificationMessage) {
        // Display player movement
    }*/

    public static boolean validMove(String currentLocation, String desiredLocation) throws IOException {
        boolean validMove = false;

        /********* Get location of each player on the board *********/
        StringBuilder response = ClueLessUtils.makeGet(gameActions.getGameUUID(), "locationUpdate");
        JSONObject responseJSON = new JSONObject(response.toString());
        JSONObject locationUpdate = (JSONObject) responseJSON.get("positionUpdates");

        ArrayList<String> locations = new ArrayList<>();

        for (String key : locationUpdate.keySet()) {
            locations.add(locationUpdate.get(key).toString());
        }
        /********* Get location of each player on the board *********/

        switch (desiredLocation) {
            case "Hall:CB": // Hallway between Conservatory and Ballroom
                if (!locations.contains("Hall:CB") || currentLocation.equals("Mr.GreenStart")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Conservatory" +
                            " and Ballroom"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hall:CL": // Hallway between Conservatory and Library
                if (!locations.contains("Hall:CL") || currentLocation.equals("Mrs.PeacockStart")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Conservatory" +
                            " and Library"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hall:LS": // Hallway between Library and Study
                if (!locations.contains("Hall:LS") || currentLocation.equals("Professor PlumStart")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Library" +
                            " and Study"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hall:LBR": // Hallway between Library and Billiard Room
                if (!locations.contains("Hall:LBR")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Library" +
                            " and Billiary Room"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hall:SH": // Hallway between Study and Hall
                if (!locations.contains("Hall:SH")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Study" +
                            " and Hall"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hall:HBR": // Hallway between Hall and Billiard Room
                if (!locations.contains("Hall:HBR")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Hall" +
                            " and Billiard Room"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hall:HL": // Hallway between Hall and Lounge
                if (!locations.contains("Hall:HL") || currentLocation.equals("Miss ScarletStart")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Hall" +
                            " and Lounge"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hall:LDR": // Hallway between Lounge and Dining Room
                if (!locations.contains("Hall:LDR") || currentLocation.equals("Colonel MustardStart")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Lounge" +
                            " and Dining Room"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hall:DRBR": // Hallway between Dining Room and Billiard Room
                if (!locations.contains("Hall:DRBR")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Dining Room" +
                            " and Billiard Room"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hall:DRK": // Hallway between Dining Room and Kitchen
                if (!locations.contains("Hall:DRK")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Dining Room" +
                            " and Kitchen"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hall:KB": // Hallway between Kitchen and Ballroom
                if (!locations.contains("Hall:KB") || currentLocation.equals("Mrs.WhiteStart")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Kitchen" +
                            " and Ballroom"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hall:BBR": // Hallway between Ballroom and Billiard Room
                if (!locations.contains("Hall:BBR")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Ballroom" +
                            " and Billiard Room"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Conservatory":
                if (currentLocation.equals("Hall:CB") || currentLocation.equals("Hall:CL")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Library":
                if (currentLocation.equals("Hall:CL") || currentLocation.equals("Hall:LS")
                        || currentLocation.equals("Hall:LBR")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Study":
                if (currentLocation.equals("Hall:LS") || currentLocation.equals("Hall:SH")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Hall":
                if (currentLocation.equals("Hall:SH") || currentLocation.equals("Hall:HBR")
                        || currentLocation.equals("Hall:HL")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Lounge":
                if (currentLocation.equals("Hall:HL") || currentLocation.equals("Hall:LDR")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Dining Room":
                if (currentLocation.equals("Hall:LDR") || currentLocation.equals("Hall:DRBR")
                        || currentLocation.equals("Hall:DRK")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Kitchen":
                if (currentLocation.equals("Hall:DRK") || currentLocation.equals("Hall:KB")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Ballroom":
                if (currentLocation.equals("Hall:CB") || currentLocation.equals("Hall:KB")
                        || currentLocation.equals("Hall:BBR")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Billiard Room":
                if (currentLocation.equals("Hall:BBR") || currentLocation.equals("Hall:LBR")
                        || currentLocation.equals("Hall:HBR") || currentLocation.equals("Hall:DRBR")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
            default:
                validMove = false;
                System.out.println(playerName + " did not enter valid location");
                break;

        }

        return validMove;
    }
}
