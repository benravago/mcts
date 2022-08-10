package lib.mcts;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.Collection;
import static java.util.stream.Collectors.*;

/**
 * A representation of nodes used by the stateless [GenericSolver] to solve a Markov Decision Process (MDP).
 *
 * This type contains several convenience properties for implementing a stateless MDP solver.
 *
 * @param StateType the type that represents the states of the MDP.
 * @param ActionType the type that represents the actions that can be taken in the MDP.
 *
 * The constructor takes in a [ActionNode] that represents the parent node and an [ActionType] that represents the
 * action taken to transition to the current node.
 */
public final class ActionNode<StateType, ActionType> extends AbstractNode<ActionType, ActionNode<StateType, ActionType>> {

  public ActionNode(ActionNode<StateType, ActionType> parent, ActionType inducingAction) {
    super(parent, inducingAction);
    this.children = new ArrayList<>();
  }

  private StateType state;
  private Set<ActionType> validActions;

  /**
   * The state at this node. This is only available if a simulation has run.
   */
  public final StateType state() {
    if (state != null) {
      return state;
    } else {
      throw new IllegalStateException("Simulation not run at depth: " + depth());
    }
  }
  public final void state(StateType state) {
    this.state = state;
  }

  /**
   * A list of actions that can be taken from this node. This is only available if a simulation has run.
   */
  @Override
  public final Set<ActionType> validActions() {
    if (validActions != null) {
      return validActions;
    } else {
      throw new IllegalStateException("Simulation not run");
    }
  }

  public final void validActions(Set<ActionType> validActions) {
    Objects.requireNonNull(validActions, "validActions");
    this.validActions = validActions;
  }

  private List<ActionNode<StateType, ActionType>> children;

  @Override
  public void addChild(ActionNode<StateType, ActionType> child) {
    children.add(child);
  }

  @Override
  public Collection<ActionNode<StateType, ActionType>> children() {
    return children;
  }

  @Override
  public Collection<ActionNode<StateType, ActionType>> children(ActionType action) {
    assert action != null;
    return children.stream().filter(c -> c.inducingAction().equals(action)).collect(toList());
  }

  @Override
  public String toString() {
    return "Action: %s, Max Reward: %.5f".formatted(inducingAction(),maxReward());
  }

}
