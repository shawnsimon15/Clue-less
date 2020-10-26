package main.java;

import java.util.List;

public class PlayerStatus {
    private String playerName;
    private String playerLocation;
    List<String> playerHand;

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

    public void setPlayerHand(List<String> hand) {
        playerHand.addAll(hand);
    }

    public List<String> getPlayerHand() {
        return playerHand;
    }
}
