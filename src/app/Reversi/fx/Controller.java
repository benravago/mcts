package app.Reversi.fx;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Duration;

class Controller {

  private final double turnTime = 30000.0;   // Timer time value in milliseconds.
  private final int initPlayerTurn = 0;      // Player turn on new game init.
  private final boolean flipMarker = true;   // Debug flipped for flipped discs.
  private final boolean moveMarker = true;   // Debug flipped for valid moves.

  private final Board board;                 // Board class object.
  private final View view;                   // View class object.
  private final Player playerOne;            // Player class object.
  private final Player playerTwo;            // Player class object.
  private int playerTurn;                    // 0 - white player, 1 - black player.
  private Timeline timeline;                 // Timeline class object for game timer.
  private boolean isTimerOn;                 // Flag for game timer.
  private boolean aiPlayer;                  // Flag for AI Player.


  Controller(Board board, View view) {
    this.board = board;
    this.view = view;
    playerOne = new Player();
    playerTwo = new Player();
    isTimerOn = false;
    aiPlayer = false;
    initController();
  }

  private void initController() {
    onGridClick();
    onExitButtonClick();
    onNewGameButtonClick();
    onSaveButtonClick();
    onLoadButtonClick();
    onTimerButtonClick();
    onAiPlayerButtonClick();
  }

  void initPlayer() {
    playerOne.discState(0);
    playerOne.name("A");
    playerTwo.discState(1);
    playerTwo.name("B");
  }

  void setPlayerTurn(int playerTurn) {
    this.playerTurn = playerTurn;
  }

  void changePlayerTurn() {
    if (playerTurn == 0) {
      setPlayerTurn(1);
    } else if (playerTurn == 1) {
      setPlayerTurn(0);
    }
  }

  void countPlayerPoints(Player player) {
    var discState = player.discState();
    player.points(board.allPlayerDiscs(discState).size());
  }

  void addSummary(Player whitePlayer, Player blackPlayer) {
    var index = 3;
    if (timeline != null) {
      timeline.pause();
    }
    var summary = view.summary(whitePlayer, blackPlayer);
    var node = view.pane.getCenter();
    var sp = (StackPane) node;
    sp.getChildren().add(index, summary);
  }

  void removeSummary() {
    var maxElements = 4;
    var summaryElementIndex = 3;
    var node = view.pane.getCenter();
    var sp = (StackPane) node;
    if (sp.getChildren().size() == maxElements) {
      sp.getChildren().remove(summaryElementIndex);
    }
  }

  void updateBoardView() {

    switchOnNoValidMoves();

    for (var square : view.bg.pane().getChildren()) {
      var col = GridPane.getColumnIndex(square);
      var row = GridPane.getRowIndex(square);
      var discState = board.discAt(row, col).state();
      var sp = (StackPane) square;

      if (sp.getChildren().size() == 2) {
        sp.getChildren().remove(1);
      }

      if (flipMarker) {
        sp.getChildren().add(view.dv.makeDisc(discState));
        for (var disc : board.flippedDiscsToMark()) {
          if (disc.row() == row && disc.col() == col) {
            var spWithMarker = new StackPane();
            sp.getChildren().remove(1);
            spWithMarker.getChildren().addAll(
              view.dv.makeDisc(discState),
              view.flipped()
            );
            sp.getChildren().add(spWithMarker);
          }
        }
      } else if (!flipMarker) {
        sp.getChildren().add(view.dv.makeDisc(discState));
      }

      if (moveMarker) {
        for (var move : board.allValidMoves()) {
          if (row == move.row() && col == move.col()) {
            sp.getChildren().add(view.validMove());
          }
        }
      }

      if (sp.getChildren().size() > 2) {
        sp.getChildren().remove(1, sp.getChildren().size() - 1);
      }
    }
    updatePointsCounters();

    updatePlayerTurnIndicators();

    if (isTimerOn) {
      setGameTimer();
    }

    if (board.allValidMoves().isEmpty()) {
      addSummary(playerOne, playerTwo);
    }

    board.flippedDiscsToMark().clear();
  }

  void resetTimerViewOnTimelineStop() {
    // reset timer view
    if (playerTurn == 0) {
      view.actions.whiteTimer().timerValue("0");
      view.actions.whiteTimer().removeHighlight();
    } else if (playerTurn == 1) {
      view.actions.blackTimer().timerValue("0");
      view.actions.blackTimer().removeHighlight();
    }
  }

  void updatePlayerTurnIndicators() {
    var maxElements = 3;
    var elementsInWhiteCounter = view.actions.whiteCounter().getChildren().size();
    var elementsInBlackCounter = view.actions.blackCounter().getChildren().size();

    if (playerTurn == 0) {
      view.actions.whiteCounter().getChildren().add(2, view.dv.makePlayerIndicator());
    } else if (playerTurn == 1) {
      view.actions.blackCounter().getChildren().add(2, view.dv.makePlayerIndicator());
    }

    if (elementsInWhiteCounter == maxElements) {
      view.actions.whiteCounter().getChildren().remove(2);
    } else if (elementsInBlackCounter == maxElements) {
      view.actions.blackCounter().getChildren().remove(2);
    }
  }

  void updatePointsCounters() {
    countPlayerPoints(playerOne);
    countPlayerPoints(playerTwo);

    // update white discAt
    var nodeWhite = view.actions.whiteCounter().getChildren().get(1);
    var textWhite = (Text) nodeWhite;
    textWhite.setText(Integer.toString(playerOne.points()));

    // update black discAt
    var nodeBlack = view.actions.blackCounter().getChildren().get(1);
    var textBlack = (Text) nodeBlack;
    textBlack.setText(Integer.toString(playerTwo.points()));

    view.highLightPoints(textWhite, textBlack, playerTurn);
  }

  void resetTimer() {
    timeline.stop();
    resetTimerViewOnTimelineStop();
  }

  void setGameTimer() {
    /** Time in seconds before timer changes color to red. */
    var timeoutSec = 10;
    timeline.setCycleCount(1);
    timeline.setAutoReverse(true);
    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(turnTime), new KeyValue(new WritableValue<Integer>() {

      @Override
      public Integer getValue() {
        return null;
      }

      @Override
      public void setValue(Integer value) {
        if (playerTurn == 0) {
          var currentTime = timeline.getCurrentTime();
          var totoalTurnTime = timeline.getTotalDuration();
          var seconds = (int) (totoalTurnTime.toSeconds() - currentTime.toSeconds());

          view.actions.whiteTimer().timerValue(Integer.toString(seconds));
          view.actions.blackTimer().timerValue("0");
          view.actions.blackTimer().removeHighlight();

          if (seconds > timeoutSec) {
            view.actions.whiteTimer().addHighlight();
          } else {
            view.actions.whiteTimer().timeoutHighlight();
          }

        } else if (playerTurn == 1) {
          var currentTime = timeline.getCurrentTime();
          var totoalTurnTime = timeline.getTotalDuration();
          var seconds = (int) (totoalTurnTime.toSeconds() - currentTime.toSeconds());

          view.actions.blackTimer().timerValue(Integer.toString(seconds));
          view.actions.whiteTimer().timerValue("0");
          view.actions.whiteTimer().removeHighlight();

          if (seconds > timeoutSec) {
            view.actions.blackTimer().addHighlight();
          } else {
            view.actions.blackTimer().timeoutHighlight();
          }
        }
      }

    }, null)));

    timeline.setOnFinished(e -> {
      if (playerTurn == 0) {
        view.actions.whiteTimer().removeHighlight();
        changePlayerTurn();
        board.validMoves(playerTurn);
        updateBoardView();
      } else if (playerTurn == 1) {
        view.actions.blackTimer().removeHighlight();
        changePlayerTurn();
        board.validMoves(playerTurn);
        updateBoardView();
      }
    });

    timeline.play();
  }

  void switchOnNoValidMoves() {
    // switch player if there are no valid moves
    if (board.allValidMoves().isEmpty()) {
      changePlayerTurn();
      updatePointsCounters();
      updatePlayerTurnIndicators();
      board.validMoves(playerTurn);
    }
  }

  /** Checks if placed move is on list. */
  boolean validatePlacedMove(int row, int col) {
    var result = false;
    for (var move : board.allValidMoves()) {
      if (row == move.row() && col == move.col()) {
        result = true;
      }
    }
    return result;
  }

  /** Runs game updates after placed move. */
  void runOnClick(int row, int col) {

    var validMove = validatePlacedMove(row, col);

    // player can place discAt only on empty square
    if (validMove) {
      board.modifyDiscState(row, col, playerTurn);
      board.flipAllDiscs(row, col, playerTurn);

      if (isTimerOn) {
        resetTimer();
      }

      // change player after update
      changePlayerTurn();

      board.validMoves(playerTurn);

      updateBoardView();
    }
  }

  /** Click handler for placed move. */
  void onGridClick() {
    view.bg.pane().getChildren().forEach(square -> {
      square.setOnMouseClicked(event -> {
        var node = (Node) event.getSource();
        var col = GridPane.getColumnIndex(node);
        var row = GridPane.getRowIndex(node);
        runOnClick(row, col);
        if (aiPlayer && playerTurn == 1) {
          randomMoveGenerator();
        }
      });
    });
  }

  /** generate a random 'AI' move */
  void randomMoveGenerator() {
    var max = board.allValidMoves().size();
    var random = (int) (Math.random() * max);
    var move = board.allValidMoves().get(random);
    runOnClick(move.row(), move.col());
  }

  // TODO: mark the generated (AI or random) move

  void onExitButtonClick() {
    view.actions.exit().setOnMouseClicked(e -> {
      if (timeline != null) {
        timeline.pause();
      }

      var alert = new Alert(AlertType.CONFIRMATION);
      alert.setContentText("Do you want to exit game?");
      var option = alert.showAndWait();
      if (ButtonType.OK.equals(option.get())) {
        System.exit(0);
      } else {
        if (timeline != null && isTimerOn) {
          timeline.play();
        }
      }
    });
  }

  void onNewGameButtonClick() {
    view.actions.newGame().setOnMouseClicked(e -> {
      if (isTimerOn) {
        if (timeline != null) {
          timeline.stop();
          timeline = new Timeline();
        } else {
          timeline = new Timeline();
        }
      } else if (!isTimerOn) {
        timeline = new Timeline();
        timeline.pause();
      }

      initPlayer();
      setPlayerTurn(initPlayerTurn);
      board.initBoard();
      board.validMoves(playerTurn);
      updateBoardView();
      removeSummary();
      aiPlayer = false;
    });
  }

  void onTimerButtonClick() {
    view.actions.newTimedGame().setOnMouseClicked(e -> {
      isTimerOn = true;
      view.actions.whiteTimer().timerValue("0");
      view.actions.blackTimer().timerValue("0");
      view.actions.whiteTimer().switchOn();
      view.actions.blackTimer().switchOn();

      if (e.getClickCount() == 2 && timeline != null) {
        isTimerOn = false;
        timeline.stop();
        view.actions.whiteTimer().switchOff();
        view.actions.blackTimer().switchOff();
      } else if (e.getClickCount() == 2) {
        isTimerOn = false;
        view.actions.whiteTimer().switchOff();
        view.actions.blackTimer().switchOff();
      }
    });
  }

  Window stage() {
    return view.pane.getScene().getWindow();
  }

  void onSaveButtonClick() {
    view.actions.save().setOnMouseClicked(e -> {
      if (timeline != null) {
        timeline.pause();
      }

      Filer.save(stage(), board, playerTurn);

      if (timeline != null && isTimerOn) {
        timeline.play();
      }
    });
  }

  void onLoadButtonClick() {
    view.actions.load().setOnMouseClicked(e -> {
      if (timeline != null) {
        timeline.pause();
      }

      // loading file when previous game ended
      removeSummary();

      var playerState = Filer.load(stage(), board);

      if (playerState == 0 || playerState == 1) {
        setPlayerTurn(playerState);

        if (timeline != null) {
          resetTimer();
          if (!isTimerOn) {
            view.actions.whiteTimer().switchOff();
            view.actions.blackTimer().switchOff();
          }
        } else {
          timeline = new Timeline();
          initPlayer();
        }

        board.validMoves(playerTurn);
        updateBoardView();

        return;
      }

      if (timeline != null && isTimerOn) {
        timeline.play();
      }
    });
  }

  void onAiPlayerButtonClick() {
    view.actions.aiPlayer().setOnMouseClicked(event -> aiPlayer = !aiPlayer );
  }

}
