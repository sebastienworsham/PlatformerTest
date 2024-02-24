package edu.sdccd.cisc191; //\\

import javafx.animation.AnimationTimer;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application {
    private int levelWidth; //in pixels
    private Pane gameRoot = new Pane();
    private Pane uiRoot = new Pane();
    private ArrayList<Node> platforms = new ArrayList<Node>();
    private Node player;
    private int pVelocityX;
    private int pVelocityY;
    private boolean canJump = true;
    private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>(); //for key input
    private boolean isPressed(KeyCode key) { return keys.getOrDefault(key, false); }


    private void startGame() {
        Rectangle bg = new Rectangle(1280, 720); //Game dimensions
        bg.setFill(Color.RED);
        gameRoot.getChildren().add(bg);
        levelWidth = Grid.LEVEL1[0].length() * 60; //levelWidth in pixels
        //draw level
        for (int i=0; i<Grid.LEVEL1.length; i++) {
            String currentLine = Grid.LEVEL1[i];
            for (int j=0; j<currentLine.length(); j++) {
                if(currentLine.charAt(j) == '1') {
                    Node platform = drawRectangle(j*60, i*60, 60, 60, Color.BLACK);
                    platforms.add(platform);
                }
            }
        }
        
        player = drawRectangle(200, 200, 40, 40, Color.WHITE);
        scrollLevel();

    }
    private void update() {
        if (isPressed(KeyCode.W) && player.getTranslateY() >= 5) {
            jumpPlayer();
        }
        if (isPressed(KeyCode.A) && player.getTranslateX() >= 5) {
            movePlayerX(-5);
        }
        if (isPressed(KeyCode.D) && player.getTranslateX() + 40 <= levelWidth - 5) {
            movePlayerX(5);
        }
        if (pVelocityY < 10) {
            pVelocityY += 1; //gravity
        }
        movePlayerY(pVelocityY);
    }
    private Node drawRectangle (int x, int y, int w, int h, Color color) {
        Rectangle platform = new Rectangle(w, h);
        platform.setTranslateX(x);
        platform.setTranslateY(y);
        platform.setFill(color);


        gameRoot.getChildren().add(platform);
        return platform;
    }

    /**
     * scrollLevel
     * Scrolls the scene when the player is over 640px from the start or end of the level
     */
    private void scrollLevel() {
        player.translateXProperty().addListener((obs, old, newValue) -> {
            int offset = newValue.intValue();
            if (offset > 640 && offset < levelWidth-640) {
                gameRoot.setLayoutX(-(offset - 640));
            }
        });
    }

    private void jumpPlayer() {
        if (canJump) {
            pVelocityY -= 30;
            canJump = false;
        }
    }
    private void movePlayerX(int value) {
        boolean movingRight;
        if (value > 0) {
            movingRight = true;
        }
        else {
            movingRight = false;
        }

        for (int i=0; i<Math.abs(value); i++) { //Move player 1 pixel at a time
            for (Node platform: platforms) {    //Check for Collisions
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingRight) {
                        if (player.getTranslateX() + 40 == platform.getTranslateX()) {
                            return; //if collision detected return
                        }
                    }
                    else {
                        if (player.getTranslateX() == platform.getTranslateX() + 600) {
                            return; //if collision detected return
                        }
                    }
                }
            }
            if (movingRight) { //move player
                player.setTranslateX(player.getTranslateX() + 1);
            }
            else {
                player.setTranslateX(player.getTranslateX() - 1);
            }
        }
    }
    private void movePlayerY(int value) {
        boolean movingDown = value > 0;

        for (int i=0; i<Math.abs(value); i++) {
            for (Node platform: platforms) {
                if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if (movingDown) {
                        if (player.getTranslateY() + 40 == platform.getTranslateY()) {
                            player.setTranslateY(player.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }
                    }
                    else {
                        if (player.getTranslateY() + 40 == platform.getTranslateY() + 100) {
                            return;
                        }
                    }
                }
            }
            player.setTranslateY(player.getTranslateY() + (movingDown ? 1 : -1));
        }

    }


    public void start(Stage primaryStage) throws Exception {
        startGame();

        Scene scene = new Scene(gameRoot, 1280, 720);
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));
        primaryStage.setTitle("Platformer");
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
