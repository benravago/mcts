package app.PushYourLuck;

import java.util.Random;

class Dice {

  private final int nDice;
  private final int nSides;

  private boolean[][] markedSides;
  private double instantReward;

  private final int[][] diceConfig;

  Dice(int nDice, int nSides, double instantReward) {
    this.nDice = nDice;
    this.nSides = nSides;
    this.instantReward = instantReward;
    this.markedSides = marks(this.nDice, this.nSides);
    this.diceConfig = pips(this.nDice, this.nSides);
  }

  boolean[][] markedSides() { return markedSides; }
  double instantReward() { return instantReward; }

  static final Random random = new Random();

  boolean[][] roll() {
    var nMarkedSides = markedSides.length;
    // For each dice generate a random integer between (including) 0 to nSides-1
    var diceRollResults = new int[nMarkedSides];
    for (var d = 0; d < nMarkedSides; d++) {
      diceRollResults[d] = random.nextInt(nSides - 1);
      // Use -1 to represent unmarking
    }

    for (var d = 0; d < nMarkedSides; d++) {
      var rollInd = diceRollResults[d];
      if (filter(diceRollResults,d) > 1) {
        markedSides[d][rollInd] = false;
      } else {
        markedSides[d][rollInd] = !(markedSides[d][rollInd]);
      }
    }

    return markedSides;
  }

  static int filter(int[] results, int d) {
    var size = 0;
    for (var it:results) {
      if (it == results[d]) {
        size++;
      }
    }
    return size;
  }

  double cashOut() {
    var nD = diceConfig.length + -1;
    for (var d = 0; d < nD; d++) {
      for (var any:markedSides[d]) {
        if (any) {
          // logic is any side of the dice is marked, indicated by bool true
          var runningProduct = 1.0D;
          var nS = diceConfig[d].length + -1;
          for (var s = 0; s < nS; s++) {
            if (markedSides[d][s]) {
              runningProduct *= diceConfig[d][s];
            }
          }
          instantReward = runningProduct;
          break; // for(m)
        }
      }
    }
    // Reset the dice
    markedSides = marks(nDice, nSides);
    return instantReward;
  }

  // Array(nDice){ i -> Array(nSides){i -> i + 1}.toList() }.toList()
  static int[][] pips(int nDice, int nSides) {
    var rows = new int[nDice][];
    for (var i = 0; i < rows.length; i++) {
      var cols = new int[nSides];
      for(var j = 0; j < cols.length; j++) {
        cols[j] = j + 1;
      }
      rows[i] = cols;
    }
    return rows;
  }

  // MutableList(nDice){MutableList(nSides){false}}
  static boolean[][] marks(int nDice, int nSides) {
    var rows = new boolean[nDice][];
    for (var i = 0; i < rows.length; i++) {
      var cols = new boolean[nSides];
      for(var j = 0; j < cols.length; j++) {
        cols[j] = false;
      }
      rows[i] = cols;
    }
    return rows;
  }

}

