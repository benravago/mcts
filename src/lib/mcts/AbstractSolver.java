package lib.mcts;

import java.util.Objects;
import static java.lang.Math.*;

public abstract class AbstractSolver<ActionType, NodeType extends Node<ActionType, NodeType>> implements Solver<ActionType,NodeType> {

  public AbstractSolver(boolean verbose, double explorationConstant) {
    this.verbose = verbose;
    this.explorationConstant = explorationConstant;
  }

  private final boolean verbose;
  private final double explorationConstant;

  protected final boolean verbose() { return verbose; }
  protected final double explorationConstant() { return explorationConstant; }

  public abstract void root(NodeType root);

  // SOLVER

  @Override
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
   * The default implementation runs [select], [expand], [simulate], [backpropagate] in sequence.
   * This can be overridden to improve performance for specific problem domains.
   */
  @Override
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
    var parent = node.parent();
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

  protected final int compareUCT(NodeType x, NodeType y) {
    var a = calculateUCT(x);
    var b = calculateUCT(y);
    return a < b ? -1 : a > b ? 1 : 0;
  }

  /**
   * Returns the best action from the root by choosing the node with the highest amount of visits. This function
   * can be overridden to modify the behavior to improve performance for a specific problem domain.
   */
  public ActionType extractOptimalAction() {
    var mostVisited = root().children().stream().max(this::compareN).orElse(null);
    return mostVisited != null ? mostVisited.inducingAction() : null;
  }

  protected final int compareN(NodeType a, NodeType b) {
    return a.n() - b.n();
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
    var parent = node.parent();
    if (parent!= null) {
      displayNode(parent);
    }
    var depth = node.depth();
    if (depth > 0) {
      System.out.print(" ".repeat((node.depth() - 1)*2) + " " + boxur);
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
    if (node == null || node.depth() > depthLimit) {
      return;
    }
    System.out.format("%s %s (n: %d, reward: %.5f, UCT: %.5f)",
      indent, formatNode(node), node.n(), node.reward(), calculateUCT(node)
    );
    var any = node.children();
    if (!any.isEmpty()) {
      var n = any.size();
      for (var child:any) {
        var b = n-- > 0 ? " "+boxvr : " "+boxur;
        displayTree(depthLimit, child, generateIndent(indent) + b);
      }
    }
  }

  private static String generateIndent(String indent) {
    return indent.replace(boxvr, boxv).replace(boxur, ' ');
  }

  final static char
    boxur = '\u2514',  // &boxur;
    boxvr = '\u251c',  // &boxvr;
    boxv  = '\u2502';  // &boxv;

}
