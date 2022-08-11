package app.GridWorld;

class Reward extends Position {

  private final double value;

  Reward(int x, int y, double value) {
    super(x,y);
    this.value = value;
  }

  double value() { return value; }

}
