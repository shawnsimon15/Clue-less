package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import main.java.*;

class AutoMessageCheckTest {

	@org.junit.jupiter.api.Test
	public void startAutoMessageCheckTest()
	{
		UUID gameUUID = UUID.randomUUID();
		String playerName = "Miss Scarlet";
		
		ClueLessUtils.makePost(gameUUID, playerName, 3);
		
		JSONObject movePlayer = new JSONObject();
		movePlayer.put("gameID", gameUUID);
		movePlayer.put("playerName", playerName);
		movePlayer.put("newLocation", "Ballroom");
		
		ClueLessUtils.makePut(movePlayer.toString(), "");
		
		AutoMessageCheck autoMsgCheck = new AutoMessageCheck();
		
		autoMsgCheck.startAutoMessageCheck(gameUUID, playerName);
		
	}

}
