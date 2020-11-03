package main.java;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class AutoMessageCheck {

    private StartGameTask startGameTask;
    private MakeAccusationTask makeAccusationTask;
    private TurnUpdateTask turnUpdateTask;
    private LocationUpdateTask locationUpdateTask;
    private SuggestionTask suggestionTask;
    private ContradictTask contradictTask;

    public static Timer timer;
    private String gameUUID;

    /**
     * Function: AutoMessageCheck()
     * Description: Constructor for AutoMessageCheck class
     * Does not return but initializes all data members and schedules thread
     *      for querying database
     **/
    public AutoMessageCheck(){
        makeAccusationTask = new MakeAccusationTask();
        turnUpdateTask = new TurnUpdateTask();
        locationUpdateTask = new LocationUpdateTask();
        suggestionTask = new SuggestionTask();
        contradictTask = new ContradictTask();

    }

    public void startAutoMessageCheck(String gameUUId) {
        gameUUID = gameUUId;
        startGameTask = new StartGameTask(gameUUID);
        timer = new Timer();
        timer.schedule(startGameTask, 1000, 5000); // Run every 5 minutes (300000)
    }

    public StringBuilder getResponse() {
        StringBuilder startGame = startGameTask.getStartGameResponse();
        return startGame;
    }
}

/********** Class StartGameTask **********/
class StartGameTask extends TimerTask {
    private int count = 0;
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
        count++;
        String msg = " ";
        if (startGameResponse != null) {
            JSONObject startGameJSON = new JSONObject(startGameResponse.toString());
            msg = startGameJSON.get("messageType").toString();
            if (msg.equals("startGame")) {
                // Kills thread when game has started
                //TODO: Take this out; for peace of mind
                System.out.println("Killing Thread");
                AutoMessageCheck.timer.cancel();
                AutoMessageCheck.timer.purge();
            }
        }

        if (!msg.equals("startGame")) {
            // periodically query game server for msgs
            try {
                startGameResponse = ClueLessUtils.makeGet(gameID, "startGame");
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

class MakeAccusationTask extends TimerTask {
    public MakeAccusationTask() {
        // Constructor for subclass
    }

    /**
     * Function: run()
     * Description: Will run every X minutes querying GET messages in database
     * Does not return
     **/
    @Override
    public void run() {
        // periodically query game server for msgs
        //TODO: how to specify which get and how to get response back to player
        //ClueLessUtils.makeGet("");
    }
}

class TurnUpdateTask extends TimerTask {
    public TurnUpdateTask() {
        // Constructor for subclass
    }

    /**
     * Function: run()
     * Description: Will run every X minutes querying GET messages in database
     * Does not return
     **/
    @Override
    public void run() {
        // periodically query game server for msgs
        //TODO: how to specify which get and how to get response back to player
        //ClueLessUtils.makeGet("");
    }
}

class LocationUpdateTask extends TimerTask {
    public LocationUpdateTask() {
        // Constructor for subclass
    }

    /**
     * Function: run()
     * Description: Will run every X minutes querying GET messages in database
     * Does not return
     **/
    @Override
    public void run() {
        // periodically query game server for msgs
        //TODO: how to specify which get and how to get response back to player
        //ClueLessUtils.makeGet("");
    }
}

class SuggestionTask extends TimerTask {
    public SuggestionTask() {
        // Constructor for subclass
    }

    /**
     * Function: run()
     * Description: Will run every X minutes querying GET messages in database
     * Does not return
     **/
    @Override
    public void run() {
        // periodically query game server for msgs
        //TODO: how to specify which get and how to get response back to player
        //ClueLessUtils.makeGet("");
    }
}

class ContradictTask extends TimerTask {
    public ContradictTask() {
        // Constructor for subclass
    }

    /**
     * Function: run()
     * Description: Will run every X minutes querying GET messages in database
     * Does not return
     **/
    @Override
    public void run() {
        // periodically query game server for msgs
        //TODO: how to specify which get and how to get response back to player
        //ClueLessUtils.makeGet("");
    }
}