package main.java;

import org.json.JSONObject;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static main.java.ClueLessConstants.MAIN_MENU_ENTRIES;

public class UserInterface extends JFrame implements ActionListener {

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
        put("MissScarlet", missScarletLable);
        put("ColonelMustard", colonelMustardLable);
        put("Mrs.White", mrsPeacokLable);
        put("Mr.Green", mrGreenLable);
        put("Mrs.Peacock", mrsWhiteLable);
        put("ProfessorPlum", profPlumLable);
        }};
    JPanel cardsPanel = new JPanel();
    Popup p;
    private JComboBox<String> inputBox;
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

    public UserInterface() {

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
        boardPanel.setBackground(Color.CYAN);
        // CONTROLS PANNEL SETUP
        JButton moveUp = new JButton("Move Up");
        JButton moveDown = new JButton("Move Down");
        JButton moveLeft = new JButton("Move Left");
        JButton moveRight = new JButton("Move Right");
        JButton MovePassage = new JButton("Enter Passage");
        JButton makeGuess = new JButton("Make Guess");
        JButton makeAccussation = new JButton("Make Accussation");
        JButton endTurn = new JButton("End Turn");

        controlButtons.add(moveUp);
        controlButtons.add(moveDown);
        controlButtons.add(moveLeft);
        controlButtons.add(moveRight);
        controlButtons.add(MovePassage);
        controlButtons.add(makeGuess);
        controlButtons.add(makeAccussation);
        controlButtons.add(endTurn);


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

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case "Create Game":
                p.hide();
                createMenu();
                break;
            case "Join Game":
                p.hide();
                joinMenu();
                break;
            case "Connect to Game":
                gameUUID = uuidField.getText().trim();
                gameUUIDLable.setText(gameUUIDLable.getText() + " " + gameUUID);

                try {
                    joinGame();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;
            case "Start Game":
                p.hide();
                try {
                    createGame();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;

        }
        System.out.println(e);
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
        int i = 0;
        ArrayList<String> players = new ArrayList<>();

        JSONObject responseJSON = new JSONObject(startGameResponse);
        JSONObject activePlayers = (JSONObject) responseJSON.get("activePlayers");

        int counter =2;
        for (String key : activePlayers.keySet()) {
            JLabel current = playersToLableMap.get(key);
            current.setText(current.getText() + "Player: "+ counter);
            counter++;
            players.add(activePlayers.get(key).toString()); // all active players in game
        }
        printListContents(players, "players");
        ArrayList<String> turnOrderForGame = obtainTurnOrder(players);
        loadPlayerInfoIntoGameStatus(turnOrderForGame);


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
        ArrayList<PlayerStatus> gameStatusPL = new ArrayList<>();
        for (String plyr : turnOrderForGame) {
            PlayerStatus playerStatus = new PlayerStatus();
            playerStatus.setPlayerName(plyr);

            ArrayList<String> emptyList = new ArrayList<>();
            playerStatus.setPlayerHand(emptyList);

            playerStatus.setPlayerLocation(plyr + "Start");
            gameStatusPL.add(playerStatus);
        }
        gameStatus.setActivePlayerList(gameStatusPL); // add ordered active players to GameStatus
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
        waitingForPlayers();
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
                    //TODO move on board
                    ps.setPlayerLocation(playerNewLocation);
                }
            }
        }
    }

    public void playerTurnUpdate(String newTurnName) throws IOException {

        boolean movedPlayer = false;
        boolean playerMadeSuggestion = false;
        boolean justContradicted = false;

        StringBuilder suggestionThreadResponse = autoMessageCheck.getSuggestionResponse();
        StringBuilder contradictThreadResponse = autoMessageCheck.getContradictResponse();
        StringBuilder locationUpdateThreadResponse = autoMessageCheck.getLocationUpdateResponse();

        // Check if a suggestion was made
        String suggestionResponse = " ";
        JSONObject suggestionJSON = null;
        if (suggestionThreadResponse != null) {
            suggestionJSON = new JSONObject(suggestionThreadResponse.toString());
            suggestionResponse = suggestionJSON.get("messageType").toString();
        }

        // Check if anyone has disproved/passed a suggestion
        String contradictResponse = " ";
        JSONObject contradictJSON = null;
        if (contradictThreadResponse != null) {
            contradictJSON = new JSONObject(contradictThreadResponse.toString());
            contradictResponse = contradictJSON.get("messageType").toString();
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
            // When it is the player's first turn, assign them cards
            if (playerList.get(whoseTurn).getPlayerHand().isEmpty()) {
                // Assign cards to player
                StringBuilder cardAssignGET = ClueLessUtils
                        .makeGet(gameActions.getGameUUID(), "whoCards",
                                "assignCards");
                JSONObject cardAssignJSON = new JSONObject(cardAssignGET.toString());
                ArrayList<String> cards = getPlayerCards(cardAssignJSON); // TODO: change function o getListFromJSON
                for(String card: cards){
                    cardsPanel.add(new JLabel("card"));                }
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

                for(JButton button: controlButtons) {
                    button.setEnabled(true);
                }
            } else {
                // suggestion has been made, so player needs to act accordingly
                // need to get a response from each player, then delete sus msgs in db
                String playerWhoSuggested = suggestionJSON.get("playerWhoSuggested").toString();
                System.out.println(playerWhoSuggested + " has made the following suggestion: ");
                JSONObject cardsSuggested = (JSONObject) suggestionJSON.get("cardsSuggested");

                System.out.print(cardsSuggested.get("suspect") + ", " + cardsSuggested.get("weapon") + ", " +
                        cardsSuggested.get("location"));
                System.out.println();
                Scanner input = new Scanner(System.in);
                System.out.println("What would you like to do? ");
                System.out.println("    a) Disprove a Suggestion");
                System.out.println("    b) Pass Suggestion");
                String choice = input.nextLine();

                switch (choice.toLowerCase()) {
                    case "a":
                        System.out.println("Which card do you want to reveal?");
                        String card = input.nextLine();

                        ClueLessUtils.disproveSuggestionPost(gameActions.getGameUUID(), playerName, card);
                        break;
                    case "b":
                        int currentPlayer = whoseTurn;
                        if (currentPlayer == (playerList.size() - 1)) {
                            currentPlayer = 0;
                        } else {
                            currentPlayer++;
                        }
                        ClueLessUtils.passSuggestionPost(gameActions.getGameUUID(), playerName,
                                playerList.get(currentPlayer).getPlayerName());
                        break;
                }
                justContradicted = true;
            }

            // Send endTurn msg to signal player is done
            boolean inARoom = ClueLessConstants.ROOM_LIST
                    .contains(playerList.get(whoseTurn).getPlayerLocation());

            if ((validInput && !movedPlayer) || (movedPlayer && !inARoom)) {
                if (whoseTurn == (playerList.size() - 1)) {
                    whoseTurn = 0;
                } else {
                    whoseTurn++;
                }
                movedPlayer = false;
                // send msg to db ending player's turn
                gameActions.endTurn(playerName, playerList.get(whoseTurn).getPlayerName());
            }
            if (endOfGame.equals("playerLost") || endOfGame.equals("gameOver")) {
                autoMessageCheck.setStopThreads();
            }
            if (endOfGame.equals("playerLost")) {
                // Delete player who lost from db
                ClueLessUtils.deleteItem(gameActions.getGameUUID(),
                        playerName, "anything");
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
                TimeUnit.SECONDS.sleep(2);

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


}
