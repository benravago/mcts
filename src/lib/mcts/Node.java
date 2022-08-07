package lib.mcts;

import java.util.Collection;
import java.util.Collections;

/**
 * A representation of tree nodes used for Monte Carlo Tree Search (MCTS).
 *
 * This abstract type contains no logic but includes the definitions of functions that must be supported for each tree
 * node as well as commonly used values including depth, number of visits, current reward and the max reward among its
 * children.
 *
 * @param ActionType the type that represents the actions that can be taken in the MDP.
 * @param SelfType a convenience parameter to represent the type of itself.
 */
public abstract class Node<ActionType, SelfType extends Node<ActionType, SelfType>> {

  public Node(SelfType parent, ActionType inducingAction) {
    this.parent = parent;
    this.inducingAction = inducingAction;
    this.depth = this.parent == null ? 0 : this.parent.depth() + 1;
  }

  private final SelfType parent;
  private final ActionType inducingAction;

  public final SelfType parent() { return this.parent; }
  public final ActionType inducingAction() { return this.inducingAction; }

  public Iterable<? extends ActionType> validActions() { return Collections.emptyList(); }

  /**
   * The depth of the node.
   */
  private final int depth;
  public final int depth() { return this.depth; }

  /**
   * The number of visits to the node.
   */
  private int n;
  public final int n() { return this.n; }
  public final void n(int n) { this.n = n; }

  /**
   * The reward value of the node.
   */
  private double reward;
  public final double reward() { return this.reward; }
  public final void reward(double reward) { this.reward = reward; }
 
  /**
   * The max reward value among the children of this node.
   */
  private double maxReward;
  public final double maxReward() { return this.maxReward; }
  public final void maxReward(double maxReward) { this.maxReward = maxReward; }

  /**
   * Add a child to the current node.
   */
  public abstract void addChild(SelfType child);

  /**
   * Get all the children of the current node.
   */
  public abstract Collection<SelfType> children(ActionType action);
  public abstract Collection<SelfType> children();

}