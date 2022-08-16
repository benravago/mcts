package lib.mcts;

import static java.lang.Math.*;
import static java.util.stream.Collectors.*;

/**
 * A stateful solver for a Markov Decision Process (MDP).
 *
 * This solver permanently store states at each of the nodes in the tree. Simulations are run once throughout the entire
 * MCTS run. This may not work well in some stochastic MDPs with high branching factors but may improve performance for
 * deterministic MDPs with lower branching factor by reducing the number of simulations run.
 *
 * @param StateType the type that represents the states of the MDP.
 * @param ActionType the type that represents the actions that can be taken in the MDP.
 *
 * The constructor takes in a [MDP], a depth limit for simulations, a exploration constant, a reward discount factor
 * and a verbosity flag.
 */
public class StatefulSolver<StateType, ActionType> extends AbstractSolver<ActionType, StateNode<StateType, ActionType>> {

  public StatefulSolver(MDP<StateType, ActionType> mdp, int simulationDepthLimit, double explorationConstant, double rewardDiscountFactor, boolean verbose) {
    super(verbose, explorationConstant);
    assert mdp != null : "mdp";
    this.mdp = mdp;
    this.simulationDepthLimit = simulationDepthLimit;
    this.rewardDiscountFactor = rewardDiscountFactor;
    this.root = createNode(null, null, this.mdp.initialState());
  }

  private final MDP<StateType, ActionType> mdp;
  private final int simulationDepthLimit;
  private final double rewardDiscountFactor;

  public final MDP<StateType, ActionType> mdp() { return mdp; }
  public final int simulationDepthLimit() { return simulationDepthLimit; }
  public final double rewardDiscountFactor() { return rewardDiscountFactor; }

  private StateNode<StateType, ActionType> root;

  @Override
  public final StateNode<StateType, ActionType> root() {
    return root;
  }

  @Override
  public final void root(StateNode<StateType, ActionType> root) {
    assert root != null : "root";
    this.root = root;
  }

  // SOLVER

  @Override
  public StateNode<StateType, ActionType> select(StateNode<StateType, ActionType> node) {
    assert node != null : "node";
    var currentNode = node;
    for (;;) {
      // If the node is terminal, return it
      if (mdp.isTerminal(currentNode.state())) {
        return currentNode;
      }

      var exploredActions = currentNode.exploredActions();
      assert(currentNode.validActions().size() >= exploredActions.size());

      // This state has not been fully explored
      if (currentNode.validActions().size() > exploredActions.size()) {
        return currentNode;
      }

      // This state has been explored, select best action
      currentNode = currentNode.children().stream().max(this::compareUCT).orElseThrow(() -> new IllegalStateException("There were no children for explored node"));
    }
  }

  @Override
  public StateNode<StateType, ActionType> expand(StateNode<StateType, ActionType> node) {
    assert node != null : "node";
    // If the node is terminal, return it
    if (node.isTerminal()) {
      return node;
    }

    // Expand an unexplored action
    var exploredActions = node.children().stream().map(c -> c.inducingAction()).collect(toSet());
    var unexploredActions = node.validActions().stream().filter(a -> !exploredActions.contains(a));
    var actionTaken = unexploredActions.findAny().orElseThrow(() -> new IllegalStateException("No unexplored actions available"));

    // Transition to new state for given action
    var newState = mdp.transition(node.state(), actionTaken);
    return createNode(node, actionTaken, newState);
  }

  @Override
  public double simulate(StateNode<StateType, ActionType> node) {
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
      var randomAction = validActions.stream().findAny().get();
      var newState = mdp.transition(currentState, randomAction);

      if (verbose()) {
        trace("-> " + randomAction);
        trace("-> " + newState);
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

  @Override
  public void backPropagate(StateNode<StateType, ActionType> node, double reward) {
    assert node != null : "node";
    var currentStateNode = node;
    var currentReward = reward;

    for (;;) {
      currentStateNode.maxReward(max(currentStateNode.maxReward(), currentReward));
      currentStateNode.reward(currentStateNode.reward() + currentReward);
      currentStateNode.n(currentStateNode.n() + 1);
      if ((currentStateNode = currentStateNode.parent()) == null) break;
      currentReward *= rewardDiscountFactor;
    }
  }

  // Utilities

  private final StateNode<StateType, ActionType> createNode(StateNode<StateType, ActionType> parent, ActionType inducingAction, StateType state) {
    var validActions = mdp.actions(state);
    var isTerminal = mdp.isTerminal(state);
    var stateNode = new StateNode<>(parent, inducingAction, state, validActions, isTerminal);
    if (parent != null) {
      parent.addChild(stateNode);
    }
    return stateNode;
  }

}
