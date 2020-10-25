package test;

import java.util.UUID;
import org.json.simple.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import main.java.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClueLessUtilsTest {
	private UUID gameUUID = UUID.randomUUID();
	
	
	@org.junit.jupiter.api.Test
	public void makePostTest()
	{	//Create Game
		String result = ClueLessUtils.makePost(gameUUID, "Miss Scarlet", 3);
		assertEquals("200", result);
	}
	@org.junit.jupiter.api.Test
	public void makePutTest()
	{	//Move Player
		JSONObject movePlayer = new JSONObject();
		movePlayer.put("gameID", gameUUID);
		movePlayer.put("playerName", "Miss Scarlet");
		movePlayer.put("newLocation", "Ballroom");
		
		String result = ClueLessUtils.makePut(movePlayer.toString(), "");
		
		assertEquals("200", result);
	}
	@org.junit.jupiter.api.Test
	public void makeGetTest()
	{	//Join Game
		JSONObject joinGame = new JSONObject();
		joinGame.put("gameID", gameUUID);
		joinGame.put("playerName", "Mr. Green");
		
		String result = ClueLessUtils.makeGet(joinGame.toString(), "");
		JsonParser parser = new JsonParser();
        JsonObject resultObj = (JsonObject) parser.parse(result);
		
		assertEquals("welcomeToGame", resultObj.get("messageType"));
		
		
	}
	
	
	
	/*
	 * Tests for other scenarios
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
		
		String result = ClueLessUtils.makePut(makeSuggestion.toString(), "");
		
		assertEquals("200", result);
	}
	
	@org.junit.jupiter.api.Test
	public void makePutTestPassSuggestion()
	{
		JSONObject passSuggestion = new JSONObject();
		passSuggestion.put("messageType", "PassSuggestion");
		passSuggestion.put("gameID", gameUUID);
		passSuggestion.put("playerWhoSuggested", "Mr. Green");
		passSuggestion.put("nextPlayer", "Miss Scarlet");
		
		String result = ClueLessUtils.makePut(passSuggestion.toString(), "");
		
		assertEquals("200", result);
	}
	@org.junit.jupiter.api.Test
	public void makePutTestEndTurn()
	{
		JSONObject endTurn = new JSONObject();
		endTurn.put("gameID", gameUUID);
		endTurn.put("playerFinishedTurn", "Miss Scarlet");
		endTurn.put("newLocation", "Mr.Green");
		
		String result = ClueLessUtils.makePut(endTurn.toString(), "");
		
		assertEquals("200", result);
	}

	@org.junit.jupiter.api.Test
	public void makePutTestDisproveSuggestion()
	{
		JSONObject disproveSuggestion = new JSONObject();
		disproveSuggestion.put("messageType", "disporveSuggestion");
		disproveSuggestion.put("gameID", gameUUID);
		disproveSuggestion.put("playerWhoSuggested", "Mr. Green");
		disproveSuggestion.put("cardReveled", "Kitchen");
		
		String result = ClueLessUtils.makePut(disproveSuggestion.toString(), "");
		
		assertEquals("200", result);
	}
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
		
		String result = ClueLessUtils.makeGet(makeAccusation.toString(), "");
		JsonParser parser = new JsonParser();
        JsonObject resultObj = (JsonObject) parser.parse(result);
		
		assertEquals("messageType", resultObj.get("accusationResult"));
		
	}
	
}
