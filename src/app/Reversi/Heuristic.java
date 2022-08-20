package app.Reversi;

import java.util.ArrayList;

import lib.mcts.StateNode;

class Heuristic extends Solver {

  Heuristic(State initialState) {
    super(initialState);
  }

  static final int[][] heuristicWeight = {
    { 100, -10, 11, 6, 6, 11, -10, 100 },
    { -10, -20,  1, 2, 2,  1, -20, -10 },
    {  10,   1,  5, 4, 4,  5,   1,  10 },
    {   6,   2,  4, 2, 2,  4,   2,   6 },
    {   6,   2,  4, 2, 2,  4,   2,   6 },
    {  10,   1,  5, 4, 4,  5,   1,  10 },
    { -10, -20,  1, 2, 2,  1, -20, -10 },
    { 100, -10, 11, 6, 6, 11, -10, 100 }
  };

  @Override
  public double simulate(StateNode<State, Position> node) {
    assert node != null : "node";
    traceln("Simulation:");

    // If state is terminal, the reward is defined by MDP
    if (node.isTerminal()) {
      traceln("Terminal state reached");
      var parent = node.parent();
      return mdp.reward(parent != null ? parent.state() : null, node.inducingAction(), node.state());
    }

    var depth = 0;
    var currentState = node.state();
    var discount = rewardDiscountFactor;

    for (;;) {
      var validActions = mdp.actions(currentState);

      var bestActionScore = Integer.MIN_VALUE;
      var bestActions = new ArrayList<Position>();

      for (var action:validActions) {
        var score = heuristicWeight[action.x][action.y];
        if (score > bestActionScore) {
          bestActionScore = score;
          bestActions.clear();
        }
        if (score == bestActionScore) {
          bestActions.add(action);
        }
      }

      var randomAction = bestActions.stream().findAny().get();
      var newState = mdp.transition(currentState, randomAction);

      if (verbose()) {
        trace("-> " + randomAction);
        trace("=> " + newState);
      }

      if (mdp.isTerminal(newState)) {
        var reward = mdp.reward(currentState, randomAction, newState) * discount;
        if (verbose()) {
          traceln("-> Terminal state reached : " + reward);
        }
        return reward;
      }

      currentState = newState;
      depth++;
      discount *= rewardDiscountFactor;

      if (depth > simulationDepthLimit) {
        var reward = mdp.reward(currentState, randomAction, newState) * discount;
        if (verbose()) {
          traceln("-> Depth limit reached: " + reward);
        }
        return reward;
      }
    }
  }

}
