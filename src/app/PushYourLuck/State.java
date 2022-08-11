package app.PushYourLuck;

import java.util.Objects;

class State {

  State(boolean[][] state) {
    Objects.requireNonNull(state, "state");
    this.state = state;
  }

  private boolean[][] state;

  boolean[][] state() { return state; }
  void state(boolean[][] state) { this.state = state; }

}