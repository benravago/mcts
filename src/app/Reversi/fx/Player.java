package app.Reversi.fx;

class Player {

  private String name;    // Player name
  private int points;     // Number of player points
  private int discState;  // Player disc state which translates to disc color

  String name() { return name; }
  void name(String name) { this.name = name; }

  int points() { return points; }
  void points(int points) { this.points = points; }

  int discState() { return discState; }
  void discState(int discState) { this.discState = discState; }

}
