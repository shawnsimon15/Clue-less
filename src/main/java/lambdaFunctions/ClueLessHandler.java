package lambdaFunctions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ClueLessHandler implements RequestStreamHandler {
    private static final String DYNAMODB_GAMEDATA = "GameData";
    private static final String DYNAMODB_MESSAGES = "Messages";
    public static final ArrayList<String> suspectCards = new ArrayList<>(Arrays
            .asList("MissScarlet", "ColonelMustard", "Mrs.White",
                    "Mr.Green", "Mrs.Peacock", "ProfessorPlum"));
    public static final ArrayList<String> weaponCards = new ArrayList<>(Arrays
            .asList("Candlestick", "Revolver", "Knife", "Lead Pipe", "Rope", "Wrench"));
    public static final ArrayList<String> locationCards = new ArrayList<>(Arrays
            .asList("Kitchen", "Hall", "Ballroom", "Conservatory",
                    "Dining Room", "Study", "Billiard Room", "Library", "Lounge"));


    @Override
    public void handleRequest(InputStream inputStream,
                              OutputStream outputStream,
                              Context context) throws IOException {
        JSONParser parser = new JSONParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDb = new DynamoDB(client);

        try {
            JSONObject event = (JSONObject) parser.parse(reader);
            String gameID = event.get("gameID").toString();
            JSONObject response = new JSONObject();
            Item game = dynamoDb.getTable(DYNAMODB_GAMEDATA)
                    .getItem("UUID", gameID);
            String currentPlayers = "";
            String[] listOfPlayers = new String[0];

            if (game != null) {
                currentPlayers = game.get("Current Players").toString();
                listOfPlayers = currentPlayers.split(", ");
            }

            switch (event.get("messageType").toString()){
                case "CreateGame":
                    String selectedPlayer = event.get("selectedPlayer").toString();
                    int numOfPlayers = Integer.parseInt(event.get("numberOfPlayers").toString());

                    String gameStatus = "New Game";

                    int suspectIndex = (int) ((Math.random() * (5)));

                    // To ensure that suspect is not an active player
                    /*while (suspectCards[suspectIndex].equals(selectedPlayer)) {
                        suspectIndex = (int) ((Math.random() * (5)));
                    }*/

                    int weaponIndex = (int) ((Math.random() * (5)));
                    int locationIndex = (int) ((Math.random() * (8)));

                    String chosenSuspect = suspectCards.get(suspectIndex);
                    String chosenWeapon = weaponCards.get(weaponIndex);
                    String chosenLocation = locationCards.get(locationIndex);

                    String winningSecret = chosenSuspect + ", " + chosenWeapon + ", " + chosenLocation;

                    ArrayList<String> tempSusList = new ArrayList<>();
                    for (String card : suspectCards) {
                        if (!card.equals(chosenSuspect)) {
                            tempSusList.add(card);
                        }
                    }

                    ArrayList<String> tempWeapList = new ArrayList<>();
                    for (String card : weaponCards) {
                        if (!card.equals(chosenWeapon)) {
                            tempWeapList.add(card);
                        }
                    }

                    ArrayList<String> tempLocList = new ArrayList<>();
                    for (String card : locationCards) {
                        if (!card.equals(chosenLocation)) {
                            tempLocList.add(card);
                        }
                    }

                    dynamoDb.getTable(DYNAMODB_GAMEDATA)
                            .putItem(new PutItemSpec().withItem(new Item()
                                    .withString("UUID", gameID)
                                    .withString("Game Status", gameStatus)
                                    .withString("Winning Secret", winningSecret)
                                    .withString("Current Players", selectedPlayer)
                                    .withInt("Max Players", numOfPlayers)
                                    .withList("Remaining Suspect Cards", tempSusList)
                                    .withList("Remaining Weapon Cards", tempWeapList)
                                    .withList("Remaining Location Cards", tempLocList)));
                    break;
                case "MakeSuggestion":
                    String playerWhoSuggested = event.get("playerWhoSuggested").toString();
                    JSONObject cardsSuggested = (JSONObject) event.get("cardsSuggested");

                    String suspect = cardsSuggested.get("suspect").toString();
                    String weapon = cardsSuggested.get("weapon").toString();
                    String location = cardsSuggested.get("location").toString();

                    // Create msg in db for each player
                    for (String plyr : listOfPlayers) {
                        String susMsgID = "makeSus_" + plyr;
                        String susMsg = "SuggestionMade: " + suspect +
                                ", " + weapon + ", " + location;

                        dynamoDb.getTable(DYNAMODB_MESSAGES)
                                .putItem(new PutItemSpec().withItem(new Item()
                                        .withString("UUID", susMsgID)
                                        .withString("GameID", gameID)
                                        .withString("Player Name", playerWhoSuggested)
                                        .withString("Message", susMsg)));
                    }
                    break;
                case "PassSuggestion":
                    String playerName = event.get("playerWhoSuggested").toString();
                    String nextPlayer = event.get("nextPlayer").toString();

                    // Create msg in db for each player
                    for (String plyr : listOfPlayers) {
                        String passMsgID = "passSus_" + plyr;
                        String passMsg = "PassSuggestion: " + nextPlayer;

                        dynamoDb.getTable(DYNAMODB_MESSAGES)
                                .putItem(new PutItemSpec().withItem(new Item()
                                        .withString("UUID", passMsgID)
                                        .withString("GameID", gameID)
                                        .withString("Player Name", playerName)
                                        .withString("Message", passMsg)));
                    }
                    break;
                case "EndTurn":
                    String playerFinishedTurn = event.get("playerFinishedTurn").toString();
                    String nPlayer = event.get("nextPlayer").toString();

                    String msg = "EndTurn: " + nPlayer;

                    // Create msg in db for each player
                    for (String plyr : listOfPlayers) {
                        String endTurnMsgID = "endTurn_" + plyr;
                        dynamoDb.getTable(DYNAMODB_MESSAGES)
                                .putItem(new PutItemSpec().withItem(new Item()
                                        .withString("UUID", endTurnMsgID)
                                        .withString("GameID", gameID)
                                        .withString("Player Name", playerFinishedTurn)
                                        .withString("Message", msg)));
                    }
                    break;
                case "MovePlayer":
                    String movePlayerName = event.get("playerName").toString();
                    String newLocation = event.get("newLocation").toString();

                    String moveMsg = "MovePlayer: " + movePlayerName + " moved to " + newLocation;

                    // Create msg in db for each player
                    for (String plyr : listOfPlayers) {
                        String moveMsgID = "movePlayer_" + plyr;
                        dynamoDb.getTable(DYNAMODB_MESSAGES)
                                .putItem(new PutItemSpec().withItem(new Item()
                                        .withString("UUID", moveMsgID)
                                        .withString("GameID", gameID)
                                        .withString("Player Name", movePlayerName)
                                        .withString("Message", moveMsg)));
                    }
                    break;
                case "DisproveSuggestion":
                    String playerWhoDisprove = event.get("playerWhoSuggested").toString();
                    String card = event.get("cardRevealed").toString();

                    // need way of generating UUID for msgs
                    String disMsg = "DisproveSuggestion: " + playerWhoDisprove + " revealed " + card;

                    for (String plyr : listOfPlayers) {
                        String disMsgID = "disproveSus_" + plyr;
                        dynamoDb.getTable(DYNAMODB_MESSAGES)
                                .putItem(new PutItemSpec().withItem(new Item()
                                        .withString("UUID", disMsgID)
                                        .withString("GameID", gameID)
                                        .withString("Player Name", playerWhoDisprove)
                                        .withString("Message", disMsg)));
                    }
                    break;
                case "JoinGame":
                    String newPlayerName = event.get("playerName").toString();

                    game = dynamoDb.getTable(DYNAMODB_GAMEDATA).getItem("UUID", gameID);
                    if (game != null) {
                        int maxPlayers = Integer.parseInt(game.get("Max Players").toString());
                        String players = game.get("Current Players").toString();
                        String[] list = players.split(", ");
                        int listSize = list.length;

                        String winningString = game.get("Winning Secret").toString();
                        String[] winningStringList = winningString.split(", ");

                        ArrayList<String> susList = new ArrayList<>();
                        for (String c : suspectCards) {
                            if (!c.equals(winningStringList[0])) {
                                susList.add(c);
                            }
                        }

                        ArrayList<String> weapList = new ArrayList<>();
                        for (String c : weaponCards) {
                            if (!c.equals(winningStringList[1])) {
                                weapList.add(c);
                            }
                        }

                        ArrayList<String> locList = new ArrayList<>();
                        for (String c : locationCards) {
                            if (!c.equals(winningStringList[2])) {
                                locList.add(c);
                            }
                        }

                        if( maxPlayers >= (listSize + 1)) {
                            String playersInGame = players + ", " + newPlayerName;
                            if (maxPlayers == (listSize + 1)) {
                                response.put("gameStarting", "The game will now start");
                                ArrayList<String> playerList = new ArrayList<>(Arrays
                                        .asList(playersInGame.split(", ")));

                                String orderedPlayers = "";
                                for (int i = 0; i < suspectCards.size(); ++i) {
                                    if (playerList.contains(suspectCards.get(i))) {
                                        if (orderedPlayers.isEmpty()){
                                            orderedPlayers = suspectCards.get(i);
                                        } else {
                                            orderedPlayers = orderedPlayers + ", " + suspectCards.get(i);
                                        }
                                    }
                                }
                                dynamoDb.getTable(DYNAMODB_GAMEDATA)
                                        .putItem(new PutItemSpec().withItem(new Item()
                                                .withString("UUID", gameID)
                                                .withString("Game Status",
                                                        game.get("Game Status").toString())
                                                .withString("Winning Secret",
                                                        game.get("Winning Secret").toString())
                                                .withString("Current Players", orderedPlayers)
                                                .withInt("Max Players",
                                                        Integer.parseInt(game.get("Max Players")
                                                                .toString()))
                                                .withList("Remaining Suspect Cards", susList)
                                                .withList("Remaining Weapon Cards", weapList)
                                                .withList("Remaining Location Cards", locList)));
                            } else {
                                dynamoDb.getTable(DYNAMODB_GAMEDATA)
                                        .putItem(new PutItemSpec().withItem(new Item()
                                                .withString("UUID", gameID)
                                                .withString("Game Status",
                                                        game.get("Game Status").toString())
                                                .withString("Winning Secret",
                                                        game.get("Winning Secret").toString())
                                                .withString("Current Players", playersInGame)
                                                .withInt("Max Players",
                                                        Integer.parseInt(game.get("Max Players")
                                                                .toString()))
                                                .withList("Remaining Suspect Cards", susList)
                                                .withList("Remaining Weapon Cards", weapList)
                                                .withList("Remaining Location Cards", locList)));
                            }

                            // Do we respond with Welcome to the Game or write that to the db?
                            response.put("messageType", "welcomeToGame");

                        } else {
                            response.put("messageType", "MAX PLAYERS");
                            response.put("gameID", gameID.toString());
                            response.put("max players", maxPlayers);
                        }
                    } else {
                        response.put("messageType", "NO GAME");
                        response.put("gameID", gameID.toString());
                    }
                    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
                    writer.write(response.toString());
                    writer.close();
                    break;
                case "GameOver":
                    String playerWhoWon = event.get("playerWhoWon").toString();

                    String message = "gameOver: " + playerWhoWon + " has WON the game on a correct accusation";

                    for (String plyr : listOfPlayers) {
                        String gameOverMsgID = "gameOver_" + plyr;
                        dynamoDb.getTable(DYNAMODB_MESSAGES)
                                .putItem(new PutItemSpec().withItem(new Item()
                                        .withString("UUID", gameOverMsgID)
                                        .withString("GameID", gameID)
                                        .withString("Player Name", playerWhoWon)
                                        .withString("Message", message)));
                    }
                    break;
                case "PlayerLost":
                    String playerWhoLost = event.get("playerWhoWon").toString();

                    String lostMessage = "playerLost: " + playerWhoLost + " has lost the game on a wrong accusation";

                    for (String plyr : listOfPlayers) {
                        String lostMsgID = "playerLost_" + plyr;
                        dynamoDb.getTable(DYNAMODB_MESSAGES)
                                .putItem(new PutItemSpec().withItem(new Item()
                                        .withString("UUID", lostMsgID)
                                        .withString("GameID", gameID)
                                        .withString("Player Name", playerWhoLost)
                                        .withString("Message", lostMessage)));
                    }
                    break;
                case "Delete":
                    String playerToDelete = event.get("playerToDelete").toString();
                    String ID = event.get("msgID").toString();

                    String deleteMsgID = ID + playerToDelete;

                    dynamoDb.getTable(DYNAMODB_MESSAGES).deleteItem(
                            new PrimaryKey("UUID", deleteMsgID));
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String handleGet(InputStream inputStream,
                          OutputStream outputStream,
                          Context context) throws IOException {

        JSONParser parser = new JSONParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDb = new DynamoDB(client);

        Item game = null;
        JSONObject response = new JSONObject();

        try {
            JSONObject event = (JSONObject) parser.parse(reader);
            JSONObject params = (JSONObject) event.get("params");
            JSONObject path = (JSONObject) params.get("path");
            String gameUUID = path.get("gameUUID").toString();
            game = dynamoDb.getTable(DYNAMODB_GAMEDATA).getItem("UUID", gameUUID);
            JSONObject queryString = (JSONObject) params.get("querystring");
            String msgType = queryString.get("messageType").toString();

            if(msgType.equals("MakeAccusation")) {
                String winningString = game.get("Winning Secret").toString();
                String[] list = winningString.split(", ");
                response.put("suspect", list[0]);
                response.put("weapon", list[1]);
                response.put("location", list[2]);

            } else if (msgType.equals("RequestMessage")) {
                //TODO: need to think of way to get latest msgs from Message Table
                // Certain msgs have certain UUID's?
                // i.e. Suggestions start with 4, Turn msgs start with 1, etc.
                String type = queryString.get("Type").toString();
                int maxPlayers = Integer.parseInt(game.get("Max Players").toString());
                String players = game.get("Current Players").toString();
                String[] list = players.split(", ");
                int listSize = list.length;

                if(type.equals("turnUpdate")){
                    response.put("messageType", "playerTurnUpdate");
                    Item endTurnMsg = dynamoDb.getTable(DYNAMODB_MESSAGES)
                            .getItem("UUID", "endTurn_" + list[0]);
                    if (endTurnMsg != null) {
                        String msg = endTurnMsg.get("Message").toString();
                        String[] endTurnList = msg.split(": ");
                        response.put("currentTurn", endTurnList[1]);
                    } else {
                        response.put("currentTurn", list[0]);
                    }

                } else if(type.equals("locationUpdate")){
                    response.put("messageType", "locationUpdate");
                    JSONObject positionUpdate = new JSONObject();
                    // TODO:Need logic to determine location
                    String[] locations = {"location1", "hallway", "location3", "location4"};
                    int i = 0;
                    for (String player : list) {
                        positionUpdate.put(player, locations[i]);
                        i++;
                    }
                    response.put("positionUpdates", positionUpdate);

                } else if (type.equals("playerLocation")) {
                    response.put("messageType", "playerLocation");
                    String player = queryString.get("Player").toString();
                    Item msg = dynamoDb.getTable(DYNAMODB_MESSAGES).getItem("UUID",
                            "movePlayer_" + player);
                    response.put("playerWhoMoved", msg.get("Player Name").toString());
                    if (msg != null) {
                        String receivedMsg = msg.get("Message").toString();
                        String[] parseMsg = receivedMsg.split(": ");
                        String[] moreParsedMsg = parseMsg[1].split(" ");

                        if (moreParsedMsg.length == 4) {
                            response.put("location", moreParsedMsg[3]);
                        } else {
                            response.put("location", moreParsedMsg[3] + " " + moreParsedMsg[4]);
                        }
                    } else {
                        response.put("location", player + "Start");
                    }

                } else if(type.equals("suggestion")){
                    // TODO:Need way of getting msg UUID
                    String player = queryString.get("Player").toString();
                    Item msg = dynamoDb.getTable(DYNAMODB_MESSAGES).getItem("UUID",
                            "makeSus_" + player);
                    if (msg != null) {
                        response.put("messageType", "suggestionMade");
                        String playerWhoSuggested = msg.get("Player Name").toString();
                        response.put("playerWhoSuggested", playerWhoSuggested);

                        //Subject to change based on how format of msgs in DB
                        String suggestion = msg.get("Message").toString();
                        String[] suggestionTypeParse = suggestion.split(": ");
                        String[] suggestionsParse = suggestionTypeParse[1].split(", ");

                        JSONObject cardsSuggested = new JSONObject();
                        cardsSuggested.put("suspect", suggestionsParse[0]);
                        cardsSuggested.put("weapon", suggestionsParse[1]);
                        cardsSuggested.put("location", suggestionsParse[2]);
                        response.put("cardsSuggested", cardsSuggested);
                        //TODO: need way to make ID (DON'T THINK WE NEED susID)
                        //response.put("suggestionID", );
                    } else {
                        response.put("messageType", "noSuggestionMade");
                    }

                } else if(type.equals("contradict")){
                    response.put("messageType", "contradictSuggestion");
                    //TODO:Need way of getting msg UUID
                    Item msg = dynamoDb.getTable(DYNAMODB_MESSAGES).getItem("UUID", "1234");

                    String playerWhoSuggested = msg.get("Player Name").toString();
                    response.put("playerWhoSuggested", playerWhoSuggested);
                    //TODO: need way to make ID
                    response.put("suggestionID", 1234);

                } else if(type.equals("startGame")){
                    response.put("messageType", "startGame");
                    if (maxPlayers == listSize) {
                        JSONObject activePlayers = new JSONObject();
                         int i = 1;
                         for (String player : list) {
                             activePlayers.put("player" + i, player);
                             i++;
                         }
                         response.put("activePlayers", activePlayers);

                        ArrayList<String> suspectList
                                = (ArrayList<String>) game.get("Remaining Suspect Cards");
                        ArrayList<String> weaponList
                                = (ArrayList<String>) game.get("Remaining Weapon Cards");
                        ArrayList<String> locationList
                                = (ArrayList<String>) game.get("Remaining Location Cards");

                        ArrayList<String> toRemoveSus = new ArrayList<>();
                        ArrayList<String> toRemoveWeap = new ArrayList<>();
                        ArrayList<String> toRemoveLoc = new ArrayList<>();

                        int totalSize = suspectList.size() +
                                weaponList.size() + locationList.size();

                        JSONObject cardsAssigned = new JSONObject();

                        if (listSize == 2) {
                            // Give player 9 cards
                            assignCards(cardsAssigned, suspectList, weaponList, locationList,
                                    toRemoveSus, toRemoveWeap, toRemoveLoc, 9);
                        } else if (listSize == 3) {
                            // Give player 6 cards
                            assignCards(cardsAssigned, suspectList, weaponList, locationList,
                                    toRemoveSus, toRemoveWeap, toRemoveLoc, 6);
                        } else if (listSize == 4) {
                            // Give player 4 cards (two players get 5 cards)
                            if (totalSize == 18 || totalSize == 13) {
                                assignCards(cardsAssigned, suspectList, weaponList, locationList,
                                        toRemoveSus, toRemoveWeap, toRemoveLoc, 5);
                            } else {
                                assignCards(cardsAssigned, suspectList, weaponList, locationList,
                                        toRemoveSus, toRemoveWeap, toRemoveLoc, 4);
                            }
                        } else if (listSize == 5) {
                            // Give player 3 cards (three players get 4 cards)
                            if (totalSize == 18 || totalSize == 14 || totalSize == 10) {
                                assignCards(cardsAssigned, suspectList, weaponList, locationList,
                                        toRemoveSus, toRemoveWeap, toRemoveLoc, 4);
                            } else {
                                assignCards(cardsAssigned, suspectList, weaponList, locationList,
                                        toRemoveSus, toRemoveWeap, toRemoveLoc, 3);
                            }
                        } else if (listSize == 6) {
                            // Give player 3 cards
                            assignCards(cardsAssigned, suspectList, weaponList, locationList,
                                    toRemoveSus, toRemoveWeap, toRemoveLoc, 3);
                        }
                        response.put("cardsAssigned", cardsAssigned);

                        suspectList.removeAll(toRemoveSus);
                        weaponList.removeAll(toRemoveWeap);
                        locationList.removeAll(toRemoveLoc);

                        dynamoDb.getTable(DYNAMODB_GAMEDATA)
                                .putItem(new PutItemSpec().withItem(new Item()
                                        .withString("UUID", gameUUID)
                                        .withString("Game Status",
                                                game.get("Game Status").toString())
                                        .withString("Winning Secret",
                                                game.get("Winning Secret").toString())
                                        .withString("Current Players",
                                                game.get("Current Players").toString())
                                        .withInt("Max Players",
                                                Integer.parseInt(game.get("Max Players")
                                                        .toString()))
                                        .withList("Remaining Suspect Cards", suspectList)
                                        .withList("Remaining Weapon Cards", weaponList)
                                        .withList("Remaining Location Cards", locationList)));

                     } else {
                         response.put("messageType", "Game does not have enough players to start");
                     }
                } else if (type.equals("disprove") || (type.equals("pass"))){
                    String player = queryString.get("Player").toString();
                    Item disproveMsg = dynamoDb.getTable(DYNAMODB_MESSAGES).getItem("UUID",
                            "disproveSus_" + player);
                    Item passMsg = dynamoDb.getTable(DYNAMODB_MESSAGES).getItem("UUID",
                            "passSus_" + player);

                    if (disproveMsg != null) {
                        response.put("messageType", "disproveMade");
                        String playerWhoDisproved = disproveMsg.get("Player Name").toString();
                        response.put("playerWhoDisproved", playerWhoDisproved);

                        //Subject to change based on how format of msgs in DB
                        String disprove = disproveMsg.get("Message").toString();
                        String[] disproveTypeParse = disprove.split(": ");
                        String[] disproveParse = disproveTypeParse[1].split(" ");

                        if (disproveParse.length == 3) {
                            response.put("cardRevealed", disproveParse[2]);
                        } else {
                            response.put("cardRevealed", disproveParse[2] + " " + disproveParse[3]);
                        }

                    } else if (passMsg != null) {
                        response.put("messageType", "passMade");
                        String playerWhoPassed = passMsg.get("Player Name").toString();
                        response.put("playerWhoPassed", playerWhoPassed);

                        String pass = passMsg.get("Message").toString();
                        String[] passTypeParse = pass.split(": ");
                        String[] passParse = passTypeParse[1].split(" ");

                        response.put("nextPlayer", passParse[0]);
                    }
                    else {
                        response.put("messageType", "noDisproveMade");
                    }

                } else if (type.equals("gOPL")) {
                    String player = queryString.get("Player").toString();
                    Item gameOverMsg = dynamoDb.getTable(DYNAMODB_MESSAGES).getItem("UUID",
                            "gameOver_" + player);
                    Item playerLostMsg = dynamoDb.getTable(DYNAMODB_MESSAGES).getItem("UUID",
                            "playerLost_" + player);

                    if (gameOverMsg != null) {
                        response.put("messageType", "gameOver");
                        String winner = gameOverMsg.get("Player Name").toString();
                        response.put("Winner", winner);
                    } else if (playerLostMsg != null) {
                        response.put("messageType", "playerLost");
                        String loser = gameOverMsg.get("Player Name").toString();
                        response.put("Loser", loser);
                    } else {
                        response.put("messageType", "nothing");
                    }
                 }
            } else {
                response.put("messageType", "L");
            }

            OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write(response.toString());
            writer.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public ArrayList<Integer> shuffleList(ArrayList<Integer> notShuffled) {
        if (notShuffled.size() != 0) {
            Collections.shuffle(notShuffled);
        }
        return notShuffled;
    }

    public void assignCards(JSONObject cardsAssigned, ArrayList<String> suspectList,
                            ArrayList<String> weaponList, ArrayList<String> locationList,
                            ArrayList<String> toRemoveSus, ArrayList<String> toRemoveWeap,
                            ArrayList<String> toRemoveLoc, int numberOfCards) {
        ArrayList<Integer> suspectIndices = new ArrayList<>();
        ArrayList<Integer> weaponIndices = new ArrayList<>();
        ArrayList<Integer> locationIndices = new ArrayList<>();

        for (int j = 0; j < suspectList.size(); ++j) {
            suspectIndices.add(new Integer(j));
            weaponIndices.add(new Integer(j));
        }
        for (int j = 0; j < locationList.size(); ++j) {
            locationIndices.add(new Integer(j));
        }
        suspectIndices = shuffleList(suspectIndices);
        weaponIndices = shuffleList(weaponIndices);
        locationIndices = shuffleList(locationIndices);

        boolean stopSus = false;
        boolean stopWeap = false;
        boolean stopLoc = false;
        int cardNumber = 1;
        int susIndicesIndex = 0;
        int weapIndicesIndex = 0;
        int locIndicesIndex = 0;

        while(cardsAssigned.size() != numberOfCards) {
            int randomList = pickRandomList(stopSus, stopWeap, stopLoc);

            if (randomList == 0) {
                if (suspectIndices.size() != 0) {
                    toRemoveSus = addCard(suspectIndices, susIndicesIndex,
                            cardsAssigned, cardNumber, suspectList, toRemoveSus);
                    susIndicesIndex++;
                }
            } else if (randomList == 1) {
                if (weaponIndices.size() != 0) {
                    toRemoveWeap = addCard(weaponIndices, weapIndicesIndex,
                            cardsAssigned, cardNumber, weaponList, toRemoveWeap);
                    weapIndicesIndex++;
                }
            } else {
                if (locationIndices.size() != 0) {
                    toRemoveLoc = addCard(locationIndices, locIndicesIndex,
                            cardsAssigned, cardNumber, locationList, toRemoveLoc);
                    locIndicesIndex++;
                }
            }
            if (susIndicesIndex == 4)
                stopSus = true;
            if (weapIndicesIndex == 4)
                stopWeap = true;
            if (locIndicesIndex == 7)
                stopLoc = true;
            cardNumber++;
        }
    }

    public int pickRandomList(boolean stopSus, boolean stopWeap, boolean stopLoc) {
        int randomList = 0;
        if (!stopSus && !stopWeap && !stopLoc) {
            randomList = (int) ((Math.random() * (2)));
        } else if (stopSus && !stopWeap && !stopLoc) {
            randomList = (int) ((Math.random() * (1)) + 1);
        } else if (!stopSus && stopWeap && !stopLoc) {
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(0);
            temp.add(2);
            Collections.shuffle(temp);
            randomList = temp.get(0);
        } else if (!stopSus && !stopWeap && stopLoc) {
            randomList = (int) ((Math.random() * (1)));
        }
        return randomList;
    }

    public ArrayList<String> addCard(ArrayList<Integer> suspectIndices, int susIndicesIndex,
                                     JSONObject cardsAssigned, int cardNumber,
                                     ArrayList<String> suspectList, ArrayList<String> removeArray) {
        int suspectIndex = suspectIndices.get(susIndicesIndex);
        cardsAssigned.put("cardName" + cardNumber,
                suspectList.get(suspectIndex));
        removeArray.add(suspectList.get(suspectIndex));

        return removeArray;
    }
}
