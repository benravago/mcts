package lib.mcts;

import java.util.Objects;
import static java.lang.Math.*;

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
public abstract class Solver<ActionType, NodeType extends Node<ActionType, NodeType>> {

  public Solver(boolean verbose, double explorationConstant) {
    this.verbose = verbose;
    this.explorationConstant = explorationConstant;
  }

  private final boolean verbose;
  private final double explorationConstant;

  protected final boolean verbose() { return this.verbose; }
  protected final double explorationConstant() { return this.explorationConstant; }

  /**
   * the root node of the tree.
   */
  public abstract NodeType root();
  public abstract void root(NodeType node);

  /**
   * Returns a leaf node in the tree given a starting node in the tree.
   */
  public abstract NodeType select(NodeType node);

  /**
   * Creates and returns a new child node given a leaf node.
   */
  public abstract NodeType expand(NodeType node);

  /**
   * Runs a simulation from the given leaf node and computes a score for the node.
   */
  public abstract double simulate(NodeType node);

  /**
   * Propagates the reward for the given node to the root of the tree.
   */
  public abstract void backPropagate(NodeType node, double reward);

  /**
   * Runs a given number of iterations of MCTS. The order is specified by [runTreeSearchIteration]
   */
  public void runTreeSearch(int iterations) {
    for (var i = 0; i < iterations; i++) {
      if (verbose) {
        traceln("");
        traceln("New iteration " + i);
        traceln("=============");
      }
      runTreeSearchIteration();
    }
  }

  /**
   * Runs a single iterations of MCTS. The default implementation runs [select], [expand], [simulate], [backpropagate]
   * in sequence. This can be overridden to improve performance for specific problem domains.
   */
  public void runTreeSearchIteration() {
    // Selection
    var best = select(root());

    if (verbose) {
      traceln("Selected:");
      displayNode(best);
    }

    // Expansion
    var expanded = expand(best);

    if (verbose) {
      traceln("Expanding:");
      displayNode(expanded);
    }

    // Simulation
    var simulatedReward = simulate(expanded);

    traceln("Simulated Reward: " + simulatedReward);

    // Update
    backPropagate(expanded, simulatedReward);
  }

  // Utilities

  /**
   * Calculates the UCT score of a given node in the tree.
   */
  protected final double calculateUCT(NodeType node) {
    Objects.requireNonNull(node, "node");
    var parent = node.getParent();
    var parentN = parent != null ? parent.n() : node.n();
    return calculateUCT(parentN, node.n(), node.reward(), explorationConstant);
  }

  /**
   * Calculates the UCT score given specific input parameters for the parent, the number of visits, the reward
   * and the exploration constant.
   */
  protected double calculateUCT(double parentN, double n, double reward, double explorationConstant) {
    return reward/n + explorationConstant * sqrt(log(parentN) / n );
  }

  /**
   * Returns the best action from the root by choosing the node with the highest amount of visits. This function
   * can be overridden to modify the behavior to improve performance for a specific problem domain.
   */
  public ActionType extractOptimalAction() {
    var list = this.root().children();
    return list.isEmpty() ? null
      : list.stream().max((x,y) -> x.n() - x.n()).get().getInducingAction();
  }

  // Debug and Diagnostics

  /**
   * Prints the string with a new line if verbose output is enabled.
   */
  protected void traceln(String string) {
    if (verbose) {
      System.out.println(string);
    }
  }

  /**
   * Prints the string if verbose output is enabled.
   */
  protected void trace(String string) {
    if (verbose) {
      System.out.print(string);
    }
  }

  /**
   * Formats a given node into a string.
   */
  protected String formatNode(NodeType node) {
    Objects.requireNonNull(node, "node");
    return node.toString();
  }

  /**
   * Prints the tree starting from the given node.
   */
  public void displayNode(NodeType node) {
    Objects.requireNonNull(node, "node");
    var parent = node.getParent();
    if (parent!= null) {
      displayNode(parent);
    }
    var depth = node.getDepth();
    if (depth > 0) {
      System.out.print(" ".repeat((node.getDepth() - 1)*2) + " \u2514");
    }
    System.out.println(formatNode(node));
  }

  /**
   * Prints the tree starting from the root up to a given depth.
   */
  public void displayTree(int depthLimit) { // default = 3
    displayTree(depthLimit, root(), "");
  }

  private final void displayTree(int depthLimit, NodeType node, String indent) {
    if (node == null || node.getDepth() > depthLimit) {
      return;
    }
    System.out.format("%s %s (n: %d, reward: %.5f, UCT: %.5f)",
      indent, formatNode(node), node.n(), node.reward(), calculateUCT(node)
    );
    var any = node.children();
    if (!any.isEmpty()) {
      var n = any.size();
      for (var child:any) {
        var b = n-- > 0 ? " \u251c" : " \u2514";
        displayTree(depthLimit, child, generateIndent(indent) + b);
      }
    }
  }

  private static String generateIndent(String indent) {
    return indent.replace('\u251c', '\u2502').replace('\u2514', ' ');
  }

}
