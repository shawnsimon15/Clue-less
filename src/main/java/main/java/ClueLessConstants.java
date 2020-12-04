package main.java;

import java.util.*;

public class ClueLessConstants {
    public static String START_GAME_API;
    public static String JOIN_GAME_API;
    public static String MOVE_PLAYER_API;
    public static String MAKE_SUGGESTION_OR_ACCUSATION_API;
    public static String RESPOND_TO_SUGGESTION_OR_ACCUSATION_API;
    public static String QUERY_FOR_MESSAGES_API;
    public static ArrayList<String> SUSPECT_LIST = new ArrayList(Arrays.asList("MissScarlet", "ColonelMustard", "Mrs.White", "Mr.Green",
            "Mrs.Peacock", "ProfessorPlum"));
    public static ArrayList<String> WEAPON_LIST = new ArrayList(Arrays.asList("Candlestick", "Revolver", "Knife", "Lead Pipe", "Rope", "Wrench"));
    public static ArrayList<String> HALL_LIST;
    public static ArrayList<String> ROOM_LIST  = new ArrayList(Arrays.asList("Kitchen", "Hall", "Ballroom", "Conservatory", "Dining Room", "Study",
            "Billiard Room", "Library", "Lounge"));
    public static Map<Integer, String> TURN_ORDER;
    public static ArrayList<String> study  = new ArrayList(Arrays.asList("nomove", "Hallway:SH", "Hallway:LS", "nomove", "Kitchen"));
    public static ArrayList<String> hall  = new ArrayList(Arrays.asList("nomove", "Hallway:HL", "Hallway:HBR", "Hallway:SH","nomove"));
    public static ArrayList<String> lounge  = new ArrayList(Arrays.asList("nomove", "nomove", "Hallway:LDR", "Hallway:HL", "Conservatory"));
    public static ArrayList<String> library  = new ArrayList(Arrays.asList("Hallway:LS", "Hallway:LBR", "Hallway:CL", "nomove", "nomove" ));
    public static ArrayList<String> biliardRoom  = new ArrayList(Arrays.asList("Hallway:HBR", "Hallway:DRBR", "Hallway:BBR", "Hallway:LBR", "nomove"));
    public static ArrayList<String> diningRoom  = new ArrayList(Arrays.asList("Hallway:LDR", "nomove", "Hallway:DRK", "Hallway:DRBR", "nomove"));
    public static ArrayList<String> conseratory  = new ArrayList(Arrays.asList("Hallway:CL", "Hallway:CB", "nomove", "nomove", "Lounge"));
    public static ArrayList<String> ballRoom  = new ArrayList(Arrays.asList("Hallway:BBR", "Hallway:KB",  "nomove", "Hallway:CB", "nomove"));
    public static ArrayList<String> kitchen  = new ArrayList(Arrays.asList("Hallway:DRK", "nomove", "nomove", "Hallway:KB", "Study" ));
    public static ArrayList<String> hallwayLS  = new ArrayList(Arrays.asList("Study", "nomove", "Library", "nomove", "nomove"));
    public static ArrayList<String> hallwayCB  = new ArrayList(Arrays.asList("Conservatory", "Ballroom", "nomove", "nomove"));
    public static ArrayList<String> hallwayCL  = new ArrayList(Arrays.asList("Library", "nomove", "Conservatory",  "nomove", "nomove"));
    public static ArrayList<String> hallwayLBR  = new ArrayList(Arrays.asList("nomove",  "Billiard Room", "nomove", "Library", "nomove"));
    public static ArrayList<String> hallwaySH  = new ArrayList(Arrays.asList("nomove", "Hall", "nomove", "Study", "nomove"));
    public static ArrayList<String> hallwayHBR  = new ArrayList(Arrays.asList("Hall", "nomove", "Billiard Room", "nomove", "nomove"));
    public static ArrayList<String> hallwayHL  = new ArrayList(Arrays.asList("nomove", "Lounge", "nomove", "Hall", "nomove"));
    public static ArrayList<String> hallwayLDR  = new ArrayList(Arrays.asList("Lounge", "nomove", "Dining Room", "nomove", "nomove"));
    public static ArrayList<String> hallwayDRBR  = new ArrayList(Arrays.asList("nomove", "Dining Room", "nomove", "Billiard Room", "nomove"));
    public static ArrayList<String> hallwayDRK  = new ArrayList(Arrays.asList("Dining Room", "nomove", "Kitchen", "nomove"));
    public static ArrayList<String> hallwayKB  = new ArrayList(Arrays.asList("nomove", "Kitchen", "Ballroom", "nomove", "nomove"));
    public static ArrayList<String> hallwayBBR  = new ArrayList(Arrays.asList("Billiard Room", "nomove", "nomove", "Ballroom", "nomove"));
    public static ArrayList<String> professorPlumStart  = new ArrayList(Arrays.asList("nomove", "Hallway:LS", "nomove", "nomove", "nomove"));
    public static ArrayList<String> colonelMustardStart  = new ArrayList(Arrays.asList("nomove", "nomove", "nomove", "Hallway:LDR", "nomove"));
    public static ArrayList<String> mrsPeacockStart  = new ArrayList(Arrays.asList("nomove", "Hallway:CL", "nomove", "nomove", "nomove"));
    public static ArrayList<String> mrGreenStart  = new ArrayList(Arrays.asList("Hallway:CB", "nomove", "nomove", "nomove", "nomove"));
    public static ArrayList<String> missScarletStart  = new ArrayList(Arrays.asList("nomove", "nomove", "Hallway:HL", "nomove", "nomove"));
    public static ArrayList<String> mrsWhiteStart  = new ArrayList(Arrays.asList("Hallway:KB", "nomove", "nomove", "nomove", "nomove"));

    public static Map<String, List<String>> ADJACENCY_MAP = new HashMap(){{
        put("Study", study);
        put("Hall", hall);
        put("Lounge",lounge);
        put("Library", library);
        put("Billiard Room", biliardRoom);
        put("Dining Room", diningRoom);
        put("Conservatory", conseratory);
        put("Ballroom", ballRoom);
        put("Kitchen", kitchen);
        put("Hallway:LS", hallwayLS);
        put("Hallway:CB", hallwayCB);
        put("Hallway:CL", hallwayCL);
        put("Hallway:LBR", hallwayLBR);
        put("Hallway:SH", hallwaySH);
        put("Hallway:HBR", hallwayHBR);
        put("Hallway:HL", hallwayHL);
        put("Hallway:LDR", hallwayLDR);
        put("Hallway:DRBR", hallwayDRBR);
        put("Hallway:DRK", hallwayDRK);
        put("Hallway:KB", hallwayKB);
        put("Hallway:BBR", hallwayBBR);
        put("Mrs.WhiteStart", mrsWhiteStart);
        put("ColonelMustardStart", colonelMustardStart);
        put("MissScarletStart", missScarletStart);
        put("Mrs.PeacockStart", mrsPeacockStart);
        put("Mr.GreenStart", mrGreenStart);
        put("ProfessorPlumStart", professorPlumStart);
    }};
    public static ArrayList<String> MAIN_MENU_ENTRIES = new ArrayList(Arrays.asList("a", "b", "c", "d"));

}
