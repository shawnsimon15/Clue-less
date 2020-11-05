package main.java;

import java.util.ArrayList;
import java.util.List;

public class GameStatus {
    private ArrayList<PlayerStatus> activePlayerList;

    public void setActivePlayerList(ArrayList<PlayerStatus> aPL) {
        activePlayerList = new ArrayList<PlayerStatus>();
        activePlayerList.addAll(aPL);
    }

    public List<PlayerStatus> getActivePlayerList() {
        return activePlayerList;
    }
}
