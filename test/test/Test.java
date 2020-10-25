package test;

import main.java.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

import lambdaFunctions.ClueLessHandler;


class Test {

	@org.junit.jupiter.api.Test
	public void testPlayerStatusName() {
		PlayerStatus testStatus = new PlayerStatus();
		
		testStatus.setPlayerName("Miss Scarlett");
		
		assertEquals(testStatus.getPlayerName(), "Miss Scarlett");
		
	}
	
	@org.junit.jupiter.api.Test
	public void testPlayerStatusLocation() {
		PlayerStatus testStatus = new PlayerStatus();
		testStatus.setPlayerLocation("Ballroom");
		assertEquals(testStatus.getPlayerLocation(), "Ballroom");
	}
	
	@org.junit.jupiter.api.Test
	public void testPlayerStatusHands() {
		PlayerStatus testStatus = new PlayerStatus();
		
		List<String> expectedHands = new ArrayList<String>();
		expectedHands.add("Knife");
		expectedHands.add("Mrs. White");
		expectedHands.add("Kitchen");
		
		testStatus.setPlayerHand(expectedHands);
		
		List<String> getPlayerHands = testStatus.getPlayerHand();
		
		assertEquals(getPlayerHands, expectedHands);
		
	}
	
	@org.junit.jupiter.api.Test
	public void testGameStatus() {
		PlayerStatus statusA = new PlayerStatus();
		PlayerStatus statusB = new PlayerStatus();
		PlayerStatus statusC = new PlayerStatus();
		statusA.setPlayerName("Miss Scarlett");
		statusB.setPlayerName("Ms. White");
		statusC.setPlayerName("Mr. Green");
		
		List<PlayerStatus> playerStatusList = new ArrayList<PlayerStatus>();
		
		playerStatusList.add(statusA);
		playerStatusList.add(statusB);
		playerStatusList.add(statusC);
		
		GameStatus test = new GameStatus();
		
		test.setActivePlayerList(playerStatusList);
		
		List<PlayerStatus> actualList = test.getActivePlayerList();
		
		assertEquals(actualList.get(0).getPlayerName(), playerStatusList.get(0).getPlayerName());
	}
	
	@org.junit.jupiter.api.Test
	public void testClientToDB() throws IOException {
		JSONObject createGameJS = new JSONObject();
		createGameJS.put("messageType", "CreateGame");
		createGameJS.put("gameID", "1");
		createGameJS.put("selectedPlayer", "Miss Scarlett");
		createGameJS.put("numberOfPlayer", "3");
		
		InputStream in = new ByteArrayInputStream(createGameJS.toString().getBytes());
		
		ClueLessHandler test = new ClueLessHandler();
		test.handleRequest(in, null,  null);
		
		//check db
		
	}
}
	