package lib.mcts;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.List;
import java.util.Collection;
import java.util.Collections;

/**
 * A representation of nodes used by the stateful [StatefulSolver] to solve a Markov Decision Process (MDP).
 *
 * This type contains several convenience properties for implementing a stateful MDP solver.
 *
 * @param StateType the type that represents the states of the MDP.
 * @param ActionType the type that represents the actions that can be taken in the MDP.
 *
 * The constructor takes in a [StateNode] that represents the parent node, an [ActionType] that represents the
 * action taken to transition to the current node, a [StateType] that represents the state at this node, a set of valid
 * actions that can be taken from this node and whether this node represents a terminal state
 */
public final class StateNode<StateType, ActionType> extends AbstractNode<ActionType, StateNode<StateType, ActionType>> {

  public StateNode(StateNode<StateType, ActionType> parent, ActionType inducingAction, StateType state, Set<ActionType> validActions, boolean isTerminal) {
    super(parent, inducingAction);
    this.state = state;
    assert validActions != null : "validActions";
    this.validActions = validActions;
    this.isTerminal = isTerminal;
    this.children = new LinkedHashMap<>();
  }

  private final StateType state;
  private final boolean isTerminal;
  private final Set<ActionType> validActions;

  public final StateType state() { return state; }
  public final boolean isTerminal() { return isTerminal; }

  @Override
  public final Set<ActionType> validActions() { return validActions; }

  private final Map<ActionType, StateNode<StateType, ActionType>> children;

  @Override
  public void addChild(StateNode<StateType, ActionType> child) {
    var action = child.inducingAction();
    if (action == null) {
      throw new IllegalArgumentException("Inducing action must be set on child");
    }
    if (children.containsKey(action)) {
      throw new IllegalArgumentException("A child with the same inducing action has already been added");
    }

    children.put(action, child);
  }

  @Override
  public Collection<StateNode<StateType, ActionType>> children()  {
    return children.values();
  }

  @Override
  public Collection<StateNode<StateType, ActionType>> children(ActionType action)  {
    assert action != null;
    var child = children.get(action);
    return child == null ? Collections.emptyList() : List.of(child);
  }

  @Override
  public String toString() {
    return "State: %s, Max Reward: %.5f".formatted(state(),maxReward());
  }

  /**
   * Returns all actions that have been taken at least once from this node.
   */
  public final Set<ActionType> exploredActions() {
    return children.keySet();
  }

}
