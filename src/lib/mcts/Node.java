package lib.mcts;

import java.util.Collection;

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
public interface Node<ActionType, SelfType extends Node<ActionType, SelfType>> {

  SelfType parent();
  ActionType inducingAction();

  /**
   * The depth of the node.
   */
  int depth();

  /**
   * The number of visits to the node.
   */
  int n();

  /**
   * The reward value of the node.
   */
  double reward();

  /**
   * The max reward value among the children of this node.
   */
  double maxReward();

  /**
   * Add a child to the current node.
   */
  void addChild(SelfType child);

  /**
   * Get all the children of the current node.
   */
  Collection<SelfType> children();

}
