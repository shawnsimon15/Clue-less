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
    private GameOver_PlayerLostTask gOPLTask;
    private TurnUpdateTask turnUpdateTask;

    public static Timer startGameTimer;
    public static Timer suggestionTimer;
    public static Timer contradictTimer;
    public static Timer locationUpdateTimer;
    public static Timer gOPLTimer;
    public static Timer turnUpdateTimer;

    private Game ui;
    private String gameUUID;

    /**
     * Function: AutoMessageCheck()
     * Description: Constructor for AutoMessageCheck class
     * Does not return but initializes all data members and schedules thread
     *      for querying database
     **/
    public AutoMessageCheck(Game ui){
        this.ui = ui;
    }

    public void startGameAutoMessageCheck(String gameID) {
        gameUUID = gameID;
        startGameTask = new StartGameTask(gameUUID, ui);
        startGameTimer = new Timer();
        startGameTimer.schedule(startGameTask, 1000, 5000); // Run every 5 minutes (300000)
    }

    public void suggestionAutoMessageCheck(String gameUUId, String pName) {
        gameUUID = gameUUId;
        suggestionTask = new SuggestionTask(gameUUID, pName, ui); // when someone makes a suggestion,
                                                              // everyone needs to be alerted and game
        // shifts to a state where player either disproves or passes
        suggestionTimer = new Timer();
        // delay start of thread for 10 seconds and then query every 2 seconds
        suggestionTimer.schedule(suggestionTask, 10000, 2000);
    }

    public void contradictAutoMessageCheck(String gameUUId, String pName) {
        gameUUID = gameUUId;
        contradictTask = new ContradictTask(gameUUID, pName, ui); // when someone makes a suggestion,
        // everyone needs to be alerted and game
        // shifts to a state where player either disproves or passes
        contradictTimer = new Timer();
        // delay start of thread for 10 seconds and then query every 2 seconds
        contradictTimer.schedule(contradictTask, 10000, 2000);
    }

    public void locationUpdateAutoMessageCheck(String gameUUId, String pName) {
        gameUUID = gameUUId;
        locationUpdateTask = new LocationUpdateTask(gameUUID, pName, ui); // when someone makes a suggestion,
        // Keep track of where players are
        locationUpdateTimer = new Timer();
        // delay start of thread for 1 second and then query every 2 seconds
        locationUpdateTimer.schedule(locationUpdateTask, 1000, 2000);
    }

    public void gOPLAutoMessageCheck(String gameUUId, String pName) {
        gameUUID = gameUUId;
        gOPLTask = new GameOver_PlayerLostTask(gameUUID, pName, ui); // when someone makes a suggestion,
        // Keep track of where players are
        gOPLTimer = new Timer();
        // delay start of thread for 1 second and then query every 2 seconds
        gOPLTimer.schedule(gOPLTask, 1000, 2000);
    }


    public void startTurnCheck(String gameUUID, String pName) {

        turnUpdateTask = new TurnUpdateTask(gameUUID, pName,ui);
        turnUpdateTimer = new Timer();
        turnUpdateTimer.schedule(turnUpdateTask, 1000, 5000); // Run every 5 minutes (300000)
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

    public StringBuilder getGOPLResponse() {
        StringBuilder gOPL = gOPLTask.getGOPLResponse();
        return gOPL;
    }

    public void setStopThreads() {
        suggestionTask.setStopThreads();
        contradictTask.setStopThreads();
        locationUpdateTask.setStopThreads();
        gOPLTask.setStopThreads();
    }
}

class GameOver_PlayerLostTask extends TimerTask {
    private String gameID;
    private String playerName;
    private String stopThreads;
    private volatile StringBuilder gOPLResponse;
    private Game ui;

    public GameOver_PlayerLostTask(String gameID, String pName, Game ui) {
        // Constructor for subclass
        this.ui = ui;
        stopThreads = " ";
        this.gameID = gameID;
        this.playerName = pName;
        gOPLResponse = null;
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
            if(!stopThreads.equals("stop")) {
                gOPLResponse = ClueLessUtils.makeGet(gameID, playerName, "gOPL");
                if (gOPLResponse.equals("gameOver")) {
                    ui.gameOver(gOPLResponse.toString());
                } else if (gOPLResponse.equals("playerLost")) {
                    ui.playerEliminated(gOPLResponse.toString());
                }
                AutoMessageCheck.gOPLTimer.cancel();
                AutoMessageCheck.gOPLTimer.purge();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder getGOPLResponse() {
        return gOPLResponse;
    }
    public void setStopThreads() {this.stopThreads = "stop";}

}

class LocationUpdateTask extends TimerTask {
    private String gameID;
    private String playerName;
    private String stopThreads;
    private volatile StringBuilder locationUpdateResponse;
    private Game ui;

    public LocationUpdateTask(String gameID, String pName, Game ui) {
        // Constructor for subclass
        this.ui = ui;
        stopThreads = " ";
        this.gameID = gameID;
        this.playerName = pName;
        locationUpdateResponse = null;
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
            if(!stopThreads.equals("stop")) {
                locationUpdateResponse = ClueLessUtils.makeGet(gameID, playerName, "playerLocation");
                if (locationUpdateResponse != null) {
                    JSONObject locationUpdateJSON = new JSONObject(locationUpdateResponse.toString());
                    String locationUpdateResponse = locationUpdateJSON.get("messageType").toString();
                    ui.updateLocation(locationUpdateResponse, locationUpdateJSON);
                }


                               //JSONObject suggestionJSON = new JSONObject(suggestionResponse.toString());
                //String msg = suggestionJSON.get("messageType").toString();
            } else {
                AutoMessageCheck.locationUpdateTimer.cancel();
                AutoMessageCheck.locationUpdateTimer.purge();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder getLocationUpdateResponse() {
        return locationUpdateResponse;
    }
    public void setStopThreads() {this.stopThreads = "stop";}
}

class ContradictTask extends TimerTask {
    private String gameID;
    private String playerName;
    private String stopThreads;
    private volatile StringBuilder contradictResponse;
    private Game ui;

    public ContradictTask(String gameID, String pName, Game ui) {
        // Constructor for subclass
        this.ui = ui;
        stopThreads = " ";
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
            if(!stopThreads.equals("stop")) {
                contradictResponse = ClueLessUtils.makeGet(gameID, playerName, "disprove");
                //JSONObject suggestionJSON = new JSONObject(suggestionResponse.toString());
                //String msg = suggestionJSON.get("messageType").toString();
            } else {
                AutoMessageCheck.contradictTimer.cancel();
                AutoMessageCheck.contradictTimer.purge();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder getContradictResponse() {
        return contradictResponse;
    }
    public void setStopThreads() {this.stopThreads = "stop";}
}

class SuggestionTask extends TimerTask {
    private String gameID;
    private String playerName;
    private String stopThreads;
    private volatile StringBuilder suggestionResponse;
    private Game ui;

    public SuggestionTask(String gameID, String pName, Game ui) {
        // Constructor for subclass
        this.ui = ui;
        this.gameID = gameID;
        this.playerName = pName;
        stopThreads = " ";
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
            if(!stopThreads.equals("stop")) {
                suggestionResponse = ClueLessUtils.makeGet(gameID, playerName, "suggestion");
                //JSONObject suggestionJSON = new JSONObject(suggestionResponse.toString());
                //String msg = suggestionJSON.get("messageType").toString();
            } else {
                AutoMessageCheck.suggestionTimer.cancel();
                AutoMessageCheck.suggestionTimer.purge();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder getSuggestionResponse() {
        return suggestionResponse;
    }
    public void setStopThreads() {this.stopThreads = "stop";}
}

/********** Class StartGameTask **********/
class StartGameTask extends TimerTask {
    private String gameID;
    private volatile StringBuilder startGameResponse;
    private Game ui;

    public StartGameTask(String gameID, Game ui) {
        this.ui = ui;
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
        if (startGameResponse != null && startGameResponse.length() != 0) {
            JSONObject startGameJSON = new JSONObject(startGameResponse.toString());
            msg = startGameJSON.get("messageType").toString();
            if (msg.equals("startGame")) {
                // Kills thread when game has started
                AutoMessageCheck.startGameTimer.cancel();
                AutoMessageCheck.startGameTimer.purge();
                ui.preGamePrep(startGameJSON.toString());
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



class TurnUpdateTask extends TimerTask {
    private String gameID;
    private String playerName;
    private String stopThreads;
    private volatile StringBuilder suggestionResponse;
    private Game ui;
    private String currentlyTurn = " ";
    public TurnUpdateTask(String gameID, String pName, Game ui) {
        // Constructor for subclass
        this.ui = ui;
        this.gameID = gameID;
        this.playerName = pName;
        stopThreads = " ";
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
            if(!stopThreads.equals("stop")) {


                suggestionResponse = ClueLessUtils.makeGet(gameID,
                        playerName, "turnUpdate");
                JSONObject turnUpdateJSON = new JSONObject(suggestionResponse.toString());
                String turnUpdateResponse = turnUpdateJSON.get("currentTurn").toString();

                if(!turnUpdateResponse.contentEquals(currentlyTurn)){
                    currentlyTurn = turnUpdateResponse;
                    ui.playerTurnUpdate(turnUpdateResponse);

                }
                //JSONObject suggestionJSON = new JSONObject(suggestionResponse.toString());
                //String msg = suggestionJSON.get("messageType").toString();
            } else {
                AutoMessageCheck.suggestionTimer.cancel();
                AutoMessageCheck.suggestionTimer.purge();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder getSuggestionResponse() {
        return suggestionResponse;
    }
    public void setStopThreads() {this.stopThreads = "stop";}
}
