package app.reversi.fx;

import java.util.List;
import java.util.ArrayList;

class Board {

  record Move(int row, int col) {} // a.equals(b) true if {a,b}.row and {a,b}.col values match

  private final int rows = 8;  // Number of rows.
  private final int cols = 8;  // Number of columns.

  private final Disc[][] boardGrid;             // Representation of boardGrid in 2d array.
  private final List<Move> allValidMoves;       // List of all valid moves for current player; a list of arrays with coordinates [row, col].
  private final List<Disc> flippedDiscsToMark;  // List of all opponent discs captured by player.

  Board() {
    boardGrid = new Disc[rows][cols];
    allValidMoves = new ArrayList<>();
    flippedDiscsToMark = new ArrayList<>();
    initBoard();
  }

  Disc[][] grid() {
    return boardGrid;
  }

  /**
   * Inits boardGrid with two discs for each player.
   * All discs are initially set to -1 which translates in view to empty square on boardGrid.
   */
  final void initBoard() {
    var state = -1;
    for (var row = 0; row < boardGrid.length; row++) {
      for (var col = 0; col < boardGrid[row].length; col++) {
        addDisc(row, col, state);
      }
    }
    initDiscs();
  }

  void initDiscs() {
    var firstWhiteDiscRow = 3;
    var firstWhiteDiscCol = 3;
    var whiteDiscState = 0;
    modifyDiscState(firstWhiteDiscRow, firstWhiteDiscCol, whiteDiscState);

    var secWhiteDiscRow = 4;
    var secWhiteDiscCol = 4;
    modifyDiscState(secWhiteDiscRow, secWhiteDiscCol, whiteDiscState);

    var firstBlackDiscRow = 3;
    var firstBlackDiscCol = 4;
    var blackDiscState = 1;
    modifyDiscState(firstBlackDiscRow, firstBlackDiscCol, blackDiscState);

    var secBlackDiscRow = 4;
    var secBlackDiscCol = 3;
    modifyDiscState(secBlackDiscRow, secBlackDiscCol, blackDiscState);
  }

  /** Adds d object to boardGrid array. */
  void addDisc(int row, int col, int discState) {
    var disc = new Disc();
    disc.row(row);
    disc.col(col);
    disc.state(discState);
    boardGrid[row][col] = disc;
  }

  /** Gets d object from boardGrid for given coordinates. */
  Disc discAt(int row, int col) {
    return boardGrid[row][col];
  }

  /** Returns all valid moves for current player. */
  List<Move> allValidMoves() {
    return allValidMoves;
  }

  /** Returns list of disk to mark. */
  List<Disc> flippedDiscsToMark() {
    return flippedDiscsToMark;
  }

  void flipAllDiscs(int row, int col, int playerTurn) {
    flipHorizontalDiscs(row, col, playerTurn);
    flipVerticalDiscs(row, col, playerTurn);
    flipDiagonalDiscs(row, col, playerTurn);
  }

  void modifyDiscState(int row, int col, int discState) {
    var disc = discAt(row, col);
    disc.state(discState);
  }

  List<Disc> allPlayerDiscs(int currentPlayer) {
    var list = new ArrayList<Disc>();
    for (var row = 0; row < boardGrid.length; row++) {
      for (var col = 0; col < boardGrid[row].length; col++) {
        var disc = discAt(row, col);
        var discState = disc.state();
        if (discState == currentPlayer) {
          list.add(disc);
        }
      }
    }
    return list;
  }

  List<Move> horizontalMoves(Disc disc) {
    var discRow = disc.row();
    var discCol = disc.col();
    var colRight = discCol + 1;
    var opponentDiscState = 0;
    var nextDiscState = -1;
    var result = new ArrayList<Move>();

    if (disc.state() == 0) {
      opponentDiscState = 1;
    } else if (disc.state() == 1) {
      opponentDiscState = 0;
    }

    // search right
    if (colRight <= boardGrid.length - 1) {
      nextDiscState = discAt(discRow, colRight).state();
    }

    while (nextDiscState == opponentDiscState) {
      colRight++;

      if (colRight > boardGrid.length - 1) {
        break;
      }

      nextDiscState = discAt(discRow, colRight).state();

      if (nextDiscState == -1) {
        result.add(new Move(discRow,colRight));
        break;
      }
    }

    // search left
    var colLeft = discCol - 1;
    var nextDiscStateLeft = -1;

    if (colLeft >= 0) {
      nextDiscStateLeft = discAt(discRow, colLeft).state();
    }

    while (nextDiscStateLeft == opponentDiscState) {
      colLeft--;

      if (colLeft < 0) {
        break;
      }

      nextDiscStateLeft = discAt(discRow, colLeft).state();

      if (nextDiscStateLeft == -1) {
        result.add(new Move(discRow,colLeft));
        break;
      }
    }
    return result;
  }

  List<Move> verticalMoves(Disc disc) {
    var discRow = disc.row();
    var discCol = disc.col();
    var rowUp = discRow - 1;
    var opponentDiscState = 0;
    var nextDiscStateUp = -1;
    var result = new ArrayList<Move>();

    if (disc.state() == 0) {
      opponentDiscState = 1;
    } else if (disc.state() == 1) {
      opponentDiscState = 0;
    }

    // search up
    if (rowUp >= 0) {
      nextDiscStateUp = discAt(rowUp, discCol).state();
    }

    while (nextDiscStateUp == opponentDiscState) {
      rowUp--;
      if (rowUp < 0) {
        break;
      }

      nextDiscStateUp = discAt(rowUp, discCol).state();

      if (nextDiscStateUp == -1) {
        result.add(new Move(rowUp,discCol));
        break;
      }
    }

    var rowDown = discRow + 1;
    var nextDiscStateDown = -1;

    // search down
    if (rowDown <= boardGrid.length - 1) {
      nextDiscStateDown = discAt(rowDown, discCol).state();
    }

    while (nextDiscStateDown == opponentDiscState) {
      rowDown++;
      if (rowDown > boardGrid.length - 1) {
        break;
      }

      nextDiscStateDown = discAt(rowDown, discCol).state();

      if (nextDiscStateDown == -1) {
        result.add(new Move(rowDown,discCol));
        break;
      }
    }
    return result;
  }

  List<Move> diagonalMoves(Disc disc) {
    var result = new ArrayList<Move>();
    var row = disc.row();
    var col = disc.col();
    var discState = disc.state();
    var nextDiscState = -1;
    var opponentDiscState = 0;
    var prevDiscState = discState;

    if (disc.state() == 0) {
      opponentDiscState = 1;
    } else if (disc.state() == 1) {
      opponentDiscState = 0;
    }

    // diagonal up right
    for (var i = row - 1; i >= 0; i--) {
      col++;

      if (col > boardGrid.length - 1) {
        break;
      }

      nextDiscState = discAt(i, col).state();

      if (nextDiscState == discState) {
        break;
      }

      if (nextDiscState == opponentDiscState) {
        prevDiscState = opponentDiscState;
        continue;
      }

      if (nextDiscState == -1 && prevDiscState != discState) {
        result.add(new Move(i,col));
        break;
      } else if (nextDiscState == -1 && prevDiscState == discState) {
        break;
      }
    }

    row = disc.row();
    col = disc.col();
    discState = disc.state();
    nextDiscState = -1;
    opponentDiscState = 0;
    prevDiscState = discState;

    if (disc.state() == 0) {
      opponentDiscState = 1;
    } else if (disc.state() == 1) {
      opponentDiscState = 0;
    }

    // diagonal down left
    for (var i = row + 1; i < boardGrid.length; i++) {
      col--;

      if (col < 0) {
        break;
      }

      nextDiscState = discAt(i, col).state();

      if (nextDiscState == discState) {
        break;
      }

      if (nextDiscState == opponentDiscState) {
        prevDiscState = opponentDiscState;
        continue;
      }

      if (nextDiscState == -1 && prevDiscState != discState) {
        result.add(new Move(i,col));
        break;
      } else if (nextDiscState == -1 && prevDiscState == discState) {
        break;
      }
    }

    row = disc.row();
    col = disc.col();
    discState = disc.state();
    nextDiscState = -1;
    opponentDiscState = 0;
    prevDiscState = discState;

    if (disc.state() == 0) {
      opponentDiscState = 1;
    } else if (disc.state() == 1) {
      opponentDiscState = 0;
    }

    // diagonal up left
    for (var i = col - 1; i >= 0; i--) {
      row--;

      if (row < 0) {
        break;
      }

      nextDiscState = discAt(row, i).state();

      if (nextDiscState == discState) {
        break;
      }

      if (nextDiscState == opponentDiscState) {
        prevDiscState = opponentDiscState;
        continue;
      }

      if (nextDiscState == -1 && prevDiscState != discState) {
        result.add(new Move(row,i));
        break;
      } else if (nextDiscState == -1 && prevDiscState == discState) {
        break;
      }
    }

    row = disc.row();
    col = disc.col();
    discState = disc.state();
    nextDiscState = -1;
    opponentDiscState = 0;
    prevDiscState = discState;

    if (disc.state() == 0) {
      opponentDiscState = 1;
    } else if (disc.state() == 1) {
      opponentDiscState = 0;
    }

    // diagonal down right
    for (var i = col + 1; i < boardGrid.length; i++) {
      row++;

      if (row > boardGrid.length - 1) {
        break;
      }

      nextDiscState = discAt(row, i).state();

      if (nextDiscState == discState) {
        break;
      }

      if (nextDiscState == opponentDiscState) {
        prevDiscState = opponentDiscState;
        continue;
      }

      if (nextDiscState == -1 && prevDiscState != discState) {
        result.add(new Move(row,i));
        break;
      } else if (nextDiscState == -1 && prevDiscState == discState) {
        break;
      }
    }
    return result;
  }

  void validMoves(int newPlayerTurn) {
    allValidMoves.clear();
    // generate posible moves for player
    var list = allPlayerDiscs(newPlayerTurn);
    for (var d : list) {
      var hMoves = horizontalMoves(d);
      for (var m : hMoves) {
        allValidMoves.add(m);
      }

      var vMoves = verticalMoves(d);
      for (var m : vMoves) {
        allValidMoves.add(m);
      }

      var dMoves = diagonalMoves(d);
      for (var m : dMoves) {
        allValidMoves.add(m);
      }
    }
    removeDuplicatedValidMoves();
  }

  void removeDuplicatedValidMoves() {
    for (var i = 0; i < allValidMoves.size(); i++) {
      var iMove = allValidMoves.get(i);
      for (var j = 0; j < allValidMoves.size(); j++) {
        var jMove = allValidMoves.get(j);
        if (i != j && iMove.equals(jMove)) {
          allValidMoves.remove(j);
        }
      }
    }
  }

  /** Changes state of opponent d captured by player. */
  void flipHorizontalDiscs(int row, int col, int currentPlayer) {
    var nextDiscState = -1;
    var primaryDiscState = currentPlayer;
    var discsToFlip = new ArrayList<Disc>();

    // add loop to check if placed m "close" opponent discs on right
    for (var i = col + 1; i <= boardGrid.length - 1; i++) {
      nextDiscState = discAt(row, i).state();
      if (nextDiscState != primaryDiscState) {
        var opponentDisc = discAt(row, i);
        discsToFlip.add(opponentDisc);
      }

      if (nextDiscState == -1) {
        discsToFlip.clear();
        break;
      } else if (nextDiscState == primaryDiscState) {
        for (var d : discsToFlip) {
          discAt(d.row(), d.col()).state(primaryDiscState);
          flippedDiscsToMark.add(d);
        }
        break;
      } else if (i + 1 > boardGrid.length - 1) {
        discsToFlip.clear();
        break;
      }
    }

    // add loop to check if placed m "close" opponent discs on left
    for (var i = col - 1; i >= 0; i--) {
      nextDiscState = discAt(row, i).state();
      if (nextDiscState != primaryDiscState) {
        var opponentDisc = discAt(row, i);
        discsToFlip.add(opponentDisc);
      }

      if (nextDiscState == -1) {
        discsToFlip.clear();
        break;
      } else if (nextDiscState == primaryDiscState) {
        for (var d : discsToFlip) {
          discAt(d.row(), d.col()).state(primaryDiscState);
          flippedDiscsToMark.add(d);
        }
        break;
      } else if (i - 1 < 0) {
        discsToFlip.clear();
        break;
      }
    }
  }

  /** Changes state of opponent d captured by player. */
  void flipVerticalDiscs(int row, int col, int currentPlayer) {
    var nextDiscState = -1;
    var primaryDiscState = currentPlayer;
    var discsToFlip = new ArrayList<Disc>();

    // add loop to check if placed m "close" opponent discs up
    for (var i = row - 1; i >= 0; i--) {
      nextDiscState = discAt(i, col).state();
      if (nextDiscState != primaryDiscState) {
        var opponentDisc = discAt(i, col);
        discsToFlip.add(opponentDisc);
      }

      if (nextDiscState == -1) {
        discsToFlip.clear();
        break;
      } else if (nextDiscState == primaryDiscState) {
        for (var d : discsToFlip) {
          discAt(d.row(), d.col()).state(primaryDiscState);
          flippedDiscsToMark.add(d);
        }
        break;
      } else if (i - 1 < 0) {
        discsToFlip.clear();
        break;
      }
    }

    // add loop to check if placed m "close" opponent discs down
    for (var i = row + 1; i <= boardGrid.length - 1; i++) {
      nextDiscState = discAt(i, col).state();
      if (nextDiscState != primaryDiscState) {
        var opponentDisc = discAt(i, col);
        discsToFlip.add(opponentDisc);
      }

      if (nextDiscState == -1) {
        discsToFlip.clear();
        break;
      } else if (nextDiscState == primaryDiscState) {
        for (var d : discsToFlip) {
          discAt(d.row(), d.col()).state(primaryDiscState);
          flippedDiscsToMark.add(d);
        }
        break;
      } else if (i + 1 > boardGrid.length - 1) {
        discsToFlip.clear();
        break;
      }
    }
  }

  /** Changes state of opponent d captured by player. */
  void flipDiagonalDiscs(int rowValue, int colValue, int currentPlayer) {
    var row = rowValue;
    var col = colValue;
    var nextDiscState = -1;
    var primaryDiscState = currentPlayer;
    var tmpRow = row;
    var tmpCol = col;
    var discsToFlip = new ArrayList<Disc>();

    /** loop to check if placed move "close"
     *  opponent discs  (diagonal up right)
     */
    for (var i = row - 1; i >= 0; i--) {
      col++;

      if (col > boardGrid.length - 1) {
        break;
      }

      nextDiscState = discAt(i, col).state();
      if (nextDiscState != primaryDiscState) {
        var opponentDisc = discAt(i, col);
        discsToFlip.add(opponentDisc);
      }

      if (nextDiscState == -1) {
        discsToFlip.clear();
        break;
      } else if (nextDiscState == primaryDiscState) {
        for (var d : discsToFlip) {
          discAt(d.row(), d.col()).state(primaryDiscState);
          flippedDiscsToMark.add(d);
        }
        break;
      } else if (i - 1 < 0 || col + 1 > boardGrid.length - 1) {
        discsToFlip.clear();
        break;
      }
    }

    row = tmpRow;
    col = tmpCol;

    /**
     * loop to check if placed move "close"
     * opponent discs (diagonal down left)
     */
    for (var i = row + 1; i < boardGrid.length; i++) {
      col--;

      if (col < 0) {
        break;
      }

      nextDiscState = discAt(i, col).state();
      if (nextDiscState != primaryDiscState) {
        var opponentDisc = discAt(i, col);
        discsToFlip.add(opponentDisc);
      }

      if (nextDiscState == -1) {
        discsToFlip.clear();
        break;
      } else if (nextDiscState == primaryDiscState) {
        for (var d : discsToFlip) {
          discAt(d.row(), d.col()).state(primaryDiscState);
          flippedDiscsToMark.add(d);
        }
        break;
      } else if (i + 1 > boardGrid.length - 1 || col - 1 < 0) {
        discsToFlip.clear();
        break;
      }
    }

    row = tmpRow;
    col = tmpCol;

    /**
     * loop to check if placed move "close"
     * opponent discs (diagonal up left)
     */
    for (var i = row - 1; i >= 0; i--) {
      col--;

      if (col < 0) {
        break;
      }

      nextDiscState = discAt(i, col).state();
      if (nextDiscState != primaryDiscState) {
        var opponentDisc = discAt(i, col);
        discsToFlip.add(opponentDisc);
      }

      if (nextDiscState == -1) {
        discsToFlip.clear();
        break;
      } else if (nextDiscState == primaryDiscState) {
        for (var d : discsToFlip) {
          discAt(d.row(), d.col()).state(primaryDiscState);
          flippedDiscsToMark.add(d);
        }
        break;
      } else if (i - 1 < 0 || col - 1 < 0) {
        discsToFlip.clear();
        break;
      }
    }

    row = tmpRow;
    col = tmpCol;

    /**
     * loop to check if placed move "close"
     * opponent discs (diagonal down right)
     */
    for (var i = row + 1; i < boardGrid.length; i++) {
      col++;

      if (col > boardGrid.length - 1) {
        break;
      }

      nextDiscState = discAt(i, col).state();
      if (nextDiscState != primaryDiscState) {
        var opponentDisc = discAt(i, col);
        discsToFlip.add(opponentDisc);
      }

      if (nextDiscState == -1) {
        discsToFlip.clear();
        break;
      } else if (nextDiscState == primaryDiscState) {
        for (var d : discsToFlip) {
          discAt(d.row(), d.col()).state(primaryDiscState);
          flippedDiscsToMark.add(d);
        }
        break;
      } else if (i + 1 > boardGrid.length - 1 || col + 1 > boardGrid.length - 1) {
        discsToFlip.clear();
        break;
      }
    }
  }

}
