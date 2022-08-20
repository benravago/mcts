package app.Reversi;

import java.util.function.Function;

class Main {
  public static void main(String...args) throws Exception {
    new Main().adversarialSim();
  }

  record Player(String name, Function<State,Position> move) {
    Position getMove(State state) { return move.apply(state.copy()); }
  }

  void simulate(Player[] players, int iterations) {

    var first = players[0];
    var second = players[1];

    System.out.printf("%s playing Dark and goes first, %s playing Light \n", first.name, second.name);

    var lightWins = 0;
    var darkWins = 0;

    for (var i = 0; i < iterations; i++) {
      var state = State.of(8);

      while (state.currentPlayer != Square.EMPTY) {
        System.out.print('.');
        if (state.currentPlayer == Square.DARK) {
          Controller.executeMove(state, first.getMove(state));
        }
        if (state.currentPlayer == Square.LIGHT) {
          Controller.executeMove(state, second.getMove(state));
        }
      }
      System.out.println();
      
      var dark = 0;
      var light = 0;
      // for (var square : state.squares.flatten()) {}
      for (var r:state.squares) for (var square:r) {
        if (square == Square.DARK) {
          dark++;
        } else if (square == Square.LIGHT) {
          light++;
        }
      }

      
      if (dark > light) {
        if (i % 1 == 0) {
          System.out.printf("%s won by %d to %d \n", first.name, dark, light);
        }
        darkWins++;
      } else if (dark < light) {
        if (i % 1 == 0) {
          System.out.printf("%s won by %d to %d \n", second.name, light, dark);
        }
        lightWins++;
      } else if (i % 1 == 0) {
        System.out.printf("Tied %d to %d \n", dark, light);
      }
    }
    
    System.out.printf("%s wins: %d, %s wins: %d \n", first.name, darkWins, second.name, lightWins);
  }

  void adversarialSim() {
    var startMillis = System.currentTimeMillis();

    var iterations = 20;
    Player[] players = {
      new Player("Heuristic", s -> new Heuristic(s).getMove()),
      new Player("Base", s -> new Solver(s).getMove())
    };

    simulate(players, iterations);
    simulate(reverse(players), iterations);

    var elapsedMillis = System.currentTimeMillis() - startMillis;

    System.out.println("Simulation took " + elapsedMillis + " ms");
  }

  static Player[] reverse(Player[] a) { return new Player[] { a[1], a[0] }; }

}
