package main.java;

//import javafx.util.Pair;

import java.io.IOException;
import java.util.*;


public class Main {

    private static GameActions gameActions;
    private static AutoMessageCheck autoMessageCheck;
    private static GameStatus gameStatus;
    private static String playerName;

    public static final ArrayList<String> turnOrder = new ArrayList<String>(Arrays
            .asList("MissScarlet", "ColonelMustard", "Mrs.White",
            "Mr.Green", "Mrs.Peacock", "ProfessorPlum")); // Clockwise from Miss Scarlet
    public static final ArrayList<String> locations = new ArrayList<String>(Arrays
            .asList("Kitchen", "Hall", "Ballroom", "Conservatory",
                    "Dining Room", "Study", "Billiard Room", "Library", "Lounge"));
    public static final ArrayList<String> weapons = new ArrayList<String>(Arrays
            .asList("Candlestick", "Revolver", "Knife", "Lead Pipe", "Rope", "Wrench"));


    // main will run be the application run by a player
    public static void main(String[] args) throws IOException, InterruptedException {
        Game newGame = new Game();

    }
}
