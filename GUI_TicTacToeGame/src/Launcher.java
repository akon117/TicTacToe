/**
 * @author Fernando Ramirez
 * Class: Coding Fundamentals 2
 * Date: 10/20/2020
 * Tic Tac Toe GUI HW: Launcher for application. It will create an instnace of the TIc Tac Toe
 * game, and create the scene for it, then launch it.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Launcher extends Application {
    private BackgroundImage backgroundImage;
    private BackgroundSize backgroundSize;
    private Background background;
    private Image bgImage;
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method will create an instance of the TicTacToePane and a background image.
     * the Pane and the image will be added to the scene and will be presented.
     *
     * @param primaryStage the stage where the scene will go to.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        TicTacToePane pane = new TicTacToePane();
        Pane root = pane;

        bgImage = new Image("File:cityscapeRED.png");
        backgroundSize = new BackgroundSize(500,500,true,true,true, false);
        backgroundImage = new BackgroundImage(bgImage, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, backgroundSize);
        background = new Background(backgroundImage);
        root.setBackground(background);

        Scene scene = new Scene(root, 500, 500);

        primaryStage.setTitle("Tic Tac Toe!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
