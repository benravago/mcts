package app.PushYourLuck;

class State {

  State(boolean[][] state) {
    assert state != null : "state";
    this.state = state;
  }

  private boolean[][] state;

  boolean[][] state() { return state; }
  void state(boolean[][] state) { this.state = state; }

}
