package app.Twenty48;

import org.junit.jupiter.api.Test;

import app.ExtendedStatelessSolver;

import static org.junit.jupiter.api.Assertions.*;

import static app.Twenty48.Controller.*;

class GameTests {

  @Test
  void testScenario1() {
    var scenarioGrid = new int[][] {
      { 1024, 0, 0, 0 },
      { 512, 512, 0, 0 },
      { 0, 0, 0, 0 },
      { 0, 0, 0, 0 }
    };

    var score = testGame(scenarioGrid);
    assertTrue(score >= 2048, "Scenario reached a suboptimal solution of score: "+score);
  }

  int testGame(int[][] scenarioGrid) {

    var testGrid = copyOf(scenarioGrid);

    var initialGameState = new State(new Position(testGrid));
    var mdp = new MDP(initialGameState);

    var solver = new ExtendedStatelessSolver<>(mdp, 500, 1.4, 0.95, true);
    solver.runTreeSearch(200);
    solver.displayTree(3);

    System.out.println("optimalAction: " + solver.extractOptimalAction());

    var solList = solver.getOptimalHorizon();
    System.out.println("optimal Horizon: " + solList);

    // simply replay the 2048 game using the solution
    for (var a: solList) {
      testGrid = Controller.manipulateGrid(testGrid, a);
    }
    var replay = new State(new Position(testGrid));
    var score = new State(new Position(testGrid)).score();

    System.out.println("replay: " + replay +" -> "+ score);
    return score;
  }

}