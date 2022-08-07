package lib.mcts;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collection;
import static java.util.stream.Collectors.toList;


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
public final class ActionNode<StateType, ActionType> extends Node<ActionType, ActionNode<StateType, ActionType>> {

  public ActionNode(ActionNode<StateType, ActionType> parent, ActionType inducingAction) {
    super(parent, inducingAction);
    this.children = new ArrayList<>();
  }

  /**
   * The state at this node. This is only available if a simulation has run.
   */
  private StateType state;

  public final StateType state() {
    var s = this.state;
    if (s != null) {
       return s;
    } else {
       throw new IllegalStateException("Simulation not run at depth: " + this.depth());
    }
  }
  public final void state(StateType state) {
    this.state = state;
  }

  /**
   * A list of actions that can be taken from this node. This is only available if a simulation has run.
   */
  private Iterable<? extends ActionType> validActions;

  @Override
  public final Iterable<? extends ActionType> validActions() {
    var v = validActions;
    if (v != null) {
       return v;
    } else {
       throw new IllegalStateException("Simulation not run");
    }
  }

  public final void validActions(Iterable<? extends ActionType> validActions) {
    Objects.requireNonNull(validActions, "validActions");
    this.validActions = validActions;
  }

  private List<ActionNode<StateType, ActionType>> children;

  @Override
  public void addChild(ActionNode<StateType, ActionType> child) {
    Objects.requireNonNull(child, "child");
    children.add(child);
  }

  @Override
  public Collection<ActionNode<StateType, ActionType>> children() {
    return children;
  }

  @Override
  public Collection<ActionNode<StateType, ActionType>> children(ActionType action) {
    assert action != null;
    return children.stream().filter(c -> action.equals(c.inducingAction())).collect(toList());
  }

  @Override
  public String toString() {
    return "Action: %s, Max Reward: %.5f".formatted(inducingAction(),maxReward());
  }

}
