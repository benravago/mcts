package app.GridWorld;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class AppTests {

  static char[] c(char...c) { return c; }
  static char[][] b(char[]...b) { return b; }
  static char[][][] a(char[][]...a) { return a; }

  char[][][] correctSolutionsOpenSpace = a(
    b( c('U', '1'), c('U')  , c('L', 'U'), c('L', 'U'), c('L', 'U'), c('L', 'U'), c('L', 'U'), c('L', 'U') ),
    b( c('1')     , c('1')  , c('L')     , c('L')     , c('L')     , c('L')     , c('L')     , c('L')      ),
    b( c('D', '1'), c('D')  , c('L', 'D'), c('L', 'D'), c('L', 'D'), c('L', 'D'), c('L', 'D'), c('L', 'D') ),
    b( c('D', '1'), c('D')  , c('L', 'D'), c('L', 'D'), c('L', 'D'), c('L', 'D'), c('L', 'D'), c('L', 'D') ),
    b( c('D', '1'), c('D')  , c('L', 'D'), c('L', 'D'), c('L', 'D'), c('0')     , c('D'),      c('L', 'D') )
    );

  List<Reward> worldFeaturesOpenSpace = List.of(
    new Reward(5, 4, -0.5),
    new Reward(1, 1, 1.0)
  );

  char[][][] correctSolutionsWall = a(
    b( c('U', '1'), c('U')  , c('L', 'U'), c('L', 'U'), c('L', 'U'), c('0')     , c('1', 'U'), c('L', 'U') ),
    b( c('1')     , c('1')  , c('L')     , c('L')     , c('L')     , c('0')     , c('1', 'U'), c('1', 'U') ),
    b( c('D', '1'), c('D')  , c('L', 'D'), c('L', 'D'), c('L', 'D'), c('0')     , c('1', 'U'), c('L', 'U') ),
    b( c('D', '1'), c('D')  , c('L', 'D'), c('L', 'D'), c('L', 'D'), c('0')     , c('1', 'U'), c('L', 'U') ),
    b( c('D', '1'), c('D')  , c('L', 'D'), c('L', 'D'), c('L', 'D'), c('L')     , c('L'     ), c('L')      )
  );

  List<Reward> worldFeaturesWall = List.of(
    new Reward(5, 3, -0.1),
    new Reward(5, 2, -0.1),
    new Reward(5, 1, -0.1),
    new Reward(5, 0, -0.1),
    new Reward(1, 1, 1.0)
  );

  double testGridWorld(List<Reward> worldFeatures, char[][][] solutionArray) {
    // Open space small world test.

    var gw = new Solve(8, 5, worldFeatures, 0.85, 9999, 75, 0.9, 0.9, false);

    gw.getWorldSolve();
    // gw.visualizeWorldSolve();

    var numIncorrect = 0.0;
    for (var s:gw.solutions().entrySet()) {
      var index = s.getKey();
      var first = (int)(index >>> 32);
      var second = (int)(index & 0xffffffff);
      if (!correct(s.getValue(), solutionArray[second][first])) {
        numIncorrect += 1;
      }
    }

    // println(gw.solutions());
    // println("incorrect="+numIncorrect+", solutions="+gw.solutions().size()+", features="+worldFeatures.size());
    return numIncorrect / (gw.solutions().size() - worldFeatures.size());
  }

  @Test
  void gridWorldTestOpenSpace() {
    // The world solves should be 95% accurate
    // Run 10 times, and get the average
    var openWorldErrors = new double[10];
    for (var i = 0; i < 10; i++) {
      openWorldErrors[i] = testGridWorld(worldFeaturesOpenSpace, correctSolutionsOpenSpace);
    }
    var average = average(openWorldErrors);
    assertTrue(average < 0.17, "Error percentage for grid world solution, (open space) " + average + " must be less than: 0.16");
  }

  @Test
  void gridWorldTestWall() {
    var wallErrorValues = new double[10];
    for (var i = 0; i < 10; i++){
      wallErrorValues[i] = testGridWorld(worldFeaturesWall, correctSolutionsWall);
    }
    var average = average(wallErrorValues);
    assertTrue( average < 0.17 , "Error percentage for grid world solution, (wall) " + average + " must be less than: 0.16");
  }

  static double average(double...a) {
    var total = 0.0;
    for (var d:a) total += d;
    return total / a.length;
  }

  boolean correct(Action a, char[] b) {
    var c = a.name().charAt(0);
    for (var d:b) if (d == c) return true;
    return false;
  }

}
