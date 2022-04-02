/**
 * @author Fernando Ramirez
 * Class: Coding Fundamentals 2
 * Date: 10/20/2020
 * Tic Tac Toe GUI HW: THis is the Pane of the Tic Tac Toe game. It will create the GUI for it
 * with an empty board. Methods are created to change and play songs, do turns for the AI, checki for wins
 * and blocks, and a methoid for the button being pressed.
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TicTacToePane extends VBox {

    //data
    private int wins, losses, ties, numOfGames, turn;
    private boolean gameOver = false;
    private boolean aiThinking = false;


    //Gui Components
    private Button[][] ticTacToeButtons;
    private Label headerLabel, winsLabel, lossesLabel, tiesLabel, numOfGamesLabel;
    private Button playAgain;
    private ChoiceBox<String> choice;
    private AudioClip[] tunes;
    private AudioClip current;
    private Button playButton, stopButton;




    public TicTacToePane() {
        ticTacToeButtons = new Button[3][3];

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        for (int c = 0; c < 3; c++) {
            for (int r = 0; r < 3; r++) {
                ticTacToeButtons[c][r] = new Button("");
                ticTacToeButtons[c][r].setFont(new Font(30));
                ticTacToeButtons[c][r].setPrefWidth(75);
                ticTacToeButtons[c][r].setPrefHeight(75);
                gridPane.add(ticTacToeButtons[c][r], c, r);
                ticTacToeButtons[c][r].setOnAction(this::doButtonAction);
            }
        }

        headerLabel = new Label("");
        headerLabel.setFont(new Font("Broadway",24));
        headerLabel.setTextFill(Color.WHITE);
        headerLabel.setStyle("-fx-background-color:black");


        setData();
        resetGame();

        //Set stat labels
        winsLabel = new Label("Wins: " + wins);
        winsLabel.setTextFill(Color.WHITE);
        winsLabel.setFont(new Font("Broadway",18));
        winsLabel.setStyle("-fx-background-color:black");
        lossesLabel = new Label("Losses: " + losses);
        lossesLabel.setTextFill(Color.WHITE);
        lossesLabel.setFont(new Font("Broadway",18));
        lossesLabel.setStyle("-fx-background-color:black");
        tiesLabel = new Label("Ties: " + ties);
        tiesLabel.setTextFill(Color.WHITE);
        tiesLabel.setFont(new Font("Broadway",18));
        tiesLabel.setStyle("-fx-background-color:black");
        numOfGamesLabel = new Label("Games: " + numOfGames);
        numOfGamesLabel.setTextFill(Color.WHITE);
        numOfGamesLabel.setFont(new Font("Broadway",18));
        numOfGamesLabel.setStyle("-fx-background-color:black");

        VBox statBox = new VBox(winsLabel, lossesLabel, tiesLabel, numOfGamesLabel);
        //statBox.setStyle("-fx-background-color:black");
        statBox.setAlignment(Pos.CENTER_RIGHT);
        statBox.setSpacing(10);
        statBox.setPrefWidth(150);
        statBox.setPrefHeight(180);



        //set Music Box
        String[] names = {"A Woman","Price","Layer Cake","Beneath The Mask"};

        File[] audioFiles = {new File("J-MUSIC Ensemble - METAGROOVE - 08 A Woman.mp3"),
                new File("J-MUSIC Ensemble - METAGROOVE - 09 Price -feat. Norman Edwards-.mp3"),
                new File("J-MUSIC Ensemble - METAGROOVE - 12 Layer Cake.mp3"),
                new File("J-MUSIC Ensemble - METAGROOVE - 13 Beneath The Mask -feat. Ruby Choi-.mp3")};

        tunes = new AudioClip[audioFiles.length];
        for(int i = 0; i < audioFiles.length; i++)
            tunes[i] = new AudioClip(audioFiles[i].toURI().toString());

        current = tunes[0];

        Label songLabel = new Label("Select a song:");
        songLabel.setFont(new Font("Broadway",14));
        songLabel.setTextFill(Color.WHITE);
        songLabel.setStyle("-fx-background-color:black");
        
        choice = new ChoiceBox<String>();
        choice.getItems().addAll(names);
        choice.getSelectionModel().selectFirst();
        choice.setOnAction(this::selectSong);

        playButton = new Button("Play");
        stopButton = new Button("Stop");
        HBox songButtons = new HBox(playButton, stopButton);
        songButtons.setSpacing(10);
        songButtons.setPadding(new Insets(15,0,0,0));
        songButtons.setAlignment(Pos.CENTER);

        playButton.setOnAction(this::processSongButtons);
        stopButton.setOnAction(this::processSongButtons);

        VBox songRoot = new VBox(songLabel,choice,songButtons);
        songRoot.setPadding(new Insets(15,15,15,25));
        songRoot.setSpacing(10);
        songRoot.setAlignment(Pos.CENTER);

        HBox middleBox = new HBox(statBox, gridPane,songRoot);
        middleBox.setSpacing(20);
        middleBox.setAlignment(Pos.CENTER);


        setAlignment(Pos.CENTER);
        getChildren().addAll(headerLabel, middleBox);
    }

    /**
     * This method processes the song buttons to stop or play the song.
     * @param actionEvent button being pressed.
     */
    private void processSongButtons(ActionEvent actionEvent) {
        current.stop();

        if(actionEvent.getSource() == playButton)
            current.play();

    }

    /**
     * This method selects the song from the list of songs.
     *
     * @param event song being selected
     */
    private void selectSong(Event event) {
        current.stop();
        current = tunes[choice.getSelectionModel().getSelectedIndex()];

    }

    /**
     * This method will check, and say whose turn it is. Depending on whose turn it is, will change the symbol being placed
     * on the grid. If the game gets to turn 9, the game will end.
     *
     * @param event place on the grid being selected. THat being an 'X' or an 'O'
     */
    private void doButtonAction(ActionEvent event) {
        System.out.printf("\n%s's turn.", turn % 2 == 0 ? "X" : "O");
        System.out.println("doButtonAction");

        if (!gameOver && !aiThinking) {
            Button clickedBtn = (Button) event.getSource();

            //If spot taken, exit method
            if (clickedBtn.getText().length() > 0) {
                if (turn % 2 != 0) {
                    System.out.println("Doing bot turn again");
                    doTurnForAI();
                }
                return;
            }
            //Spot not taken
            String place;

            if (turn % 2 == 0) {
                //currently X's turn
                place = "X";
            } else {
                //currently O's turn
                place = "O";
            }

            turn++;
            clickedBtn.setFont(new Font("Broadway",24));
            clickedBtn.setText(place);

            if (turn >= 5) {
                //TODO - Check for win
                if (checkIfWon(place)) {
                    //TODO - Stop game
                    headerLabel.setText(String.format("%s won!", place));
                    gameOver = true;
                    saveData();
                    playAgain();
                    return;
                }
            }

            if (turn == 9) {
                gameOver = true;
                headerLabel.setText("Game over... No winner.");
                saveData();
                playAgain();
                return;
            }

            headerLabel.setText(String.format("%s's turn.", turn % 2 == 0 ? "X" : "O"));

            if (turn % 2 != 0) {
                //AI's turn
                aiThinking = true;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            doTurnForAI();
                        });
                    }
                };
                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                service.schedule(runnable, 1000, TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * This method will do the turn for the AI for every odd turn. Will check for possible wins
     * or blocks if possible. If it can't find any, it will select a random spot.
     */
    private void doTurnForAI() {
        //TODO - Make smart
        System.out.println("AI Turn");
        aiThinking = false;
            if(!(turn >= 2 && checkPossibleWins())){
                if(!(turn >= 3 && checkBlock())){
                    ticTacToeButtons[(int) (Math.random() * 3)][(int) (Math.random() * 3)].fire();
                }
            }

    }

    /**
     * This method will check for a win for the player.
     *
     * @param player The  player or symbol that is being checked for a win. That being
     *            'X' or 'O'
     * @return true if the player has a win.
     */
    private boolean checkIfWon(String player) {
        //TODO - Check ticTacToeButtons array to see if player won.

        //TODO - use for loop to check all rows horizontally
        if ((player.equals(ticTacToeButtons[0][0].getText()) &&
                player.equals(ticTacToeButtons[0][1].getText()) &&
                player.equals(ticTacToeButtons[0][2].getText())) ||
                (player.equals(ticTacToeButtons[1][0].getText()) &&
                        player.equals(ticTacToeButtons[1][1].getText()) &&
                        player.equals(ticTacToeButtons[1][2].getText())) ||
                (player.equals(ticTacToeButtons[2][0].getText()) &&
                        player.equals(ticTacToeButtons[2][1].getText()) &&
                        player.equals(ticTacToeButtons[2][2].getText()))) {
            return true;
        } else
            //TODO -use for loop to check all cols vertically
            if ((player.equals(ticTacToeButtons[0][0].getText()) &&
                    player.equals(ticTacToeButtons[1][0].getText()) &&
                    player.equals(ticTacToeButtons[2][0].getText())) ||
                    (player.equals(ticTacToeButtons[0][1].getText()) &&
                            player.equals(ticTacToeButtons[1][1].getText()) &&
                            player.equals(ticTacToeButtons[2][1].getText())) ||
                    (player.equals(ticTacToeButtons[0][2].getText()) &&
                            player.equals(ticTacToeButtons[1][2].getText()) &&
                            player.equals(ticTacToeButtons[2][2].getText()))) {
                return true;
            } else
                //TODO - check diagonally both ways
                if ((player.equals(ticTacToeButtons[0][0].getText()) &&
                        player.equals(ticTacToeButtons[1][1].getText()) &&
                        player.equals(ticTacToeButtons[2][2].getText())) ||
                        (player.equals(ticTacToeButtons[0][2].getText()) &&
                                player.equals(ticTacToeButtons[1][1].getText()) &&
                                player.equals(ticTacToeButtons[2][0].getText()))) {
                    return true;
                }

        return false;
    }

    /**
     * This method will read the TicTacToeData.txt file and update the wins, loses, draws, and number of
     * games played. If the file can't be read, the stats will be set to zero.
     */
    private void setData() {
        //read a file for data: wins, losses, ties, numbersOfGames
        //TODO - Read file and set wins, losses, ties, numberOfGames.
        File gameFile = new File("TicTacToeData.txt");
        try {
            Scanner fileScan = new Scanner(gameFile);

            while (fileScan.hasNextLine()) {
                String line = fileScan.nextLine();
                System.out.println(line);
                Scanner lineScan = new Scanner(line);
                lineScan.useDelimiter(",");

                wins = lineScan.nextInt();
                losses = lineScan.nextInt();
                ties = lineScan.nextInt();
                numOfGames = lineScan.nextInt();
            }
            System.out.println("Num of games: " + numOfGames);
            System.out.println("ties: " + ties);
            System.out.println("wins: " + wins);
            System.out.println("losses: " + losses);
            fileScan.close();
        } catch (FileNotFoundException e) {
            //If file can't be read, set data
            System.out.println("File not found");
            wins = 0;
            losses = 0;
            ties = 0;
            numOfGames = 0;
        }


    }

    /**
     * This method will reset the tic tac toe board to blank spaces when the game is over. This will also
     * remove the play again button.
     *
     * @return null
     */
    private EventHandler<ActionEvent> resetGame() {
        for (int c = 0; c < 3; c++) {
            for (int r = 0; r < 3; r++) {
                ticTacToeButtons[c][r].setText("");
            }
        }
        turn = 0;
        gameOver = false;
        headerLabel.setText("X's turn. Click button to take spot");
        getChildren().remove(playAgain);


        return null;
    }

    /**
     * This method will create a button that says play again when the game is over. If the user
     * clicks on the button, the game will reset by calling the resetGame() method.
     */
    private void playAgain() {
        System.out.println("Ask user to play again");
        playAgain = new Button();
        getChildren().add(playAgain);
        playAgain.setFont(new Font(10));
        playAgain.setPrefSize(100, 30);
        playAgain.setText("Play Again?");
        playAgain.setOnAction(event -> resetGame());

    }

    /**
     * This method will update the stats for the game by reading the TicTacToeData.txt file
     * and overwriting the data in it to update it.
     */
    private void saveData() {

        numOfGames++;
        numOfGamesLabel.setText("Games: " + numOfGames);
        if (turn == 9) {
            ties++;
            tiesLabel.setText("Ties: " + ties);
        } else if (turn % 2 == 1) {
            wins++;
            winsLabel.setText("Wins: " + wins);
        } else {
            losses++;
            lossesLabel.setText("Losses: " + losses);
        }

        System.out.println("Num of games: " + numOfGames);
        System.out.println("ties: " + ties);
        System.out.println("wins: " + wins);
        System.out.println("losses: " + losses);

        File gameFile = new File("TicTacToeData.txt");
        try {
            FileWriter fileWriter = new FileWriter(gameFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(wins + "," + losses + "," + ties + "," + numOfGames);
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("File not found");
        }


    }

    //method that checks if there is a possible win

    /**
     * This method will check for possible wins by looking through the Tic Tac Toe grid and look for
     * all possible combinations of a win.
     *
     * @return true if there is a possible win.
     */
    private boolean checkPossibleWins() {

        //checks diagonals
        System.out.println("Checking diagonals");
        if (((ticTacToeButtons[0][0].getText().equals(ticTacToeButtons[1][1].getText())) &&
                (hasO(0,0))) &&
                (ticTacToeButtons[2][2].getText().length() == 0)) {
            ticTacToeButtons[2][2].fire();
            return true;
        }
        else if ((((ticTacToeButtons[0][0].getText().equals(ticTacToeButtons[2][2].getText())) &&
                (hasO(0,0))) &&
                (ticTacToeButtons[1][1].getText().length() == 0)) ||
                ((hasO(0,2)) &&
                ((ticTacToeButtons[0][2].getText().equals(ticTacToeButtons[2][0].getText())) &&
                (ticTacToeButtons[1][1].getText().length() == 0)))) {
            ticTacToeButtons[1][1].fire();
            return true;
        }
        else if (((ticTacToeButtons[1][1].getText().equals(ticTacToeButtons[2][2].getText())) &&
                (ticTacToeButtons[0][0].getText().length() == 0)) &&
                (hasO(1,1))) {
            ticTacToeButtons[0][0].fire();
            return true;
        }
        else if ((ticTacToeButtons[0][2].getText().equals(ticTacToeButtons[1][1].getText())) &&
                ((ticTacToeButtons[2][0].getText().length() == 0) &&
                hasO(0,2))) {
            ticTacToeButtons[2][0].fire();
            return true;
        }
        else if ((ticTacToeButtons[2][0].getText().equals(ticTacToeButtons[1][1].getText())) &&
                ((ticTacToeButtons[0][2].getText().length() == 0) &&
                (hasO(2,0)))) {
            ticTacToeButtons[0][2].fire();
            return true;
        } else {
            //checking columns
            for (int c = 0; c < 3; c++) {
                System.out.println("Checking columns");
                if (((ticTacToeButtons[c][0].getText().equals(ticTacToeButtons[c][1].getText())) &&
                        (ticTacToeButtons[c][0].getText().length() > 0)) &&
                        (ticTacToeButtons[c][2].getText().length() == 0 && hasO(c, 0))) {
                    System.out.println(1);
                    ticTacToeButtons[c][2].fire();
                    return true;

                } else if ((ticTacToeButtons[c][0].getText().equals(ticTacToeButtons[c][2].getText())) &&
                        (ticTacToeButtons[c][1].getText().length() == 0 && hasO(c, 0))) {
                    System.out.println(2);
                    ticTacToeButtons[c][1].fire();
                    return true;

                } else if ((ticTacToeButtons[c][1].getText().equals(ticTacToeButtons[c][2].getText())) &&
                        (ticTacToeButtons[c][0].getText().length() == 0 && hasO(c, 1))) {
                    System.out.println(3);
                    ticTacToeButtons[c][0].fire();
                    return true;
                }
            }
            //checking rows
            for (int r = 0; r < 3; r++) {
                System.out.println("Checking rows");
                if ((ticTacToeButtons[0][r].getText().equals(ticTacToeButtons[1][r].getText())) &&
                    (ticTacToeButtons[2][r].getText().length() == 0 && hasO(0,r))) {
                    ticTacToeButtons[2][r].fire();
                    return true;
                }
                else if ((ticTacToeButtons[0][r].getText().equals(ticTacToeButtons[2][r].getText())) &&
                        (ticTacToeButtons[1][r].getText().length() == 0 && hasO(0,r))) {
                        ticTacToeButtons[1][r].fire();
                        return true;
                }
                else if ((ticTacToeButtons[1][r].getText().equals(ticTacToeButtons[2][r].getText())) &&
                        ticTacToeButtons[0][r].getText().length() == 0 && hasO(1,r)) {
                        ticTacToeButtons[0][r].fire();
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * This method will check to see if the is a possible win for the player so the computer can block its move.
     *
     * @return true if there is a possible block move.
     */
    private boolean checkBlock(){
        //checks diagonals
        System.out.println("Checking diagonals");
        if (((ticTacToeButtons[0][0].getText().equals(ticTacToeButtons[1][1].getText())) &&
                (hasX(0,0))) &&
                (ticTacToeButtons[2][2].getText().length() == 0)) {
            ticTacToeButtons[2][2].fire();
            return true;
        }
        else if ((((ticTacToeButtons[0][0].getText().equals(ticTacToeButtons[2][2].getText())) &&
                (hasX(0,0))) &&
                (ticTacToeButtons[1][1].getText().length() == 0)) ||
                ((hasX(0,2)) &&
                        ((ticTacToeButtons[0][2].getText().equals(ticTacToeButtons[2][0].getText())) &&
                                (ticTacToeButtons[1][1].getText().length() == 0)))) {
            ticTacToeButtons[1][1].fire();
            return true;
        }
        else if (((ticTacToeButtons[1][1].getText().equals(ticTacToeButtons[2][2].getText())) &&
                (ticTacToeButtons[0][0].getText().length() == 0)) &&
                (hasX(1,1))) {
            ticTacToeButtons[0][0].fire();
            return true;
        }
        else if ((ticTacToeButtons[0][2].getText().equals(ticTacToeButtons[1][1].getText())) &&
                ((ticTacToeButtons[2][0].getText().length() == 0) &&
                        hasX(0,2))) {
            ticTacToeButtons[2][0].fire();
            return true;
        }
        else if ((ticTacToeButtons[2][0].getText().equals(ticTacToeButtons[1][1].getText())) &&
                ((ticTacToeButtons[0][2].getText().length() == 0) &&
                        (hasX(2,0)))) {
            ticTacToeButtons[0][2].fire();
            return true;
        } else {
            //checking columns
            for (int c = 0; c < 3; c++) {
                System.out.println("Checking columns");
                if (((ticTacToeButtons[c][0].getText().equals(ticTacToeButtons[c][1].getText())) &&
                        (ticTacToeButtons[c][0].getText().length() > 0)) &&
                        (ticTacToeButtons[c][2].getText().length() == 0 && hasX(c, 0))) {
                    System.out.println(1);
                    ticTacToeButtons[c][2].fire();
                    return true;

                } else if ((ticTacToeButtons[c][0].getText().equals(ticTacToeButtons[c][2].getText())) &&
                        (ticTacToeButtons[c][1].getText().length() == 0 && hasX(c, 0))) {
                    System.out.println(2);
                    ticTacToeButtons[c][1].fire();
                    return true;

                } else if ((ticTacToeButtons[c][1].getText().equals(ticTacToeButtons[c][2].getText())) &&
                        (ticTacToeButtons[c][0].getText().length() == 0 && hasX(c, 1))) {
                    System.out.println(3);
                    ticTacToeButtons[c][0].fire();
                    return true;
                }
            }
            //checking rows
            for (int r = 0; r < 3; r++) {
                System.out.println("Checking rows");
                if ((ticTacToeButtons[0][r].getText().equals(ticTacToeButtons[1][r].getText())) &&
                        (ticTacToeButtons[2][r].getText().length() == 0 && hasX(0,r))) {
                    ticTacToeButtons[2][r].fire();
                    return true;
                }
                else if ((ticTacToeButtons[0][r].getText().equals(ticTacToeButtons[2][r].getText())) &&
                        (ticTacToeButtons[1][r].getText().length() == 0 && hasX(0,r))) {
                    ticTacToeButtons[1][r].fire();
                    return true;
                }
                else if ((ticTacToeButtons[1][r].getText().equals(ticTacToeButtons[2][r].getText())) &&
                        ticTacToeButtons[0][r].getText().length() == 0 && hasX(1,r)) {
                    ticTacToeButtons[0][r].fire();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method will see if the symbol at a location on the TicTacToe grid is an X
     * 
     * @param c x axis of the TicTacToe grid
     * @param r y axis of the TicTacToe grid
     * @return true if the location is an X
     */
    private boolean hasX(int c, int r){
        if(ticTacToeButtons[c][r].getText().equalsIgnoreCase("X")){
            return true;
        }
        return false;
    }

    /**
     * This method will see if the symbol at a location on the TicTacToe grid is an O
     *
     * @param c x axis of the TIcTacToe grid.
     * @param r y axis of the TicTacToe grid
     * @return true if the location is an O
     */
    private boolean hasO(int c, int r){
        if(ticTacToeButtons[c][r].getText().equalsIgnoreCase("O")){
            return true;
        }
        return false;
    }

}
