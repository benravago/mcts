package app.GridWorld;

class Position {

  protected final int x, y;

  Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int x() { return x; }
  public int y() { return y; }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Position p && x == p.x && y == p.y;
  }

}
