package main.java;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class AutoMessageCheck {

    private Task queryMessage;
    private Timer timer;
    private UUID gameUUID;
    private String playerName;


    public AutoMessageCheck(){
        // Constructor
        gameUUID = UUID.randomUUID();
        playerName = "";
        queryMessage = new Task();
        timer = new Timer();

    }

    public void startAutoMessageCheck(UUID gameUUId, String pName) {
        // Start AMC
        gameUUID = gameUUId;
        playerName = pName;
        // sets queryMessage but to what ?????

        // need an http request GET to see if any msg from server for this player
    }

    public class Task extends TimerTask {
        public Task() {
            // Constructor for subclass
        }

        @Override
        public void run() {
            // periodically query game server for msgs
            ClueLessUtils.makeGet("", "");
        }
    }
}

