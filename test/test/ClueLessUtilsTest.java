package test;

import java.util.UUID;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import main.java.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClueLessUtilsTest {
	private UUID gameUUID = UUID.randomUUID();
	
	/**
	 * Test Case: Make Post - Create Game
	 * Description: Create game for player Miss Scarlet by calling 
	 * Expected to return 200 if successfully created game
	 */
	@org.junit.jupiter.api.Test
	public void makePostTestCreateGame() {	
		String result = ClueLessUtils.makePost(gameUUID, "Miss Scarlet", 3);
		System.out.print("Make Post - Create Game: ");
		System.out.println(result);
		assertEquals("200", result);
	}
	
	/**
	 * Test Case: Make Put - Move Player
	 * Description: Move Player Miss Scarlet to Ballroom 
	 * Expected to return 200 if successfully moved player
	 */
	@org.junit.jupiter.api.Test
	public void makePutTestMovePlayer() {	
		JSONObject movePlayer = new JSONObject();
		movePlayer.put("gameID", gameUUID);
		movePlayer.put("playerName", "Miss Scarlet");
		movePlayer.put("newLocation", "Ballroom");
		
		String result = ClueLessUtils.makePut(movePlayer.toString(), 
				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
				+ "/Test/game");
		
		System.out.print("Make Put - Move Player: ");
		System.out.println(result);
		assertEquals("200", result);
	}
	
	/**
	 * Test Case: Make Put - Make Suggestion
	 * Description: Mr. Green making suggestion 
	 * Expected to return 200 if suggestion successfully sent to server
	 */
	@org.junit.jupiter.api.Test
	public void makePutTestMakeSuggestion()
	{
		JSONObject makeSuggestion = new JSONObject();
		makeSuggestion.put("gameID", gameUUID);
		makeSuggestion.put("playerWhoSuggested", "Mr. Green");
		JSONObject suggestions = new JSONObject();
		suggestions.put("suspect", "Miss Scarlet");
		suggestions.put("weapon", "Knife");
		suggestions.put("location", "Kitchen");
		makeSuggestion.put("cardsSuggested", suggestions);
		
		String result = ClueLessUtils.makePut(makeSuggestion.toString(), 
				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
				+ "/Test/game");
		
		System.out.print("Make Put - Make Suggestion: ");
		System.out.println(result);
		assertEquals("200", result);
	}
	
	/**
	 * Test Case: Make Put - Pass Suggestion
	 * Description: Mr. Green passing suggestion 
	 * Expected to return 200 if message successfully sent to server
	 */
	@org.junit.jupiter.api.Test
	public void makePutTestPassSuggestion()
	{
		JSONObject passSuggestion = new JSONObject();
		passSuggestion.put("messageType", "PassSuggestion");
		passSuggestion.put("gameID", gameUUID);
		passSuggestion.put("playerWhoSuggested", "Mr. Green");
		passSuggestion.put("nextPlayer", "Miss Scarlet");
		
		String result = ClueLessUtils.makePut(passSuggestion.toString(), 
				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
				+ "/Test/game");
		
		System.out.print("Make Put - Pass Suggestion: ");
		System.out.println(result);
		assertEquals("200", result);
	}
	
	/**
	 * Test Case: Make Put - End Turn
	 * Description: Miss Scarlet ending her turn 
	 * Expected to return 200 if message successfully sent to server
	 */
	@org.junit.jupiter.api.Test
	public void makePutTestEndTurn()
	{
		JSONObject endTurn = new JSONObject();
		endTurn.put("gameID", gameUUID);
		endTurn.put("playerFinishedTurn", "Miss Scarlet");
		endTurn.put("newLocation", "Mr.Green");
		
		String result = ClueLessUtils.makePut(endTurn.toString(), 
				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
				+ "/Test/game");
		
		System.out.print("Make Put - End Turn: ");
		System.out.println(result);
		assertEquals("200", result);
	}

	/**
	 * Test Case: Make Put - Disprove Suggestion
	 * Description: Disproving Mr. Green's suggestion
	 * Expected to return 200 if message successfully sent to server
	 */
	@org.junit.jupiter.api.Test
	public void makePutTestDisproveSuggestion()
	{
		JSONObject disproveSuggestion = new JSONObject();
		disproveSuggestion.put("messageType", "disporveSuggestion");
		disproveSuggestion.put("gameID", gameUUID);
		disproveSuggestion.put("playerWhoSuggested", "Mr. Green");
		disproveSuggestion.put("cardReveled", "Kitchen");
		
		String result = ClueLessUtils.makePut(disproveSuggestion.toString(), 
				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
				+ "/Test/game");
		
		System.out.print("Make Put - Disprove Suggestion: ");
		System.out.println(result);
		assertEquals("200", result);
	}
	
	/**
	 * Test Case: Make Get - Join Game
	 * Description: Let Mr. Green to Join Game 
	 * Expected to return "welcomeToGame" if successfully moved player
	 */
	@org.junit.jupiter.api.Test
	public void makeGetTestJoinGame() {	
		JSONObject joinGame = new JSONObject();
		joinGame.put("gameID", gameUUID);
		joinGame.put("playerName", "Mr. Green");
		
		String result = ClueLessUtils.makeGet(joinGame.toString(), 
				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
				+ "/Test/game/" + gameUUID);
		JsonParser parser = new JsonParser();
        JsonObject resultObj = (JsonObject) parser.parse(result);
        
        System.out.print("Make Get - Join Game: ");
        System.out.println(result);
		assertEquals("welcomeToGame", resultObj.get("messageType"));
	}
	
	/**
	 * Test Case: Make Get - Make Accusation
	 * Description: Mr. Green making accusation
	 * Expected to return accusationResult for messageType if message 
	 * successfully sent to server
	 */
	@org.junit.jupiter.api.Test
	public void makeGetTestMakeAccusation()
	{
		JSONObject makeAccusation = new JSONObject();
		makeAccusation.put("gameID", gameUUID);
		makeAccusation.put("playerWhoAccused", "Mr. Green");
		JSONObject suggestions = new JSONObject();
		suggestions.put("suspect", "Miss Scarlet");
		suggestions.put("weapon", "Knife");
		suggestions.put("location", "Ballroom");
		makeAccusation.put("cardsSuggested", suggestions);
		
		String result = ClueLessUtils.makeGet(makeAccusation.toString(), 
				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
				+ "/Test/game/" + gameUUID);
		JsonParser parser = new JsonParser();
        JsonObject resultObj = (JsonObject) parser.parse(result);
        
        System.out.print("Make Get - Make Accusation: ");
        System.out.println(result);
		assertEquals("accusationResult", resultObj.get("messageType"));
		
	}
}
