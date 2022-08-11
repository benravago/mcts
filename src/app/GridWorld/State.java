package app.GridWorld;

class State extends Position {

  private final boolean isTerminal;

  State(int x, int y, boolean isTerminal) {
    super(x,y);
    this.isTerminal = isTerminal;
  }

  boolean isTerminal() { return isTerminal; }

  boolean isNeighbourValid(Action action, int xSize, int ySize) {
    return switch (action) {
      case UP -> y != ySize - 1;
      case RIGHT -> x != xSize - 1;
      case DOWN -> y != 0;
      case LEFT -> x != 0;
    };
  }

  State resolveNeighbour(Action action, int xSize, int ySize) {
    return switch (action) {
      case UP -> (y == ySize - 1) ? null : new State(x, y + 1, false);
      case RIGHT -> (x == xSize - 1) ? null : new State(x + 1, y, false);
      case DOWN -> (y == 0) ? null : new State(x, y - 1, false);
      case LEFT -> (x == 0) ? null : new State(x - 1, y, false);
    };
  }

}
