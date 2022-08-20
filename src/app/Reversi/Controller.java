package app.Reversi;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.util.function.Consumer;

class Controller {

  static Square getOpponent(Square player) {
    return switch(player) {
      case LIGHT -> Square.DARK;
      case DARK -> Square.LIGHT;
      case EMPTY -> Square.EMPTY;
    };
  }

  static boolean executeMove(State state, Position move) {
    var flips = getAllFlips(state, move, state.currentPlayer);

    if (!flips.isEmpty()) {
      // Flip all
      for (var flip:flips) {
        state.set(flip, state.currentPlayer);
      }
      state.set(move, state.currentPlayer);

      var nextPlayer = getOpponent(state.currentPlayer);

      if (anyFeasibleMoves(state, nextPlayer)) {
        state.currentPlayer = nextPlayer;
      } else if (!anyFeasibleMoves(state, state.currentPlayer)) {
        state.currentPlayer = Square.EMPTY;
      }
    }

    return !flips.isEmpty();
  }

  static Set<Position> resolveFeasibleMoves(State state) {
    return resolveFeasibleMoves(state, state.currentPlayer);
  }

  static Set<Position> resolveFeasibleMoves(State state, Square player) {
    var moves = new HashSet<Position>();
    for (var r = 0; r < state.size; r++) {
      for (var c = 0; c < state.size; c++) {
        var p = new Position(r,c);
        if (anyFlips(state, p, player)) {
          moves.add(p);
        }
      }
    }
    return moves;
  }

  static boolean anyFeasibleMoves(State state, Square player) {
    for (var r = 0; r < state.size; r++) {
      for (var c = 0; c < state.size; c++) {
        if (anyFlips(state, new Position(r, c), player)) {
          return true;
        }
      }
    }
    return false;
  }

  static boolean anyFlips(State state, Position move, Square player) {
    if (state.squares[move.x][move.y] != Square.EMPTY) {
      return false;
    }

    if (anyFlips(state, move, player, p -> { north(p);          } )) return true;
    if (anyFlips(state, move, player, p -> { north(p); east(p); } )) return true;
    if (anyFlips(state, move, player, p -> { east(p);           } )) return true;
    if (anyFlips(state, move, player, p -> { south(p); east(p); } )) return true;
    if (anyFlips(state, move, player, p -> { south(p);          } )) return true;
    if (anyFlips(state, move, player, p -> { south(p); west(p); } )) return true;
    if (anyFlips(state, move, player, p -> { west(p);           } )) return true;
    if (anyFlips(state, move, player, p -> { north(p); west(p); } )) return true;

    return false;
  }

  static boolean anyFlips(State state, Position origin, Square player, Consumer<Position> nextPoint) {
    var flipped = false;
    var current = new Position(origin);
    nextPoint.accept(current);

    for (;;) {
      // Out of bounds
      if (current.x < 0 || current.y < 0 || current.x >= state.size || current.y >= state.size) {
        return false;
      }

      var square = state.squares[current.x][current.y];

      // Unflippable
      if (square == Square.EMPTY) {
        return false;
      }

      // Flipping has completed
      if (square == player) {
        break;
      }

      // Flipped the current square
      flipped = true;

      nextPoint.accept(current);
    }

    return flipped;
  }

  static List<Position> getAllFlips(State state, Position move, Square player) {
    if (state.squares[move.x][move.y] != Square.EMPTY ) {
      return Collections.emptyList();
    }

    var flips = new ArrayList<Position>();

    flips.addAll(getFlips(state, move, player, p -> { north(p);          }));
    flips.addAll(getFlips(state, move, player, p -> { north(p); east(p); }));
    flips.addAll(getFlips(state, move, player, p -> { east(p);           }));
    flips.addAll(getFlips(state, move, player, p -> { south(p); east(p); }));
    flips.addAll(getFlips(state, move, player, p -> { south(p);          }));
    flips.addAll(getFlips(state, move, player, p -> { south(p); west(p); }));
    flips.addAll(getFlips(state, move, player, p -> { west(p);           }));
    flips.addAll(getFlips(state, move, player, p -> { north(p); west(p); }));

    return flips;
}

  static List<Position> getFlips(State state, Position origin, Square player, Consumer<Position> nextPoint) {
    var flips = new ArrayList<Position>();
    var current = new Position(origin);
    nextPoint.accept(current);

    for (;;) {
      // Out of bounds
      if (current.x < 0 || current.y < 0 || current.x >= state.size || current.y >= state.size) {
        return Collections.emptyList();
      }

      var square = state.squares[current.x][current.y];

      // Unflippable
      if (square == Square.EMPTY) {
        return Collections.emptyList();
      }

      // Flipping has completed
      if (square == player) {
        break;
      }

      // Flip the current square
      flips.add(new Position(current));

      nextPoint.accept(current);
    }

    return flips.isEmpty() ? Collections.emptyList() : flips;
  }

  static void north(Position p) { p.x--; }
  static void east(Position p) { p.y++; }
  static void south(Position p) { p.x++; }
  static void west(Position p) { p.y--; }

}
