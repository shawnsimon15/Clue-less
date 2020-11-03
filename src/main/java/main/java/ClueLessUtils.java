package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;

public class ClueLessUtils {
    private static String url = "https://jroe630mfb.execute-api.us-east-2.amazonaws.com/Test/game/";

    // setUpHttpConnection is called each time the a GET, PUT or POST request need to be made
    public static HttpURLConnection setUpHttpConnection(String requstMethod) throws IOException {
        URL myUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) myUrl.openConnection();
        con.setRequestMethod(requstMethod);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        return con;
    }

    // makeGet will be called when POST HTTP Request method is needed
    public static int makePost(String gameUUID, String playerName,
                               int numberOfPlayers, String typeOfPost) throws IOException {
        // make http request
        HttpURLConnection con = setUpHttpConnection("POST");
        JSONObject jsonObject = new JSONObject();
        if (typeOfPost.equals("createGame")) {
            jsonObject.put("messageType", "CreateGame");
            jsonObject.put("gameID", gameUUID);
            jsonObject.put("selectedPlayer", playerName);
            jsonObject.put("numberOfPlayers", numberOfPlayers);
        } else if (typeOfPost.equals("disproveSuggestion")) {
            jsonObject.put("messageType", "DisproveSuggestion");
            jsonObject.put("gameID", gameUUID);
            jsonObject.put("playerWhoSuggested", playerName);
            jsonObject.put("cardRevealed", "Blanket");
        } else if (typeOfPost.equals("endTurn")) {
            jsonObject.put("messageType", "EndTurn");
            jsonObject.put("gameID", gameUUID);
            jsonObject.put("playerFinishedTurn", playerName);
            jsonObject.put("nextPlayer", "TOK");
        } else if (typeOfPost.equals("joinGame")) {
            jsonObject.put("messageType", "JoinGame");
            jsonObject.put("gameID", gameUUID);
            jsonObject.put("playerName", playerName);
        } else if (typeOfPost.equals("movePlayer")) {
            jsonObject.put("messageType", "MovePlayer");
            jsonObject.put("gameID", gameUUID);
            jsonObject.put("playerName", playerName);
            jsonObject.put("newLocation", "Place");
        } else if (typeOfPost.equals("passSuggestion")) {
            jsonObject.put("messageType", "PassSuggestion");
            jsonObject.put("gameID", gameUUID);
            jsonObject.put("playerWhoSuggested", playerName);
            jsonObject.put("nextPlayer", "Billy");
        } else if (typeOfPost.equals("suggestion")) {
            jsonObject.put("messageType", "MakeSuggestion");
            jsonObject.put("gameID", gameUUID);
            jsonObject.put("playerWhoSuggested", playerName);
            JSONObject cardsSuggested = new JSONObject();
            cardsSuggested.put("suspect", "Who");
            cardsSuggested.put("weapon", "What");
            cardsSuggested.put("location", "Where");
            jsonObject.put("cardsSuggested", cardsSuggested);
        } else if (typeOfPost.equals("gameOver")) {
            jsonObject.put("messageType", "GameOver");
            jsonObject.put("gameID", gameUUID);
            jsonObject.put("playerWhoWon", playerName);
        } else if (typeOfPost.equals("playerLost")) {
            jsonObject.put("messageType", "PlayerLost");
            jsonObject.put("gameID", gameUUID);
            jsonObject.put("playerWhoWon", playerName);
        }
        String requestString = jsonObject.toString();

        try(OutputStream outStream = con.getOutputStream()) {
            outStream.write(requestString.getBytes("UTF-8"));
        }
        int code = con.getResponseCode();
        //System.out.println("Reponse for " + typeOfPost + ": " + code);

        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))){
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            //System.out.println(response.toString());
        }
        return code;
    }

    // makeGet will be called when PUT HTTP Request method is needed
    public static int makePut(String jsonMessage) {
        // make http request
        return 200;
    }

    // makeGet will be called when GET HTTP Request method is needed
    public static StringBuilder makeGet(String gameID, String typeOfGet) throws IOException {
        // make http request
        HttpURLConnection con = null;
        StringBuilder response;
        String newUrl = url + gameID;
        switch (typeOfGet){
            case "makeAccusation":
                newUrl = newUrl + "?messageType=MakeAccusation";
                URL accusationURL = new URL(newUrl);
                con= (HttpURLConnection) accusationURL.openConnection();
                con.setRequestMethod("GET");
                // compare Winning secret to player's accusation
                break;
            case "startGame":
                newUrl = newUrl + "?messageType=RequestMessage&Type=startGame";
                URL startGameURL = new URL(newUrl);
                con= (HttpURLConnection) startGameURL.openConnection();
                con.setRequestMethod("GET");
                break;
            case "turnUpdate":
                newUrl = newUrl + "?messageType=RequestMessage&Type=turnUpdate";
                URL turnUpdateURL = new URL(newUrl);
                con = (HttpURLConnection) turnUpdateURL.openConnection();
                con.setRequestMethod("GET");
                break;
            case "locationUpdate":
                newUrl = newUrl + "?messageType=RequestMessage&Type=locationUpdate";
                URL locationUpdateURL = new URL(newUrl);
                con = (HttpURLConnection) locationUpdateURL.openConnection();
                con.setRequestMethod("GET");
                break;
            case "suggestion":
                newUrl = newUrl + "?messageType=RequestMessage&Type=suggestion";
                URL suggestionURL = new URL(newUrl);
                con = (HttpURLConnection) suggestionURL.openConnection();
                con.setRequestMethod("GET");
                break;
            case "contradict":
                newUrl = newUrl + "?messageType=RequestMessage&Type=contradict";
                URL contradictURL = new URL(newUrl);
                con = (HttpURLConnection) contradictURL.openConnection();
                con.setRequestMethod("GET");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + typeOfGet);

        }

        int code = con.getResponseCode();
        //System.out.println(typeOfGet +": " + code);
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String line;
            response = new StringBuilder();
            while ((line = in.readLine()) != null) {
                response.append(line);
                response.append(System.lineSeparator());
            }
        }
        return response;
    }

    public static String parseMessages(String jsonMessage) {
        //
        JsonParser parser = new JsonParser();
        JsonObject event = (JsonObject) parser.parse(jsonMessage);

        if(event.get("messageType").toString() == "playerTurnUpdate") {
            // get playerName from currentTurn
            // main.updateGameBoard(playerName);
        }
        else if(event.get("messageType").toString() == "locationUpdate") {
            // get location from playerName1
            // get location from playerName2
            // main.updateGameBoard(playerName ,);
        }
        else if(event.get("messageType").toString() == "suggestionMade") {
            // get playerName from playerWhoSuggested
            // get suspect from cardSuggested
            // get weapon from cardSuggested
            // get location from cardSuggested
            // get UUID from suggestionID
        }
        else if(event.get("messageType").toString() == "contradictSuggestion") {
            // get playerName from playerWhoSuggested
            // get UUID from suggestionID
        }
        else if(event.get("messageType").toString() == "startGame") {
            // get players from activePlayers
            // get cards from assignedCards
            // main.startGame(pList); // playerList from http request
        }
        else {
            System.out.println("Did not recognize JSON");
        }


        return "";
    }
}
