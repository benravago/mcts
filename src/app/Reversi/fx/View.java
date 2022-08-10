package app.reversi.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

class View {

  private final double width = 65.0;              // Square size. Also sets center of disc.
  private final double height = 65.0;             // Square size. Also sets center of disc.
  private final double radius = 28.0;             // Radius of disc.
  private final int indicatorRadius = 32;         // Radius of indicator circle.
  private final int frameStrokeWidth = 2;         // Frame border stroke.
  private final double frameInWidth = 600;        // Frame size parameter.
  private final double frameInHeight = 600;       // Frame size parameter.
  private final double frameOutWidth = 620;       // Frame size parameter.
  private final double frameOutHeight = 620;      // Frame size parameter.
  private final double markerWidth = width - 2;   // Valid move flipped width.
  private final double markerHeight = height - 2; // Valid move flipped height.
  private final double shadowOpacity = 0.35;      // Board shadow effect opacity.
  private final double markerRadius = 5.0;        // Flip flipped radius.

  final BorderPane pane;   // Main container which contains all view elemants.
  final Actions actions;   // Contains all elements placed in top part of border pane.
  final BoardGrid bg;
  final DiscView dv;

  View() {
    pane = new BorderPane();
    actions = new Actions();
    bg = new BoardGrid();
    dv = new DiscView();
  }

  /** Adds highlight effect to points counter. */
  void highLightPoints(Text textWhite, Text textBlack, int playerTurn) {
    var opacity = 1.0;
    if (playerTurn == 0) {
      textWhite.setFill(Color.web("#9DC8E4", opacity));
      textBlack.setFill(Color.WHITE);
    } else if (playerTurn == 1) {
      textBlack.setFill(Color.web("#9DC8E4", opacity));
      textWhite.setFill(Color.BLACK);
    }
  }

  class Actions {
    private final DiscView dv;          // DiscView class object.
    private TimerView timerViewWhite;   // TimerView class object for white player.
    private TimerView timerViewBlack;   // TimerView class object for balck player.
    private Text timerValueWhite;       // Timer clock value to display.
    private Text timerValueBlack;       // Timer clock value to display.
    private final Button newGameButton; // New game button.
    private Button newTimedGameButton;  // Timed game button.
    private Button aiPlayerButton;      // AI Player button.
    private final Button loadButton;    // Load game button.
    private final Button saveButton;    // Save game button.
    private final Button exitButton;    // Exit game button.
    private StackPane whiteCounter;     // Points counter view for white player.
    private StackPane blackCounter;     // Points counter view for black player.
    private final HBox scoreHbox;       // Score panel.
    private final HBox menu;            // Menu bar.
    private final VBox vbox;            // Vbox with action and scorePanel panel elements; added to top border pane

    Actions() {
      dv = new DiscView();
      timerViewWhite = new TimerView();
      timerViewBlack = new TimerView();
      newGameButton = new Button("New Game");
      newTimedGameButton = new Button("Timer");
      aiPlayerButton = new Button("AI Player");
      loadButton = new Button("Load");
      saveButton = new Button("Save");
      exitButton = new Button("Exit");
      scoreHbox = new HBox();
      menu = new HBox();
      vbox = new VBox();
      vbox.getChildren().addAll(actionBar(), scorePanel());
      pane.setTop(vbox);
    }

    /** Adds scorePanel panel view. */
    final HBox scorePanel() {
      var top = 5.0;
      var right = 0.0;
      var bottom = 5.0;
      var left = 0.0;
      var spacing = 25;
      var prefHeight = 80;
      scoreHbox.setPadding(new Insets(top, right, bottom, left));
      scoreHbox.setSpacing(spacing);
      scoreHbox.setPrefHeight(prefHeight);
      scoreHbox.getStyleClass().add("hbox");
      scoreHbox.setAlignment(Pos.CENTER);
      whiteCounter = dv.makePointsCounterView(0);
      blackCounter = dv.makePointsCounterView(1);
      timerValueWhite = timerViewWhite.timerValue();
      timerValueBlack = timerViewBlack.timerValue();

      scoreHbox.getChildren().addAll(
        timerViewWhite.makeTimerView(timerValueWhite),
        whiteCounter, blackCounter,
        timerViewBlack.makeTimerView(timerValueBlack)
      );
      return scoreHbox;
    }

    /** Adds action bar. */
    final HBox actionBar() {
      var prefWidth = 100;
      var prefHeight = 10;
      var spacing = 25;
      newGameButton.setPrefSize(prefWidth, prefHeight);
      newTimedGameButton.setPrefSize(prefWidth, prefHeight);
      aiPlayerButton.setPrefSize(prefWidth, prefHeight);
      loadButton.setPrefSize(prefWidth, prefHeight);
      saveButton.setPrefSize(prefWidth, prefHeight);
      exitButton.setPrefSize(prefWidth, prefHeight);

      menu.setSpacing(spacing);
      menu.getStyleClass().add("hbox2");
      menu.getChildren().addAll(
        newGameButton, newTimedGameButton,
        aiPlayerButton,
        loadButton, saveButton,
        exitButton
      );
      return menu;
    }

    /** Gets new game button. */
    Button newGame() { return newGameButton; }

    /** Gets load game button. */
    Button load() { return loadButton; }

    /** Gets save game button. */
    Button save() { return saveButton; }

    /** Gets exit game button. */
    Button exit() { return exitButton; }

    /** Gets points counter view for white player. */
    StackPane whiteCounter() { return whiteCounter; }

    /** Gets points counter view for black player. */
    StackPane blackCounter() { return blackCounter; }

    /** Gets timer clock view. */
    TimerView whiteTimer() { return timerViewWhite; }

    /** Sets timer view for white player. */
    void whiteTimer(TimerView timerView) { timerViewWhite = timerView; }

    /** Gets timer view. */
    TimerView blackTimer() { return timerViewBlack; }

    /** Sets timer view for black player. */
    void blackTimer(TimerView timerView) { timerViewBlack = timerView; }

    /** Gets timed game button. */
    Button newTimedGame() { return newTimedGameButton; }

    /** Sets timed game button. */
    void newTimedGame(Button newTimedGame) { newTimedGameButton = newTimedGame; }

    /** Gets AI Player button. */
    Button aiPlayer() { return aiPlayerButton; }

    /** Sets AI Player button. */
    void aiPlayer(Button aiButton) { aiPlayerButton = aiButton; }

  }

  class TimerView {

    private final Text timerValue; // Value of timer to display in clock.

    TimerView() {
      timerValue = new Text();
    }

    /**
     * Makes view of timer clock.
     * @param value timer value to display.
     * @return stack pane with timer elements.
     */
    StackPane makeTimerView(Text value) {
      var arcHeight = 30;
      var arcWidth = 30;
      var rectWidth = 70;
      var rectHeight = 30;
      var rectStroke = 1;
      var timerStroke = 4;
      var timerStack = new StackPane();
      timerStack.setAlignment(Pos.CENTER);
      var rectangle = new Rectangle(rectWidth, rectHeight, Color.web("#332211"));
      rectangle.setArcHeight(arcHeight);
      rectangle.setArcWidth(arcWidth);
      rectangle.setStroke(Color.web("#000000"));
      rectangle.setStrokeWidth(rectStroke);
      timerValue.setStrokeWidth(timerStroke);
      timerValue.setStyle("-fx-font-size: 15;");
      timerStack.getChildren().addAll(rectangle, timerValue);
      return timerStack;
    }

    /** Gets timer text object. */
    Text timerValue() {
      return timerValue;
    }

    /** Sets timer value. */
    void timerValue(String value) {
      var oneMinInSeconds = 60;
      var tenSec = 10;
      var min = Integer.valueOf(value) / oneMinInSeconds;
      var sec = Integer.valueOf(value) % oneMinInSeconds;
      if (min >= tenSec && sec >= tenSec) {
        var time = "%d:%d".formatted(min, sec);
        timerValue.setText(time);
      } else if (min < tenSec && sec < tenSec) {
        var time = "0%d:0%d".formatted(min, sec);
        timerValue.setText(time);
      } else if (min < tenSec && sec >= tenSec) {
        var time = "0%d:%d".formatted(min, sec);
        timerValue.setText(time);
      } else if (min >= tenSec && sec < tenSec) {
        var time = "%d:0%d".formatted(min, sec);
        timerValue.setText(time);
      }
    }

    /** Adds highlight to timer value displayed in clock. */
    void addHighlight() {
      timerValue.setFill(Color.web("#9DC8E4", 1.0));
    }

    /** Removes highlight from timer value displayed in clock. */
    void removeHighlight() {
      timerValue.setFill(Color.BLACK);
    }

    /** Adds red highlight to timer on remaining 10s. */
    void timeoutHighlight() {
      timerValue.setFill(Color.RED);
    }

    /** Hides timer. Changes color of text to background color. */
    void switchOff() {
      timerValue.setFill(Color.web("#332211", 1.0));
    }

    /** Shows timer. Changes color of text field to black. */
    void switchOn() {
      timerValue.setFill(Color.BLACK);
    }

  }

  class BoardGrid {

    private final GridPane grid;  // Grid pane container.
    private StackPane square;     // Square elements of board.

    BoardGrid() {
      grid = new GridPane();
      initBoardView();
    }

    /**
     * Inits board view.
     * Fills grid pane container with squares.
     */
    final void initBoardView() {
      grid.setAlignment(Pos.CENTER);
      var rowNum = 8;
      var colNum = 8;
      var opacity = 1.0;
      for (var row = 0; row < rowNum; row++) {
        for (var col = 0; col < colNum; col++) {
          square = new StackPane();
          square.getStyleClass().add("pane");
          if ((row + col) % 2 == 0) {
            var lightGreen = Color.web("#9fa881", opacity);
            square.getChildren().addAll(new Rectangle(width, height, lightGreen));
          } else {
            var darkGreen = Color.web("#6f7d42", opacity);
            square.getChildren().addAll(new Rectangle(width, height, darkGreen));
          }
          grid.add(square, col, row);
        }
      }
      pane.setCenter(addBoardToFrame(grid));
    }

    /** Adds board to frame. */
    StackPane addBoardToFrame(GridPane boardGrid) {
      var x = 10;
      var y = 10;
      var opacity = 0.80;
      var stack = new StackPane();
      var dropShadow = new DropShadow();
      var rectangleOut = new Rectangle(frameOutWidth, frameOutHeight, Color.web("#332211", opacity));
      var rectangleIn = new Rectangle(frameInWidth, frameInHeight, Color.web("#332211", opacity));
      rectangleOut.setStroke(Color.BLACK);
      rectangleOut.setStrokeWidth(frameStrokeWidth);
      rectangleIn.setStroke(Color.BLACK);
      rectangleIn.setStrokeWidth(frameStrokeWidth);
      rectangleOut.setStrokeType(StrokeType.OUTSIDE);
      rectangleIn.setStrokeType(StrokeType.OUTSIDE);

      // add shadow to board
      dropShadow.setOffsetX(x);
      dropShadow.setOffsetY(y);
      dropShadow.setColor(Color.web("#000000", shadowOpacity));
      rectangleOut.setEffect(dropShadow);

      stack.getChildren().addAll(rectangleOut, rectangleIn, grid);
      return stack;
    }

    /** Gets board grid pane. */
    GridPane pane() { return grid; }

  }

  class DiscView {

    /**
     * Makes disc filled with color defined by state.
     * @param discState disc state (0 - white, 1 - black).
     */
    Circle makeDisc(int discState) {
      var circle = new Circle();
      if (discState == 0) {
        // white disc
        circle.setCenterX(width);
        circle.setCenterY(height);
        circle.setRadius(radius);
        circle.setFill(Color.WHITE);
      } else if (discState == 1) {
        // black disc
        circle.setCenterX(width);
        circle.setCenterY(height);
        circle.setRadius(radius);
        circle.setFill(Color.BLACK);
      }

      // add spotlight effect
      circle = addSpotEffect(circle, discState);
      return circle;
    }

    Circle addSpotEffect(Circle disc, int discState) {
      var light = new Light.Spot();
      var x = 4;
      var y = 1;
      var z = 55;
      light.setColor(Color.WHITE);
      light.setX(x);
      light.setY(y);
      light.setZ(z);
      Lighting lighting = new Lighting();
      lighting.setLight(light);
      disc.setEffect(lighting);
      return disc;
    }

    /**
     * Makes highlight indicator to show player turn.
     * It is stacked with disc view and displayed on score panel.
     */
    Circle makePlayerIndicator() {
      var indicator = new Circle();
      var fillOpacity = 0.05;
      var strokeOpacity = 1.0;
      var strokeWidth = 2;
      indicator.setCenterX(width);
      indicator.setCenterY(height);
      indicator.setRadius(indicatorRadius);
      indicator.setFill(Color.web("#9DC8E4", fillOpacity));
      indicator.setStroke(Color.web("#9DC8E4", strokeOpacity));
      indicator.setStrokeWidth(strokeWidth);
      indicator.setStrokeType(StrokeType.INSIDE);
      return indicator;
    }

    /** Makes view for counter to display player points. */
    StackPane makePointsCounterView(int discState) {
      var stroke = 4;
      var dv = new DiscView();
      var disc = dv.makeDisc(discState);
      var stack = new StackPane();
      var discText = new Text();
      discText.setStrokeWidth(stroke);
      discText.setStyle("-fx-font-size: 15;");

      if (discState == 1) {
        discText.setFill(Color.WHITE);
      }

      stack.getChildren().addAll(disc, discText);
      return stack;
    }
  }

  /** Creates a summary box with information about winner. */
  StackPane summary(Player whitePlayer, Player blackPlayer) {
    return new SummaryView(whitePlayer, blackPlayer).pane();
  }

  class SummaryView {

    private StackPane summary;       // Main view container.
    private final Player playerOne;  // White player object.
    private final Player playerTwo;  // Black player object.
    private Text text;               // Text filed object.

    SummaryView(Player whitePlayer, Player blackPlayer) {
      summary = new StackPane();
      text = new Text();
      playerOne = whitePlayer;
      playerTwo = blackPlayer;
      createSummaryView();
    }

    final StackPane createSummaryView() {
      var maxHeight = 150;
      var maxWidth = 300;
      var spacing = 40;
      summary = addDropShadow(summary);
      summary.setAlignment(Pos.CENTER);
      summary.setMaxHeight(maxHeight);
      summary.setMaxWidth(maxWidth);
      summary.getStyleClass().add("pane2");

      if (playerOne.points() > playerTwo.points()) {
        text = new Text("White wins!");
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-size: 25;");
      } else if (playerOne.points() < playerTwo.points()) {
        text = new Text("Black wins!");
        text.setFill(Color.BLACK);
        text.setStyle("-fx-font-size: 25;");
      } else {
        text = new Text("Draw!");
        text.setFill(Color.BROWN);
        text.setStyle("-fx-font-size: 25;");
      }

      var vbox = new VBox();
      vbox.setSpacing(spacing);
      vbox.setAlignment(Pos.CENTER);
      vbox.getChildren().addAll(text);
      summary.getChildren().addAll(vbox);

      return summary;
    }

    /** Adds drop shadow effect to summary box. */
    StackPane addDropShadow(StackPane gameSummary) {
      var dropShadow = new DropShadow();
      var r = 1;
      var x = 4;
      var y = 4;
      dropShadow.setRadius(r);
      dropShadow.setOffsetX(x);
      dropShadow.setOffsetY(y);
      dropShadow.setColor(Color.web("#333333", shadowOpacity));
      gameSummary.setEffect(dropShadow);
      return gameSummary;
    }

    /** Gets summary object. */
    StackPane pane() { return summary; }
  }

  /**
   * Makes valid move marker.
   * It is used to highlight square on board grid.
   */
  Rectangle validMove() {
    var opacity = 0.30;
    var borderOpacity = 1.0;
    var strokeWidth = 2;
    var rectangle = new Rectangle(markerWidth, markerHeight, Color.web("#9DC8E4", opacity));
    rectangle.setStroke(Color.web("#9DC8E4", borderOpacity));
    rectangle.setStrokeWidth(strokeWidth);
    rectangle.setStrokeType(StrokeType.INSIDE);
    return rectangle;
  }

  /**
   * Makes a flipped disc marker.
   */
  Circle flipped() {
    var circle = new Circle();
    circle.setCenterX(width);
    circle.setCenterY(height);
    circle.setRadius(markerRadius);
    circle.setFill(Color.RED);
    return circle;
  }

}
