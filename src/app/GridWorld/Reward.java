package app.GridWorld;

class Reward extends Position {

  final double value;

  Reward(int x, int y, double value) {
    super(x,y);
    this.value = value;
  }

}
