package app.Twenty48;

import java.util.Set;

import static app.Twenty48.Controller.*;

class MDP implements lib.mcts.MDP<State, Action> { // (val initialGameState: Game2048State ) : MDP<Game2048State, Game2048Action>() {

  private final State initialGameState;

  MDP(State initialGameState) {
    assert initialGameState != null : "initialGameState";
    this.initialGameState = initialGameState;
  }

  @Override
  public State initialState() {
    return initialGameState;
  }

  @Override
  public double reward(State previousState, Action action, State state) {
    assert state != null;
    return Math.log(state.score()) / Math.log(2);
  }

  @Override
  public State transition(State state, Action action) {
    return state.makeMove(action, state.gameGrid());
  }

  @Override
  public Set<Action> actions(State state) {
    return Set.of(Action.values());
  }

  @Override
  public boolean isTerminal(State state) {
    var isSolvedGrid = isGridSolved(state.gameGrid());
    var isFullGrid = isGridFull(state.gameGrid());
    return isSolvedGrid || isFullGrid;
  }

}
