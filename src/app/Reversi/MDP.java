package app.Reversi;

import java.util.Set;

class MDP implements lib.mcts.MDP<State, Position> {

  private final State initialState;

  MDP(State initialState) {
    assert initialState != null : "initialState";
    this.initialState = initialState;
  }

  @Override
  public Set<Position> actions(State state) {
    assert state != null : "state";
    return Controller.resolveFeasibleMoves(state);
  }

  @Override
  public State initialState() {
    return initialState.copy();
  }

  @Override
  public boolean isTerminal(State state) {
    assert state != null : "state";
    return state.currentPlayer.equals(Square.EMPTY);
  }

  @Override
  public double reward(State previousState, Position action, State state) {
    assert state != null : "state";
    var opponentSquare = Controller.getOpponent(initialState.currentPlayer);
    var score = 0;
    for (var squares:state.squares) {
      for (var square:squares) {
        if (square == opponentSquare) {
          score--;
        } else if (square == initialState.currentPlayer) {
          score++;
        }
      }
    }
    return (double)score / (initialState.size*initialState.size);
  }

  @Override
  public State transition(State state, Position action) {
    assert state != null : "state";
    assert action != null : "action";
    var newState = state.copy();
    Controller.executeMove(newState, action);
    return newState;
  }

}
