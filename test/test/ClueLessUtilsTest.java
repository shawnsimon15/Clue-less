package test;

import java.util.UUID;

import org.json.simple.JSONObject;

import main.java.*;

public class ClueLessUtilsTest {
	
	@org.junit.jupiter.api.Test
	private void makePostTest()
	{
		UUID gameUUID = UUID.randomUUID();
		ClueLessUtils.makePost(gameUUID, "Miss Scarlet", 3);
		
		JSONObject getJS = new JSONObject();
		getJS.put("gameID", gameUUID);
		getJS.put("playerName", "Miss Scarlet");
		getJS.put("selectedPlayer", "Miss Scarlett");
		
		ClueLessUtils.makeGet(getJS.toString(), "");
		
	}
	@org.junit.jupiter.api.Test
	private void makePutTest()
	{
		
	}
	@org.junit.jupiter.api.Test
	private void makeGetTest()
	{
		
	}
	@org.junit.jupiter.api.Test
	private void parseMessagesTest() 
	{
		
	}
}
