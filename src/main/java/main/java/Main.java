package main.java;

//import javafx.util.Pair;

import org.json.simple.JSONObject;

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
        UserInterface newGame = new UserInterface();

    }
}
