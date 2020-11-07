package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import main.java.ClueLessUtils;

class ClueLessUtilsTest {
	private String gameUUID = "1234567";
	/**
	 * Test Case: Make Post - Create Game
	 * Description: Create game for player Miss Scarlet by calling 
	 * Expected to return 200 if successfully created game
	 */
	@org.junit.jupiter.api.Test
	public void makePostTestCreateGame() {	
		int result;
		try {
			result = ClueLessUtils.makePost(gameUUID, "Miss Scarlet", 3, 
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
	 * Test Case: Make Post - Disprove Suggestion
	 * Description: Miss Scarlet disproves suggestion 
	 * Expected to return 200 if message successfully sent to server
	 */
	@org.junit.jupiter.api.Test
	public void makePostTestDisproveSuggestion() {	
		
		int result;
		try {
			result = ClueLessUtils.makePost(gameUUID, "Miss Scarlet", 3, 
					"disproveSuggestion");
			System.out.print("Disprove Suggestion: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			assertEquals(200, result);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	/**
	 * Test Case: Make Post - End Turn
	 * Description: Miss Scarlet ends her turn 
	 * Expected to return 200 if message successfully sent to server
	 */
	@org.junit.jupiter.api.Test
	public void makePostTestEndTurn() {	
		
		int result;
		try {
			result = ClueLessUtils.makePost(gameUUID, "Miss Scarlet", 3, 
					"endTurn");
			System.out.print("End Turn: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			assertEquals(200, result);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	/**
	 * Test Case: Make Post - Join Game
	 * Description: Mr. Green joins the game 
	 * Expected to return 200 if message successfully sent to server
	 */
	@org.junit.jupiter.api.Test
	public void makePostTestJoinGame() {	
		
		int result;
		try {
			result = ClueLessUtils.makePost(gameUUID, "Mr. Green", 3, 
					"joinGame");
			System.out.print("Join Game: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			assertEquals(200, result);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	/**
	 * Test Case: Make Post - Move Player
	 * Description: Miss Scarlet moves 
	 * Expected to return 200 if message successfully sent to server
	 */
	@org.junit.jupiter.api.Test
	public void makePostTestMovePlayer() {	
		
		int result;
		try {
			result = ClueLessUtils.makePost(gameUUID, "Miss Scarlet", 3, 
					"movePlayer");
			System.out.print("Move Player: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			assertEquals(200, result);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	/**
	 * Test Case: Make Post - Pass Suggestion
	 * Description: Miss Scarlet passes suggestion 
	 * Expected to return 200 if message successfully sent to server
	 */
	@org.junit.jupiter.api.Test
	public void makePostTestPassSuggestion() {	
		
		int result;
		try {
			result = ClueLessUtils.makePost(gameUUID, "Miss Scarlet", 3, 
					"passSuggestion");
			System.out.print("Pass Suggestion: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			assertEquals(200, result);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	/**
	 * Test Case: Make Post - Make Suggestion
	 * Description: Mr. Green makes suggestion 
	 * Expected to return 200 if message successfully sent to server
	 */
	@org.junit.jupiter.api.Test
	public void makePostTestMakeSuggestion() {	
		
		int result;
		try {
			result = ClueLessUtils.makePost(gameUUID, "Mr. Green", 3, 
					"suggestion");
			System.out.print("Pass Suggestion: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			assertEquals(200, result);
		} catch (IOException e) {
			fail(e);
		}
	}
	
	/**
	 * Test Case: Make Get - Make Accusation
	 * Description: A player makes accusation
	 * Expected to return Weapon if message successfully sent to Server
	 */
	@org.junit.jupiter.api.Test
	public void makeGetTestMakeAccusation() throws JSONException {	
		
		StringBuilder result;
		try {
			
			result = ClueLessUtils.makeGet(String.valueOf(gameUUID), "whoCares",
					"makeAccusation");
					
			System.out.print("Make Accusation: ");
			System.out.println(result);
			System.out.println("---------------------------------------------");
			
			JSONObject resultJson = new JSONObject(result.toString());
			
			assertEquals("Weapon", resultJson.get("weapon").toString());
			
		} catch (IOException e) {
			fail(e);
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
		try {
			ClueLessUtils.makePost("12345678", "Miss Scarlet", 3,
					"createGame");
			ClueLessUtils.makePost("12345678", "Mr. Green", 3,
					"joinGame");
			ClueLessUtils.makePost("12345678", "Mrs. White", 3,
					"joinGame");
			
			result = ClueLessUtils.makeGet(String.valueOf(12345678), "whoCares",
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
			
			result = ClueLessUtils.makeGet(String.valueOf(gameUUID), "whoCares",
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
			
			result = ClueLessUtils.makeGet(String.valueOf(gameUUID), "whoCares",
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
			
			result = ClueLessUtils.makeGet(String.valueOf(gameUUID), "Mrs.White",
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
	@Test
	public void makeGetTestContradictSuggestion() throws JSONException {	
		
		StringBuilder result;
		try {
			
			result = ClueLessUtils.makeGet(String.valueOf(gameUUID), "whoCares",
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
