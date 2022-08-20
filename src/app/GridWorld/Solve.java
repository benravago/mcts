package app.GridWorld;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lib.mcts.GenericSolver;

class Solve {

  private final int xSize;
  private final int ySize;
  private final List<Reward> rewards;
  private final double transitionProbability;
  private final int iterations;
  private final int simDepth;
  private final double exploreConstant;
  private final double rewardDiscount;
  private final boolean verbose;

  private Map<Long, Action> mapOfSolutions; // mutableMapOf<Pair<Int, Int>, String>()

  Solve(int xSize, int ySize, List<Reward> rewards, double transitionProbability) {
    this(xSize, ySize, rewards, transitionProbability, 1000, 40, 1.4, 0.9, false);
  }

  Solve(int xSize, int ySize, List<Reward> rewards, double transitionProbability, int iterations, int simDepth, double exploreConstant, double rewardDiscount, boolean verbose) {
    this.xSize = xSize;
    this.ySize = ySize;
    this.rewards = rewards;
    this.transitionProbability = transitionProbability;
    this.iterations = iterations;
    this.simDepth = simDepth;
    this.exploreConstant = exploreConstant;
    this.rewardDiscount = rewardDiscount;
    this.verbose = verbose;
    mapOfSolutions = new LinkedHashMap<>();
  }

  Map<Long, Action> solutions() { return mapOfSolutions; }

  static long pair(int x, int y) { return ((long)x << 32) | (long)y; } // Pair( first:x, second:y )

  void getWorldSolve() {

    var rewardLocations = new HashSet<Long>();
    for (var r:rewards) {
      rewardLocations.add(pair(r.x, r.y));
    }

    for (var x = 0; x < xSize; x++) {
      for (var y = 0; y < ySize; y++) {
        var key = pair(x,y);
        if (rewardLocations.contains(key)) continue;

        var gridworld = new MDP(xSize, ySize, rewards, transitionProbability, new State(x, y, false));
        var solver = new GenericSolver<>(gridworld, iterations, exploreConstant, rewardDiscount, verbose);

        // println("Solving at [$x, $y]")
        solver.runTreeSearch(simDepth);
        // solver.displayTree()
        // println("Optimal action: ${solver.getNextOptimalAction()}")

        mapOfSolutions.put(key, solver.extractOptimalAction());
      }
    }
  }

  void visualizeWorldSolve() {
    var out = new StringBuilder();
    for (var y = ySize-1; y >= 0; y--) {
      out.append('[');
      for (var x = 0; x < xSize; x++) {
        var reward = singleOrNull(rewards, x, y);
        if (reward != null) {
          out.append(reward.value > 0 ? '1' : '0');
        } else {
          out.append(tag(mapOfSolutions.get(pair(x,y))));
        }
        out.append(',');
      }
      out.setCharAt(out.length()-1,']');
      System.out.println(out);
      out.setLength(iterations);
    }
  }

  static char tag(Action a) { return a.name().charAt(0); }

  static Reward singleOrNull(List<Reward> list, int x, int y) {
    Reward single = null;
    for (var r:list) {
      if (r.x == x && r.y == y) {
        if (single != null) return null;
        single = r;
      }
    }
    return single;
  }

}
