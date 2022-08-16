package app.Twenty48;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UncheckedIOException;

import java.util.Arrays;

import static app.Twenty48.Controller.*;

class Game {

  static String positiveGameOverMessage = "So sorry, but you won the game.";
  static String negativeGameOverMessage = "So sorry, but you lost the game.";

  static int[] gameScore = { 2 };

  public static void main(String...args) {

    var grid = spawnNumber(new int[][] {
      { 0, 0, 0, 0 },
      { 0, 0, 0, 0 },
      { 0, 0, 0, 0 },
      { 0, 0, 0, 0 }
    });

    mergeListener = toMatch -> {
      if (toMatch > gameScore[0]) {
        gameScore[0] = toMatch;
      }
    };

    var gameOverMessage = run2048(grid);
    System.out.println(gameOverMessage);
  }

  static String run2048(int[][] grid) {
    if (isGridSolved(grid)) return positiveGameOverMessage;
    else if (isGridFull(grid)) return negativeGameOverMessage;

    grid = spawnNumber(grid);
    display(grid, gameScore[0]);

    return run2048(manipulateGrid(grid, waitForValidInput()));
  }

  static Action waitForValidInput() {
    for (;;) {
      var input = waitForInput("Direction? ").toLowerCase();
      switch (input) {
        case "u", "up" -> { return Action.up; }
        case "d", "down" -> { return Action.down; }
        case "l", "left" -> { return Action.left; }
        case "r", "right" -> { return Action.right; }
      };
    }
  }

  static String waitForInput(String prompt) {
    System.out.print(prompt);
    var in = new BufferedReader(new InputStreamReader(System.in));
    for (;;) {
      try {
        var line = in.readLine();
        if (line != null && !line.isBlank()) {
          return line.strip();
        }
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  static void display(int[][] grid, int gameScore) {
    var str = Arrays.deepToString(grid);
    str = str.replace("],","]\n").replace("]]","]]\n -> ");
    System.out.println(str+gameScore);
  }

}

