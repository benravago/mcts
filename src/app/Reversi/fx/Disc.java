package app.Reversi.fx;

class Disc {

  private int row;    // Position in row
  private int col;    // Position in column
  private int state;  // Disc state

  int col() { return col; }
  void col(int col) { this.col = col; }

  int row() { return row; }
  void row(int row) { this.row = row; }

  int state() { return state; }
  void state(int state) { this.state = state; }

}
