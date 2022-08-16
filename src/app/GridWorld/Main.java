package app.GridWorld;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

import app.ExtendedStatelessSolver;

public class Main {
  public static void main(String...args) throws Exception {

    var rewards = List.of(
      new Reward(5, 4, -0.5),
      //  Reward(3, 3, 1.0),
      new Reward(1, 1, 1.0)
    );

    solveWorld(rewards);
    solveSingle(rewards);
  }

  static void solveWorld(List<Reward> rewards) throws Exception {
    var gw = new Solve(8, 5, rewards, 0.85);
    gw.getWorldSolve();
    gw.visualizeWorldSolve();
  }

  static void solveSingle(List<Reward> rewards) throws Exception {

    var gridworld = new MDP(8, 5, rewards, 0.8, new State(2, 2, false));
    var gwSolver = new ExtendedStatelessSolver<>(gridworld, 999, 0.28, 0.95, false);

    gwSolver.runTreeSearch(999);
    gwSolver.displayTree(3);

    // Write data
    var path = Paths.get("tmp/out_gw.txt");
    var explorationTermHistory = gwSolver.explorationTermHistory();
    Files.writeString(path, explorationTermHistory.toString() + ' ' + explorationTermHistory.size());
  }

}
