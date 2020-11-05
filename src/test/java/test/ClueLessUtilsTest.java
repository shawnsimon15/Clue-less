package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import main.java.ClueLessUtils;

class ClueLessUtilsTest {
	private UUID gameUUID = UUID.randomUUID();
	
	/**
	 * Test Case: Make Post - Create Game
	 * Description: Create game for player Miss Scarlet by calling 
	 * Expected to return 200 if successfully created game
	 */
	@org.junit.jupiter.api.Test
	public void makePostTestCreateGame() {	
		int result;
		try {
			result = ClueLessUtils.makePost(gameUUID.toString(), "Miss Scarlet", 3, 
					"createGame");

			System.out.print("Create Game: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			assertEquals(200, result);
		} catch (IOException e) {
			fail(e);
		}
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

 		int result = ClueLessUtils.makePut(movePlayer.toString(), 
 				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
 				+ "/Test/game");

 		System.out.print("Make Put - Move Player: ");
 		System.out.println(result);
		System.out.println("---------------------------------------------");
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

 		int result = ClueLessUtils.makePut(makeSuggestion.toString(), 
 				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
 				+ "/Test/game");

 		System.out.print("Make Put - Make Suggestion: ");
 		System.out.println(result);
		System.out.println("---------------------------------------------");
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

 		int result = ClueLessUtils.makePut(passSuggestion.toString(), 
 				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
 				+ "/Test/game");

 		System.out.print("Make Put - Pass Suggestion: ");
 		System.out.println(result);
		System.out.println("---------------------------------------------");
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

 		int result = ClueLessUtils.makePut(endTurn.toString(), 
 				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
 				+ "/Test/game");

 		System.out.print("Make Put - End Turn: ");
 		System.out.println(result);
		System.out.println("---------------------------------------------");
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

 		int result = ClueLessUtils.makePut(disproveSuggestion.toString(), 
 				"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
 				+ "/Test/game");

 		System.out.print("Make Put - Disprove Suggestion: ");
		System.out.println("---------------------------------------------");
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

 		StringBuilder result;
		try {
			result = ClueLessUtils.makeGet(joinGame.toString(), 
					"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
					+ "/Test/game/" + gameUUID);
	 		JSONObject resultJson = new JSONObject(result.toString());
	 		System.out.print("Make Get - Join Game: ");
	 		System.out.println(result);
			System.out.println("---------------------------------------------");
	 		
	 		assertEquals("welcomeToGame", resultJson.get("messageType"));
		} catch (IOException e) {
			fail(e);
		}
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

 		StringBuilder result;
		try {
			result = ClueLessUtils.makeGet(makeAccusation.toString(), 
					"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
					+ "/Test/game/" + gameUUID);

	 		JSONObject resultJson = new JSONObject(result.toString());
	 		
	 		System.out.print("Make Get - Make Accusation: ");
	 		System.out.println(result);
			System.out.println("---------------------------------------------");
	 		assertEquals("accusationResult", resultJson.get("messageType"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

 	}
 	
 	/**
	 * Test Case: Make Get - Start Game
	 * Description: When a game starts
	 * Expected to return startGame for messageType if message successfully 
	 * sent to Server
	 */
	@org.junit.jupiter.api.Test
	public void makeGetTestStarGame() throws JSONException {	
		
		StringBuilder result;
		UUID startGameUUID = UUID.randomUUID();
		
		//Create Mr Green
		JSONObject greenJoinGame = new JSONObject();
		greenJoinGame.put("gameID", gameUUID);
		greenJoinGame.put("playerName", "Mr. Green");
		
		//Create Mrs. White
 		JSONObject whiteJoinGame = new JSONObject();
 		whiteJoinGame.put("gameID", gameUUID);
 		whiteJoinGame.put("playerName", "Mrs. White");
		
		try {
			//Miss Scarlet creates game
			ClueLessUtils.makePost(startGameUUID.toString(), "Miss Scarlet", 3,
					"createGame");
			
			//Mr. Green joins game
			ClueLessUtils.makeGet(greenJoinGame.toString(), 
					"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
					+ "/Test/game/" + startGameUUID);
			
			//Mrs. White joins game
			ClueLessUtils.makeGet(whiteJoinGame.toString(), 
					"https://jroe630mfb.execute-api.us-east-2.amazonaws.com"
					+ "/Test/game/" + startGameUUID);
			
			
			result = ClueLessUtils.makeGet(startGameUUID.toString(), 
					"startGame"); 
					
			System.out.print("Start Game: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			
			JSONObject resultJson = new JSONObject(result.toString());
			
			assertEquals("startGame", resultJson.get("messageType").toString());
			
		} catch (IOException e) {
			fail(e);
		}
	}
	
	/**
	 * Test Case: Make Get - Turn Update
	 * Description: When a player turn needs to be updated
	 * Expected to return playerTurnUpdate for messageType if message 
	 * successfully sent to Server
	 */
	@org.junit.jupiter.api.Test
	public void makeGetTestTurnUpdate() throws JSONException {	
		
		StringBuilder result;
		try {
			
			result = ClueLessUtils.makeGet(String.valueOf(gameUUID), 
					"turnUpdate"); 
					
			System.out.print("Turn Update: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			
			JSONObject resultJson = new JSONObject(result.toString());
			
			assertEquals("playerTurnUpdate", 
					resultJson.get("messageType").toString());
			
		} catch (IOException e) {
			fail(e);
		}
	}
	
	/**
	 * Test Case: Make Get - Location Update
	 * Description: When a player needs location update
	 * Expected to return locationUpdate for messageType if message 
	 * successfully sent to Server
	 */
	@org.junit.jupiter.api.Test
	public void makeGetTestLocationUpdate() throws JSONException {	
		
		StringBuilder result;
		try {
			
			result = ClueLessUtils.makeGet(String.valueOf(gameUUID), 
					"locationUpdate"); 
					
			System.out.print("Turn Update: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			
			JSONObject resultJson = new JSONObject(result.toString());
			
			assertEquals("locationUpdate", 
					resultJson.get("messageType").toString());
			
		} catch (IOException e) {
			fail(e);
		}
	}

	/**
	 * Test Case: Make Get - Suggestion Made
	 * Description: When a suggestion is made
	 * Expected to return suggestionMade for messageType if message 
	 * successfully sent to Server
	 */
	@org.junit.jupiter.api.Test
	public void makeGetTestSuggestionMade() throws JSONException {	
		
		StringBuilder result;
		try {
			
			result = ClueLessUtils.makeGet(String.valueOf(gameUUID), 
					"suggestion"); 
					
			System.out.print("Suggestion Made: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			
			JSONObject resultJson = new JSONObject(result.toString());
			
			assertEquals("suggestionMade", 
					resultJson.get("messageType").toString());
			
		} catch (IOException e) {
			fail(e);
		}
	}
	
	/**
	 * Test Case: Make Get - Contradict Suggestion
	 * Description: When a contradiction to suggestion was made
	 * Expected to return contradictSuggestion for messageType if message 
	 * successfully sent to Server
	 */
	@org.junit.jupiter.api.Test
	public void makeGetTestContradictSuggestion() throws JSONException {	
		
		StringBuilder result;
		try {
			
			result = ClueLessUtils.makeGet(String.valueOf(gameUUID), 
					"contradict"); 
					
			System.out.print("Contradict Suggestion: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			
			JSONObject resultJson = new JSONObject(result.toString());
			assertEquals("contradictSuggestion", 
					resultJson.get("messageType").toString());
			
		} catch (IOException e) {
			fail(e);
		}
	}
}
