package app.reversi.fx;

import java.io.File;
import java.io.IOException;

import java.io.FileReader;
import java.io.BufferedReader;

import java.io.FileWriter;
import java.io.BufferedWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Window;

class Filer {

  static boolean save(Window ownerWindow, Board board, int turn) {
    var fileChooser = new FileChooser();
    fileChooser.setInitialFileName("SAVE-" + timestamp() + ".reversi");
    try {
      var file = fileChooser.showSaveDialog(ownerWindow);
      if (file != null) {
        writeBoardStateToFile(file, board, turn);
        var alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("File saved!");
        alert.showAndWait();
        return true;
      }
    } catch (IOException | NullPointerException e) {
      var alert = new Alert(AlertType.ERROR);
      alert.setContentText("Can't write file! "+e);
      alert.show();
    }
    return false;
  }

  static void writeBoardStateToFile(File file, Board board, int turn) throws IOException {
    var grid = board.grid();
    try (
      var fw = new FileWriter(file);
      var bw = new BufferedWriter(fw);
    ) {
      for (var row = 0; row < grid.length; row++) {
        for (var col = 0; col < grid[row].length; col++) {
          var disc = board.discAt(row, col);
          var s = row + "," + col + "," + disc.state() + "\n";
          bw.write(s);
        }
      }
      var playerTurnString = Integer.toString(turn);
      bw.write(playerTurnString);
    }
  }

  static int load(Window ownerWindow, Board board) {
    var fileChooser = new FileChooser();
    fileChooser.setTitle("Open Game File");
    try {
      var file = fileChooser.showOpenDialog(ownerWindow);
      if (file != null) {
        return readBoardStateFromFile(file,board);
      }
    } catch (NumberFormatException | IOException | NullPointerException e) {
      var alert = new Alert(AlertType.ERROR);
      alert.setAlertType(AlertType.ERROR);
      alert.setContentText("Can't read file: "+e);
      alert.show();
    }
    return -1;
  }

  static int readBoardStateFromFile(File file, Board board) throws IOException {
    var playerState = -2;
    try (
      var fr = new FileReader(file);
      var br = new BufferedReader(fr);
    ) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.length() > 1) {
          var splitLine = line.split(",");
          int row = Integer.valueOf(splitLine[0]);
          int col = Integer.valueOf(splitLine[1]);
          int discState = Integer.valueOf(splitLine[2]);
          board.discAt(row, col).state(discState);
        } else {
          playerState = Integer.valueOf(line);
          break;
        }
      }
    }
    return playerState;
  }

  static String timestamp() {
    var format = "yyyyMMdd-HHmmss";
    var formatter = DateTimeFormatter.ofPattern(format);
    return LocalDateTime.now().format(formatter);
  }

}