package app.Reversi;

import java.util.Arrays;

class State { // -> Board

  final Square[][] squares;
  final int size;
  Square currentPlayer;

  private State(Square[][] grid, Square player) {
    this.squares = grid;
    this.size = grid.length;
    this.currentPlayer = player;
  }

  static State of(int size) {
    var g = new Square[size][size];
    for (var r = 0; r < size; r++) {
      for (var c = 0; c < size; c++) {
        g[r][c] = Square.EMPTY;
      }
    }
    g[size / 2 - 1][size / 2 - 1] = Square.LIGHT;
    g[size / 2][size / 2] = Square.LIGHT;
    g[size / 2 - 1][size / 2] = Square.DARK;
    g[size / 2][size / 2 - 1] = Square.DARK;

    return new State(g,Square.DARK);
  }

  State copy() {
    var g = new Square[size][size];
    for (var r = 0; r < size; r++) {
      for (var c = 0; c < size; c++) {
        g[r][c] = squares[r][c];
      }
    }

    return new State(g,currentPlayer);
  }

  void set(Position move, Square player) {
    squares[move.x][move.y] = player;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof State s
        && (currentPlayer == s.currentPlayer)
        && Arrays.deepEquals(squares, s.squares);
  }

}
