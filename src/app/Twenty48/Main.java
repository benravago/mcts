package app.Twenty48;

import java.nio.file.Files;
import java.nio.file.Paths;

import app.ExtendedStatelessSolver;

class Main {
  public static void main(String...args) throws Exception {

    var testGrid = new int[][] {
      { 0, 0, 2, 0 },
      { 2, 2, 0, 0 },
      { 64, 64, 2, 2 },
      { 128, 256, 512, 1024 }
    };

    var initialGameState = new State(new Position(testGrid));

    var mdp = new MDP(initialGameState);

    var solver = new ExtendedStatelessSolver<>(mdp, 999, 1.4, 0.9, true);

    solver.runTreeSearch(999);
    solver.displayTree(3);

    System.out.println("optimalAction: " + solver.extractOptimalAction());

    var solList = solver.getOptimalHorizon(); // .map { it.toString() }
    System.out.println("optimal Horizon: " + solList);

    // simply replay the 2048 game using the solution
    for (var a:solList) {
      testGrid = Controller.manipulateGrid(testGrid, a);
    }
    var replay = new State(new Position(testGrid));
    System.out.println("replay: " + replay + " -> " + replay.score());

    // Write data
    var path = Paths.get("tmp/out_2048.txt");
    var explorationTermHistory = solver.explorationTermHistory();
    Files.writeString(path, explorationTermHistory.toString() + ' ' + explorationTermHistory.size() );
  }

}
