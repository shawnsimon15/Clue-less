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
import java.util.concurrent.TimeUnit;


public class Main {

    private static GameActions gameActions;
    private static AutoMessageCheck autoMessageCheck;
    private static GameStatus gameStatus;
    private static String playerName;

    public static final ArrayList<String> turnOrder = new ArrayList<String>(Arrays
            .asList("MissScarlet", "ColonelMustard", "Mrs.White",
            "Mr.Green", "Mrs.Peacock", "ProfessorPlum")); // Clockwise from Miss Scarlet
    public static final ArrayList<String> locations = new ArrayList<String>(Arrays
            .asList("Kitchen", "Hall", "Ballroom", "Conservatory",
                    "Dining Room", "Study", "Billiard Room", "Library", "Lounge"));
    public static final ArrayList<String> weapons = new ArrayList<String>(Arrays
            .asList("Candlestick", "Revolver", "Knife", "Lead Pipe", "Rope", "Wrench"));


    // main will run be the application run by a player
    public static void main(String[] args) throws IOException, InterruptedException {
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
    public static void userInterface() throws IOException, InterruptedException {
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
        autoMessageCheck.startGameAutoMessageCheck(gameActions.getGameUUID());

        //after you get all player info from startGame GET, then enter
        //  while loop that will end when it gets strings gameOver or youLost
        int i = 0;
        StringBuilder startGameResponse;
        String msg = " ";
        ArrayList<String> players = new ArrayList<>();
        JSONObject startGameJSON = null;

        System.out.println("Waiting for players to join....");
        while(!msg.equals("startGame")) {
            startGameResponse = autoMessageCheck.getStartGameResponse();
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
            playerStatus.setPlayerLocation(plyr + "Start");
            gameStatusPL.add(playerStatus);
        }
        /********* Determine order for the game *********/

        gameStatus.setActivePlayerList(gameStatusPL); // add ordered active players to GameStatus

        startGame(gameStatus.getActivePlayerList());
    }

    public static void startGame(List<PlayerStatus> playerList) throws IOException, InterruptedException {
        // Start the game
        String endOfGame = " ";
        int whoseTurn = 0;
        int i = 0;

        //Check db to see if start game has happened
        //Use AutoMessageCheck to make get request for start game
        autoMessageCheck.suggestionAutoMessageCheck(gameActions.getGameUUID(), playerName);
        autoMessageCheck.contradictAutoMessageCheck(gameActions.getGameUUID(), playerName);
        autoMessageCheck.locationUpdateAutoMessageCheck(gameActions.getGameUUID(), playerName);

        // start suggestion thread

        boolean playerMadeSuggestion = false;
        while (!endOfGame.equals("playerLost") && !endOfGame.equals("gameOver")) {
            // TODO: print board on screen

            StringBuilder suggestionThreadResponse = autoMessageCheck.getSuggestionResponse();
            String suggestionResponse = " ";
            JSONObject suggestionJSON = null;
            if (suggestionThreadResponse != null) {
                suggestionJSON = new JSONObject(suggestionThreadResponse.toString());
                suggestionResponse = suggestionJSON.get("messageType").toString();
            }

            // Check if anyone has disproved/passed a suggestion
            StringBuilder contradictThreadResponse = autoMessageCheck.getContradictResponse();
            String contradictResponse = " ";
            JSONObject contradictJSON = null;
            if (contradictThreadResponse != null) {
                contradictJSON = new JSONObject(contradictThreadResponse.toString());
                contradictResponse = contradictJSON.get("messageType").toString();
            }

            // Check if anyone has moved
            StringBuilder locationUpdateThreadResponse = autoMessageCheck.getLocationUpdateResponse();
            String locationUpdateResponse = " ";
            JSONObject locationUpdateJSON = null;
            if (contradictThreadResponse != null) {
                locationUpdateJSON = new JSONObject(locationUpdateThreadResponse.toString());
                locationUpdateResponse = locationUpdateJSON.get("messageType").toString();
            }

            // A player has moved, so update their location on map
            if (locationUpdateResponse.equals("playerLocation")) {
                String player = locationUpdateJSON.get("playerWhoMoved").toString();
                String playerNewLocation = locationUpdateJSON.get("location").toString();

                for (PlayerStatus ps : playerList) {
                    if(ps.getPlayerName().equals(player)) {
                        ps.setPlayerLocation(playerNewLocation);
                    }
                }
            }

            StringBuilder turnUpdate = ClueLessUtils.makeGet(gameActions.getGameUUID(), playerName, "turnUpdate");
            JSONObject turnUpdateJSON = new JSONObject(turnUpdate.toString());
            String turnUpdateResponse = turnUpdateJSON.get("currentTurn").toString();

            if (playerList.get(whoseTurn).getPlayerName().equals(playerName) && turnUpdateResponse.equals(playerName)) {

                // Delete the suggestion msgs in db after everyone has contradicted
                if (!suggestionResponse.equals("suggestionMade") || playerMadeSuggestion) {
                    if (playerMadeSuggestion) {
                        ClueLessUtils.deletePost(gameActions.getGameUUID(), playerName, "makeSus_");
                        playerMadeSuggestion = false;
                    }
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
                            boolean validMove = false;
                            while (!validMove) {
                                System.out.println("Where would you like to move?");
                                String newLocation = input.nextLine();
                                validMove = validMove(currentLocation, newLocation);
                                if (validMove) {
                                    gameActions.movePiece(gameActions.getGameUUID(), playerName, newLocation);
                                    TimeUnit.SECONDS.sleep(2); // Need to sleep to let Thread read msg
                                    playerList.get(whoseTurn).setPlayerLocation(newLocation);
                                } else {
                                    System.out.println("Please choose another location");
                                }
                            }
                            break;
                        case "b":
                            if (locations.contains(playerList.get(whoseTurn).getPlayerLocation())) {
                                String suspect = " ";
                                while (!turnOrder.contains(suspect)) {
                                    System.out.println("Who do you think is the suspect?");
                                    suspect = input.nextLine();
                                    if (!turnOrder.contains(suspect)) {
                                        System.out.println(suspect + " is not a valid suspect.");
                                    }
                                }
                                String location = " ";
                                while (!locations.contains(location)) {
                                    System.out.println("Where do you think " + suspect + " did it?");
                                    location = input.nextLine();
                                    if (!locations.contains(location)) {
                                        System.out.println(location + " is not a valid location.");
                                    }
                                }
                                String weapon = " ";
                                while (!weapons.contains(weapon)) {
                                    System.out.println("How do you think " + suspect + " did it?");
                                    weapon = input.nextLine();
                                    if (!weapons.contains(weapon)) {
                                        System.out.println(weapon + " is not a valid weapon.");
                                    }
                                }
                                gameActions.makeGuess(gameActions.getGameUUID(), playerName, suspect, weapon, location);
                                System.out.println("You have made a suggestion");
                                // Need to move player in suggestion to location of current player
                                gameActions.movePiece(gameActions.getGameUUID(), suspect, location);
                                ArrayList<PlayerStatus> gameStatusList = (ArrayList<PlayerStatus>) gameStatus.getActivePlayerList();
                                for (PlayerStatus ps : gameStatusList) {
                                    if(ps.getPlayerName().equals(suspect)) {
                                        ps.setPlayerLocation(location);
                                    }
                                }
                                playerMadeSuggestion = true;
                                TimeUnit.SECONDS.sleep(2); // Need to sleep to let Thread read msg that was
                                                                   // just written to db
                            } else {
                                System.out.println("You cannot make a suggestion because you are not in a room.");
                            }
                            break;
                        case "c":
                            if (locations.contains(playerList.get(whoseTurn).getPlayerLocation())) {
                                String suspect = " ";
                                while (!turnOrder.contains(suspect)) {
                                    System.out.println("Who do you think is the suspect?");
                                    suspect = input.nextLine();
                                    if (!turnOrder.contains(suspect)) {
                                        System.out.println(suspect + " is not a valid suspect.");
                                    }
                                }
                                String location = " ";
                                while (!location.equals(playerList.get(whoseTurn).getPlayerLocation())) {
                                    System.out.println("Where do you think " + suspect + " did it?");
                                    location = input.nextLine();
                                    if (!locations.contains(location)) {
                                        System.out.println(location + " is not where you are currently.");
                                    }
                                }
                                String weapon = " ";
                                while (!weapons.contains(weapon)) {
                                    System.out.println("How do you think " + suspect + " did it?");
                                    weapon = input.nextLine();
                                    if (!weapons.contains(weapon)) {
                                        System.out.println(weapon + " is not a valid weapon.");
                                    }
                                }
                                // update string endOfGame when needed
                                endOfGame = gameActions.makeAccusation(gameActions.getGameUUID(),
                                        playerName, suspect, weapon, location);
                                TimeUnit.SECONDS.sleep(2);
                            } else {
                                System.out.println("You must be in a room to make an accusation.");
                            }
                            break;
                        case "d":
                            break;
                        case "e":
                            System.out.println("Which card do you want to reveal?");
                            String card = input.nextLine();

                            ClueLessUtils.disproveSuggestionPost(gameActions.getGameUUID(), playerName, card);
                            TimeUnit.SECONDS.sleep(2); // Need to sleep to let Thread read msg that was
                            break;
                        case "f":
                            int currentPlayer = whoseTurn;
                            if (currentPlayer == (playerList.size() - 1)) {
                                currentPlayer = 0;
                            } else {
                                currentPlayer++;
                            }
                            ClueLessUtils.passSuggestionPost(gameActions.getGameUUID(), playerName,
                                    playerList.get(currentPlayer).getPlayerName());
                            TimeUnit.SECONDS.sleep(2); // Need to sleep to let Thread read msg that was
                            break;
                    }
                } else {
                    // suggestion has been made, so player needs to act accordingly
                    // need to get a response from each player, then delete sus msgs in db
                    String playerWhoSuggested = suggestionJSON.get("playerWhoSuggested").toString();
                    System.out.println(playerWhoSuggested + " has made the following suggestion: ");
                    JSONObject cardsSuggested = (JSONObject) suggestionJSON.get("cardsSuggested");

                    System.out.print(cardsSuggested.get("suspect") + ", " + cardsSuggested.get("weapon") + ", " +
                            cardsSuggested.get("location"));
                    System.out.println();
                    Scanner input = new Scanner(System.in);
                    System.out.println("What would you like to do? ");
                    System.out.println("    a) Disprove a Suggestion");
                    System.out.println("    b) Pass Suggestion");
                    String choice = input.nextLine();

                    switch (choice.toLowerCase()) {
                        case "a":
                            System.out.println("Which card do you want to reveal?");
                            String card = input.nextLine();

                            ClueLessUtils.disproveSuggestionPost(gameActions.getGameUUID(), playerName, card);
                            break;
                        case "b":
                            int currentPlayer = whoseTurn;
                            if (currentPlayer == (playerList.size() - 1)) {
                                currentPlayer = 0;
                            } else {
                                currentPlayer++;
                            }
                            ClueLessUtils.passSuggestionPost(gameActions.getGameUUID(), playerName,
                                    playerList.get(currentPlayer).getPlayerName());
                            break;
                    }
                }

                // Send endTurn msg to signal player is done
                if (whoseTurn == (playerList.size() - 1)) {
                    whoseTurn = 0;
                } else {
                    whoseTurn++;
                }
                // send msg to db updating location

                gameActions.endTurn(playerList.get(whoseTurn).getPlayerName());
            } else {
                System.out.println("It is not your turn. Please Wait.");
                i++;
                if (playerMadeSuggestion) {
                    if (contradictResponse.equals("disproveMade")){
                        String playerWhoDisproved = contradictJSON.get("playerWhoDisproved").toString();
                        ClueLessUtils.deletePost(gameActions.getGameUUID(), playerWhoDisproved, "makeSus_");
                        String cardRevealed = contradictJSON.get("cardRevealed").toString();
                        // add card to player who disproved
                        ArrayList<PlayerStatus> gameStatusList = (ArrayList<PlayerStatus>) gameStatus.getActivePlayerList();
                        for (PlayerStatus ps : gameStatusList) {
                            if(ps.getPlayerName().equals(playerWhoDisproved)) {
                                ps.addPlayerHand(cardRevealed);
                            }
                        }
                        System.out.println(playerWhoDisproved + " revealed " + cardRevealed + "!");


                    } else if (contradictResponse.equals("passMade")) {
                        String playerWhoPassed = contradictJSON.get("playerWhoPassed").toString();
                        ClueLessUtils.deletePost(gameActions.getGameUUID(), playerWhoPassed, "makeSus_");
                        String nextPlayer = contradictJSON.get("nextPlayer").toString();
                        System.out.println(playerWhoPassed + " passed suggestion!");

                    }
                }
                /************ For testing ************
                if (i == 0) {
                    gameActions.movePiece(gameActions.getGameUUID(), "Mrs.White", "Hallway:KB");
                    StringBuilder playerMoved = ClueLessUtils.makeGet(gameActions.getGameUUID(),
                            playerList.get(whoseTurn).getPlayerName(), "playerLocation");
                    JSONObject playerMovedGETJSON = new JSONObject(playerMoved.toString());
                    String playerLocation = playerMovedGETJSON.get("location").toString();
                    playerList.get(whoseTurn).setPlayerLocation(playerLocation);
                    System.out.println(playerList.get(whoseTurn).getPlayerLocation());
                } else if (i == 3) {
                    StringBuilder suggestionGET = ClueLessUtils.makeGet(gameActions.getGameUUID(),
                            playerList.get(whoseTurn).getPlayerName(), "suggestion");
                    JSONObject suggestionGETJSON = new JSONObject(suggestionGET.toString());
                    String playerWhoSuggested = suggestionGETJSON.get("playerWhoSuggested").toString();
                    System.out.println(playerWhoSuggested + " made a suggestion");
                    System.out.println("This was " + playerWhoSuggested + " suggestion:");
                    JSONObject cardsSuggested = (JSONObject) suggestionGETJSON.get("cardsSuggested");
                    System.out.print(cardsSuggested.get("suspect") + ", " + cardsSuggested.get("weapon") +
                            ", " + cardsSuggested.get("location"));
                    System.out.println();
                }
                i++;
                 */
                if (whoseTurn == (playerList.size() - 1)) {
                    whoseTurn = 0;
                } else {
                    whoseTurn++;
                }
                gameActions.endTurn(playerList.get(whoseTurn).getPlayerName());
                /************ For testing ************/

                /*String waitingForPlayerToFinish = turnUpdateResponse;
                while (waitingForPlayerToFinish.equals(turnUpdateResponse)) {
                    TimeUnit.SECONDS.sleep(30);
                    StringBuilder turnUpdateGET = ClueLessUtils.makeGet(gameActions.getGameUUID(), "turnUpdate");
                    JSONObject turnUpdateGETJSON = new JSONObject(turnUpdateGET.toString());
                    waitingForPlayerToFinish = turnUpdateGETJSON.get("currentTurn").toString();
                }*/
            }
            System.out.println("****************************************************");
            /********* Keeping track of whose turn it is *********/

            /********* Determine order for the game *********/
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
        StringBuilder response = ClueLessUtils.makeGet(gameActions.getGameUUID(), playerName, "locationUpdate");
        JSONObject responseJSON = new JSONObject(response.toString());
        JSONObject locationUpdate = (JSONObject) responseJSON.get("positionUpdates");

        ArrayList<String> locations = new ArrayList<>();

        for (String key : locationUpdate.keySet()) {
            locations.add(locationUpdate.get(key).toString());
        }
        /********* Get location of each player on the board *********/

        switch (desiredLocation) {
            case "Hallway:CB": // Hallway between Conservatory and Ballroom
                System.out.println("");
                if (locations.contains("Hallway:CB")) {
                    validMove = false;
                } else if (currentLocation.equals("Mr.GreenStart") ||
                           currentLocation.equals("Conservatory") ||
                           currentLocation.equals("Ballroom")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Conservatory" +
                            " and Ballroom"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hallway:CL": // Hallway between Conservatory and Library
                if (locations.contains("Hallway:CL")) {
                    validMove = false;
                } else if (currentLocation.equals("Mrs.PeacockStart") ||
                           currentLocation.equals("Conservatory") ||
                           currentLocation.equals("Library")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Conservatory" +
                            " and Library"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hallway:LS": // Hallway between Library and Study
                if (locations.contains("Hallway:LS")) {
                    validMove = false;
                } else if (currentLocation.equals("ProfessorPlumStart")  ||
                           currentLocation.equals("Library") ||
                           currentLocation.equals("Study")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Library" +
                            " and Study"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hallway:LBR": // Hallway between Library and Billiard Room
                if (!locations.contains("Hallway:LBR") &&
                        (currentLocation.equals("Library") ||
                        currentLocation.equals("Billiard Room"))) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Library" +
                            " and Billiard Room"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hallway:SH": // Hallway between Study and Hall
                if (!locations.contains("Hallway:SH") &&
                        (currentLocation.equals("Study") ||
                        currentLocation.equals("Hall"))) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Study" +
                            " and Hall"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hallway:HBR": // Hallway between Hall and Billiard Room
                if (!locations.contains("Hallway:HBR") &&
                        (currentLocation.equals("Hall") ||
                        currentLocation.equals("Billiard Room"))) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Hall" +
                            " and Billiard Room"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hallway:HL": // Hallway between Hall and Lounge
                if (locations.contains("Hallway:HL")) {
                    validMove = false;
                } else if (currentLocation.equals("MissScarletStart")  ||
                        currentLocation.equals("Hall") ||
                        currentLocation.equals("Lounge")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Hall" +
                            " and Lounge"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hallway:LDR": // Hallway between Lounge and Dining Room
                if (locations.contains("Hallway:LDR")) {
                    validMove = false;
                } else if (currentLocation.equals("ColonelMustardStart")  ||
                        currentLocation.equals("Lounge") ||
                        currentLocation.equals("Dining Room")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Lounge" +
                            " and Dining Room"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hallway:DRBR": // Hallway between Dining Room and Billiard Room
                if (!locations.contains("Hallway:DRBR") &&
                        (currentLocation.equals("Dining Room") ||
                        currentLocation.equals("Billiard Room"))) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Dining Room" +
                            " and Billiard Room"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hallway:DRK": // Hallway between Dining Room and Kitchen
                if (!locations.contains("Hallway:DRK") &&
                        (currentLocation.equals("Dining Room") ||
                        currentLocation.equals("Kitchen"))) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Dining Room" +
                            " and Kitchen"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hallway:KB": // Hallway between Kitchen and Ballroom
                if (locations.contains("Hallway:KB")) {
                    validMove = false;
                } else if (currentLocation.equals("Mrs.WhiteStart")  ||
                        currentLocation.equals("Kitchen") ||
                        currentLocation.equals("Ballroom")) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Kitchen" +
                            " and Ballroom"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Hallway:BBR": // Hallway between Ballroom and Billiard Room
                if (!locations.contains("Hallway:BBR") &&
                        (currentLocation.equals("Ballroom") ||
                                currentLocation.equals("Billiard Room"))) {
                    validMove = true;
                } else {
                    System.out.println(playerName + " cannot move to the hallway between Ballroom" +
                            " and Billiard Room"); //TODO: maybe find way to add who is in the hallway currently
                }
                break;
            case "Conservatory":
                if (currentLocation.equals("Hallway:CB") || currentLocation.equals("Hallway:CL") ||
                currentLocation.equals("Lounge")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Library":
                if (currentLocation.equals("Hallway:CL") || currentLocation.equals("Hallway:LS")
                        || currentLocation.equals("Hallway:LBR")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Study":
                if (currentLocation.equals("Hallway:LS") || currentLocation.equals("Hallway:SH") ||
                currentLocation.equals("Kitchen")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Hall":
                if (currentLocation.equals("Hallway:SH") || currentLocation.equals("Hallway:HBR")
                        || currentLocation.equals("Hallway:HL")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Lounge":
                if (currentLocation.equals("Hallway:HL") || currentLocation.equals("Hallway:LDR") ||
                currentLocation.equals("Conservatory")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Dining Room":
                if (currentLocation.equals("Hallway:LDR") || currentLocation.equals("Hallway:DRBR")
                        || currentLocation.equals("Hallway:DRK")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Kitchen":
                if (currentLocation.equals("Hallway:DRK") || currentLocation.equals("Hallway:KB") ||
                currentLocation.equals("Study")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Ballroom":
                if (currentLocation.equals("Hallway:CB") || currentLocation.equals("Hallway:KB")
                        || currentLocation.equals("Hallway:BBR")){
                    validMove = true;
                } else {
                    System.out.println(playerName + " is not in valid Hallway"); //TODO: Think of better string
                }
                break;
            case "Billiard Room":
                if (currentLocation.equals("Hallway:BBR") || currentLocation.equals("Hallway:LBR")
                        || currentLocation.equals("Hallway:HBR") || currentLocation.equals("Hallway:DRBR")){
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
