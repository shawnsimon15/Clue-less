package test;

import java.util.UUID;
import org.json.simple.JSONObject;
import main.java.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClueLessUtilsTest {
	private UUID gameUUID;
	
	@org.junit.jupiter.api.Test
	public void makePostTest()
	{
		gameUUID = UUID.randomUUID();
		ClueLessUtils.makePost(gameUUID, "Miss Scarlet", 3);
	}
	@org.junit.jupiter.api.Test
	public void makePutTest()
	{
		JSONObject movePlayer = new JSONObject();
		movePlayer.put("gameID", gameUUID);
		movePlayer.put("playerName", "Miss Scarlet");
		movePlayer.put("newLocation", "Ballroom");
		
		ClueLessUtils.makePut(movePlayer.toString(), "");
	}
	@org.junit.jupiter.api.Test
	public void makeGetTest()
	{
		JSONObject joinGame = new JSONObject();
		joinGame.put("gameID", gameUUID);
		joinGame.put("playerName", "Mr. Green");
		
		ClueLessUtils.makeGet(joinGame.toString(), "");
	}
	@org.junit.jupiter.api.Test
	public void parseMessagesTest() 
	{
		JSONObject makeSuggestion = new JSONObject();
		makeSuggestion.put("gameID", gameUUID);
		makeSuggestion.put("playerWhoSuggested", "Mr. Green");
		JSONObject suggestions = new JSONObject();
		suggestions.put("suspect", "Miss Scarlet");
		suggestions.put("weapon", "Knife");
		suggestions.put("location", "Kitchen");
		
		makeSuggestion.put("cardsSuggested", suggestions);
		
		ClueLessUtils.parseMessages(makeSuggestion.toString());
		
	}
}
