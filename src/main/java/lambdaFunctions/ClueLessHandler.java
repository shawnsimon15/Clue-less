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
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDb = new DynamoDB(client);

        try {
            JSONObject event = (JSONObject) parser.parse(reader);

            System.out.println(event.get("messageType").toString());

            if (event.get("messageType").toString().equals("CreateGame")) {
                String uuid = event.get("gameID").toString();
                String playerName = event.get("selectedPlayer").toString();
                int numOfPlayers = Integer.parseInt(event.get("numberOfPlayers").toString());

                String gameStatus = "New Game"; // Logic needed to determine gameStatus
                String winningSecret = "Person, Weapon, Location"; // Logic needed to randomly pick secret

                dynamoDb.getTable(DYNAMODB_GAMEDATA)
                        .putItem(new PutItemSpec().withItem(new Item().withString("UUID", uuid)
                                .withString("Game Status", gameStatus)
                                .withString("Winning Secret", winningSecret)
                                .withString("Current Players", playerName)
                                .withInt("Max Players", numOfPlayers)));

            } else if(event.get("messageType").toString().equals("MakeSuggestion")) {
                String gameUUID = event.get("gameID").toString();
                String playerName = event.get("playerWhoSuggested").toString();
                JSONObject cardsSuggested = (JSONObject) event.get("cardsSuggested");

                String suspect = cardsSuggested.get("suspect").toString();
                String weapon = cardsSuggested.get("weapon").toString();
                String location = cardsSuggested.get("location").toString();

                // need way of generating UUID for msgs
                String msgUUID = "1234";
                String msg = "SuggestionMade: " + suspect +
                        ", " + weapon + ", " + location;

                dynamoDb.getTable(DYNAMODB_MESSAGES)
                        .putItem(new PutItemSpec().withItem(new Item().withString("UUID", msgUUID)
                                .withString("GameID", gameUUID)
                                .withString("Player Name", playerName)
                                .withString("Message", msg)));

            } else if(event.get("messageType").toString().equals("PassSuggestion")) {
                String gameUUID = event.get("gameID").toString();
                String playerName = event.get("playerWhoSuggested").toString();
                String nextPlayer = event.get("nextPlayer").toString();

                // need way of generating UUID for msgs
                String msgUUID = "1235";
                String msg = "PassSuggestion: " + nextPlayer;

                dynamoDb.getTable(DYNAMODB_MESSAGES)
                        .putItem(new PutItemSpec().withItem(new Item().withString("UUID", msgUUID)
                                .withString("GameID", gameUUID)
                                .withString("Player Name", playerName)
                                .withString("Message", msg)));

            } else if(event.get("messageType").toString().equals("EndTurn")) {
                String gameUUID = event.get("gameID").toString();
                String playerName = event.get("playerFinishedTurn").toString();
                String nextPlayer = event.get("nextPlayer").toString();

                // need way of generating UUID for msgs
                String msgUUID = "1236";
                String msg = "EndTurn: " + nextPlayer;

                dynamoDb.getTable(DYNAMODB_MESSAGES)
                        .putItem(new PutItemSpec().withItem(new Item().withString("UUID", msgUUID)
                                .withString("GameID", gameUUID)
                                .withString("Player Name", playerName)
                                .withString("Message", msg)));
            } else if(event.get("messageType").toString().equals("MovePlayer")) {
                String gameUUID = event.get("gameID").toString();
                String playerName = event.get("playerName").toString();
                String location = event.get("newLocation").toString();

                // need way of generating UUID for msgs
                String msgUUID = "1238";
                String msg = "MovePlayer: " + playerName + " moved to " + location;

                dynamoDb.getTable(DYNAMODB_MESSAGES)
                        .putItem(new PutItemSpec().withItem(new Item().withString("UUID", msgUUID)
                                .withString("GameID", gameUUID)
                                .withString("Player Name", playerName)
                                .withString("Message", msg)));
            } else if(event.get("messageType").toString().equals("DisproveSuggestion")) {
                String gameUUID = event.get("gameID").toString();
                String playerName = event.get("playerWhoSuggested").toString();
                String card = event.get("cardRevealed").toString();

                // need way of generating UUID for msgs
                String msgUUID = "1239";
                String msg = "DisproveSuggestion: " + playerName + " revealed " + card;

                dynamoDb.getTable(DYNAMODB_MESSAGES)
                        .putItem(new PutItemSpec().withItem(new Item().withString("UUID", msgUUID)
                                .withString("GameID", gameUUID)
                                .withString("Player Name", playerName)
                                .withString("Message", msg)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void handleGet(InputStream inputStream,
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

            if (event.get("messageType").toString().equals("JoinGame")) {
                String gameUUID = event.get("gameID").toString();
                String playerName = event.get("playerName").toString();

                game = dynamoDb.getTable(DYNAMODB_GAMEDATA).getItem("UUID", gameUUID);

                if (game != null) {
                    // TODO: should be an array or an object of GameStatus
                    String currentPlayers = game.get("Current Players").toString() + ", " + playerName;

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
            } else if (event.get("messageType").toString().equals("MakeAccusation")) {
                String gameUUID = event.get("gameID").toString();
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

            }

            // Write response back to client
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write(response.toString());
            writer.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
