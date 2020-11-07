package main.java;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AutoMessageCheck {

    private StartGameTask startGameTask;
    private LocationUpdateTask locationUpdateTask;
    private SuggestionTask suggestionTask;
    private ContradictTask contradictTask;

    public static Timer startGameTimer;
    public static Timer suggestionTimer;
    public static Timer contradictTimer;
    public static Timer locationUpdateTimer;

    private String gameUUID;
    private String playerName;

    /**
     * Function: AutoMessageCheck()
     * Description: Constructor for AutoMessageCheck class
     * Does not return but initializes all data members and schedules thread
     *      for querying database
     **/
    public AutoMessageCheck(){
    }

    public void startGameAutoMessageCheck(String gameUUId) {
        gameUUID = gameUUId;
        startGameTask = new StartGameTask(gameUUID);
        startGameTimer = new Timer();
        startGameTimer.schedule(startGameTask, 1000, 5000); // Run every 5 minutes (300000)
    }

    public void suggestionAutoMessageCheck(String gameUUId, String pName) {
        gameUUID = gameUUId;
        playerName = pName;
        suggestionTask = new SuggestionTask(gameUUID, pName); // when someone makes a suggestion,
                                                              // everyone needs to be alerted and game
                                                              // shifts to a state where player either disproves or passes
        suggestionTimer = new Timer();
        suggestionTimer.schedule(suggestionTask, 1000, 2000); // Run every 5 minutes (300000)

    }
    public void contradictAutoMessageCheck(String gameUUId, String pName) {
        gameUUID = gameUUId;
        playerName = pName;
        contradictTask = new ContradictTask(gameUUID, pName); // when someone makes a suggestion,
        // everyone needs to be alerted and game
        // shifts to a state where player either disproves or passes
        contradictTimer = new Timer();
        contradictTimer.schedule(contradictTask, 1000, 2000); // Run every 5 minutes (300000)

    }

    public void locationUpdateAutoMessageCheck(String gameUUId, String pName) {
        gameUUID = gameUUId;
        playerName = pName;
        locationUpdateTask = new LocationUpdateTask(gameUUID, pName); // when someone makes a suggestion,
        // everyone needs to be alerted and game
        // shifts to a state where player either disproves or passes
        locationUpdateTimer = new Timer();
        locationUpdateTimer.schedule(locationUpdateTask, 1000, 2000); // Run every 5 minutes (300000)

    }

    public StringBuilder getStartGameResponse() {
        StringBuilder startGame = startGameTask.getStartGameResponse();
        return startGame;
    }

    public StringBuilder getSuggestionResponse() {
        StringBuilder suggestion = suggestionTask.getSuggestionResponse();
        return suggestion;
    }

    public StringBuilder getContradictResponse() {
        StringBuilder contradict = contradictTask.getContradictResponse();
        return contradict;
    }

    public StringBuilder getLocationUpdateResponse() {
        StringBuilder locationUpdate = locationUpdateTask.getLocationUpdateResponse();
        return locationUpdate;
    }
}


class LocationUpdateTask extends TimerTask {
    private String gameID;
    private String playerName;
    private volatile StringBuilder locationUpdateResponse;

    public LocationUpdateTask(String gameID, String pName) {
        // Constructor for subclass
        this.gameID = gameID;
        this.playerName = pName;
        locationUpdateResponse = null;    }

    /**
     * Function: run()
     * Description: Will run every X minutes querying GET messages in database
     * Does not return
     **/
    @Override
    public void run() {
        // periodically query game server for msgs
        try {
            locationUpdateResponse = ClueLessUtils.makeGet(gameID, playerName, "playerLocation");
            //JSONObject suggestionJSON = new JSONObject(suggestionResponse.toString());
            //String msg = suggestionJSON.get("messageType").toString();
            //TODO: should this ever stop?

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder getLocationUpdateResponse() {
        return locationUpdateResponse;
    }
}

class ContradictTask extends TimerTask {
    private String gameID;
    private String playerName;
    private volatile StringBuilder contradictResponse;

    public ContradictTask(String gameID, String pName) {
        // Constructor for subclass
        this.gameID = gameID;
        this.playerName = pName;
        contradictResponse = null;
    }

    /**
     * Function: run()
     * Description: Will run every X minutes querying GET messages in database
     * Does not return
     **/
    @Override
    public void run() {
        // periodically query game server for msgs
        try {
            contradictResponse = ClueLessUtils.makeGet(gameID, playerName, "disprove");
            //JSONObject suggestionJSON = new JSONObject(suggestionResponse.toString());
            //String msg = suggestionJSON.get("messageType").toString();
            //TODO: should this ever stop?

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder getContradictResponse() {
        return contradictResponse;
    }
}

class SuggestionTask extends TimerTask {
    private String gameID;
    private String playerName;
    private volatile StringBuilder suggestionResponse;

    public SuggestionTask(String gameID, String pName) {
        // Constructor for subclass
        this.gameID = gameID;
        this.playerName = pName;
        suggestionResponse = null;
    }

    /**
     * Function: run()
     * Description: Will run every X minutes querying GET messages in database
     * Does not return
     **/
    @Override
    public void run() {
        // periodically query game server for msgs
        try {
            suggestionResponse = ClueLessUtils.makeGet(gameID, playerName, "suggestion");
            //JSONObject suggestionJSON = new JSONObject(suggestionResponse.toString());
            //String msg = suggestionJSON.get("messageType").toString();
            //TODO: should this ever stop?

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder getSuggestionResponse() {
        return suggestionResponse;
    }
}

/********** Class StartGameTask **********/
class StartGameTask extends TimerTask {
    private String gameID;
    private volatile StringBuilder startGameResponse;

    public StartGameTask(String gameID) {
        this.gameID = gameID;
        startGameResponse = null;
    }

    /**
     * Function: run()
     * Description: Will run every X minutes querying GET messages in database
     * Does not return
     **/
    @Override
    public void run() {
        String msg = " ";
        if (startGameResponse != null) {
            JSONObject startGameJSON = new JSONObject(startGameResponse.toString());
            msg = startGameJSON.get("messageType").toString();
            if (msg.equals("startGame")) {
                // Kills thread when game has started
                //TODO: Take this out; for peace of mind
                System.out.println("Killing Thread");
                AutoMessageCheck.startGameTimer.cancel();
                AutoMessageCheck.startGameTimer.purge();
            }
        }

        if (!msg.equals("startGame")) {
            // periodically query game server for msgs
            try {
                startGameResponse = ClueLessUtils.makeGet(gameID, "whoCares", "startGame");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public StringBuilder getStartGameResponse() {
        return startGameResponse;
    }
}
/********** Class StartGameTask **********/