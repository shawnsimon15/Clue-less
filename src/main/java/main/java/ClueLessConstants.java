package main.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClueLessConstants {
    public static String START_GAME_API;
    public static String JOIN_GAME_API;
    public static String MOVE_PLAYER_API;
    public static String MAKE_SUGGESTION_OR_ACCUSATION_API;
    public static String RESPOND_TO_SUGGESTION_OR_ACCUSATION_API;
    public static String QUERY_FOR_MESSAGES_API;
    public static ArrayList<String> SUSPECT_LIST;
    public static ArrayList<String> WEAPON_LIST;
    public static ArrayList<String> HALL_LIST;
    public static ArrayList<String> ROOM_LIST;
    public static Map<String, List<String>> ADJACENCY_MAP;
    public static Map<Integer, String> TURN_ORDER;

    public ClueLessConstants() {
        SUSPECT_LIST = new ArrayList<String>();
        SUSPECT_LIST.add("MissScarlet");
        SUSPECT_LIST.add("ColonelMustard");
        SUSPECT_LIST.add("Mrs.White");
        SUSPECT_LIST.add("Mr.Green");
        SUSPECT_LIST.add("Mrs.Peacock");
        SUSPECT_LIST.add("ProfessorPlum");

        WEAPON_LIST = new ArrayList<String>();
        WEAPON_LIST.add("Candlestick");
        WEAPON_LIST.add("Revolver");
        WEAPON_LIST.add("Knife");
        WEAPON_LIST.add("Lead Pipe");
        WEAPON_LIST.add("Rope");
        WEAPON_LIST.add("Wrench");

        ROOM_LIST = new ArrayList<String>();
        ROOM_LIST.add("Kitchen");
        ROOM_LIST.add("Hall");
        ROOM_LIST.add("Ballroom");
        ROOM_LIST.add("Conservatory");
        ROOM_LIST.add("Dining Room");
        ROOM_LIST.add("Study");
        ROOM_LIST.add("Billiard Room");
        ROOM_LIST.add("Library");
        ROOM_LIST.add("Lounge");
    }
}
