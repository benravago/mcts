package lib.mcts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

/**
 * A set of unit tests to test the mctreesearch4j package.
 * A simple MDP is designed [TestStochasticMDP].
 *
 * A set of tests is designed for the [GenericSolver] and the base class [Solver].
 * The tests ensure proper implementation of the vital classes.
 */
class CoreLibraryTests {

  StochasticMDP testMDP = new StochasticMDP(0.75);
  int depthLimit = 29;
  double exploreConstant = 0.4;
  double rewardDiscount = 0.01;
  boolean verbose = false;

  GenericSolver<StochasticState,StochasticAction> solver =
    new GenericSolver<>( testMDP, depthLimit, exploreConstant, rewardDiscount, verbose );

  ActionNode<StochasticState,StochasticAction> testRoot = solver.root();

  // Generic Solver Testing

  /**
   * Tests the [GenericSolver.expand] method functionality without failure.
   */
  @Test
  void coreLibraryTestExpandMethod() {
    solver.expand(testRoot);
  }

  /**
   * Tests the [GenericSolver.select] method functionality without failure.
   */
  @Test
  void coreLibraryTestSelectMethod() {
    solver.select(testRoot);
  }

  /**
   * Tests the [GenericSolver.simulate] method functionality without failure.
   */
  @Test
  void coreLibraryTestSimulation() {
    solver.simulate(testRoot);
  }

  /**
   * Tests the both the solver [GenericSolver.expand] and [GenericSolver.simulate]
   * basic functionality working in unison.
   */
  @Test
  void coreLibraryTestExpandAndSelect() {
    solver.select(randomExpansion());
  }

  /**
   * Tests to ensure the [GenericSolver.expand] method is working properly,
   * and correct meta-info is transfered from state-to-state.
   */
  @Test
  void coreLibraryTestExpand() {
    randomExpansion();
  }

  ActionNode<StochasticState, StochasticAction> randomExpansion() {
    var nextNode = solver.expand(testRoot);
    var iterC = 2 + (int)(Math.random() * 97);
    for (var i = 1; i <= iterC; i++){
      nextNode = solver.expand(nextNode);
    }
    assertTrue(nextNode.state().counter == iterC + 1,  "Test Search Tree Expansion");
    return nextNode;
  }

  /**
   * Tests to ensure the backpropagation method backpropagates values properly.
   * In this test the monotonic relation of n visits from child to parent is always maintained.
   */
  @Test
  void coreLibraryTestBackpropagation() {
    var node = testRoot;
    var n1 = node.n();
    solver.backPropagate(testRoot, 20.0);
    var n2 = node.n();
    // println("n2: " + n2.toString())
    // println("n1: " + n1.toString())
    assertTrue(n2 >= n1,  "Monotonic guarantee of n child <= n parent in single Backpropagate");
  }

  // Base Solver Testing

  /**
   * Test all key mechanisms, run single iteration without failure.
   */
  @Test
  void coreLibraryTestSingleIteration() {
    solver.runTreeSearchIteration();
  }

  /**
   * Test to ensure the MCTS algorithm as a whole is running,
   * and guarantees a monotonic relationship from child node to parent node,
   * where n child >= n parent for any random traversal down the tree.
   * This is a guarantee for any MCTS algorithm.
   */
  @Test
  void coreLibraryTestMCTS() {
    solver.runTreeSearch(99);
    var rootN = testRoot.n();
    System.out.println("n root: "+ rootN);

    var nextNodes = testRoot.children();
    var n1 = testRoot.n();
    while (!nextNodes.isEmpty()){
      var nextNode = nextNodes.stream().findAny().get();
      var n2 = nextNode.n();
      // println("next node n: " + n2.toString())
      nextNodes = nextNode.children();
      assertTrue(n2 < n1,  "Monotonic guarantee of n child <= n parent in MCTS");
      n1 = n2;
    }
  }


  // TestStochastic classes

  enum StochasticAction { LEFT, RIGHT; }
  record StochasticState(int stateIndex, int counter) {}

  class StochasticMDP implements MDP<StochasticState, StochasticAction>  {

    StochasticMDP(double bias) {
      this.bias = bias;
    }

    private final double bias;

    @Override
    public StochasticState initialState() {
      return new StochasticState(0,0);
    }
    @Override
    public boolean isTerminal(StochasticState state) {
        return false;
    }
    @Override
    public double reward(StochasticState previousState, StochasticAction action, StochasticState state) {
      return state.stateIndex * 2;
    }
    @Override
    public StochasticState transition(StochasticState state, StochasticAction action) {
      var directionIndex = Math.random() < bias ?
        switch(action) {
          case LEFT -> state.stateIndex - 1;
          case RIGHT -> state.stateIndex + 1;
        } :
        switch(action) {
          case LEFT -> state.stateIndex + 1;
          case RIGHT -> state.stateIndex - 1;
        } ;
      return new StochasticState(directionIndex, state.counter + 1);
    }
    @Override
    public Set<StochasticAction> actions(StochasticState state) {
      return Set.of(StochasticAction.values());
    }
  }

}
