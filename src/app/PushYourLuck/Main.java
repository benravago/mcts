package app.PushYourLuck;

import java.nio.file.Files;
import java.nio.file.Paths;

import app.ExtendedStatelessSolver;

public class Main {
  public static void main(String...args) throws Exception {

    var pylMDP = new MDP(1,6);
    var solver = new ExtendedStatelessSolver<>( pylMDP, 999, 0.07, 0.99, false );

    solver.runTreeSearch(999);
    solver.displayTree(3);

    var optimalHorizon = solver.getOptimalHorizon();
    System.out.println(optimalHorizon.toString() + ' ' + optimalHorizon.size());

    // Write data
    var path = Paths.get("tmp/out_pyl.txt");
    var explorationTermHistory = solver.explorationTermHistory();
    Files.writeString(path, explorationTermHistory.toString() + ' ' + explorationTermHistory.size() );

  }
}
