package main.java;

import org.json.JSONObject;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static main.java.ClueLessConstants.MAIN_MENU_ENTRIES;

public class UserInterface extends JFrame implements ActionListener {

    //will be removed with real GUI
    private JTextArea locations;

    //constants declared for ease of access and editing.
    private final static int FRAMEXDIMENSION = 1800;
    private final static int FRAMEYDIMENSION = 1000;
    private final static String FRAMETITLE = "Clue-Less";

    private final static String MISSSSCARLET = "Miss Scarlet";
    private final static String COLONELMUSTARD = "Colonel Mustard";
    private final static String MRSPEACOCK = "Mrs. Peacock";
    private final static String MRGREEN = "Mr. Green";
    private final static String MRSWHITE = "Mrs. White";
    private final static String PROFESSORPLUM = "Professor Plum";

    private JLabel currentPlayerLable = new JLabel();
    private JLabel gameUUIDLable = new JLabel("Game UUID:");
    private JLabel missScarletLable = new JLabel(MISSSSCARLET);
    private JLabel colonelMustardLable = new JLabel(COLONELMUSTARD);
    private JLabel mrsPeacokLable = new JLabel(MRSPEACOCK);
    private JLabel mrGreenLable = new JLabel(MRGREEN);
    private JLabel mrsWhiteLable = new JLabel(MRSWHITE);
    private JLabel profPlumLable = new JLabel(PROFESSORPLUM);
    private HashMap<String, JLabel> playersToLableMap = new HashMap(){{
        put(ClueLessConstants.SUSPECT_LIST.get(0), missScarletLable);
        put(ClueLessConstants.SUSPECT_LIST.get(1), colonelMustardLable);
        put(ClueLessConstants.SUSPECT_LIST.get(2),mrsWhiteLable);
        put(ClueLessConstants.SUSPECT_LIST.get(3), mrGreenLable);
        put(ClueLessConstants.SUSPECT_LIST.get(4), mrsPeacokLable);
        put(ClueLessConstants.SUSPECT_LIST.get(5), profPlumLable);
        }};
    JPanel cardsPanel = new JPanel();
    Popup p;
    private JComboBox<String> inputBox;
    private JComboBox<String> guessBoxWeapons=  new JComboBox<>(ClueLessConstants.WEAPON_LIST.toArray(new String[0]));
    private JComboBox<String> guessBoxSuspects =  new JComboBox<>(ClueLessConstants.SUSPECT_LIST.toArray(new String[0]));
    JTextField uuidField = new JTextField();
    private GameActions gameActions;
    private AutoMessageCheck autoMessageCheck;
    private GameStatus gameStatus;
    private String playerName;
    private String gameUUID;
    ArrayList<PlayerStatus> playerList;
    private ArrayList<JButton> controlButtons = new ArrayList<>();
    JPanel boardPanel = new JPanel();
    ArrayList<String> playerHand = new ArrayList<>();
    int whoseTurn = 0;
    boolean movedPlayer = false;
    boolean playerMadeSuggestion = false;
    boolean justContradicted = false;


    JButton moveUp;
    JButton moveDown ;
    JButton moveLeft;
    JButton moveRight;
    JButton MovePassage;
    JButton makeSuggestionBtn;
    JButton makeAccusationBtn;
    JButton endTurnBtn;

    public UserInterface() {



        // create a text area, specifying the rows and columns
        locations = new JTextArea(8, 40);



        gameActions = new GameActions();
        gameStatus = new GameStatus();
        playerName = " ";
        autoMessageCheck = new AutoMessageCheck(this);
        startMenu();
        // adding the visual objects to panels than to the frame
        setTitle(FRAMETITLE);
        setSize(FRAMEXDIMENSION, FRAMEYDIMENSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlsPanel = new JPanel();
        JPanel infoPanel = new JPanel();
        JPanel notesPanel = new JPanel();


        //GAME PANNEL SETUP
        boardPanel.add(locations);
        // CONTROLS PANNEL SETUP
        moveUp = new JButton("Move Up");
        moveDown = new JButton("Move Down");
        moveLeft = new JButton("Move Left");
        moveRight = new JButton("Move Right");
        MovePassage = new JButton("Move Through Passage");
        makeSuggestionBtn = new JButton("Make Suggestion");
        makeAccusationBtn = new JButton("Make Accusation");
        endTurnBtn = new JButton("End Turn");

        controlButtons.add(moveUp);
        controlButtons.add(moveDown);
        controlButtons.add(moveLeft);
        controlButtons.add(moveRight);
        controlButtons.add(MovePassage);
        controlButtons.add(makeSuggestionBtn);
        controlButtons.add(makeAccusationBtn);
        controlButtons.add(endTurnBtn);


        controlsPanel.setLayout(new GridLayout(8, 1));

        controlsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder()));

        for(JButton button: controlButtons) {
            button.addActionListener(this);
            controlsPanel.add(button);
            button.setEnabled(false);
        }

        //NOTE PAD AREA SETTUP
        JTextArea notesField = new JTextArea();

        JPanel weaponsPanel = new JPanel();
        JPanel suspectPanel = new JPanel();
        JPanel locationsPanel = new JPanel();
        weaponsPanel.setLayout(new GridLayout(7, 1));
        weaponsPanel.add(new JLabel("Weapons"));
        weaponsPanel.add(new JCheckBox("Lead Pipe"));
        weaponsPanel.add(new JCheckBox("Rope"));
        weaponsPanel.add(new JCheckBox("Knife"));
        weaponsPanel.add(new JCheckBox("Wrench"));
        weaponsPanel.add(new JCheckBox("Candlestick"));
        weaponsPanel.add(new JCheckBox("Revolver"));
        suspectPanel.setLayout(new GridLayout(7, 1));
        suspectPanel.add(new JLabel("Suspects"));
        suspectPanel.add(new JCheckBox("Miss Scarlet"));
        suspectPanel.add(new JCheckBox("Colonel Mustard"));
        suspectPanel.add(new JCheckBox("Mrs. Peacock"));
        suspectPanel.add(new JCheckBox("Mr. Green"));
        suspectPanel.add(new JCheckBox("Mrs. White"));
        suspectPanel.add(new JCheckBox("Professor Plum"));
        locationsPanel.setLayout(new GridLayout(6, 2));
        locationsPanel.add(new JLabel("Locations"));
        locationsPanel.add(new JLabel(""));
        locationsPanel.add(new JCheckBox("Study"));
        locationsPanel.add(new JCheckBox("Lounge"));
        locationsPanel.add(new JCheckBox("Billiard Room"));
        locationsPanel.add(new JCheckBox("Conservatory"));
        locationsPanel.add(new JCheckBox("Kitchen"));
        locationsPanel.add(new JCheckBox("Hall"));
        locationsPanel.add(new JCheckBox("Library"));
        locationsPanel.add(new JCheckBox("Dining Room"));
        locationsPanel.add(new JCheckBox("Ball-room"));
        notesPanel.setLayout(new GridLayout(1, 4));
        notesPanel.add(notesField);
        notesPanel.add(weaponsPanel);
        notesPanel.add(suspectPanel);
        notesPanel.add(locationsPanel);

        //setting up info panel
        infoPanel.setLayout(new GridLayout(2, 1));
        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new GridLayout(8, 2));
        playersPanel.add(new JLabel("Players"));
        playersPanel.add(new JLabel(""));
        JPanel scarletPanel = new JPanel();
        scarletPanel.setBackground(Color.RED);
        playersPanel.add(scarletPanel);
        playersPanel.add(missScarletLable);


        JPanel mustardPanel = new JPanel();
        mustardPanel.setBackground(Color.YELLOW);
        playersPanel.add(mustardPanel);
        playersPanel.add(colonelMustardLable);


        JPanel peacokPanel = new JPanel();
        peacokPanel.setBackground(Color.BLUE);
        playersPanel.add(peacokPanel);
        playersPanel.add(mrsPeacokLable);


        JPanel greenPanel = new JPanel();
        greenPanel.setBackground(Color.GREEN);
        playersPanel.add(greenPanel);
        playersPanel.add(mrGreenLable);


        JPanel whitePanel = new JPanel();
        whitePanel.setBackground(Color.WHITE);
        playersPanel.add(whitePanel);
        playersPanel.add(mrsWhiteLable);


        JPanel plumPanel = new JPanel();
        plumPanel.setBackground(Color.MAGENTA);
        playersPanel.add(plumPanel);
        playersPanel.add(profPlumLable);

        cardsPanel.setLayout(new GridLayout(8, 1));
        cardsPanel.add(gameUUIDLable);
        cardsPanel.add(new JLabel("Card Hand"));
        infoPanel.add(playersPanel);
        infoPanel.add(cardsPanel);
        // final set up and display of the  game board
        add(controlsPanel, BorderLayout.WEST);
        add(boardPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(notesPanel, BorderLayout.SOUTH);
        validate();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void startMenu(){

        JLabel popLabel = new JLabel("Would you like to start or join a game?");
        JPanel popPanel = new JPanel();

        popPanel.setLayout(new GridLayout(2, 2));
        Button create = new Button("Create Game");
        create.addActionListener(this);
        Button join = new Button("Join Game");
        join.addActionListener(this);
        PopupFactory pf = new PopupFactory();
        // create a panel

        popPanel.add(popLabel);
        popPanel.add(new JLabel(""));
        popPanel.add(create);
        popPanel.add(join);
        // create a popup
        p = pf.getPopup(this, popPanel, 600, 350);

        p.show();
    }

    private void joinMenu(){

        JLabel popLabel = new JLabel("Please enter the game UUID");
        JPanel popPanel = new JPanel();

        popPanel.setLayout(new GridLayout(3, 1));

        Button join = new Button("Connect to Game");
        join.addActionListener(this);
        PopupFactory pf = new PopupFactory();
        // create a panel
        uuidField.setColumns(42);
        popPanel.add(popLabel);
        popPanel.add(uuidField);
        popPanel.add(join);

        // create a popup

        p = pf.getPopup(this, popPanel, 600, 350);
        p.show();
    }


    private void joinGame() throws IOException {

        String outputString = "Join Failed";
        gameActions.joinGame(gameUUID, playerName);
        if (ClueLessUtils.response != null) {
            StringBuilder joinGameReponse = ClueLessUtils.response;
            JSONObject joinGameJSON = new JSONObject(joinGameReponse.toString());

            if (joinGameJSON.get("messageType").toString().contentEquals("welcomeToGame")) {

                autoMessageCheck.startGameAutoMessageCheck(gameActions.getGameUUID());
                p.hide();
                playerName = joinGameJSON.get("yourPlayer").toString();
                currentPlayerLable.setText(playerName);
                playersToLableMap.get(playerName).setText(playersToLableMap.get(playerName).getText() + " (You)");
                outputString = "You will be playing as " + playerName;
                JTextArea text = new JTextArea(outputString);
                JOptionPane.showMessageDialog(null,text);
                waitingForPlayers();
            } else {
                outputString = "Sorry, the selected game is not available, please try a differnt game ID.";
                JTextArea text = new JTextArea(outputString);
                JOptionPane.showMessageDialog(null,text);
            }

        }
    }


    private void createMenu(){

        JLabel popLabel = new JLabel("Please select number of players");
        JPanel popPanel = new JPanel();

        popPanel.setLayout(new GridLayout(3, 1));

        Button join = new Button("Start Game");
        join.addActionListener(this);
        PopupFactory pf = new PopupFactory();
        // create a panel
        String[] choices = { "3","4", "5","6"};
        inputBox = new JComboBox<String>(choices);
        popPanel.add(popLabel);
        popPanel.add(inputBox);
        popPanel.add(join);

        // create a popup

        p = pf.getPopup(this, popPanel, 600, 350);
        p.show();
    }


    private void createGame() throws IOException {

        int suspectIndex = (int) ((Math.random() * (5)));
        playerName = ClueLessConstants.SUSPECT_LIST.get(suspectIndex);
        currentPlayerLable.setText(playerName);
        playersToLableMap.get(playerName).setText(playersToLableMap.get(playerName).getText() + " (You)");
        try {
            gameActions.createGame(playerName, Integer.parseInt(inputBox.getSelectedItem().toString()));
            autoMessageCheck.startGameAutoMessageCheck(gameActions.getGameUUID());
        } catch (IOException e) {
            e.printStackTrace();
        }
        gameUUID = gameActions.getGameUUID();

        gameUUIDLable.setText(gameUUIDLable.getText() + " " + gameUUID);
        JTextArea text = new JTextArea(gameUUIDLable.getText());
        JOptionPane.showMessageDialog(null,text);
        text.setText("You will be playing as " + playerName);
        JOptionPane.showMessageDialog(null,text);
        waitingForPlayers();

    }


    private void waitingForPlayers(){

        JLabel popLabel = new JLabel("Waiting for remaining players");
        JPanel popPanel = new JPanel();
        PopupFactory pf = new PopupFactory();
        // create a panel
        popPanel.setLayout(new GridLayout(3, 1));
        popPanel.add(new JPanel());

        popPanel.add(popLabel);
        popPanel.add(new JPanel());

        // create a popup

        p = pf.getPopup(this, popPanel, 600, 350);
        p.show();
    }



    public void preGamePrep(String startGameResponse) {
        p.hide();
        int i = 0;
        ArrayList<String> players = new ArrayList<>();

        JSONObject responseJSON = new JSONObject(startGameResponse);
        JSONObject activePlayers = (JSONObject) responseJSON.get("activePlayers");

        int counter =2;
        for (String key : activePlayers.keySet()) {
            String player = activePlayers.getString(key);
            if(!player.equals(playerName)) {
                JLabel current = playersToLableMap.get(player);
                current.setText(current.getText() + "  Player: " + counter);
                counter++;
            }
            players.add(activePlayers.get(key).toString()); // all active players in game

        }
        printListContents(players, "players");
        ArrayList<String> turnOrderForGame = obtainTurnOrder(players);
        loadPlayerInfoIntoGameStatus(turnOrderForGame);
        String eachLoc = "";
        for(PlayerStatus player: playerList){
            eachLoc +=("Player: "+player.getPlayerName()+ " is in " +player.getPlayerLocation()+ ".\n");
        }

        locations.setText(eachLoc);
        // Thread to check if suggestion has been made
        autoMessageCheck.suggestionAutoMessageCheck(gameActions.getGameUUID(), playerName);
        // Thread to check if contradiction has been made
        autoMessageCheck.contradictAutoMessageCheck(gameActions.getGameUUID(), playerName);
        // Thread to check if player has moved
        autoMessageCheck.locationUpdateAutoMessageCheck(gameActions.getGameUUID(), playerName);
        // Thread to check endOfGame msg
        autoMessageCheck.gOPLAutoMessageCheck(gameActions.getGameUUID(), playerName);

        autoMessageCheck.startTurnCheck(gameActions.getGameUUID(), playerName);


        JLabel popLabel = new JLabel("Setting up game, please wait.");
        JPanel popPanel = new JPanel();
        PopupFactory pf = new PopupFactory();
        // create a panel
        popPanel.setLayout(new GridLayout(3, 1));
        popPanel.add(new JPanel());

        popPanel.add(popLabel);
        popPanel.add(new JPanel());

        // create a popup

        p = pf.getPopup(this, popPanel, 600, 350);
        p.show();
    }


    public void loadPlayerInfoIntoGameStatus(ArrayList<String> turnOrderForGame) {
        playerList = new ArrayList<>();
        for (String plyr : turnOrderForGame) {
            PlayerStatus playerStatus = new PlayerStatus();
            playerStatus.setPlayerName(plyr);

            ArrayList<String> emptyList = new ArrayList<>();
            playerStatus.setPlayerHand(emptyList);

            playerStatus.setPlayerLocation(plyr + "Start");
            playerList.add(playerStatus);
        }
        gameStatus.setActivePlayerList(playerList); // add ordered active players to GameStatus
    }

    public ArrayList<String> obtainTurnOrder(ArrayList<String> players) {
        ArrayList<String> turnOrderForGame = new ArrayList<>();
        for (String plyr : ClueLessConstants.SUSPECT_LIST) {
            if (players.contains(plyr)) {
                turnOrderForGame.add(plyr);
            }
        }
        return turnOrderForGame;
    }

    public ArrayList<String> getPlayerCards(JSONObject startGameJSON) {
        JSONObject cardsAssigned = (JSONObject) startGameJSON.get("cardsAssigned");
        ArrayList<String> cards = new ArrayList<>();

        for (String key : cardsAssigned.keySet()) {
            cards.add(cardsAssigned.get(key).toString()); // cards for player
        }

        printListContents(cards, "cards");

        return cards;
    }


    public void printListContents(ArrayList<String> list, String typeOfList) {
        String outString = "";
        if (typeOfList.equals("players")) {
            outString += "All players have joined!";
            outString += "\nLobby includes: ";
        } else if (typeOfList.equals("cards")) {
            outString += "Your cards are: ";
        } else {
            outString += "The order for the game will be: ";
        }

        for (int i=0; i < list.size(); ++i) {
            if (i != (list.size() - 1)) {
                outString += "\n"+ list.get(i) + ", ";
            } else {
                outString += "\n" + list.get(i);
            }
        }

        JTextArea text = new JTextArea(outString);
        JOptionPane.showMessageDialog(null,text);
    }


    public void gameOver(String message){
        JSONObject gOPLJSON = new JSONObject(message);

        String winner = "The game is over! The winner is: " + gOPLJSON.get("Winner").toString();

        JTextArea text = new JTextArea(winner);
        JOptionPane.showMessageDialog(null,text);

        autoMessageCheck.setStopThreads(); // End the game
        System.exit(0);
    }


    public void playerEliminated(String message) throws IOException {

        JSONObject gOPLJSON = new JSONObject(message);


        String loser = gOPLJSON.get("Loser").toString();
        System.out.println(loser + " has lost the game on an incorrect accusation.");
        // take them out of the lineup
        int playerListSize = playerList.size();

        for (int i = 0; i < playerListSize; ++i) {
            if(playerList.get(i).getPlayerName().equals(loser)){
                playerList.remove(i);
                break;
            }
        }
        ClueLessUtils.deleteItem(gameActions.getGameUUID(),
                playerName, "playerLost_");
    }


    public void updateLocation(String locationUpdateResponse, JSONObject locationUpdateJSON) {
        // A player has moved, so update their location on map
        if (locationUpdateResponse.equals("playerLocation")) {
            String player = locationUpdateJSON.get("playerWhoMoved").toString();
            String playerNewLocation = locationUpdateJSON.get("location").toString();

            for (PlayerStatus ps : playerList) {
                if(ps.getPlayerName().equals(player)) {
                    boardSetLocation(ps.getPlayerName(), playerNewLocation);
                    ps.setPlayerLocation(playerNewLocation);
                }
            }
        }
    }

    public void playerTurnUpdate(String newTurnName) throws IOException {

        movedPlayer = false;
        playerMadeSuggestion = false;
        justContradicted = false;

        StringBuilder suggestionThreadResponse = autoMessageCheck.getSuggestionResponse();
        StringBuilder contradictThreadResponse = autoMessageCheck.getContradictResponse();
        StringBuilder locationUpdateThreadResponse = autoMessageCheck.getLocationUpdateResponse();

        // Check if a suggestion was made
        String suggestionResponse = " ";
        JSONObject suggestionJSON = null;
        if (suggestionThreadResponse != null) {
            suggestionJSON = new JSONObject(suggestionThreadResponse.toString());
            suggestionResponse = suggestionJSON.get("messageType").toString();
            System.out.println(suggestionResponse);
        }

        // Check if anyone has disproved/passed a suggestion
        String contradictResponse = " ";
        JSONObject contradictJSON = null;
        if (contradictThreadResponse != null) {
            contradictJSON = new JSONObject(contradictThreadResponse.toString());
            contradictResponse = contradictJSON.get("messageType").toString();
            System.out.println(contradictResponse);
        }

        // Check if anyone has moved
        String locationUpdateResponse = " ";
        JSONObject locationUpdateJSON = null;
        if (locationUpdateThreadResponse != null) {
            locationUpdateJSON = new JSONObject(locationUpdateThreadResponse.toString());
            locationUpdateResponse = locationUpdateJSON.get("messageType").toString();
        }


        if (playerList.get(whoseTurn).getPlayerName().equals(playerName) &&
                newTurnName.equals(playerName)) {

            p.hide();
            // When it is the player's first turn, assign them cards
            if (playerList.get(whoseTurn).getPlayerHand().isEmpty()) {
                // Assign cards to player
                StringBuilder cardAssignGET = ClueLessUtils
                        .makeGet(gameActions.getGameUUID(), "whoCards",
                                "assignCards");
                JSONObject cardAssignJSON = new JSONObject(cardAssignGET.toString());
                ArrayList<String> cards = getPlayerCards(cardAssignJSON); // TODO: change function o getListFromJSON
                for(String card: cards){
                    cardsPanel.add(new JLabel(card));
                }
                playerHand.addAll(cards);
                playerList.get(whoseTurn).setPlayerHand(cards);
            }

            // Delete the suggestion msgs in db after everyone has contradicted
            if (!suggestionResponse.equals("suggestionMade") || playerMadeSuggestion ||
                    justContradicted) {
                if (playerMadeSuggestion || justContradicted) {
                    ClueLessUtils.deleteItem(gameActions.getGameUUID(),
                            playerName, "makeSus_");
                    ClueLessUtils.deleteItem(gameActions.getGameUUID(),
                            playerName, "passSus_");
                    ClueLessUtils.deleteItem(gameActions.getGameUUID(),
                            playerName, "disproveSus_");
                    playerMadeSuggestion = false;
                    justContradicted = false;
                }

                p.hide();
                for(JButton button: controlButtons) {
                    button.setEnabled(true);

                }
                String outputString = "Your turn has begun.";
                JTextArea text = new JTextArea(outputString);
                JOptionPane.showMessageDialog(null,text);
            } else {
                // suggestion has been made, so player needs to act accordingly
                // need to get a response from each player, then delete sus msgs in db

                String playerWhoSuggested = suggestionJSON.get("playerWhoSuggested").toString();
                JSONObject cardsSuggested = (JSONObject) suggestionJSON.get("cardsSuggested");
                JLabel popLabel = new JLabel(playerWhoSuggested + " has made the following suggestion:");
                JLabel popLabelTwo = new JLabel(cardsSuggested.get("suspect") + ", " + cardsSuggested.get("weapon") + ", " +
                        cardsSuggested.get("location"));


                ArrayList<String> choices = new ArrayList();
                if(playerHand.contains(cardsSuggested.get("suspect").toString())){choices.add(cardsSuggested.get("suspect").toString());}

                if(playerHand.contains(cardsSuggested.get("weapon").toString())){choices.add(cardsSuggested.get("weapon").toString());}

                if(playerHand.contains(cardsSuggested.get("location").toString())){choices.add(cardsSuggested.get("location").toString());}

                JPanel popPanel = new JPanel();
                PopupFactory pf = new PopupFactory();
                Button option;
                // create a panel
                popPanel.setLayout(new GridLayout(5, 1));
                popPanel.add(new JPanel());

                popPanel.add(popLabel);
                popPanel.add(popLabelTwo);
                if(!choices.isEmpty()){
                    String[] choicesArray = new String[choices.size()];
                    choicesArray = choices.toArray(choicesArray);
                    inputBox = new JComboBox<String>(choicesArray);

                    option = new Button("Disprove Suggestion");
                    option.addActionListener(this);
                    popPanel.add(option);
                    popPanel.add(inputBox);
                }
                else{
                    option = new Button("Pass Suggestion");
                    option.addActionListener(this);
                    popPanel.add(option);
                }

                p = pf.getPopup(this, popPanel, 600, 350);
                p.show();
                justContradicted = true;
            }

        } else {

            p.hide();
            JLabel popLabel = new JLabel("It is currently " + newTurnName +
                    "'s turn. Please Wait.");
            JPanel popPanel = new JPanel();
            PopupFactory pf = new PopupFactory();
            // create a panel
            popPanel.setLayout(new GridLayout(3, 1));
            popPanel.add(new JPanel());

            popPanel.add(popLabel);
            popPanel.add(new JPanel());

            // create a popup

            p = pf.getPopup(this, popPanel, 600, 350);
            p.show();

            String playerWhoContradicted = "";

            String waitingForPlayerToFinish = newTurnName;

            boolean alreadyDeleted = false;
            int i = 0;
            while (waitingForPlayerToFinish.equals(newTurnName)) {
                String lastPlayer = waitingForPlayerToFinish;
                StringBuilder turnUpdateGET = ClueLessUtils.makeGet(gameActions.getGameUUID(),
                        "whoCares", "turnUpdate");
                JSONObject turnUpdateGETJSON = new JSONObject(turnUpdateGET.toString());
                waitingForPlayerToFinish = turnUpdateGETJSON.get("currentTurn").toString();
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (playerMadeSuggestion &&
                        !(waitingForPlayerToFinish.equals(lastPlayer))) {
                    StringBuilder cThreadResponse = autoMessageCheck.getContradictResponse();
                    if (contradictThreadResponse != null) {
                        contradictJSON = new JSONObject(cThreadResponse.toString());
                        contradictResponse = contradictJSON.get("messageType").toString();
                    }

                    if (contradictResponse.equals("disproveMade")) {
                        // If a player has contradicted a suggestion, delete suggestion post for each player
                        playerWhoContradicted = contradictJSON.get("playerWhoDisproved").toString();
                        String cardRevealed = contradictJSON.get("cardRevealed").toString();
                        ClueLessUtils.deleteItem(gameActions.getGameUUID(),
                                playerWhoContradicted, "disproveSus_");

                        // Add card to player who disproved
                        ArrayList<PlayerStatus> gameStatusList
                                = (ArrayList<PlayerStatus>) gameStatus.getActivePlayerList();
                        for (PlayerStatus ps : gameStatusList) {
                            if (ps.getPlayerName().equals(playerWhoContradicted)) {
                                ps.addPlayerHand(cardRevealed);
                            }
                        }
                        if (i == 0) {
                            System.out.println(playerWhoContradicted + " revealed " +
                                    cardRevealed + "!");
                            ++i;
                        }

                    } else if (contradictResponse.equals("passMade")) {
                        playerWhoContradicted = contradictJSON.get("playerWhoPassed").toString();
                        String nextPlayer = contradictJSON.get("nextPlayer").toString();
                        ClueLessUtils.deleteItem(gameActions.getGameUUID(),
                                playerWhoContradicted, "passSus_");
                        if (i == 0) {
                            System.out.println(playerWhoContradicted + " passed suggestion!");
                            ++i;
                        }

                    }
                    if (contradictResponse.equals("passMade") ||
                            contradictResponse.equals("disproveMade") && !alreadyDeleted) {
                        ClueLessUtils.deleteItem(gameActions.getGameUUID(),
                                playerWhoContradicted, "makeSus_");
                        alreadyDeleted = true;
                    }
                }
            }

            if (whoseTurn == (playerList.size() - 1)) {
                whoseTurn = 0;
            } else {
                whoseTurn++;
            }
        }
    }

    private void makeSuggestion() {

        if (ClueLessConstants.ROOM_LIST.contains(playerList.get(whoseTurn).getPlayerLocation())) {


            JLabel popLabel = new JLabel("Please select options for your suggestion.");
            JPanel popPanel = new JPanel();
            PopupFactory pf = new PopupFactory();
            JPanel headers = new JPanel();
            headers.setLayout(new GridLayout(1, 3));
            headers.add(new JLabel("Weapon:"));
            headers.add(new JLabel("Location:"));
            headers.add(new JLabel("Suspect:"));
            JPanel options = new JPanel();
            options.setLayout(new GridLayout(1, 3));
            options.add(guessBoxWeapons);
            options.add( new JLabel(playerList.get(whoseTurn).getPlayerLocation()));
            options.add(guessBoxSuspects);

            JButton inputSuggestion = new JButton("Submit Suggestion");
            inputSuggestion.addActionListener(this);

            // create a panel
            popPanel.setLayout(new GridLayout(4, 1));

            popPanel.add(popLabel);
            popPanel.add(headers);
            popPanel.add(options);
            popPanel.add(inputSuggestion);
            makeAccusationBtn.setEnabled(false);
            makeSuggestionBtn.setEnabled(false);


        } else {
            String outputString = "You cannot make a suggestion because you are not in a room.";
            JTextArea text = new JTextArea(outputString);
            JOptionPane.showMessageDialog(null,text);
        }
    }

    private void submitSuggestion() throws IOException {
        String suspect = guessBoxSuspects.getSelectedItem().toString();

        String location = playerList.get(whoseTurn).getPlayerLocation();

        String weapon = guessBoxWeapons.getSelectedItem().toString();

        gameActions.makeGuess(gameActions.getGameUUID(), playerName, suspect, weapon, location);
        System.out.println("You have made a suggestion");

        // Need to move player in suggestion to location of current player
        gameActions.movePiece(gameActions.getGameUUID(), suspect, location);
        ArrayList<PlayerStatus> gameStatusList
                = (ArrayList<PlayerStatus>) gameStatus.getActivePlayerList();

        for (PlayerStatus ps : gameStatusList) {
            if (ps.getPlayerName().equals(suspect)) {
                ps.setPlayerLocation(location);
            }
        }
        playerMadeSuggestion = true;
        movedPlayer = false;
        try {
            TimeUnit.SECONDS.sleep(2); // Need to sleep to let Thread read msg that was
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // just written to db
            String outputString = "Moving in that direction is not possible.";
            JTextArea text = new JTextArea(outputString);
            JOptionPane.showMessageDialog(null,text);
    }


    private void makeAccusation() {

        if (ClueLessConstants.ROOM_LIST.contains(playerList.get(whoseTurn).getPlayerLocation())) {


            JLabel popLabel = new JLabel("Please select options for your suggestion.");
            JPanel popPanel = new JPanel();
            PopupFactory pf = new PopupFactory();
            JPanel headers = new JPanel();
            headers.setLayout(new GridLayout(1, 3));
            headers.add(new JLabel("Weapon:"));
            headers.add(new JLabel("Location:"));
            headers.add(new JLabel("Suspect:"));
            JPanel options = new JPanel();
            options.setLayout(new GridLayout(1, 3));
            options.add(guessBoxWeapons);
            options.add( new JLabel(playerList.get(whoseTurn).getPlayerLocation()));
            options.add(guessBoxSuspects);

            JButton inputSuggestion = new JButton("Submit Accusation");
            inputSuggestion.addActionListener(this);

            // create a panel
            popPanel.setLayout(new GridLayout(4, 1));

            popPanel.add(popLabel);
            popPanel.add(headers);
            popPanel.add(options);
            popPanel.add(inputSuggestion);


        } else {
            String outputString = "You cannot make an accusation because you are not in a room.";
            JTextArea text = new JTextArea(outputString);
            JOptionPane.showMessageDialog(null,text);
        }
    }

    private void submitAccusation() throws IOException, InterruptedException {
        String suspect = guessBoxSuspects.getSelectedItem().toString();

        String location = playerList.get(whoseTurn).getPlayerLocation();

        String weapon = guessBoxWeapons.getSelectedItem().toString();

        String outputString = gameActions.makeAccusation(gameActions.getGameUUID(),
                playerName, suspect, weapon, location);
        JTextArea text = new JTextArea(outputString);
        JOptionPane.showMessageDialog(null,text);
        movedPlayer = false;
        TimeUnit.SECONDS.sleep(2);
    }


    private void boardSetLocation(String playerName, String playerNewLocation){

        String eachLoc = "";
        for(PlayerStatus player: playerList){
            eachLoc +=("Player:"+player.getPlayerName()+ " is in " +player.getPlayerLocation()+ ".\n");
        }

        locations.setText(eachLoc);

    }
    private void movePlayer(String moveType) throws InterruptedException, IOException {

        int moveDirection = 99;
        switch (moveType) {
            case "move up":
                moveDirection =0;
                break;
            case "move right":
                moveDirection =1;
                break;
            case "move down":
                moveDirection =2;
                break;
            case "move left":
                moveDirection =3;
                break;
            case "move through passage":
                moveDirection = 4;
                break;

        }
        String location = playerList.get(whoseTurn).getPlayerLocation();
        String newLocation = ClueLessConstants.ADJACENCY_MAP.get(location).get(moveDirection);
        if(!newLocation.equals("nomove")){

            moveUp.setEnabled(false);
            moveDown.setEnabled(false);
            moveLeft.setEnabled(false);
            moveRight.setEnabled(false);
            MovePassage.setEnabled(false);
            gameActions.movePiece(gameActions.getGameUUID(), playerName, newLocation);
            TimeUnit.SECONDS.sleep(2); // Need to sleep to let Thread read msg
            playerList.get(whoseTurn).setPlayerLocation(newLocation);
            movedPlayer = true;


        }
        else{
            String outputString = "Moving in that direction is not possible.";
            JTextArea text = new JTextArea(outputString);
            JOptionPane.showMessageDialog(null,text);
        }


    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getActionCommand().toLowerCase().contains("move")){
            try {
                movePlayer(e.getActionCommand().toLowerCase());
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        switch (e.getActionCommand().toLowerCase()){
            case "create game":
                p.hide();
                createMenu();
                break;
            case "join game":
                p.hide();
                joinMenu();
                break;
            case "connect to game":
                gameUUID = uuidField.getText().trim();
                gameUUIDLable.setText(gameUUIDLable.getText() + " " + gameUUID);

                try {
                    joinGame();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;
            case "start game":
                p.hide();
                try {
                    createGame();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;

            case "disprove suggestion":
                System.out.println("disprove suggestion");
                p.hide();
                try {
                    ClueLessUtils.disproveSuggestionPost(gameActions.getGameUUID(), playerName, inputBox.getSelectedItem().toString());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;

            case "pass suggestion":
                p.hide();

                int currentPlayer = whoseTurn;
                if (currentPlayer == (playerList.size() - 1)) {
                    currentPlayer = 0;
                } else {
                    currentPlayer++;
                }
                try {
                    ClueLessUtils.passSuggestionPost(gameActions.getGameUUID(), playerName,
                            playerList.get(currentPlayer).getPlayerName());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;

            case "make suggestion":
                makeSuggestion();
                break;

            case "submit suggestion":
                p.hide();
                try {
                    submitSuggestion();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;

            case "make accusation":
                makeAccusation();
                break;

            case "submit accusation":
                p.hide();
                try {
                    submitAccusation();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                break;

            case "end turn":

                endTurnBtn.setEnabled(false);
                makeAccusationBtn.setEnabled(false);
                makeSuggestionBtn.setEnabled(false);
                if (whoseTurn == (playerList.size() - 1)) {
                    whoseTurn = 0;
                } else {
                    whoseTurn++;
                }
                movedPlayer = false;
                System.out.println("I got here");
                try {
                    gameActions.endTurn(playerName, playerList.get(whoseTurn).getPlayerName());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;
        }
        System.out.println(e);
    }
}
