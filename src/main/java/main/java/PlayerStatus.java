package main.java;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatus {
    private String playerName;
    private String playerLocation;
    ArrayList<String> playerHand;

    public void setPlayerName(String name) {
        playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerLocation(String location) {
        playerLocation = location;
    }

    public String getPlayerLocation() {
        return playerLocation;
    }

    public void setPlayerHand(ArrayList<String> hand) {
        playerHand = new ArrayList<String>();
        if (hand.size() != 0) {
            playerHand.addAll(hand);
        }
    }

    public List<String> getPlayerHand() {
        return playerHand;
    }
}
