package main.java;

import java.util.List;

public class GameStatus {
    private List<PlayerStatus> activePlayerList;

    public void setActivePlayerList(List<PlayerStatus> aPL) {
        activePlayerList.addAll(aPL);
    }

    public List<PlayerStatus> getActivePlayerList() {
        return activePlayerList;
    }
}
