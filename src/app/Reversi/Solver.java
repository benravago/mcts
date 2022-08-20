package app.Reversi;

import lib.mcts.StatefulSolver;

class Solver extends StatefulSolver<State, Position> {

  Solver(State initialState) {
    super(new MDP(initialState), 999, 1.4D, 0.9D, false);
  }

  Position getMove() {
    runTreeSearch(999);
    var action = extractOptimalAction();
    assert action != null : "optimal Move";
    return action;
  }

}