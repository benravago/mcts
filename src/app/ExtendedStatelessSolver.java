package app;

import java.util.List;
import java.util.ArrayList;
import static java.lang.Math.*;

import lib.mcts.MDP;
import lib.mcts.GenericSolver;

public class ExtendedStatelessSolver<StateType, ActionType> extends GenericSolver<StateType, ActionType> {

  public ExtendedStatelessSolver(MDP<StateType, ActionType> mdp, int simulationDepthLimit, double explorationConstant, double rewardDiscountFactor, boolean verbose) {
    super(mdp, simulationDepthLimit, explorationConstant, rewardDiscountFactor, verbose);
    explorationTermHistory = new ArrayList<>();
  }

  private final List<Double> explorationTermHistory;
  public List<Double> explorationTermHistory() { return explorationTermHistory; }

  @Override
  public void runTreeSearch(int iterations) {
    for (var i = 0; i < iterations; i++) {
      runTreeSearchIteration();

      var bestChild = root().children().stream().max(this::compareUCT).orElse(null);
      if (verbose()) {
        System.out.print(root().children());
        System.out.println(" -> " + bestChild);
      }
      if (bestChild == null) continue;
      var ns = bestChild.n();
      var explorationFactor = explorationConstant() * sqrt(log((double)i) / ns);

      explorationTermHistory.add(explorationFactor);
    }
  }

  public List<ActionType> getOptimalHorizon() {
    var optimalHorizonArr = new ArrayList<ActionType>();
    var node = root();

    for (;;) {
      node = node.children().stream().max(this::compareN).orElse(null);
      if (node == null) break;
      optimalHorizonArr.add(node.inducingAction());
    }

    return optimalHorizonArr;
  }

}
