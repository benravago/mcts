package app.Reversi;

class Position {

  int x, y;

  Position(int x, int y) {
    this.x = x; this.y = y;
  }

  Position(Position p) {
    this.x = p.x; this.y = p.y;
  }

}
