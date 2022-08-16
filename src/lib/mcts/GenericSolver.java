package lib.mcts;

import static java.lang.Math.*;
import static java.util.stream.Collectors.*;

/**
 * A stateless solver for a Markov Decision Process (MDP).
 *
 * This solver does not permanently store states at each of the nodes in the tree. Instead simulations are rerun for
 * each MCTS iteration starting from the root node. This allows maximum flexibility in handling a variety of MDPs
 * but may be slower due to the repeated simulations.
 *
 * @param StateType the type that represents the states of the MDP.
 * @param ActionType the type that represents the actions that can be taken in the MDP.
 *
 * The constructor takes in a [MDP], a depth limit for simulations, a exploration constant, a reward discount factor
 * and a verbosity flag.
 */
public class GenericSolver<StateType, ActionType> extends AbstractSolver<ActionType, ActionNode<StateType, ActionType>> {

  public GenericSolver(MDP<StateType, ActionType> mdp, int simulationDepthLimit, double explorationConstant, double rewardDiscountFactor, boolean verbose) {
    super(verbose, explorationConstant);
    assert mdp != null : "mdp";
    this.mdp = mdp;
    this.simulationDepthLimit = simulationDepthLimit;
    this.rewardDiscountFactor = rewardDiscountFactor;
    this.root = new ActionNode<>(null,null);
    simulateActions(this.root);
  }

  private final MDP<StateType, ActionType> mdp;
  private final int simulationDepthLimit;
  private final double rewardDiscountFactor;

  public final MDP<StateType, ActionType> mdp() { return mdp; }
  public final int simulationDepthLimit() { return simulationDepthLimit; }
  public final double rewardDiscountFactor() { return rewardDiscountFactor; }

  private ActionNode<StateType, ActionType> root;

  @Override
  public final ActionNode<StateType, ActionType> root() {
    return root;
  }

  @Override
  public final void root(ActionNode<StateType, ActionType> root) {
    assert root != null : "root";
    this.root = root;
  }

  // SOLVER

  @Override
  public ActionNode<StateType, ActionType> select(ActionNode<StateType, ActionType> node) {
    assert node != null : "node";
    // If this node is a leaf node, return it
    if (node.children().isEmpty()) {
      return node;
    }

    var currentNode = node;
    simulateActions(node);

    // Run a simulation greedily
    for (;;) {
      if (mdp.isTerminal(currentNode.state())) {
        return currentNode;
      }

      var currentChildren = currentNode.children();
      var exploredActions = currentChildren.stream().map(c -> c.inducingAction()).collect(toSet());

      if (currentNode.validActions().stream().anyMatch(a -> !exploredActions.contains(a))) {
        // There are unexplored actions
        return currentNode;
      }

      // All actions have been explored, choose best one
      currentNode = currentChildren.stream().max((a,b) -> compareUCT(a,b)).orElseThrow(() -> new IllegalStateException("There were no children for explored node"));
      simulateActions(currentNode);
    }
  }

  @Override
  public ActionNode<StateType, ActionType> expand(ActionNode<StateType, ActionType> node) {
    assert node != null : "node";
    // If the node is terminal, return it, except root node
    if (mdp.isTerminal(node.state())) {
      return node;
    }

    // Expand an unexplored action
    var exploredActions = node.children().stream().map(c -> c.inducingAction()).collect(toSet());
    var unexploredActions = node.validActions().stream().filter(a -> !exploredActions.contains(a));
    // Action cannot be null
    var actionTaken = unexploredActions.findAny().orElseThrow(() -> new IllegalStateException("No unexplored actions available"));

    // Transition to new state for given action
    var newNode = new ActionNode<>(node, actionTaken);
    node.addChild(newNode);
    simulateActions(newNode);

    return newNode;
  }

  @Override
  public double simulate(ActionNode<StateType, ActionType> node) {
    assert node != null : "node";
    traceln("Simulation:");

    // If state is terminal, the reward is defined by MDP
    if (mdp.isTerminal(node.state())) {
      traceln("Terminal state reached");
      var parent = node.parent();
      return mdp.reward((parent != null ? parent.state() : null), node.inducingAction(), node.state());
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
        trace(" => " + newState);
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
  public void backPropagate(ActionNode<StateType, ActionType> node, double reward) {
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

  private final void simulateActions(ActionNode<StateType, ActionType> node) {
    var parent = node.parent();

    if (parent == null) {
      var initialState = mdp.initialState();
      node.state(initialState);
      node.validActions(mdp.actions(initialState));
      return;
    }

    // Parent simulation must be run before current simulation can proceed
    var parentState = parent.state();
    // If the parent node is not null, a parent action must have been specified, otherwise it's an error
    var parentAction = node.inducingAction();
    if (parentAction == null) {
      throw new IllegalStateException("Action was null for non-null parent");
    }
    var state = mdp.transition(parentState, parentAction);
    node.state(state);
    node.validActions(mdp.actions(state));
  }

}
