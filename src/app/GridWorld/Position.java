package app.GridWorld;

class Position {

  final int x, y;

  Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Position p && x == p.x && y == p.y;
  }

}
