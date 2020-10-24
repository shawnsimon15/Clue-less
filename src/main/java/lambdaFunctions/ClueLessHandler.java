package lambdaFunctions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class ClueLessHandler implements RequestStreamHandler {
    private static final String DYNAMODB_GAMEDATA = "GameData";
    private static final String DYNAMODB_MESSAGES = "Messages";


    @Override
    public void handleRequest(InputStream inputStream,
                              OutputStream outputStream,
                              Context context) throws IOException {
        JSONParser parser = new JSONParser();
        System.out.println(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        System.out.println(reader);
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDb = new DynamoDB(client);

        try {
            JSONObject event = (JSONObject) parser.parse(reader);
            String gameUUID = event.get("gameID").toString();
            Item game = null;
            JSONObject response = new JSONObject();

            switch (event.get("messageType").toString()){
                case "CreateGame":
                    String selectedPlayer = event.get("selectedPlayer").toString();
                    int numOfPlayers = Integer.parseInt(event.get("numberOfPlayers").toString());

                    String gameStatus = "New Game"; // Logic needed to determine gameStatus
                    String winningSecret = "Person, Weapon, Location"; // Logic needed to randomly pick secret

                    dynamoDb.getTable(DYNAMODB_GAMEDATA)
                            .putItem(new PutItemSpec().withItem(new Item().withString("UUID", gameUUID)
                                    .withString("Game Status", gameStatus)
                                    .withString("Winning Secret", winningSecret)
                                    .withString("Current Players", selectedPlayer)
                                    .withInt("Max Players", numOfPlayers)));
                    break;
                case "MakeSuggestion":
                    String playerWhoSuggested = event.get("playerWhoSuggested").toString();
                    JSONObject cardsSuggested = (JSONObject) event.get("cardsSuggested");

                    String suspect = cardsSuggested.get("suspect").toString();
                    String weapon = cardsSuggested.get("weapon").toString();
                    String location = cardsSuggested.get("location").toString();

                    // need way of generating UUID for msgs
                    String susMsgUUID = "1234";
                    String susMsg = "SuggestionMade: " + suspect +
                            ", " + weapon + ", " + location;

                    dynamoDb.getTable(DYNAMODB_MESSAGES)
                            .putItem(new PutItemSpec().withItem(new Item().withString("UUID", susMsgUUID)
                                    .withString("GameID", gameUUID)
                                    .withString("Player Name", playerWhoSuggested)
                                    .withString("Message", susMsg)));
                    break;
                case "PassSuggestion":
                    String playerName = event.get("playerWhoSuggested").toString();
                    String nextPlayer = event.get("nextPlayer").toString();

                    // need way of generating UUID for msgs
                    String passMsgUUID = "1235";
                    String passMsg = "PassSuggestion: " + nextPlayer;

                    dynamoDb.getTable(DYNAMODB_MESSAGES)
                            .putItem(new PutItemSpec().withItem(new Item().withString("UUID", passMsgUUID)
                                    .withString("GameID", gameUUID)
                                    .withString("Player Name", playerName)
                                    .withString("Message", passMsg)));
                    break;
                case "EndTurn":
                    String playerFinishedTurn = event.get("playerFinishedTurn").toString();
                    String nPlayer = event.get("nextPlayer").toString();

                    // need way of generating UUID for msgs
                    String msgUUID = "1236";
                    String msg = "EndTurn: " + nPlayer;

                    dynamoDb.getTable(DYNAMODB_MESSAGES)
                            .putItem(new PutItemSpec().withItem(new Item().withString("UUID", msgUUID)
                                    .withString("GameID", gameUUID)
                                    .withString("Player Name", playerFinishedTurn)
                                    .withString("Message", msg)));
                    break;
                case "MovePlayer":
                    String movePlayerName = event.get("playerName").toString();
                    String newLocation = event.get("newLocation").toString();

                    // need way of generating UUID for msgs
                    String moveMsgUUID = "1238";
                    String moveMsg = "MovePlayer: " + movePlayerName + " moved to " + newLocation;

                    dynamoDb.getTable(DYNAMODB_MESSAGES)
                            .putItem(new PutItemSpec().withItem(new Item().withString("UUID", moveMsgUUID)
                                    .withString("GameID", gameUUID)
                                    .withString("Player Name", movePlayerName)
                                    .withString("Message", moveMsg)));
                    break;
                case "DisproveSuggestion":
                    String playerWhoDisprove = event.get("playerWhoSuggested").toString();
                    String card = event.get("cardRevealed").toString();

                    // need way of generating UUID for msgs
                    String disMsgUUID = "1239";
                    String disMsg = "DisproveSuggestion: " + playerWhoDisprove + " revealed " + card;

                    dynamoDb.getTable(DYNAMODB_MESSAGES)
                            .putItem(new PutItemSpec().withItem(new Item().withString("UUID", disMsgUUID)
                                    .withString("GameID", gameUUID)
                                    .withString("Player Name", playerWhoDisprove)
                                    .withString("Message", disMsg)));
                    break;
                case "JoinGame":
                    String newPlayerName = event.get("playerName").toString();

                    game = dynamoDb.getTable(DYNAMODB_GAMEDATA).getItem("UUID", gameUUID);
                    System.out.println("IN THE GAME");
                    if (game != null) {
                        // TODO: should be an array or an object of GameStatus
                        int maxPlayers = Integer.parseInt(game.get("Max Players").toString());
                        if( maxPlayers >= (maxPlayers + 1)) {
                            String currentPlayers = game.get("Current Players").toString() + ", " + newPlayerName;

                            System.out.println("ADDING OUR BOY");
                            dynamoDb.getTable(DYNAMODB_GAMEDATA)
                                    .putItem(new PutItemSpec().withItem(new Item().withString("UUID", gameUUID)
                                            .withString("Game Status", game.get("Game Status").toString())
                                            .withString("Winning Secret", game.get("Winning Secret").toString())
                                            .withString("Current Players", currentPlayers)
                                            .withInt("Max Players",
                                                    Integer.parseInt(game.get("Max Players").toString()))));

                            // Do we respond with Welcome to the Game or write that to the db?
                            response.put("messageType", "welcomeToGame");
                        } else {
                            response.put("messageType", "joinFailed");
                        }
                    } else {
                        response.put("messageType", "joinFailed");
                    }
                    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
                    writer.write(response.toString());
                    writer.close();
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
                game = dynamoDb.getTable(DYNAMODB_MESSAGES).getItem("UUID", gameUUID);
            } else {
                response.put("messageType", "L");
            }

            /*if (event.get("messageType").toString().equals("MakeAccusation")) {
                String playerName = event.get("playerWhoAccused").toString();
                JSONObject cardsSuggested = (JSONObject) event.get("cardsSuggested");

                String suspect = cardsSuggested.get("suspect").toString();
                String weapon = cardsSuggested.get("weapon").toString();
                String location = cardsSuggested.get("location").toString();

                game = dynamoDb.getTable(DYNAMODB_GAMEDATA).getItem("UUID", gameUUID);

                if(game != null) {
                    String[] secret = game.get("Winning Secret").toString().split(", ");
                    String realSus = secret[0];
                    String realWeapon = secret[1];
                    String realLocation = secret[2];

                    if (suspect.equals(realSus) && weapon.equals(realWeapon) && location.equals(realLocation)) {
                        response.put("messageType", "accusationResult");
                        response.put("playerWhoAccused", playerName);
                        response.put("accusationTrue", "True");
                    } else {
                        response.put("messageType", "accusationResult");
                        response.put("playerWhoAccused", playerName);
                        response.put("accusationTrue", "False");
                    }
                }

            } else if (event.get("messageType").toString().equals("RequestMessage")) {
                String gameUUID = event.get("gameID").toString();
                String playerName = event.get("playerName").toString();
                String lastRequest = event.get("lastSuccessfulRequest").toString();

                //TODO: need to think of way to get latest msgs from Message Table
                    // Certain msgs have certain UUID's?
                        // i.e. Suggestions start with 4, Turn msgs start with 1, etc.
                game = dynamoDb.getTable(DYNAMODB_MESSAGES).getItem("UUID", gameUUID);

                // Create response according to what msg player will receive

            } else {
                response.put("messageType", "YouLost");
            } */
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write(response.toString());
            writer.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}
