package lib.mcts;

/**
 * A representation of Markov Decision Process (MDP) solvers using Monte Carlo Tree Search (MCTS) methods.
 *
 * This abstract type contains four functions that must be implemented for a valid solver. Additional utility functions
 * are provided for convenience but can be overridden. The derived types of this class are used by the user to perform
 * MCTS on a given MDP.
 *
 * @param ActionType the type that represents the actions that can be taken in the MDP.
 * @param NodeType the type that represents the nodes of the tree that represents the MDP.
 *
 * The constructor takes in a boolean flag for debugging and a exploration constant to be used when choosing nodes to
 * visit during selection.
 */
public interface Solver<ActionType, NodeType extends Node<ActionType, NodeType>> {

  /**
   * the root node of the tree.
   */
  NodeType root();

  /**
   * Returns a leaf node in the tree given a starting node in the tree.
   */
  NodeType select(NodeType node);

  /**
   * Creates and returns a new child node given a leaf node.
   */
  NodeType expand(NodeType node);

  /**
   * Runs a simulation from the given leaf node and computes a score for the node.
   */
  double simulate(NodeType node);

  /**
   * Propagates the reward for the given node to the root of the tree.
   */
  void backPropagate(NodeType node, double reward);

  /**
   * Runs a given number of iterations of MCTS. The order is specified by [runTreeSearchIteration]
   */
  default void runTreeSearch(int iterations) {
    for (var i = 0; i < iterations; i++) runTreeSearchIteration();
  }

  /**
   * Runs a single iterations of MCTS; typically:
   *
   *   runTreeSearchIteration() {
   *     best = select(root)
   *     expanded = expand(best)
   *     simulatedReward = simulate(expanded)
   *     backPropagate(expanded, simulatedReward)
   *   }
   */
  void runTreeSearchIteration();

}
