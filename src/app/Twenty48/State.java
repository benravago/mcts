package app.Twenty48;

import static app.Twenty48.Controller.*;

class State {

  private final Position gamePosition;

  State(Position gamePosition) {
    assert rectangular(gamePosition.grid()) : "game grid must be rectangular";
    this.gamePosition = gamePosition;
  }

  int[][] gameGrid() {
    return gamePosition.grid();
  }

  int score() {
    int max = 0;
    for (var row:gameGrid()) for (var col:row) if (max < col) max = col;
    return max;
  }

//  val gameGrid = gamePosition.grid
//  val score = gameGrid.mapNotNull{ it.max() }.max()

  State makeMove(Action action, int[][] gameGrid) { // : Array<Array<Int>> = this.gameGrid): Game2048State {
    var newPosition = new Position(spawnNumber(manipulateGrid(gameGrid, action)));
    return new State(newPosition);
  }

//  val newPosition = Game2048Position(gameObject.spawnNumber(gameObject.manipulateGrid(gameGrid, action.toString())))
//  return Game2048State(newPosition)

  @Override
  public String toString() {
    return gamePosition.toString();
  }

}