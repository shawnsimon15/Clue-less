package main.java;

//import javafx.util.Pair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game {
    private GameActions gameActions;
    private AutoMessageCheck autoMessageCheck;
    private GameStatus gameStatus;
    private String playerName;
    private List<PlayerStatus> activePlayerList;

    public Game() throws IOException, InterruptedException {
        // Default Constructor
        gameActions = new GameActions();
        gameStatus = new GameStatus();
        playerName = " ";
        activePlayerList = new ArrayList<>();
        autoMessageCheck = new AutoMessageCheck();
        userInterface();
    }

    public void createOrJoinGame(String gameType, Scanner input) throws IOException {
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
    }

    public void preGamePrep() throws IOException {
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
                }
            }
        }
        printListContents(players, "players");

        ArrayList<String> cards = getPlayerCards(startGameJSON);
        ArrayList<String> turnOrderForGame = obtainTurnOrder(players);

        loadPlayerInfoIntoGameStatus(cards, turnOrderForGame);

    }

    public void loadPlayerInfoIntoGameStatus (ArrayList<String> cards, ArrayList<String> turnOrderForGame) {
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
        gameStatus.setActivePlayerList(gameStatusPL); // add ordered active players to GameStatus
    }

    public ArrayList<String> obtainTurnOrder(ArrayList<String> players) {
        ArrayList<String> turnOrderForGame = new ArrayList<>();
        for (String plyr : ClueLessConstants.SUSPECT_LIST) {
            if (players.contains(plyr)) {
                turnOrderForGame.add(plyr);
            }
        }
        return turnOrderForGame;
    }

    public ArrayList<String> getPlayerCards(JSONObject startGameJSON) {
        JSONObject cardsAssigned = (JSONObject) startGameJSON.get("cardsAssigned");
        ArrayList<String> cards = new ArrayList<>();

        for (String key : cardsAssigned.keySet()) {
            cards.add(cardsAssigned.get(key).toString()); // cards for player
        }

        printListContents(cards, "cards");
        System.out.println();

        return cards;
    }

    public void printListContents(ArrayList<String> list, String typeOfList) {
        if (typeOfList.equals("players")) {
            System.out.println("All players have joined!");
            System.out.print("Lobby includes: ");
        } else if (typeOfList.equals("cards")) {
            System.out.print("\nYour cards are: ");
        } else {
            System.out.print("The order for the game will be: ");
        }

        for (int i=0; i < list.size(); ++i) {
            if (i != (list.size() - 1)) {
                System.out.print(list.get(i) + ", ");
            } else {
                System.out.print(list.get(i));
            }
        }
    }

    /**
     * Function: userInterface()
     * Description: Interacts with user for gameplay experience (test-based or GUI)
     * Does not return
     **/
    public void userInterface() throws IOException, InterruptedException {
        // TODO: Create GUI
        Scanner input = new Scanner(System.in);
        System.out.println("Would you like to Create or Join a game? ");

        String gameType = input.nextLine();

        // TODO: Ensure two players can't get same player
        int suspectIndex = (int) ((Math.random() * (5)));
        playerName = ClueLessConstants.SUSPECT_LIST.get(suspectIndex);
        System.out.println("You will be: " + playerName);

        /********* Creating/Joining Game *********/
        createOrJoinGame(gameType, input);
        /********* Creating/Joining Game *********/

        // Check db to see if start game has happened
        // Use AutoMessageCheck to make get request for start game
        autoMessageCheck.startGameAutoMessageCheck(gameActions.getGameUUID());

        // After you get all player info from startGame GET, then enter
        // while loop that will end when it gets strings gameOver or youLost
        preGamePrep();
        startGame((ArrayList<PlayerStatus>) gameStatus.getActivePlayerList());
    }

    public void updateLocation(String locationUpdateResponse, JSONObject locationUpdateJSON,
                               ArrayList<PlayerStatus> playerList) {
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
    }

    public String getPlayerInputForSuggestion(Scanner input, String cardType) {
        String card = " ";
        ArrayList<String> list;
        if (cardType.equals("suspect")) {
            list = ClueLessConstants.SUSPECT_LIST;
        } else if (cardType.equals("location")) {
            list = ClueLessConstants.ROOM_LIST;
        } else {
            list = ClueLessConstants.WEAPON_LIST;
        }

        if (cardType.equals("suspect") || cardType.equals("weapon") || cardType.equals("location")) {
            while (!list.contains(card)) {
                System.out.println("The " + cardType + " is?");
                card = input.nextLine();
                if (!list.contains(card)) {
                    System.out.println(card + " is not a valid suspect.");
                }
            }
        } else if (cardType.equals("disprove")) {
            while (!ClueLessConstants.SUSPECT_LIST.contains(card) ||
                    !ClueLessConstants.ROOM_LIST.contains(card) ||
                    !ClueLessConstants.WEAPON_LIST.contains(card)) {

                System.out.println("Which card do you want to reveal?");
                card = input.nextLine();
            }
        }
        return card;
    }

    public void startGame(ArrayList<PlayerStatus> playerList) throws IOException, InterruptedException {
        // Start the game
        String endOfGame = " ";
        String gOPLResponse = " ";
        JSONObject gOPLJSON = null;
        int whoseTurn = 0;
        int i = 0;

        // Thread to check if suggestion has been made
        autoMessageCheck.suggestionAutoMessageCheck(gameActions.getGameUUID(), playerName);
        // Thread to check if contradiction has been made
        autoMessageCheck.contradictAutoMessageCheck(gameActions.getGameUUID(), playerName);
        // Thread to check if player has moved
        autoMessageCheck.locationUpdateAutoMessageCheck(gameActions.getGameUUID(), playerName);
        // Thread to check endOfGame msg
        autoMessageCheck.gOPLAutoMessageCheck(gameActions.getGameUUID(), playerName);

        // TODO: call GET for card assignment
        boolean playerMadeSuggestion = false;
        while (!endOfGame.equals("playerLost") && !endOfGame.equals("gameOver") &&
                !gOPLResponse.equals("gameOver") && !gOPLResponse.equals("playerLost")) {
            // TODO: print board on screen

            StringBuilder gOPLThreadResponse = autoMessageCheck.getGOPLResponse();
            if (gOPLThreadResponse != null) {
                gOPLJSON = new JSONObject(gOPLThreadResponse.toString());
                gOPLResponse = gOPLJSON.get("messageType").toString();
            }

            if (gOPLResponse.equals("gameOver")) {
                String winner = gOPLJSON.get("Winner").toString();
                System.out.println("The game is over! The winner is: " + winner);
            } else {
                if (gOPLResponse.equals("playerLost")) {
                    String loser = gOPLJSON.get("Loser").toString();
                    System.out.println(loser + " has lost the game on an incorrect accusation.");
                    // take them out of the lineup
                    int playerListIndex = 0;
                    for (PlayerStatus ps : playerList) {
                        if (ps.getPlayerName().equals(loser)) {
                            playerList.remove(playerListIndex);
                        }
                        playerListIndex++;
                    }
                    // TODO: also take player out of the game in db
                }

                boolean validInput = true;
                StringBuilder suggestionThreadResponse = autoMessageCheck.getSuggestionResponse();
                StringBuilder contradictThreadResponse = autoMessageCheck.getContradictResponse();
                StringBuilder locationUpdateThreadResponse = autoMessageCheck.getLocationUpdateResponse();

                // Check if a suggestion was made
                String suggestionResponse = " ";
                JSONObject suggestionJSON = null;
                if (suggestionThreadResponse != null) {
                    suggestionJSON = new JSONObject(suggestionThreadResponse.toString());
                    suggestionResponse = suggestionJSON.get("messageType").toString();
                }

                // Check if anyone has disproved/passed a suggestion
                String contradictResponse = " ";
                JSONObject contradictJSON = null;
                if (contradictThreadResponse != null) {
                    contradictJSON = new JSONObject(contradictThreadResponse.toString());
                    contradictResponse = contradictJSON.get("messageType").toString();
                }

                // Check if anyone has moved
                String locationUpdateResponse = " ";
                JSONObject locationUpdateJSON = null;
                if (contradictThreadResponse != null) {
                    locationUpdateJSON = new JSONObject(locationUpdateThreadResponse.toString());
                    locationUpdateResponse = locationUpdateJSON.get("messageType").toString();
                }
                updateLocation(locationUpdateResponse, locationUpdateJSON, playerList);

                StringBuilder turnUpdate = ClueLessUtils.makeGet(gameActions.getGameUUID(),
                        playerName, "turnUpdate");
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
                                        validInput = false;
                                    }
                                }
                                break;
                            case "b":
                                if (ClueLessConstants.ROOM_LIST.contains(playerList.get(whoseTurn).getPlayerLocation())) {
                                    String suspect;
                                    suspect = getPlayerInputForSuggestion(input, "suspect");

                                    String location;
                                    location = getPlayerInputForSuggestion(input, "location");

                                    String weapon;
                                    weapon = getPlayerInputForSuggestion(input, "weapon");

                                    gameActions.makeGuess(gameActions.getGameUUID(), playerName, suspect, weapon, location);
                                    System.out.println("You have made a suggestion");

                                    // Need to move player in suggestion to location of current player
                                    gameActions.movePiece(gameActions.getGameUUID(), suspect, location);
                                    ArrayList<PlayerStatus> gameStatusList
                                            = (ArrayList<PlayerStatus>) gameStatus.getActivePlayerList();

                                    for (PlayerStatus ps : gameStatusList) {
                                        if (ps.getPlayerName().equals(suspect)) {
                                            ps.setPlayerLocation(location);
                                        }
                                    }
                                    playerMadeSuggestion = true;
                                    TimeUnit.SECONDS.sleep(2); // Need to sleep to let Thread read msg that was
                                    // just written to db
                                } else {
                                    System.out.println("You cannot make a suggestion because you are not in a room.");
                                    validInput = false;
                                }
                                break;
                            case "c":
                                if (ClueLessConstants.ROOM_LIST.contains(playerList.get(whoseTurn).getPlayerLocation())) {
                                    String suspect;
                                    suspect = getPlayerInputForSuggestion(input, "suspect");

                                    String location;
                                    location = getPlayerInputForSuggestion(input, "location");

                                    String weapon;
                                    weapon = getPlayerInputForSuggestion(input, "weapon");

                                    // update string endOfGame when needed
                                    endOfGame = gameActions.makeAccusation(gameActions.getGameUUID(),
                                            playerName, suspect, weapon, location);
                                    System.out.println(endOfGame);
                                    if (endOfGame.equals("playerLost") || endOfGame.equals("gameOver")) {
                                        autoMessageCheck.setStopThreads();
                                    }
                                    TimeUnit.SECONDS.sleep(2);

                                } else {
                                    System.out.println("You must be in a room to make an accusation.");
                                    validInput = false;
                                }
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
                    if (validInput) {
                        if (whoseTurn == (playerList.size() - 1)) {
                            whoseTurn = 0;
                        } else {
                            whoseTurn++;
                        }
                        // send msg to db ending player's turn
                        gameActions.endTurn(playerList.get(whoseTurn).getPlayerName());
                    }

                } else {
                    System.out.println("It is not your turn. Please Wait.");
                    i++;
                    if (playerMadeSuggestion) {
                        if (contradictResponse.equals("disproveMade")) {
                            // If a player has contradicted a suggestion, delete suggestion post for each player
                            String playerWhoDisproved = contradictJSON.get("playerWhoDisproved").toString();
                            ClueLessUtils.deletePost(gameActions.getGameUUID(), playerWhoDisproved, "makeSus_");
                            String cardRevealed = contradictJSON.get("cardRevealed").toString();

                            // Add card to player who disproved
                            ArrayList<PlayerStatus> gameStatusList = (ArrayList<PlayerStatus>) gameStatus.getActivePlayerList();
                            for (PlayerStatus ps : gameStatusList) {
                                if (ps.getPlayerName().equals(playerWhoDisproved)) {
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
            }
            System.out.println("****************************************************");
        }
    }

   /* public void updateGameStatus(ArrayList<Pair<String, String>> playerPositionUpdates,
                                 String currentPlayerTurn,
                                 Optional<String> notificationType,
                                 Optional<String> notificationMessage) {
    } */

    public boolean validMove(String currentLocation, String desiredLocation) throws IOException {
        boolean validMove = false;

        /********* Get location of each player on the board *********/
        StringBuilder response = ClueLessUtils.makeGet(gameActions.getGameUUID(),
                playerName, "locationUpdate");
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
