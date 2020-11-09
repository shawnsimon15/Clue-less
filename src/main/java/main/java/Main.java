package main.java;

//import javafx.util.Pair;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;


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
        System.out.println("Welcome to ClueLess!");

        //autoMessageCheck = new AutoMessageCheck();
        //gameStatus = new GameStatus();
        //gameActions = new GameActions();
        //ClueLessConstants constants = new ClueLessConstants(); // Calls constructor to initialize constants

        //userInterface();

        ClueLessConstants constants = new ClueLessConstants(); // Calls constructor to initialize constants
        Game newGame = new Game();


        /********** Testing db **********
        ClueLessUtils.makePost("444444", "Rick", 5, "disproveSuggestion");
        ClueLessUtils.makePost("444444", "Rick", 5, "endTurn");

        ClueLessUtils.makePost("444444", "Rick", 5, "passSuggestion");
        ClueLessUtils.makePost("444444", "Rick", 5, "suggestion");

        gameActions.createGame("Steve", 4);
        gameActions.joinGame("444444", "Rock");
        gameActions.joinGame("123458", "Rock");

        System.out.println(ClueLessUtils.makeGet("123458", "makeAccusation"));
        System.out.println(ClueLessUtils.makeGet("123458", "startGame"));
        System.out.println(ClueLessUtils.makeGet("123458", "turnUpdate"));
        //ClueLessUtils.makeGet("", "locationUpdate");
        gameActions.movePiece("123458", "Luke", "location1", "Room1");
        gameActions.makeGuess("123458","Luke", "feather", "location1");
        gameActions.makeAccusation("123458","Luke", "feather", "location1");
        gameActions.makeAccusation("123458","Person", "Weapon", "Location");

        System.out.println(ClueLessUtils.makeGet("123458", "suggestion"));
        System.out.println(ClueLessUtils.makeGet("123458", "contradict"));
         ********** Testing db **********/
    }
}
